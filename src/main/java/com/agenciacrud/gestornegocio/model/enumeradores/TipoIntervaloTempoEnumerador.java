package com.agenciacrud.gestornegocio.model.enumeradores;

public enum TipoIntervaloTempoEnumerador {

	DIA(0, "Dia(s)", 0), QUINZENA(1, "Quinzena(s)", 15), MENSAL(2, "Mês(es)", 30), BIMESTRAL(
			3, "Bimestre(s)", 60), SEMESTRAL(4, "Semestre(s)", 180), ANUAL(5, "Ano(s)",
			365);

	int codigo;
	String descricao;
	int quantidadeDias;

	private TipoIntervaloTempoEnumerador(int codigo, String descricao, int quantidadeDias) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.quantidadeDias = quantidadeDias;
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

	public int getQuantidadeDias() {
		return quantidadeDias;
	}

	public void setQuantidadeDias(int quantidadeDias) {
		this.quantidadeDias = quantidadeDias;
	}

	public String toString() {
		return getDescricao();
	}
}
