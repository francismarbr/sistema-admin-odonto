package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import com.agenciacrud.gestornegocio.model.Categoria;
import com.agenciacrud.gestornegocio.model.Dentista;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.ReceitaMedica;
import com.agenciacrud.gestornegocio.repositorio.Categorias;
import com.agenciacrud.gestornegocio.repositorio.ReceitasMedicas;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.ReceitaMedicaService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CReceitaMedicaBean")
@ViewScoped
public class CReceitaMedicaBean extends CGeral implements Serializable {
	

	private static final long serialVersionUID = 1L;

	
	@Inject
	private ReceitaMedicaService receitaMedicaService;
	
	@Inject
	private RGeral rGeral;
	
	private List<ReceitaMedica> buscaUltimoRegistro;
	
	private ReceitaMedica receita;
	
	@Inject
	private ReceitasMedicas receitasMedicas;
	
	private ConsultaFilter filtro;
	private List<ReceitaMedica> receitasMedicasFiltradas;
	
	private ReceitaMedica receitaMedicaSelecionada;
	
	private Long chaveRegistroEdicao;
	private List<ReceitaMedica> buscaRegistroEdicao;
	private List<Dentista> listaDentistas;
	
	public CReceitaMedicaBean(){
		filtro = new ConsultaFilter();
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()){
			this.listaDentistas = this.rGeral.buscaDentistas(Dentista.class, getEmpresaLogada());
		}
	}

	private void limpar() {
		receita = new ReceitaMedica();
	}
	
	public String salvar(){
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.receita.getId())){
			ReceitaMedica chaveRegistro = null;
			
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<ReceitaMedica>) rGeral.buscaUltimoRegistroPorEmpresa(ReceitaMedica.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				receita.setChaveRegistro(new Long(1));  
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				receita.setChaveRegistro(codigo);
			}
		}
		this.receita.setDataRegistro(new Date());
		this.receita.setEmpresa(getEmpresaLogada());
		//vai até a classe de serviço verificar se já existe o login cadastrado, pois não pode ser igual
		this.receita = receitaMedicaService.salvar(this.receita);
		limpar();
		
		FacesUtil.addInfoMessage("Receita criada com sucesso");
		return "PesquisaReceitasMedicas?faces-redirect=true";
	}
	
	public void excluir() {
		receitasMedicas.remover(receitaMedicaSelecionada);
		receitasMedicasFiltradas.remove(receitaMedicaSelecionada);
		
		FacesUtil.addInfoMessage("Receita  " + receitaMedicaSelecionada.getDescricao()
				+ "excluída com sucesso.");
	}

	public void pesquisar() {
		receitasMedicasFiltradas = receitasMedicas.filtrados(filtro, getEmpresaLogada());
	}
	
	//busca pessoas por nome e atualiza o autocomplete
	public List<Pessoa> completarCliente(String nome) {
		return this.rGeral.porNome(Pessoa.class, nome, getEmpresaLogada());
	}

	public List<ReceitaMedica> getReceitasMedicasFiltrados() {
		return receitasMedicasFiltradas;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public List<ReceitaMedica> getReceitasMedicasFiltradas() {
		return receitasMedicasFiltradas;
	}

	public ReceitaMedica getReceitaMedicaSelecionada() {
		return receitaMedicaSelecionada;
	}

	public void setReceitaMedicaSelecionada(ReceitaMedica receitaMedicaSelecionada) {
		this.receitaMedicaSelecionada = receitaMedicaSelecionada;
	}
	
	public ReceitaMedica getReceita(){
		return receita;
	}

	public void setReceita(ReceitaMedica receita) {
		this.receita = receita;
	}
	
	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<ReceitaMedica> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<ReceitaMedica> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			ReceitaMedica itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<ReceitaMedica>) rGeral.porChaveRegistro(ReceitaMedica.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.receita = itemEdicao;			
		}
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.receita.getId() != null;
	}

	public List<ReceitaMedica> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<ReceitaMedica> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

	public List<Dentista> getListaDentistas() {
		return listaDentistas;
	}

	public void setListaDentistas(List<Dentista> listaDentistas) {
		this.listaDentistas = listaDentistas;
	}

}
