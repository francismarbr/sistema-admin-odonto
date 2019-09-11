package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Produto;
import com.agenciacrud.gestornegocio.repositorio.Produtos;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class ProdutoService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Produtos produtos;
	
	@Transactional
	public Produto salvar(Produto produto) {
		produto = this.produtos.guardar(produto);
		return produto;
	}

}
