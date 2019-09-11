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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;


@Entity
public class AnamneseModelo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long chaveRegistro;
	
	@NotBlank
	private String nome;
	
	@OneToMany(mappedBy = "anamneseModelo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ItemAnamneseModelo> itens = new ArrayList<>();
	

	@ManyToOne
	private Empresa empresa;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getChaveRegistro() {
		return chaveRegistro;
	}
	public void setChaveRegistro(Long chaveRegistro) {
		this.chaveRegistro = chaveRegistro;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public List<ItemAnamneseModelo> getItens() {
		return itens;
	}
	public void setItens(List<ItemAnamneseModelo> itens) {
		this.itens = itens;
	}
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	public void adicionarItemVazio() {
		ItemAnamneseModelo item = new ItemAnamneseModelo();
		item.setAnamneseModelo(this);
		this.getItens().add(item);
	}
	
	public void removerItemVazio() {
		//se alguma linha foi adicionada, mas não possui pergunta nenhuma, ao salvar, este será removido
		for(int i=0; i < this.getItens().size(); i++) {
			if(this.getItens().get(i) != null && this.getItens().get(i).getPergunta() == null) {
				this.getItens().remove(i);
			} 

		}		
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
		AnamneseModelo other = (AnamneseModelo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
