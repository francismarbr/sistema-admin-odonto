package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.agenciacrud.gestornegocio.model.enumeradores.PlanoEmpresa;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoPessoaEnumerador;

@Entity
public class Empresa  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3124742855786449208L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long chaveRegistro;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	private TipoPessoaEnumerador tipo;
	
	private Date dataCadastro;
	
	private String nome;

	private String nomeFantasia;

	private String cpfCnpj;
	
	private String email;
	
	private String cei;

	private String rgInscEstadual;

	private String inscMunicipal;
	
	private String telefone1;
	
	private String telefone2;

	private String logo;

	@OneToOne(optional = true, cascade = CascadeType.ALL)
	private Endereco endereco;

	@OneToMany(mappedBy = "socioNa", cascade = CascadeType.ALL)
	private List<Socio> socios;
	
	@Enumerated(EnumType.ORDINAL)
	private PlanoEmpresa plano;
	
	private String obs;
	
	private boolean ativa;
	
	@OneToOne
	private CategoriaConta categoriaConta;

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

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRgInscEstadual() {
		return rgInscEstadual;
	}

	public void setRgInscEstadual(String rgInscEstadual) {
		this.rgInscEstadual = rgInscEstadual;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}
	
	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	public List<Socio> getSocios() {
		if(socios == null)
			socios = new ArrayList<Socio>();
		return socios;
	}

	public void setSocios(List<Socio> socios) {
		this.socios = socios;
	}

	public PlanoEmpresa getPlano() {
		return plano;
	}

	public void setPlano(PlanoEmpresa plano) {
		this.plano = plano;
	}

	public CategoriaConta getCategoriaConta() {
		return categoriaConta;
	}

	public void setCategoriaConta(CategoriaConta categoriaConta) {
		this.categoriaConta = categoriaConta;
	}

	public String getInscMunicipal() {
		return inscMunicipal;
	}

	public void setInscMunicipal(String inscMunicipal) {
		this.inscMunicipal = inscMunicipal;
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

	public String getCei() {
		return cei;
	}

	public void setCei(String cei) {
		this.cei = cei;
	}

	public Empresa(Long id) {
		this.id = id;
	}

	public Empresa() {
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public TipoPessoaEnumerador getTipo() {
		return tipo;
	}

	public void setTipo(TipoPessoaEnumerador tipo) {
		this.tipo = tipo;
	}
	
	public Date getDataCadastro() {
		return dataCadastro;
	}
	
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Endereco getEndereco() {
		if(endereco == null)
			endereco = new Endereco();
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}
}
