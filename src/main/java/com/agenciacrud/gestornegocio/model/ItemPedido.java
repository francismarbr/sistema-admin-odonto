package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="item_pedido")
public class ItemPedido implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 3)
	private Integer quantidade = 1 ;
	
	@Column(name = "valor", nullable = false, precision = 10, scale = 2)
	private BigDecimal valor = BigDecimal.ZERO;
	
	@ManyToOne
	@JoinColumn(name = "procedimento_id", nullable = false)
	private Procedimento procedimento;
	
	private String dente;
	
	@ManyToOne
	@JoinColumn(name = "pedido_id", nullable = false)
	private Pedido pedido;
	
		
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
		ItemPedido other = (ItemPedido) obj;
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
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public Procedimento getProcedimento() {
		return procedimento;
	}
	public void setProcedimento(Procedimento procedimento) {
		this.procedimento = procedimento;
	}
	
	public String getDente() {
		return dente;
	}
	public void setDente(String dente) {
		this.dente = dente;
	}
	
	public Pedido getPedido() {
		return pedido;
	}
	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}
	
	@Transient
	public BigDecimal getValorTotal() {
		return this.getValor().multiply(new BigDecimal(this.getQuantidade()));
				
	}
	
	@Transient
	public boolean isProcedimentoAssociado() {
		return this.getProcedimento() != null && this.getProcedimento().getId() != null;
	}
	
	/*@Transient 
	public boolean isEstoqueSuficiente() {
		return this.getPedido().isEmitido() || this.getProcedimento().getId() == null
				 || this.getProcedimento().getQuantidadeEstoque() >= this.getQuantidade();
	}*/
	
	/*@Transient
	public boolean isEstoqueInsuficiente() {
		return !this.isEstoqueSuficiente();
	}*/

}
