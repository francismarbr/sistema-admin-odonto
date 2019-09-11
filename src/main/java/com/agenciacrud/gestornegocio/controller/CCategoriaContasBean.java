package com.agenciacrud.gestornegocio.controller;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.repositorio.CategoriasContas;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.CategoriaContaService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CCategoriaContasBean")
@ViewScoped
public class CCategoriaContasBean extends CGeral implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private CategoriaContaService categoriaContaService;
	
	@Inject
	private CategoriasContas categoriasContas;
	
	@Inject
	private RGeral rGeral;
	
	private List<CategoriaConta> categoriasFiltradas;	
	private ConsultaFilter filtro;
	
	private CategoriaConta categoriaSelecionada;
	
	private CategoriaConta categoriaConta;
	private List<CategoriaConta> listaCategorias;
	private List<CategoriaConta> buscaUltimoRegistro;
	
	private Long chaveRegistroEdicao;
	private List<CategoriaConta> buscaRegistroEdicao;
	
	public CCategoriaContasBean() {
		limpar();
	}
	
	public void inicializar() {
		
		
	}
	
	private void limpar() {
		categoriaConta = new CategoriaConta();
		filtro = new ConsultaFilter();
		categoriasFiltradas = new ArrayList<>();
		listaCategorias = null;
	}
	
	public void salvar() {
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.categoriaConta.getId())){
			CategoriaConta chaveRegistro = null;
			
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<CategoriaConta>) rGeral.buscaUltimoRegistroPorEmpresa(CategoriaConta.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				categoriaConta.setChaveRegistro(new Long(1));  
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				categoriaConta.setChaveRegistro(codigo);
			}
		}
		categoriaConta.setEmpresa(getEmpresaLogada());
		this.categoriaConta = this.categoriaContaService.salvar(this.categoriaConta);
		FacesUtil.addInfoMessage("Categoria salva com sucesso!");
		limpar();
	}
	
	public void pesquisar() {
		categoriasFiltradas = categoriasContas.filtrados(filtro, getEmpresaLogada());
	}
	
	public void buscaSubcategorias(){
		this.listaCategorias = this.categoriasContas.todasCategorias(categoriaConta.getTipo(), getEmpresaLogada());
	}
	
	public void excluir() {
		categoriasContas.remover(categoriaSelecionada);
		categoriasFiltradas.remove(categoriaSelecionada);
		
		FacesUtil.addInfoMessage("A Categoria de código " + categoriaSelecionada.getChaveRegistro()
				+ " foi excluída com sucesso.");
	}

	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public List<CategoriaConta> getcategoriasFiltradas() {
		return categoriasFiltradas;
	}

	public CategoriaConta getCategoriaSelecionada() {
		return categoriaSelecionada;
	}

	public void setCategoriaSelecionada(CategoriaConta categoriaSelecionada) {
		this.categoriaSelecionada = categoriaSelecionada;
	}

	public CategoriaConta getCategoriaConta() {
		return categoriaConta;
	}

	public void setCategoriaConta(CategoriaConta categoriaConta) {
		this.categoriaConta = categoriaConta;
	}

	public List<CategoriaConta> getListaCategorias() {
		return listaCategorias;
	}

	public void setListaCategorias(List<CategoriaConta> listaCategorias) {
		this.listaCategorias = listaCategorias;
	}
	
	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<CategoriaConta> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<CategoriaConta> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			CategoriaConta itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<CategoriaConta>) rGeral.porChaveRegistro(CategoriaConta.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.categoriaConta = itemEdicao;
			this.listaCategorias = this.categoriasContas.todasCategorias(categoriaConta.getTipo(), getEmpresaLogada());
		}
		//retorna se banco não existir, retorna falso, caso contrário, verdadeiro.
		return this.categoriaConta.getId() != null;
	}
	
	public List<CategoriaConta> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<CategoriaConta> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}


	
}

