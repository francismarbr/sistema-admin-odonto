package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.agenciacrud.gestornegocio.util.Numero;


@Entity
public class Despesa implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 10564654L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long chaveRegistro;
	
	@NotNull
	private String nome;

	@Temporal(TemporalType.DATE)
	private Date dataLancamento;
	
	@NotNull
	@Temporal(TemporalType.DATE)
	private Date dataVencimento;
	
	@NotNull
	private Integer quantidadeParcelas;
	
	private BigDecimal desconto = BigDecimal.ZERO;
	
	private BigDecimal valorAPagar = BigDecimal.ZERO;

	@ManyToOne
	private Empresa empresa;
	
	@ManyToOne
	private Fornecedor fornecedor;
	
	@ManyToOne
	private CategoriaConta categoriaConta;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "despesa")
	private List<Parcela> parcelas;

	@Column(columnDefinition = "text")
	private String obs;

	private BigDecimal valorTotal;

	public boolean isPossuiBaixaOUCancelamento() {
		for (Parcela parcela : getParcelas()) {
			if (parcela.getCancelamento() != null
					|| parcela.getRecebimentos().size() > 0)
				return true;

		}
		return false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(Date dataLancamento) {
		this.dataLancamento = dataLancamento;
	}

	public BigDecimal getValorTotal() {
		if(!Numero.isMaiorZero(valorTotal)) {
			return null;
		} else {
			return valorTotal;
		}
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public Integer getQuantidadeParcelas() {
		return quantidadeParcelas;
	}

	public void setQuantidadeParcelas(Integer quantidadeParcelas) {
		this.quantidadeParcelas = quantidadeParcelas;
	}

	public Empresa getEmpresa() {
		if (empresa == null)
			empresa = new Empresa();
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Fornecedor getFornecedor() {
		if (fornecedor == null)
			fornecedor = new Fornecedor();
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public CategoriaConta getCategoriaConta() {
		return categoriaConta;
	}

	public void setCategoriaConta(CategoriaConta categoriaConta) {
		this.categoriaConta = categoriaConta;
	}

	public List<Parcela> getParcelas() {
		if (parcelas == null)
			parcelas = new ArrayList<Parcela>();
		return parcelas;
	}

	public void setParcelas(List<Parcela> parcelas) {
		this.parcelas = parcelas;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public Long getChaveRegistro() {
		return chaveRegistro;
	}

	public void setChaveRegistro(Long chaveRegistro) {
		this.chaveRegistro = chaveRegistro;
	}

	public BigDecimal getDesconto() {
		return desconto;
	}

	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	public BigDecimal getValorAPagar() {
		BigDecimal total = new BigDecimal(0);
		
		if (getId() != null) {
			for (Parcela parcela : getParcelas()) {
				total = total.add(parcela.getValor());
			}
			return total;
		} else {
			if(this.getValorTotal() == null)
				return null;
			valorAPagar = new BigDecimal(0);
			valorAPagar = this.getValorTotal().subtract(this.getDesconto());
			return valorAPagar;
		}
	}

	public void setValorAPagar(BigDecimal valorAPagar) {
		this.valorAPagar = valorAPagar;
	}
	
	

}