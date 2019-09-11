package com.agenciacrud.gestornegocio.model.enumeradores;

public enum StatusAgendamentoEnumerador {

	A_ACONTECER(0, "A Acontecer"), CONCLUIDO(1, "Concluído"), CANCELADO(2,"Cancelado");

	int ordinal;
	String descricao;

	private StatusAgendamentoEnumerador(int codigo, String descricao) {
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
