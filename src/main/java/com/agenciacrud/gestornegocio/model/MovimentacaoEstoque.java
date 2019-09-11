package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.agenciacrud.gestornegocio.model.enumeradores.TipoCategoriaContaEnumerador;

@Entity
public class MovimentacaoEstoque implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long chaveRegistro;
	
	@Temporal(TemporalType.DATE)
	private Date dataLancamento;
	
	@Temporal(TemporalType.DATE)
	private Date dataCompra;
	private BigDecimal quantidade;
	private BigDecimal valorUnitario;
	private BigDecimal estoqueAnterior;
	private BigDecimal estoquePosterior;
	private String observacao;
	
	private TipoCategoriaContaEnumerador tipoMovimentacao;
	
	@ManyToOne
	private Produto produto;
	
	@ManyToOne
	private Fornecedor fornecedor;
	
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

	public Date getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(Date dataLancamento) {
		this.dataLancamento = dataLancamento;
	}

	public Date getDataCompra() {
		return dataCompra;
	}

	public void setDataCompra(Date dataCompra) {
		this.dataCompra = dataCompra;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getEstoqueAnterior() {
		return estoqueAnterior;
	}

	public void setEstoqueAnterior(BigDecimal estoqueAnterior) {
		this.estoqueAnterior = estoqueAnterior;
	}

	public BigDecimal getEstoquePosterior() {
		return estoquePosterior;
	}

	public void setEstoquePosterior(BigDecimal estoquePosterior) {
		this.estoquePosterior = estoquePosterior;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public TipoCategoriaContaEnumerador getTipoMovimentacao() {
		return tipoMovimentacao;
	}

	public void setTipoMovimentacao(TipoCategoriaContaEnumerador tipoMovimentacao) {
		this.tipoMovimentacao = tipoMovimentacao;
	}

	public Produto getProduto() {
		if(produto == null) 
			produto = new Produto();
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
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
		MovimentacaoEstoque other = (MovimentacaoEstoque) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
