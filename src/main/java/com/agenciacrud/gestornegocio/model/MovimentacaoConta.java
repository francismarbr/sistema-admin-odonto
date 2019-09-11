package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.agenciacrud.gestornegocio.model.enumeradores.TipoMovimentacao;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoOperacao;

@Entity
public class MovimentacaoConta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8227802213215425628L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private BigDecimal valorMovimentado = BigDecimal.ZERO;

	private BigDecimal valorCancelado = BigDecimal.ZERO;

	private BigDecimal valorDesconto = BigDecimal.ZERO;
	
	@OneToOne(optional=true, cascade = CascadeType.ALL)
	private Cheque cheque;

	//recebe se é uma operacao de Crédito ou Débito
	private TipoOperacao tipoOperacao;
	
	//Recebe se é um pagamento, recebimento ou transferência
	private TipoMovimentacao tipoMovimentacao;
	
	private String descricao;
	
	private boolean estornado;
	
	private BigDecimal saldo = BigDecimal.ZERO;

	@ManyToOne
	@JoinColumn(name = "parcela_id", nullable = true)
	public Parcela parcela;
	
	@ManyToOne
	@JoinColumn(name = "pessoa_id", nullable = true)
	public Pessoa pessoa;
	
	@ManyToOne
	@JoinColumn(name = "fornecedor_id", nullable = true)
	public Fornecedor fornecedor;

	@ManyToOne
	@JoinColumn(name = "conta_id", nullable = false)
	private ContaBancaria conta;

	@Temporal(TemporalType.DATE)
	private Date dataLancamento;
	
	//@Temporal(TemporalType.TIMESTAMP)
	//private Date dataRecebimento;
	
	@ManyToOne
	@JoinColumn(name = "empresa_id", nullable = false)
	public Empresa empresa;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getValorMovimentado() {
		if (valorMovimentado == null)
			valorMovimentado = new BigDecimal(0);
		return valorMovimentado;
	}

	public void setValorMovimentado(BigDecimal valorRecebido) {
		this.valorMovimentado = valorRecebido;
	}

	public BigDecimal getValorCancelado() {
		if (valorCancelado == null)
			valorCancelado = new BigDecimal(0);
		return valorCancelado;
	}

	public void setValorCancelado(BigDecimal valorCancelado) {
		this.valorCancelado = valorCancelado;
	}

	public BigDecimal getValorDesconto() {
		if (valorDesconto == null)
			valorDesconto = new BigDecimal(0);
		return valorDesconto;
	}

	public void setValorDesconto(BigDecimal valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public BigDecimal getComissaoDentista() {
		BigDecimal porcentagem = new BigDecimal(100);
		if(getParcela().getReceita().getDentista() == null)
			return new BigDecimal(0);
		else
			return getValorMovimentado().multiply(getParcela().getReceita().getDentista().getComissao().divide(porcentagem));
	}
	
	public Parcela getParcela() {
		if (parcela == null)
			parcela = new Parcela();
		return parcela;
	}

	public void setParcela(Parcela parcela) {
		this.parcela = parcela;
	}
	
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	public Fornecedor getFornecedor(){
		return fornecedor;
	}
	
	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public ContaBancaria getConta() {
		if (conta == null)
			conta = new ContaBancaria();
		return conta;
	}

	public void setConta(ContaBancaria conta) {
		this.conta = conta;
	}

	public Date getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(Date dataLancamento) {
		this.dataLancamento = dataLancamento;
	}
	
	public Empresa getEmpresa() {
		if (empresa == null)
			empresa = new Empresa();
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public TipoOperacao getTipoOperacao() {
		return tipoOperacao;
	}

	public void setTipoOperacao(TipoOperacao tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isEstornado() {
		return estornado;
	}

	public void setEstornado(boolean estornado) {
		this.estornado = estornado;
	}

	public TipoMovimentacao getTipoMovimentacao() {
		return tipoMovimentacao;
	}

	public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
		this.tipoMovimentacao = tipoMovimentacao;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	
	public Cheque getCheque() {
		if(cheque==null)
			cheque = new Cheque();
		return cheque;
	}

	public void setCheque(Cheque cheque) {
		this.cheque = cheque;
	}

}