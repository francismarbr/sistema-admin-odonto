package com.agenciacrud.gestornegocio.controller;



import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import com.agenciacrud.gestornegocio.model.Agendamento;
import com.agenciacrud.gestornegocio.model.Dentista;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.StatusPedido;
import com.agenciacrud.gestornegocio.model.enumeradores.StatusAgendamentoEnumerador;
import com.agenciacrud.gestornegocio.repositorio.Agendamentos;
import com.agenciacrud.gestornegocio.repositorio.Clientes;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.AgendamentoService;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CAgendamentoBean")
@ViewScoped
public class CAgendamentoBean extends CGeral implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private Agendamentos agendamentos;
	
	@Inject
	private AgendamentoService agendamentoService;
	
	@Inject
	private Clientes clientes;
	
	@Inject
	private RGeral rGeral;
		
	private List<Dentista> listaDentistas;
	private List<Agendamento> buscaUltimoRegistro;
	private List<Agendamento> agendamentosFiltrados;	
	private List<Agendamento> buscaRegistroEdicao;
	
	private ConsultaFilter filtro;
	private StatusAgendamentoEnumerador statusAgendamento;
	private Agendamento agendamento;
	private boolean filtroAgendamentoProgramado;
	
	private LazyScheduleModel agendamentoModel;
	private DefaultScheduleEvent evento;
	
	private Long chaveRegistroEdicao;
	
	
	public CAgendamentoBean() {
		filtro = new ConsultaFilter();
		agendamentosFiltrados = new ArrayList<>();
		limpar();	
	}
	
	public void inicializar() throws ParseException {
		
		if(FacesUtil.isNotPostback()) {
			this.listaDentistas = this.rGeral.buscaDentistas(Dentista.class, getEmpresaLogada());
		}
		
		agendamentoModel = new LazyScheduleModel() {
			
			@Override
			public void loadEvents(Date dataInicial, Date dataFinal) {
				
				filtro.setDataInicial(dataInicial);
				filtro.setDataFinal(dataFinal);
				
				//realiza busca no banco fazendo filtro
				agendamentosFiltrados = agendamentos.filtrados(filtro, getEmpresaLogada(), getStatusAgendamento(), filtroAgendamentoProgramado);
				
				for(Agendamento ag : agendamentosFiltrados) {
					
					//concatena hora inicial com a data
					String dtInicial = DataUtil.formatoPadrao(ag.getData());
					dtInicial = dtInicial +" "+ DataUtil.formatoPadraoHora(ag.getHoraInicial());
					SimpleDateFormat formatoDataInicial = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date horaInicio = null;
					try {
						horaInicio = formatoDataInicial.parse(dtInicial);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					//concatena hora final com a data
					String dtFinal = DataUtil.formatoPadrao(ag.getData());
					dtFinal = dtFinal +" "+ DataUtil.formatoPadraoHora(ag.getHoraFinal());
					SimpleDateFormat formatoDataFinal = new SimpleDateFormat("dd/MM/yyyy HH:mm");
					Date horaFim = null;
					try {
						horaFim = formatoDataFinal.parse(dtFinal);
					} catch (ParseException e) {
						e.printStackTrace();
					}
								
					evento = new DefaultScheduleEvent();
								
					evento.setStartDate(horaInicio);
					evento.setEndDate(horaFim);
					evento.setTitle(ag.getCliente().getNome() +" - "+ ag.getLembrete());
					evento.setData(ag.getId());
					evento.setDescription(ag.getObservacao());
					evento.setAllDay(false);
					evento.setEditable(true);
					
					if(ag.getStatus().equals(StatusAgendamentoEnumerador.CONCLUIDO))
						evento.setStyleClass("itemScheduleConcluido");
					else if(ag.getStatus().equals(StatusAgendamentoEnumerador.CANCELADO))
						evento.setStyleClass("itemScheduleCancelado");
					
					addEvent(evento);
					
					//agendamentoModel.addEvent(evento);
				}
			}
		};
	}
	
	private void limpar() {
		agendamento = new Agendamento();
		filtro.setDataInicial(new Date());
	}
	
	public void salvar() {
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.agendamento.getId())){
			Agendamento chaveRegistro = null;
			
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<Agendamento>) rGeral.buscaUltimoRegistroPorEmpresa(Agendamento.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				agendamento.setChaveRegistro(new Long(1));  
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				agendamento.setChaveRegistro(codigo);
			}
			agendamento.setStatus(StatusAgendamentoEnumerador.A_ACONTECER);
		}
		this.agendamento.setEmpresa(getEmpresaLogada());	
		this.agendamento.setDataDoRegistro(new Date());
		this.agendamento = this.agendamentoService.salvar(this.agendamento);
		
		/*if(agendamento.isRepetir() && DataUtil.obterDataDiaInformadoMesAtual(agendamento.getDiaRealizacao()).compareTo(new Date()) > 0){
			Agendamento agenda = new Agendamento();
			setBuscaUltimoRegistro(null);
			agenda.setLembrete(agendamento.getLembrete());
			agenda.setData(DataUtil.obterDataDiaInformadoMesAtual(agendamento.getDiaRealizacao()));
			agenda.setCliente(agendamento.getCliente());
			agenda.setStatus(agendamento.getStatus());
			agenda.setEmpresa(agendamento.getEmpresa());
			agenda.setDentista(agendamento.getDentista());
			agenda.setDataDoRegistro(new Date());
			agenda.setRepetir(false);
			if(agendamento.getHorario() != "")
				agenda.setHorario(agendamento.getHorario());
			
			
			//geração de sequência para chave de registro
			if(!Numero.isMaiorZero(agenda.getId())){
				Agendamento chaveRegistro = null;
				//Recebe em uma lista o último registro da empresa
				setBuscaUltimoRegistro((List<Agendamento>) rGeral.buscaUltimoRegistroPorEmpresa(Agendamento.class, getEmpresaLogada()));
				
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
			this.agendamentoService.salvar(agenda);
		}*/
		
		limpar();
		FacesUtil.addInfoMessage("Agendamento salvo com sucesso!");
	}
	
	//método para quando o agendamento for selecionado na view
		public void itemSelecionado(SelectEvent itemSelecionado) {
			
			ScheduleEvent event = (ScheduleEvent) itemSelecionado.getObject();
			
			for(Agendamento ag : agendamentosFiltrados) {
				if(event!=null && ag.getId() == (Long) event.getData()) {
					agendamento = ag;
					break;
				}
			}
		}
		
		//método para quando for selecionado na view um item(data) vazio
		public void itemVazioSelecionado(SelectEvent itemVazioSelecionado) {
			
			ScheduleEvent event = new DefaultScheduleEvent("",(Date) itemVazioSelecionado.getObject(), (Date) itemVazioSelecionado.getObject());
			agendamento = new Agendamento();
			agendamento.setHoraInicial(event.getStartDate());
			agendamento.setHoraFinal(event.getEndDate());
			System.out.println("Status ao inserir item novo é "+agendamento.getStatus());
		}
	
	public List<Pessoa> completarCliente(String nome) {
		return this.clientes.porNome(nome, getEmpresaLogada());
	}
	
	public void pesquisar() {
		
		agendamentosFiltrados = agendamentos.filtrados(filtro, getEmpresaLogada(), getStatusAgendamento(), filtroAgendamentoProgramado);
	}
	
	/*public void dataSelecionada(SelectEvent selectEvent) {
        evento = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
    }
	
	public void eventoSelecionado(SelectEvent selectEvent) {
        evento = (DefaultScheduleEvent) selectEvent.getObject();
    }*/
		
	public void agendaDoDia() {
		filtro.setDataInicial(new Date());
		filtro.setDataFinal(new Date());
		agendamentosFiltrados = agendamentos.agendaDoDia(filtro, getEmpresaLogada(), StatusAgendamentoEnumerador.A_ACONTECER, filtroAgendamentoProgramado);
	}
	
	//busca pessoas por nome e atualiza o autocomplete
	public List<Pessoa> completarPessoa(String nome) {
		return this.rGeral.porNome(Pessoa.class, nome, getEmpresaLogada());
	}
	
	/*public void addMessage() {
		if(agendamento.isRepetir()){
			FacesUtil.addInfoMessage("Você marcou para REPETIR este lembrete");
		} else {
			FacesUtil.addErrorMessage("Você marcou para NÃO repetir este lembrete");
		}
    }*/
	
	public void excluir() {
		agendamentos.remover(agendamento);
		agendamentosFiltrados.remove(agendamento);
		FacesUtil.addInfoMessage("Agendamento foi excluído com sucesso.");
	}
	
	public void concluirAgendamento(){
		agendamento.setStatus(StatusAgendamentoEnumerador.CONCLUIDO);
		agendamentoService.salvar(agendamento);
		FacesUtil.addInfoMessage("Agendamento concluído");
	}
	
	public void cancelarAgendamento(){
		agendamento.setStatus(StatusAgendamentoEnumerador.CANCELADO);
		agendamentoService.salvar(agendamento);
		FacesUtil.addInfoMessage("Agendamento cancelado");
	}
	
	//retorna um array com status
	public StatusPedido[] getStatuses() {
		return StatusPedido.values(); //values retorna array do tipo da enumeração; no caso do tipo pedido
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public StatusAgendamentoEnumerador getStatusAgendamento() {
		return statusAgendamento;
	}

	public void setStatusAgendamento(StatusAgendamentoEnumerador statusAgendamento) {
		this.statusAgendamento = statusAgendamento;
	}

	public List<Agendamento> getAgendamentosFiltrados() {
		return agendamentosFiltrados;
	}

	public Agendamento getAgendamento() {
		return agendamento;
	}

	public void setAgendamento(Agendamento agendamento) {
		this.agendamento = agendamento;
	}

	public ScheduleModel getAgendamentoModel() {
		return agendamentoModel;
	}

	public DefaultScheduleEvent getEvento() {
		return evento;
	}

	public void setEvento(DefaultScheduleEvent evento) {
		this.evento = evento;
	}
	
	public boolean isFiltroAgendamentoProgramado() {
		return filtroAgendamentoProgramado;
	}

	public void setFiltroAgendamentoProgramado(boolean filtroAgendamentoProgramado) {
		this.filtroAgendamentoProgramado = filtroAgendamentoProgramado;
	}

	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Dentista> getListaDentistas() {
		return listaDentistas;
	}

	public void setListaDentistas(List<Dentista> listaDentistas) {
		this.listaDentistas = listaDentistas;
	}

	public List<Agendamento> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Agendamento> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			Agendamento itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Agendamento>) rGeral.porChaveRegistro(Agendamento.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.agendamento = itemEdicao;			
		}
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.agendamento.getId() != null;
	}

	public List<Agendamento> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Agendamento> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

	
}

