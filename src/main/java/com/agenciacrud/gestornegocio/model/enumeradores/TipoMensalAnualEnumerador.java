package com.agenciacrud.gestornegocio.model.enumeradores;

public enum TipoMensalAnualEnumerador {

	MENSAL(0, "Mensal"), ANUAL(1, "Anual");

	int ordinal;
	String descricao;
	

	private TipoMensalAnualEnumerador(int codigo, String descricao) {
		this.ordinal = codigo;

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
	
	

}
