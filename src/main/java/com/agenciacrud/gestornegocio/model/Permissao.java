package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Permissao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 100)
	private String nome;
	
	@Column(length = 255)
	private String descricao;
	
	private boolean planoInicial;
	
	private boolean planoMega;
	
	private boolean planoCompleto;
	
	private boolean empresaMaster = true;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isPlanoInicial() {
		return planoInicial;
	}
	public void setPlanoInicial(boolean planoInicial) {
		this.planoInicial = planoInicial;
	}
	public boolean isPlanoMega() {
		return planoMega;
	}
	public void setPlanoMega(boolean planoMega) {
		this.planoMega = planoMega;
	}
	public boolean isPlanoCompleto() {
		return planoCompleto;
	}
	public void setPlanoCompleto(boolean planoCompleto) {
		this.planoCompleto = planoCompleto;
	}
	public boolean isEmpresaMaster() {
		return empresaMaster;
	}
	public void setEmpresaMaster(boolean empresaMaster) {
		this.empresaMaster = empresaMaster;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permissao other = (Permissao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	

}
