package com.agenciacrud.gestornegocio.controller;



import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.AnamneseModelo;
import com.agenciacrud.gestornegocio.model.AnamnesePaciente;
import com.agenciacrud.gestornegocio.model.ItemAnamneseModelo;
import com.agenciacrud.gestornegocio.model.ItemAnamnesePaciente;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.repositorio.AnamnesesModelos;
import com.agenciacrud.gestornegocio.repositorio.AnamnesesPacientes;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.service.AnamneseService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CAnamnesePacienteBean")
@ViewScoped
public class CAnamnesePacienteBean extends CGeral implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private AnamneseService anamneseService;
	
	@Inject
	private AnamnesesPacientes anamneses;
	
	@Inject
	private AnamnesesModelos anamnesesModelos;
	
	private AnamnesePaciente anamnesePaciente;
	private AnamnesePaciente anamneseSelecionada;

	private List<AnamnesePaciente> buscaUltimoRegistro;
	private List<AnamnesePaciente> listaAnamnesesPacientes;
	private List<AnamneseModelo> listaModelos;
	
	private AnamneseModelo anamneseModelo;
	
	
	private Long chaveRegistroEdicao;
	private List<AnamnesePaciente> buscaRegistroEdicao;
	
	private Date dataInicioRegistro;
	private Date dataFinalRegistro;
	
	public CAnamnesePacienteBean() {
		
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()) {
			setListaModelos(this.anamnesesModelos.todasModelosAnamneses(getEmpresaLogada()));	
		}
		
	}
	
	private void limpar() {
		anamnesePaciente = new AnamnesePaciente();
	}
	
	public void pesquisar() {
		listaAnamnesesPacientes = anamneses.filtro(getEmpresaLogada(), dataInicioRegistro, dataFinalRegistro, anamnesePaciente.getPessoa());
	}
	
	public void selecionaModelo(){
		/*
		 * Conforme o modelo é selecionadao na view, é atualizado as perguntas
		 * para o paciente
		 */
		anamnesePaciente.getItens().clear();
		for(ItemAnamneseModelo itemModelo : anamneseModelo.getItens()) {
			ItemAnamnesePaciente itemAnamnesePaciente = new ItemAnamnesePaciente();
			itemAnamnesePaciente.setAnamnesePaciente(anamnesePaciente);
			itemAnamnesePaciente.setPergunta(itemModelo.getPergunta());
			itemAnamnesePaciente.setOrdem(itemModelo.getOrdem());
			anamnesePaciente.getItens().add(itemAnamnesePaciente);
		}
	}
	
	public void salvar() {
			//geração de sequência para chave de registro
			if(!Numero.isMaiorZero(this.anamnesePaciente.getId())){
				AnamnesePaciente chaveRegistro = null;
				
				//Recebe em uma lista o último registro da empresa
				setBuscaUltimoRegistro((List<AnamnesePaciente>) rGeral.buscaUltimoRegistroPorEmpresa(AnamnesePaciente.class, getEmpresaLogada()));
				
				if (getBuscaUltimoRegistro()==null) {
					//se a lista for vazia, soma 1 ao novo objeto
					anamnesePaciente.setChaveRegistro(new Long(1));  
				} else {
					//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
					chaveRegistro = buscaUltimoRegistro.get(0);
					Long codigo = (Long) chaveRegistro.getChaveRegistro();
					codigo += 1;
					anamnesePaciente.setChaveRegistro(codigo);
				}
			}
			
			this.anamnesePaciente.setEmpresa(getEmpresaLogada());	
			this.anamnesePaciente = this.anamneseService.salvarAnamnesePaciente(this.anamnesePaciente);
			
			FacesUtil.addInfoMessage("Anamnese criada com sucesso!");

	}
	
	public void excluir() {
		anamneses.remover(anamneseSelecionada);
		pesquisar();
		FacesUtil.addInfoMessage("Anamnese removida com êxito");
	}
	
	//busca pessoas por nome e atualiza o autocomplete
	public List<Pessoa> completarPessoa(String nome) {
		return this.rGeral.porNome(Pessoa.class, nome, getEmpresaLogada());
	}

	public List<AnamnesePaciente> getListaAnamnesesPacientes() {
		return listaAnamnesesPacientes;
	}

	public List<AnamneseModelo> getListaModelos() {
		return listaModelos;
	}
	
	public void setListaModelos(List<AnamneseModelo> listaModelos) {
		this.listaModelos = listaModelos;
	}

	public AnamneseModelo getAnamneseModelo() {
		return anamneseModelo;
	}

	public void setAnamneseModelo(AnamneseModelo anamneseModelo) {
		this.anamneseModelo = anamneseModelo;
	}	

	public AnamnesePaciente getAnamnesePaciente() {
		return anamnesePaciente;
	}

	public AnamnesePaciente getAnamneseSelecionada() {
		return anamneseSelecionada;
	}

	public void setAnamneseSelecionada(AnamnesePaciente anamneseSelecionada) {
		this.anamneseSelecionada = anamneseSelecionada;
	}

	public void setAnamnesePaciente(AnamnesePaciente anamnesePaciente) {
		this.anamnesePaciente = anamnesePaciente;
	}

	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<AnamnesePaciente> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<AnamnesePaciente> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public Date getDataInicioRegistro() {
		return dataInicioRegistro;
	}

	public void setDataInicioRegistro(Date dataInicioRegistro) {
		this.dataInicioRegistro = dataInicioRegistro;
	}

	public Date getDataFinalRegistro() {
		return dataFinalRegistro;
	}

	public void setDataFinalRegistro(Date dataFinalRegistro) {
		this.dataFinalRegistro = dataFinalRegistro;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			AnamnesePaciente itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<AnamnesePaciente>) rGeral.porChaveRegistro(AnamnesePaciente.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.anamnesePaciente = itemEdicao;			
		}

		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.anamnesePaciente.getId() != null;
	}

	public List<AnamnesePaciente> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<AnamnesePaciente> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}
	
}

