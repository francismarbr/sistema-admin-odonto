package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Usuario implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long chaveRegistro;
	
	@NotNull @Size(max = 100)
	@Column(nullable = false, length = 100)
	private String nome;
	
	@NotNull @Size(max = 150)
	@Column(nullable = false, length = 150, unique = true)
	private String login;
	
	@NotNull @Size(max = 150)
	@Column(nullable = false, length = 150)
	private String email;
	
	@NotNull @Size(max = 256)
	@Column(nullable = false, length = 256)
	private String senha;
	
	@Size(max = 128)
	@Column(length = 128)
	private String tokenTemporario;
	
	private boolean ativo = true;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "usuario_perfil", joinColumns = @JoinColumn(name = "usuario_id"), 
		inverseJoinColumns = @JoinColumn(name = "perfil_id"))
	private List<PerfilAcesso> perfisAcesso = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name = "empresa_id", nullable = false)
	private Empresa empresa;
		
	
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
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public Long getId() {
		return id;
	}
	public Long getChaveRegistro() {
		return chaveRegistro;
	}
	public void setChaveRegistro(Long chaveRegistro) {
		this.chaveRegistro = chaveRegistro;
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
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public String getTokenTemporario() {
		return tokenTemporario;
	}
	
	public void setTokenTemporario(String tokenTemporario) {
		this.tokenTemporario = tokenTemporario;
	}
	
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	public List<PerfilAcesso> getPerfisAcesso() {
		return perfisAcesso;
	}
	public void setPerfisAcesso(List<PerfilAcesso> perfisAcesso) {
		this.perfisAcesso = perfisAcesso;
	}
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	

}
