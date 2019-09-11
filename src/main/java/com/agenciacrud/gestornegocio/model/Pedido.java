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


@Entity
public class Pedido implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long chaveRegistro;
	
	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name = "data_criacao", nullable = false)
	private Date dataCriacao;
	
	@Column(columnDefinition = "text")
	private String observacao;
		
	@NotNull
	@Column( nullable = false, precision = 10, scale = 2)
	private BigDecimal valorAdicional = BigDecimal.ZERO;
	
	@NotNull
	@Column(name = "valor_desconto", nullable = false, precision = 10, scale = 2)
	private BigDecimal valorDesconto = BigDecimal.ZERO;
	
	@NotNull
	@Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
	private BigDecimal valorTotal = BigDecimal.ZERO;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private StatusPedido status = StatusPedido.ORCAMENTO;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "pessoa_id", nullable = false)
	private Pessoa pessoa;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "dentista_id", nullable = false)
	private Dentista dentista;
	
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ItemPedido> itens = new ArrayList<>();
	
	@OneToOne(cascade = CascadeType.ALL)
	private CondicaoPagamento condicaoPagamento;
	
	@OneToOne(optional=true, cascade = CascadeType.ALL)
	private VendaCancelamento cancelamento;
	
	@OneToOne(optional=true, cascade = CascadeType.ALL)
	private Receita receita;
	
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
	public Date getDataCriacao() {
		if(dataCriacao == null) {
			dataCriacao = new Date();
		}
		return dataCriacao;
	}
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	public String getObservacao() {
		return observacao;
	}
	
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public BigDecimal getValorAdicional() {
		return valorAdicional;
	}
	public void setValorAdicional(BigDecimal valorAdicional) {
		this.valorAdicional = valorAdicional;
	}
	public BigDecimal getValorDesconto() {
		return valorDesconto;
	}
	public void setValorDesconto(BigDecimal valorDesconto) {
		this.valorDesconto = valorDesconto;
	}
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	public StatusPedido getStatus() {
		return status;
	}
	public void setStatus(StatusPedido status) {
		this.status = status;
	}

	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	public Dentista getDentista() {
		return dentista;
	}
	public void setDentista(Dentista dentista) {
		this.dentista = dentista;
	}
	public List<ItemPedido> getItens() {
		return itens;
	}
	public void setItens(List<ItemPedido> itens) {
		this.itens = itens;
	}
	
	public CondicaoPagamento getCondicaoPagamento() {
		if(condicaoPagamento == null)
			condicaoPagamento = new CondicaoPagamento();
		return condicaoPagamento;
	}
	public void setCondicaoPagamento(CondicaoPagamento condicaoPagamento) {
		this.condicaoPagamento = condicaoPagamento;
	}
	public VendaCancelamento getCancelamento() {
		return cancelamento;
	}
	public void setCancelamento(VendaCancelamento cancelamento) {
		this.cancelamento = cancelamento;
	}
	public Receita getReceita() {
		if(receita == null)
			receita = new Receita();
		return receita;
	}
	public void setReceita(Receita receita) {
		this.receita = receita;
	}
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	@Transient
	public boolean isNovo(){
		return getId() == null;
	}
	
	@Transient
	public boolean isExistente(){
		return !isNovo();
	}
		
	public BigDecimal getValorSubtotal() {
		return this.getValorTotal().subtract(this.getValorAdicional()).add(this.getValorDesconto());
	}
	
	public void recalcularValorToral() {
		BigDecimal total = BigDecimal.ZERO;
		
		total = total.add(this.getValorAdicional()).subtract(this.getValorDesconto());
		
		for(ItemPedido item : this.getItens()) {
			if(item.getProcedimento() != null && item.getProcedimento().getId() != null) {
				total = total.add(item.getValorTotal());
			}
		}
		
		this.setValorTotal(total);
		
	}
	public void adicionarItemVazio() {
		if(this.isOrcamento()){
			Procedimento procedimento = new Procedimento();
						
			ItemPedido item = new ItemPedido();
			item.setProcedimento(procedimento);
			item.setPedido(this);
			
			this.getItens().add(0, item);
		}
	}
	
	@Transient
	public boolean isOrcamento() {
		return StatusPedido.ORCAMENTO.equals(this.getStatus());
	}
	public void removerItemVazio() {
		ItemPedido primeiroItem = this.getItens().get(0);
		
		if(primeiroItem != null && primeiroItem.getProcedimento().getId() == null) {
			this.getItens().remove(0);
		}
		
	}
	
	@Transient
	public boolean isValorTotalNegativo() {
		return this.getValorTotal().compareTo(BigDecimal.ZERO) < 0;
	}
	
	@Transient
	public boolean isEmitido() {
		return StatusPedido.APROVADO.equals(this.getStatus());
	}
	
	@Transient
	public boolean isNaoEmissivel() {
		return !this.isEmissivel();
	}
	
	@Transient
	public boolean isEmissivel() {
		return this.isExistente() && this.isOrcamento();
	}
	
	@Transient
	private boolean isCancelado() {
		return StatusPedido.CANCELADO.equals(this.getStatus());
	}
	
	@Transient
	public boolean isCancelavel() {
		return this.isExistente() && !this.isCancelado();
	}
	
	@Transient
	public boolean isNaoCancelavel() {
		return !this.isCancelavel();
	}
	
	@Transient
	public boolean isAlteravel() {
		return this.isOrcamento();
	}
	
	@Transient
	public boolean isNaoAlteravel() {
		return !this.isAlteravel();
	}
	
	@Transient
	public boolean isNaoEnviavelPorEmail() {
		return this.isNovo() || this.isCancelado();
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
		Pedido other = (Pedido) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
