package com.agenciacrud.gestornegocio.model.enumeradores;

public enum TipoNotaFiscal {

	ENTRADA(0, "Entrada"), SAIDA(1, "Sa�da");

	int codigo;
	String descricao;

	private TipoNotaFiscal(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
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
