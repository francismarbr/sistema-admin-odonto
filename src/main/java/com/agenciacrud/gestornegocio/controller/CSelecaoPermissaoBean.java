package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import com.agenciacrud.gestornegocio.model.Permissao;
import com.agenciacrud.gestornegocio.repositorio.Permissoes;


@Named("CSelecaoPermissaoBean")
@ViewScoped
public class CSelecaoPermissaoBean extends CGeral implements Serializable  {
	
	private static final long serialVersionUID = 1L;

	private Permissao permissao;
	
	@Inject
	private Permissoes permissoes;

	private List<Permissao> permissoesFiltradas;
	private String nome;
	
	public void pesquisar() {
		permissoesFiltradas = permissoes.porNome(nome);
	}
	
	public void selecionar(Permissao permissao) {
		RequestContext.getCurrentInstance().closeDialog(permissao);
	}
	
	public void abrirDialogo() {
		Map<String, Object> opcoes = new HashMap<>();
		opcoes.put("modal", true);
		opcoes.put("resizable", false);
		opcoes.put("contentHeight", 270);
		opcoes.put("contentWidth", 900);
		RequestContext.getCurrentInstance().openDialog("/dialogo/SelecaoPermissao", opcoes, null);
	}
		
	public Permissao getPermissao(){
		return permissao;
	}
	
	public void setPermissao(Permissao permissao) {
		this.permissao = permissao;
	}
	
	public List<Permissao> getPermissoesFiltradas() {
		return permissoesFiltradas;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}	
}
