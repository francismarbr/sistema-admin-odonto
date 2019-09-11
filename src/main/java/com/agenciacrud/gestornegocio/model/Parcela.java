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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.Numero;


@Entity
public class Parcela implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7792755399558613552L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer numero;

	@Temporal(TemporalType.DATE)
	private Date dataVencimento;

	private BigDecimal valor = BigDecimal.ZERO;

	@Enumerated(EnumType.ORDINAL)
	private SituacaoPagamentoEnumerador situacao;

	@ManyToOne(optional = true)
	private Despesa despesa;

	@ManyToOne(optional = true)
	private Receita receita;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "parcela")
	private List<MovimentacaoConta> recebimentos;

	@Column(columnDefinition = "text")
	private String historico;

	@ManyToOne(optional = true, cascade = CascadeType.ALL)
	private ParcelaCancelamento cancelamento;

	@Transient
	private BigDecimal valorTotalRecebidoPorParcela = BigDecimal.ZERO;
	
	@Transient
	private BigDecimal valorTotalCanceladoPorParcela = BigDecimal.ZERO;
	
	@Transient
	private BigDecimal valorTotalDescontoPorParcela = BigDecimal.ZERO;
	
	@Transient
	private BigDecimal valorTotalSaldoPorParcela = BigDecimal.ZERO;

	@Transient
	private BigDecimal valorPagamento = BigDecimal.ZERO;

	@Transient
	private SituacaoPagamentoEnumerador novaSituacao = SituacaoPagamentoEnumerador.QUITADA;

	public int getDiasParaVencer() {
		int dias = 0;
		if (getId() != null && getId().intValue() > 0) {
			dias = DataUtil.diferencaDias(new Date(), getDataVencimento());
		}
		return dias;
	}

	public String getDiasParaVencerTexto() {
		int dias = getDiasParaVencer();
		if (dias > 0) {
			return dias + " dias";
		} else if (dias < 0) {
			return "Há " + (dias * -1) + " dia(s)";
		} else {
			return "Hoje";
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public SituacaoPagamentoEnumerador getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoPagamentoEnumerador situacao) {
		this.situacao = situacao;
	}

	public Despesa getDespesa() {
		if(despesa == null)
			despesa = new Despesa();
		return despesa;
	}

	public void setDespesa(Despesa despesa) {
		this.despesa = despesa;
	}

	public Receita getReceita() {
		if(receita ==  null)
			receita = new Receita();
		return receita;
	}

	public void setReceita(Receita receita) {
		this.receita = receita;
	}

	public List<MovimentacaoConta> getRecebimentos() {
		if (recebimentos == null)
			recebimentos = new ArrayList<MovimentacaoConta>();
		return recebimentos;
	}

	public void setRecebimentos(List<MovimentacaoConta> recebimentos) {
		this.recebimentos = recebimentos;
	}

	public ParcelaCancelamento getCancelamento() {
		return cancelamento;
	}

	public void setCancelamento(ParcelaCancelamento cancelamento) {
		this.cancelamento = cancelamento;
	}

	public BigDecimal getValorTotalRecebidoPorParcela() {
		valorTotalRecebidoPorParcela = new BigDecimal(0);
		for (MovimentacaoConta recebimento : getRecebimentos()) {
			valorTotalRecebidoPorParcela = valorTotalRecebidoPorParcela.add(recebimento.getValorMovimentado());
		}
		return valorTotalRecebidoPorParcela;
	}

	public void setValorTotalRecebidoPorParcela(
			BigDecimal valorTotalRecebidoPorParcela) {
		this.valorTotalRecebidoPorParcela = valorTotalRecebidoPorParcela;
	}

	public BigDecimal getValorTotalCanceladoPorParcela() {
		valorTotalCanceladoPorParcela = new BigDecimal(0);
		if (situacao.equals(SituacaoPagamentoEnumerador.CANCELADA)) {
			valorTotalCanceladoPorParcela = getValor();
		} else
			for (MovimentacaoConta recebimento : getRecebimentos()) {
				valorTotalCanceladoPorParcela = valorTotalCanceladoPorParcela.add(recebimento
						.getValorCancelado());
			}
		return valorTotalCanceladoPorParcela;
	}

	public void setValorTotalCanceladoPorParcela(
			BigDecimal valorTotalCanceladoPorParcela) {
		this.valorTotalCanceladoPorParcela = valorTotalCanceladoPorParcela;
	}

	public BigDecimal getValorTotalDescontoPorParcela() {
		valorTotalDescontoPorParcela = new BigDecimal(0);
		for (MovimentacaoConta recebimento : getRecebimentos()) {
			valorTotalDescontoPorParcela = valorTotalDescontoPorParcela.add(recebimento.getValorDesconto());
		}
		return valorTotalDescontoPorParcela;
	}

	public void setValorTotalDescontoPorParcela(
			BigDecimal valorTotalDescontoPorParcela) {
		this.valorTotalDescontoPorParcela = valorTotalDescontoPorParcela;
	}

	public BigDecimal getValorTotalSaldoPorParcela() {
		if (cancelamento != null) {
			return new BigDecimal(0);
		} else {
			return getValor().subtract(getValorTotalCanceladoPorParcela())
					.subtract(getValorTotalDescontoPorParcela())
					.subtract(getValorTotalRecebidoPorParcela());
		}

	}

	public void setValorTotalSaldoPorParcela(BigDecimal valorTotalSaldoPorParcela) {
		this.valorTotalSaldoPorParcela = valorTotalSaldoPorParcela;
	}
	
	public BigDecimal getComissaoDentista() {
		BigDecimal porcentagem = new BigDecimal(100);
		if(getReceita().getDentista() == null)
			return new BigDecimal(0);
		else
			return getValor().multiply(getReceita().getDentista().getComissao().divide(porcentagem));
	}

	public String getHistorico() {
		return historico;
	}

	public void setHistorico(String historico) {
		this.historico = historico;
	}

	public BigDecimal getValorPagamento() {
		if (!Numero.isMaiorZero(valorPagamento))
			valorPagamento = getValorTotalSaldoPorParcela();
		return valorPagamento;
	}

	public void setValorPagamento(BigDecimal valorPagamento) {
		this.valorPagamento = valorPagamento;
	}

	public SituacaoPagamentoEnumerador getNovaSituacao() {
		return novaSituacao;
	}

	public void setNovaSituacao(SituacaoPagamentoEnumerador novaSituacao) {
		this.novaSituacao = novaSituacao;
	}

}