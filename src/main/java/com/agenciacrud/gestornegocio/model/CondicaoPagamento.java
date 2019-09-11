package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class CondicaoPagamento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1023564654L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 60)
	private String nome;

	private String aVista = "n";

	private BigDecimal valorEntrada = BigDecimal.ZERO;
	
	private Integer qtParcelas = 1;
	
	@OneToOne(optional=true)
	private FormaPagamento formaPagamento;

	public Long getId() {
		return id;
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

	public Integer getQtParcelas() {
		return qtParcelas;
	}

	public void setQtParcelas(Integer qtParcelas) {
		this.qtParcelas = qtParcelas;
	}

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public BigDecimal getValorEntrada() {
		if (valorEntrada == null)
			valorEntrada = new BigDecimal(0);
		return valorEntrada;
	}

	public void setValorEntrada(BigDecimal valorEntrada) {
		this.valorEntrada = valorEntrada;
	}

	public String getaVista() {
		return aVista;
	}

	public void setaVista(String aVista) {
		this.aVista = aVista;
	}
}