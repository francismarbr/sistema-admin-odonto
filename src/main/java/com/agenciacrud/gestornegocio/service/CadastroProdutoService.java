package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Produto;
import com.agenciacrud.gestornegocio.repositorio.Produtos;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

/*
 * Classe responsável pelas regras de negócio de cadastro de produto
 */
public class CadastroProdutoService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Produtos produtos;
	
	@Transactional
	public Produto salvar(Produto produto) {
		
		/*Produto produtoExistente = produtos.porSku(produto.getSku());
		
		if(produtoExistente !=null && !produtoExistente.equals(produto)) {
			throw new NegocioException("Já existe um produto com o SKU informado.");
		}*/
		if(!Numero.isMaiorZero(produto.getValorUnitario()) || !Numero.isMaiorZero(produto.getMinimo()) ) {
			throw new NegocioException("O campo valor unitário e quantidade mínima devem ser maior que zero.");
		}
		return produtos.guardar(produto);
	}

}
