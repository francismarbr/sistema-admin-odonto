package com.agenciacrud.gestornegocio.model.enumeradores;

public enum TipoMovimentacao {

	RECEBIMENTO(0, "Recebimento", "R"), PAGAMENTO(1, "Pagamento", "P"), TRANSFERENCIA(2, "Transferência", "T");

	int codigo;
	String descricao;
	String sigla;

	private TipoMovimentacao(int codigo, String descricao, String sigla) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.sigla = sigla;
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
