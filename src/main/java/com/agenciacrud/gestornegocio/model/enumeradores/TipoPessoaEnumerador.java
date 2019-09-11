package com.agenciacrud.gestornegocio.model.enumeradores;

public enum TipoPessoaEnumerador {
	
	FISICA(0, "Pessoa Física"), JURIDICA(1, "Pessoa Jurídica");
	
	int codigo;
	String descricao;
	
	private TipoPessoaEnumerador(int codigo, String descricao) {
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
