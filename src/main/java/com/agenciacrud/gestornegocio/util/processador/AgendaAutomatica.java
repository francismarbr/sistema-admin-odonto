package com.agenciacrud.gestornegocio.util.processador;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.agenciacrud.gestornegocio.model.Agendamento;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.ExecucaoProcessador;
import com.agenciacrud.gestornegocio.model.enumeradores.StatusAgendamentoEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoExecucaoProcessadorEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoMensalAnualEnumerador;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.Numero;


public class AgendaAutomatica implements Job {


	private final Logger log = LogManager
			.getLogger(AgendaAutomatica.class);
	

	
	EntityManagerFactory factory = Persistence.createEntityManagerFactory("con");
	EntityManager manager = factory.createEntityManager();
	
	EntityTransaction trx = manager.getTransaction();
	
	private List<Agendamento> buscaUltimoRegistro;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
				
		if (isExecutarGerador()) {
			log.info(" ******** INICIANDO GERADOR AGENDA AUTOMÁTICA ******** ");

			List<Agendamento> listaInclusao = obterAgendamentosAutomaticos(Agendamento.class, true, StatusAgendamentoEnumerador.A_ACONTECER);
			if(listaInclusao == null) {
				log.info("*** LISTA VAZIA - NÃO EXISTE AGENDAMENTOS PROGRAMADOS ***");
			} else  {
				
				//enquanto a tiver valor na lista, os dados s�o inseridos na tabela autom�tica
				for (Agendamento agendamento : listaInclusao) {
					
					if (agendamento.getTipoIntervalo().equals(TipoMensalAnualEnumerador.MENSAL)) {
						Agendamento agenda = new Agendamento();
						
						agenda.setLembrete(agendamento.getLembrete());
						//agenda.setData(DataUtil.obterDataDiaInformadoMesAtual(agendamento.getDiaRealizacao()));
						agenda.setCliente(agendamento.getCliente());
						agenda.setStatus(agendamento.getStatus());
						agenda.setEmpresa(agendamento.getEmpresa());
						agenda.setDataDoRegistro(new Date());
						agenda.setDentista(agendamento.getDentista());
						/*if(agendamento.getHorario() != "")
							agenda.setHorario(agendamento.getHorario());*/
						
						
						//geração de sequência para chave de registro
						if(!Numero.isMaiorZero(agenda.getId())){
							Agendamento chaveRegistro = null;
							//Recebe em uma lista o último registro da empresa
							setBuscaUltimoRegistro((List<Agendamento>) buscaUltimoRegistroPorEmpresa(Agendamento.class, agendamento.getEmpresa()));
							
							if (getBuscaUltimoRegistro()==null) {
								//se a lista for vazia, soma 1 ao novo objeto
								agenda.setChaveRegistro(new Long(1)); 
							} else {
								//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
								chaveRegistro = buscaUltimoRegistro.get(0);
								Long codigo = (Long) chaveRegistro.getChaveRegistro();
								codigo += 1;
								agenda.setChaveRegistro(codigo);
							}
						}
						trx.begin();
							manager.persist(agenda);
						trx.commit();
					} else {
						Date data = new Date();
						GregorianCalendar dataCal = new GregorianCalendar();
						dataCal.setTime(data);
						int mes = dataCal.get(Calendar.MONTH);
						//aqui o m�s tem valor 0
						Agendamento agenda = new Agendamento();
						
						
						/*if ((mes+1) == agendamento.getMesRealizacao()){
							
						agenda.setLembrete(agendamento.getLembrete());
						//agenda.setData(DataUtil.obterDataDiaInformadoMesAtual(agendamento.getDiaRealizacao()));
						agenda.setCliente(agendamento.getCliente());
						agenda.setStatus(agendamento.getStatus());
						agenda.setEmpresa(agendamento.getEmpresa());
						agenda.setDataDoRegistro(new Date());
						agenda.setDentista(agendamento.getDentista());
						if(agendamento.getHorario() != "")
							agenda.setHorario(agendamento.getHorario());
						
						//geração de sequência para chave de registro
						if(!Numero.isMaiorZero(agenda.getId())){
							Agendamento chaveRegistro = null;
							//Recebe em uma lista o último registro da empresa
							setBuscaUltimoRegistro((List<Agendamento>) buscaUltimoRegistroPorEmpresa(Agendamento.class, agendamento.getEmpresa()));
							
							if (getBuscaUltimoRegistro()==null) {
								//se a lista for vazia, soma 1 ao novo objeto
								agenda.setChaveRegistro(new Long(1)); 
							} else {
								//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
								chaveRegistro = buscaUltimoRegistro.get(0);
								Long codigo = (Long) chaveRegistro.getChaveRegistro();
								codigo += 1;
								agenda.setChaveRegistro(codigo);
							}
						}
						trx.begin();
							manager.persist(agenda);
						trx.commit();
						
						}*/
					}
					
				}				
				
			}
			
			ExecucaoProcessador execucao = new ExecucaoProcessador();
			execucao.setData(new Date());
			execucao.setTipo(TipoExecucaoProcessadorEnumerador.AGENDA);
			//executorService.salvar(execucao);
			trx.begin();
				manager.persist(execucao);
			trx.commit();
			log.info(" ******** GERADOR AGENDAMENTOS EXECUTADO ******** ");
			
			manager.close();
		}
		
	} 


	private boolean isExecutarGerador() {
		boolean retorno = false;
		List<ExecucaoProcessador> execucoes = obterListaDeExecucaoProcessadorNoMesAtual(TipoExecucaoProcessadorEnumerador.AGENDA);
		
		if (execucoes != null && execucoes.size() == 0) {
			System.out.println("Execuções aqui é +"+execucoes+ " e o tamanho é +"+execucoes.size());
			retorno = true;
		}

		return retorno;
	}

	public void executarImportacoes() {

	}
	
	@SuppressWarnings("unchecked")
	public List<ExecucaoProcessador> obterListaDeExecucaoProcessadorNoMesAtual(TipoExecucaoProcessadorEnumerador tipo){
		
		List<ExecucaoProcessador> resultado = null;
		Date inicio = DataUtil.setPrimeiroDiaMes(new Date());
		Date fim = DataUtil.setUltimoDiaMes(new Date());
		
		String sql = "SELECT o FROM ExecucaoProcessador o WHERE o.tipo = :tipo "
				+ " AND o.data between :inicio AND :fim ORDER BY o.id DESC)";
		
		resultado = this.manager.createQuery(sql)
				.setParameter("tipo", tipo)
				.setParameter("inicio", inicio)
				.setParameter("fim", fim)
				.getResultList();
		if(resultado.size() < 1) {
			System.out.println("Aqui o resultado é "+resultado.size());;
		} 
		return resultado;
		
	}
	
	public List obterAgendamentosAutomaticos(Class classe, boolean repetir, StatusAgendamentoEnumerador status){
		List resultado = null;
		String sql = "SELECT o FROM " + classe.getSimpleName() + " o WHERE o.repetir = :repetir and o.status = :status)";
		resultado = this.manager.createQuery(sql)
				.setParameter("repetir", repetir)
				.setParameter("status", status)
				.getResultList();
		if(resultado.size() < 1) {
			return null;
		} else {
			return resultado;
		}
	}
	
	public List buscaUltimoRegistroPorEmpresa(Class classe, Empresa empresa) {
		List resultado = null;
		String sql = "SELECT o FROM " + classe.getSimpleName() + " o WHERE o.chaveRegistro=(SELECT MAX(x.chaveRegistro) FROM "
				+ classe.getSimpleName() + " x WHERE x.empresa = :empresa)";
		resultado = this.manager.createQuery(sql).setParameter("empresa", empresa).getResultList();
		if(resultado.size() < 1) {
			return null;
		} else {
			return resultado;
		}
			
	}

	public List<Agendamento> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Agendamento> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}
}



