package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.Permissao;
import com.agenciacrud.gestornegocio.repositorio.Permissoes;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CPermissaoBean")
@ViewScoped
public class CPermissaoBean extends CGeral implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private Permissao permissao;
	
	@Inject
	private Permissoes permissoes;
	
	@Inject
	private RGeral rGeral; //repositório Geral
		
	private ConsultaFilter filtro;
	private List<Permissao> permissoesFiltradas;
	private List<Permissao> buscaUltimoRegistro;
	
	private Permissao permissaoSelecionada;
	
	private Long chaveRegistroEdicao;
	private List<Permissao> buscaRegistroEdicao;
	
	public CPermissaoBean() {
		limpar();
	}
	
	private void limpar() {
		permissao = new Permissao();
		filtro = new ConsultaFilter();
	}
	
	public void salvar(){
		this.permissao = permissoes.guardar(this.permissao);
		limpar();
		
		FacesUtil.addInfoMessage("Permissão salva com sucesso");
	}
	
	public void excluir() {
		permissoes.remover(permissaoSelecionada);
		permissoesFiltradas.remove(permissaoSelecionada);
		
		FacesUtil.addInfoMessage("A permissão " + permissaoSelecionada.getNome()
				+ " foi excluído com sucesso.");
	}
	
	public void editar(){
		
		
	}

	public void pesquisar() {
		permissoesFiltradas = permissoes.todasPermissoes();
	}


	public Permissao getPermissao() {
		return permissao;
	}

	public void setPermissao(Permissao permissao) {
		this.permissao = permissao;
	}

	public Permissao getBanco(){
		return permissao;
	}

	public void setBanco(Permissao banco) {
		this.permissao = banco;
	}
	
	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			Permissao itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Permissao>) rGeral.porChaveRegistro(Permissao.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.permissao = itemEdicao;			
		}
		//retorna se banco não existir, retorna falso, caso contrário, verdadeiro.
		return this.permissao.getId() != null;
	}
	
	public List<Permissao> getPermissoesFiltradas() {
		return permissoesFiltradas;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public Permissao getPermissaoSelecionada() {
		return permissaoSelecionada;
	}

	public void setPermissaoSelecionada(Permissao permissaoSelecionada) {
		this.permissaoSelecionada = permissaoSelecionada;
	}

	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Permissao> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Permissao> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public List<Permissao> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Permissao> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

}
