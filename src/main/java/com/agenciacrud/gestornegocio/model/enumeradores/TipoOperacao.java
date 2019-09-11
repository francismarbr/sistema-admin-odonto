package com.agenciacrud.gestornegocio.model.enumeradores;

public enum TipoOperacao {

	DEBITO(0, "Débito", "D"), CREDITO(1, "Crédito", "C");

	int ordinal;
	String descricao;
	String sigla;

	private TipoOperacao(int codigo, String descricao, String sigla) {
		this.ordinal = codigo;
		this.descricao = descricao;
		this.sigla = sigla;
	}

	public int getCodigo() {
		return ordinal;
	}

	public void setCodigo(int codigo) {
		this.ordinal = codigo;
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

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String toString() {
		return getDescricao();
	}
}
