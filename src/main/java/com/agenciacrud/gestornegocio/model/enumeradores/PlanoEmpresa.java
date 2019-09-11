package com.agenciacrud.gestornegocio.model.enumeradores;

public enum PlanoEmpresa {

	INICIAL(0, "INICIAL", "INICIAL - R$67/Mês"), MEGA(1, "MEGA", "MEGA - R$97/Mês"), COMPLETO(2,
			"COMPLETO", "COMPLETO - R$137/Mês"), MASTER(3, "MASTER", "MASTER");

	int codigo;
	String descricao;
	String obs;

	private PlanoEmpresa(int codigo, String descricao, String obs) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.obs = obs;
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
	
	public String getObs() {
		return obs;
	}
	
	public void setObs(String obs) {
		this.obs = obs;
	}

	public String toString() {
		return getDescricao();
	}

}
