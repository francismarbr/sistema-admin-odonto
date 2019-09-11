package com.agenciacrud.gestornegocio.model.enumeradores;

public enum SituacaoPagamentoEnumerador {

	ABERTA(0, "Aberta"), QUITADA(1, "Quitada"), PAGA_PARCIALMENTE(2,
			"Parcial"), CANCELADA(3, "Cancelada");

	int ordinal;
	String descricao;

	private SituacaoPagamentoEnumerador(int codigo, String descricao) {
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

	public String toString() {
		return getDescricao();
	}

}
