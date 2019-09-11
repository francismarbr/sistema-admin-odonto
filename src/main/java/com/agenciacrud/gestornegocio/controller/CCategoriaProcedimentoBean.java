package com.agenciacrud.gestornegocio.controller;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.CategoriaProcedimento;
import com.agenciacrud.gestornegocio.repositorio.CategoriasProcedimentos;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.CategoriaProcedimentoService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CCategoriaProcedimentoBean")
@ViewScoped
public class CCategoriaProcedimentoBean extends CGeral implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private CategoriaProcedimentoService categoriaProcedimentoService;
	
	@Inject
	private CategoriasProcedimentos categoriasProcedimentos;
	
	@Inject
	private RGeral rGeral;
	
	private List<CategoriaProcedimento> procedimentosFiltrados;	
	private ConsultaFilter filtro;
	
	private CategoriaProcedimento categoriaSelecionada;
	
	private CategoriaProcedimento categoriaProcedimento;
	private List<CategoriaProcedimento> listaCategorias;
	private List<CategoriaProcedimento> buscaUltimoRegistro;
	
	private Long chaveRegistroEdicao;
	private List<CategoriaProcedimento> buscaRegistroEdicao;
	
	public CCategoriaProcedimentoBean() {
		limpar();
	}
	
	public void inicializar() {
		
		
	}
	
	private void limpar() {
		categoriaProcedimento = new CategoriaProcedimento();
		filtro = new ConsultaFilter();
		procedimentosFiltrados = new ArrayList<>();
		listaCategorias = null;
	}
	
	public void salvar() {
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.categoriaProcedimento.getId())){
			CategoriaProcedimento chaveRegistro = null;
			
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<CategoriaProcedimento>) rGeral.buscaUltimoRegistroPorEmpresa(CategoriaProcedimento.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				categoriaProcedimento.setChaveRegistro(new Long(1));  
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				categoriaProcedimento.setChaveRegistro(codigo);
			}
		}
		categoriaProcedimento.setEmpresa(getEmpresaLogada());
		this.categoriaProcedimento = this.categoriaProcedimentoService.salvar(this.categoriaProcedimento);
		FacesUtil.addInfoMessage("Categoria salva com sucesso!");
		limpar();
	}
	
	public void pesquisar() {
		procedimentosFiltrados = categoriasProcedimentos.filtrados(filtro, getEmpresaLogada());
	}
	
	public void buscaSubcategorias(){
		this.listaCategorias = this.categoriasProcedimentos.todasCategorias(getEmpresaLogada());
	}
	
	public void excluir() {
		categoriasProcedimentos.remover(categoriaSelecionada);
		procedimentosFiltrados.remove(categoriaSelecionada);
		
		FacesUtil.addInfoMessage("A Categoria de código " + categoriaSelecionada.getChaveRegistro()
				+ " foi excluída com sucesso.");
	}

	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public List<CategoriaProcedimento> getcategoriasFiltradas() {
		return procedimentosFiltrados;
	}

	public CategoriaProcedimento getCategoriaSelecionada() {
		return categoriaSelecionada;
	}

	public void setCategoriaSelecionada(CategoriaProcedimento categoriaSelecionada) {
		this.categoriaSelecionada = categoriaSelecionada;
	}

	public CategoriaProcedimento getCategoriaProcedimento() {
		return categoriaProcedimento;
	}

	public void setCategoriaProcedimento(CategoriaProcedimento categoriaProcedimento) {
		this.categoriaProcedimento = categoriaProcedimento;
	}

	public List<CategoriaProcedimento> getListaCategorias() {
		return listaCategorias;
	}

	public void setListaCategorias(List<CategoriaProcedimento> listaCategorias) {
		this.listaCategorias = listaCategorias;
	}
	
	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<CategoriaProcedimento> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<CategoriaProcedimento> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			CategoriaProcedimento itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<CategoriaProcedimento>) rGeral.porChaveRegistro(CategoriaProcedimento.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.categoriaProcedimento = itemEdicao;
			this.listaCategorias = this.categoriasProcedimentos.todasCategorias(getEmpresaLogada());
		}
		//retorna se banco não existir, retorna falso, caso contrário, verdadeiro.
		return this.categoriaProcedimento.getId() != null;
	}
	
	public List<CategoriaProcedimento> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<CategoriaProcedimento> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}


	
}

