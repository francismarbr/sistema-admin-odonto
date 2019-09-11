package com.agenciacrud.gestornegocio.util.processador;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import com.agenciacrud.gestornegocio.model.Agendamento;
import com.agenciacrud.gestornegocio.model.ExecucaoProcessador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoExecucaoProcessadorEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoMensalAnualEnumerador;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.service.AgendamentoService;
import com.agenciacrud.gestornegocio.service.ExecutorService;
import com.agenciacrud.gestornegocio.util.DataUtil;


//@Singleton
public class RotinasAgendamento {

long intervalo = 25000; // 24h

	
	private final Logger log = (Logger) LogManager.getLogger(RotinasAgendamento.class);
	
	
	@Inject
	private AgendamentoService agendamentoService;
	
	@Inject
	private ExecutorService executorService;
	
	@Inject
	private RGeral rGeral;
	
	
	
	//long intervalo = 86400000; // 24h
	//long intervalo = 5000; // 24h
	


				//@Cron(cronExpression = "0/60 * * * * ?")
				public void scheduledMethod() {
				if (isExecutarGerador()) {
					log.info(" ******** INICIANDO GERADOR AGENDA AUTOMÁTICA ******** ");

					List<Agendamento> listaInclusao = rGeral.obterAgendamentosAutomaticos(Agendamento.class, true);
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
							
							agendamentoService.salvar(agenda);
						
						} else {
							Date data = new Date();
							GregorianCalendar dataCal = new GregorianCalendar();
							dataCal.setTime(data);
							int mes = dataCal.get(Calendar.MONTH);
							//aqui o m�s tem valor 0
							Agendamento agenda = new Agendamento();
							
							
							/*if ((mes+1) == agendamento.getMesRealizacao()){
								
							agenda.setLembrete(agendamento.getLembrete());
							agenda.setData(DataUtil.obterDataDiaInformadoMesAtual(agendamento.getDiaRealizacao()));
							agenda.setCliente(agendamento.getCliente());
							agenda.setStatus(agendamento.getStatus());
							agenda.setEmpresa(agendamento.getEmpresa());
							agenda.setDataDoRegistro(new Date());
							agenda.setDentista(agendamento.getDentista());
							if(agendamento.getHorario() != "")
								agenda.setHorario(agendamento.getHorario());
							agendamentoService.salvar(agenda);
							
							}*/
						}
						
					}

					ExecucaoProcessador execucao = new ExecucaoProcessador();
					execucao.setData(new Date());
					execucao.setTipo(TipoExecucaoProcessadorEnumerador.AGENDA);
					executorService.salvar(execucao);
					
					log.info(" ******** GERADOR AGENDAMENTOS EXECUTADO ******** ");

				}
				
				}

	private boolean isExecutarGerador() {
		System.out.println("Dentro do executor de rotinas");
		boolean retorno = false;
		List<ExecucaoProcessador> execucoes = rGeral.obterListaDeExecucaoProcessadorNoMesAtual(TipoExecucaoProcessadorEnumerador.AGENDA);			
		if (execucoes != null && execucoes.size() == 0) {
			retorno = true;
		}

		return retorno;
	}

	public void dormir() throws InterruptedException {
		
		Thread.sleep(intervalo);
	}

	public void executarImportacoes() {

	}

	
}
