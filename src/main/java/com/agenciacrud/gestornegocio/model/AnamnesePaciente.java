package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;


@Entity
@Table(name="anamnese_paciente")
public class AnamnesePaciente implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long chaveRegistro;
	
	@Temporal(TemporalType.DATE)
	private Date dataRegistro;
	
	@NotNull
	@ManyToOne
	private Pessoa pessoa;
	
	@OneToMany(mappedBy = "anamnesePaciente", cascade = CascadeType.ALL)
	private List<ItemAnamnesePaciente> itens = new ArrayList<>();
	

	@ManyToOne
	private Empresa empresa;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDataRegistro() {
		if(dataRegistro == null)
			dataRegistro = new Date();
		return dataRegistro;
	}
	public void setDataRegistro(Date dataRegistro) {
		this.dataRegistro = dataRegistro;
	}
	public Long getChaveRegistro() {
		return chaveRegistro;
	}
	public void setChaveRegistro(Long chaveRegistro) {
		this.chaveRegistro = chaveRegistro;
	}
	
	public List<ItemAnamnesePaciente> getItens() {
		return itens;
	}
	public void setItens(List<ItemAnamnesePaciente> itens) {
		this.itens = itens;
	}
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	@Transient
	public boolean isNovo(){
		return getId() == null;
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
		AnamnesePaciente other = (AnamnesePaciente) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
