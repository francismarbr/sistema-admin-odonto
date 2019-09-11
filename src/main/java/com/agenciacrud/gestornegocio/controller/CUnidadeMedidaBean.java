package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.Banco;
import com.agenciacrud.gestornegocio.model.UnidadeMedida;
import com.agenciacrud.gestornegocio.repositorio.Bancos;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.UnidadesMedidas;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CUnidadeMedidaBean")
@ViewScoped
public class CUnidadeMedidaBean extends CGeral implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private UnidadeMedida unidadeMedida;
	
	@Inject
	private UnidadesMedidas unidadesMedidas;
	
	@Inject
	private RGeral rGeral; //repositório Geral
		
	private ConsultaFilter filtro;
	private List<UnidadeMedida> unidadesMedidasFiltradas;
	
	private UnidadeMedida unidadeMedidaSelecionada;
	
	private List<UnidadeMedida> buscaRegistroEdicao;
	
	public CUnidadeMedidaBean() {
		limpar();
	}
	
	private void limpar() {
		unidadeMedida = new UnidadeMedida();
		filtro = new ConsultaFilter();
	}
	
	public void salvar(){
		this.unidadeMedida = unidadesMedidas.guardar(this.unidadeMedida);
		limpar();
		
		FacesUtil.addInfoMessage("Unidade de Medida salva com sucesso");
	}
	
	public void excluir() {
		unidadesMedidas.remover(unidadeMedidaSelecionada);
		unidadesMedidasFiltradas.remove(unidadeMedidaSelecionada);
		
		FacesUtil.addInfoMessage("A Unidade de Medida de código " + unidadeMedidaSelecionada.getId()
				+ " foi excluído com sucesso.");
	}
	
	public void editar(){
		
		
	}

	public void pesquisar() {
		unidadesMedidasFiltradas = unidadesMedidas.filtrados(filtro);
	}


	public UnidadeMedida getUnidadeMedida(){
		return unidadeMedida;
	}

	public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}
	
	public boolean isEditando() {
		//retorna se banco não existir, retorna falso, caso contrário, verdadeiro.
		return this.unidadeMedida.getId() != null;
	}
	
	public List<UnidadeMedida> getUnidadesMedidasFiltradas() {
		return unidadesMedidasFiltradas;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public UnidadeMedida getUnidadeMedidaSelecionada() {
		return unidadeMedidaSelecionada;
	}

	public void setUnidadeMedidaSelecionada(UnidadeMedida unidadeMedidaSelecionada) {
		this.unidadeMedidaSelecionada = unidadeMedidaSelecionada;
	}


}
