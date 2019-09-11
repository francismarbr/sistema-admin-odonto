package com.agenciacrud.gestornegocio.repositorio.filtro;

import java.io.Serializable;

import com.agenciacrud.gestornegocio.validation.SKU;

public class ProdutoFilter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@SKU
	private String sku;
	private String nome;
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku == null ? null : sku.toUpperCase(); //se sku for nulo, atribui nulo, senão coloca em maiúsculo
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

}
