package com.agenciacrud.gestornegocio.model.enumeradores;

public enum TipoExecucaoProcessadorEnumerador {

	RECEITA_DESPESA(0), AGENDA(1);

	int ordinal;

	private TipoExecucaoProcessadorEnumerador(int codigo) {
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

}
