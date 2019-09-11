package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.validator.constraints.NotBlank;

import com.agenciacrud.gestornegocio.model.enumeradores.TipoCategoriaContaEnumerador;


@Entity
public class CategoriaConta  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 813508796772193875L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long chaveRegistro;

	@Enumerated(EnumType.ORDINAL)
	private TipoCategoriaContaEnumerador tipo;
	
	@NotBlank
	private String nome;

	@ManyToOne
	@JoinColumn(name = "categoriasuperior_id")
	private CategoriaConta categoriaSuperior;
	
	@OneToMany(mappedBy = "categoriaSuperior", cascade = CascadeType.ALL)
	private List<CategoriaConta> subcategorias = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name = "empresa_id", nullable = false)
	public Empresa empresa;
	
	private boolean padrao;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoCategoriaContaEnumerador getTipo() {
		return tipo;
	}

	public void setTipo(TipoCategoriaContaEnumerador tipo) {
		this.tipo = tipo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public CategoriaConta getCategoriaSuperior() {
		return categoriaSuperior;
	}

	public void setCategoriaSuperior(CategoriaConta categoriaSuperior) {
		this.categoriaSuperior = categoriaSuperior;
	}

	public Long getChaveRegistro() {
		return chaveRegistro;
	}

	public void setChaveRegistro(Long chaveRegistro) {
		this.chaveRegistro = chaveRegistro;
	}

	public List<CategoriaConta> getSubcategorias() {
		return subcategorias;
	}

	public void setSubcategorias(List<CategoriaConta> subcategorias) {
		this.subcategorias = subcategorias;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public boolean isPadrao() {
		return padrao;
	}

	public void setPadrao(boolean padrao) {
		this.padrao = padrao;
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
		CategoriaConta other = (CategoriaConta) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	

}