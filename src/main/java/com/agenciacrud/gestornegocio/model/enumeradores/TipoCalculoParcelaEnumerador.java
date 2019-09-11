package com.agenciacrud.gestornegocio.model.enumeradores;

public enum TipoCalculoParcelaEnumerador {

	POR_VALOR_PARCELA(0, "Por Parcela"), POR_VALOR_TOTAL(1, "Por Total");

	int ordinal;
	String descricao;

	private TipoCalculoParcelaEnumerador(int codigo, String descricao) {
		this.ordinal = codigo;
		this.descricao = descricao;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String toString(){
		return getDescricao();
	}
}
