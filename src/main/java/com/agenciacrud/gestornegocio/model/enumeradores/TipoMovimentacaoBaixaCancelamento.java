package com.agenciacrud.gestornegocio.model.enumeradores;

public enum TipoMovimentacaoBaixaCancelamento {

	BAIXA(1, "Baixar"), CANCELAR(0, "Cancelar");

	int codigo;
	String descricao;

	private TipoMovimentacaoBaixaCancelamento(int codigo, String descricao) {
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
