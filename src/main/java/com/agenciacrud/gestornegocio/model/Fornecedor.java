package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.agenciacrud.gestornegocio.model.enumeradores.TipoPessoaEnumerador;


@Entity
public class Fornecedor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long chaveRegistro;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private TipoPessoaEnumerador tipo;
	
	@NotBlank @Size(max = 100)
	@Column(name="nome_razao_social", nullable = false)
	private String nome;
	
	@NotBlank @Size(max = 100)
	@Column(name="nome_fantasia", nullable = false)
	private String nomeFantasia;
	
	//@NotBlank
	//@Temporal(TemporalType.DATE)
	@Column(name="data_nascimento_fundacao", nullable = true)
	private Date dataNascimento;
	
	@Column(name="cpf_cnpj", nullable = true, length = 18)
	private String cpfCnpj;
	
	private String cei;
	
	private String rgInscEstadual;
	
	private String inscMunicipal;	
	
	@Column(length = 150)
	private String email;
	
	private String telefone1;
	
	private String telefone2;
	
	private String telefone3;
	
	@Column(name="data_cadastro", nullable = true)
	private Date dataCadastro;
	
	private String status;
	
	private String observacoes;
	
	@ManyToOne
	private Empresa empresa;
	
	@OneToOne(optional = true, cascade = CascadeType.ALL)
	private RegimeTributario regimeTributario;
	
	@OneToMany(mappedBy = "fornecedor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Socio> socios;
	
	@OneToOne(optional =  true, cascade = CascadeType.ALL)
	private Endereco endereco;
			
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

	public TipoPessoaEnumerador getTipo() {
		return tipo;
	}

	public void setTipo(TipoPessoaEnumerador tipo) {
		this.tipo = tipo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public String getCei() {
		return cei;
	}

	public void setCei(String cei) {
		this.cei = cei;
	}

	public String getRgInscEstadual() {
		return rgInscEstadual;
	}

	public void setRgInscEstadual(String rgInscEstadual) {
		this.rgInscEstadual = rgInscEstadual;
	}

	public String getInscMunicipal() {
		return inscMunicipal;
	}

	public void setInscMunicipal(String inscMunicipal) {
		this.inscMunicipal = inscMunicipal;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone1() {
		return telefone1;
	}

	public void setTelefone1(String telefone1) {
		this.telefone1 = telefone1;
	}

	public String getTelefone2() {
		return telefone2;
	}

	public void setTelefone2(String telefone2) {
		this.telefone2 = telefone2;
	}

	public String getTelefone3() {
		return telefone3;
	}

	public void setTelefone3(String telefone3) {
		this.telefone3 = telefone3;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}


	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Socio> getSocios() {
		if(socios == null)
			socios = new ArrayList<Socio>();
		return socios;
	}

	public void setSocios(List<Socio> socios) {
		this.socios = socios;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Endereco getEndereco() {
		if(endereco == null)
			endereco = new Endereco();
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public RegimeTributario getRegimeTributario() {
		return regimeTributario;
	}

	public void setRegimeTributario(RegimeTributario regimeTributario) {
		this.regimeTributario = regimeTributario;
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
		Fornecedor other = (Fornecedor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
