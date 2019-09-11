package com.agenciacrud.gestornegocio.model.enumeradores;

public enum TipoCategoriaContaEnumerador {
	
	ENTRADA(0, "Entrada"), SAIDA(1, "Saída");
	
	int codigo;
	String descricao;
	
	private TipoCategoriaContaEnumerador(int codigo, String descricao) {
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

}
