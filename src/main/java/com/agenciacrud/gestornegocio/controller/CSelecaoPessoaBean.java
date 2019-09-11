package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.repositorio.Clientes;


@Named("CSelecaoPessoaBean")
@ViewScoped
public class CSelecaoPessoaBean extends CGeral implements Serializable  {
	
	private static final long serialVersionUID = 1L;

	private Pessoa pessoa;
	
	@Inject
	private Clientes clientes;

	private List<Pessoa> clientesFiltrados;
	private String nome;
	
	public void pesquisar() {
		clientesFiltrados = clientes.porNome(nome, getEmpresaLogada());
	}
	
	public void selecionar(Pessoa cliente) {
		RequestContext.getCurrentInstance().closeDialog(cliente);
	}
	
	public void abrirDialogo() {
		Map<String, Object> opcoes = new HashMap<>();
		opcoes.put("modal", true);
		opcoes.put("resizable", false);
		opcoes.put("contentHeight", 270);
		opcoes.put("contentWidth", 900);
		RequestContext.getCurrentInstance().openDialog("/dialogo/SelecaoCliente", opcoes, null);
	}
		
	public Pessoa getPessoa(){
		return pessoa;
	}
	
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	public List<Pessoa> getClientesFiltrados() {
		return clientesFiltrados;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}	
}
