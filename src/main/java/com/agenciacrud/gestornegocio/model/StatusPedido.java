package com.agenciacrud.gestornegocio.model;

public enum StatusPedido {

	ORCAMENTO("Em Orçamento"), APROVADO("Aprovado"), CANCELADO("Cancelado");
	
	private String descricao;
	
	StatusPedido(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
}
