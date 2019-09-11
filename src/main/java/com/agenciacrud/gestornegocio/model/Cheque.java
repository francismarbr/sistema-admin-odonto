package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Cheque implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long chaveRegistro;
	
	@OneToOne
	private Banco banco;
	private String agencia;
	private String conta;
	private String serie;
	private String numero;
	private BigDecimal valor = BigDecimal.ZERO;
	private Date dataEmissao;
	private Date dataPreDatado;
	private Date dataLancamento;
	
	private Pessoa emitidoPor;
	private Pessoa recebidoDe;
	private Pessoa repassadoA;
	
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
	public Banco getBanco() {
		return banco;
	}
	public void setBanco(Banco banco) {
		this.banco = banco;
	}
	public String getAgencia() {
		return agencia;
	}
	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}
	public String getConta() {
		return conta;
	}
	public void setConta(String conta) {
		this.conta = conta;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public Date getDataEmissao() {
		return dataEmissao;
	}
	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}
	public Date getDataPreDatado() {
		return dataPreDatado;
	}
	public void setDataPreDatado(Date dataPreDatado) {
		this.dataPreDatado = dataPreDatado;
	}
	public Pessoa getEmitidoPor() {
		return emitidoPor;
	}
	public void setEmitidoPor(Pessoa emitidoPor) {
		this.emitidoPor = emitidoPor;
	}
	public Pessoa getRecebidoDe() {
		return recebidoDe;
	}
	public void setRecebidoDe(Pessoa recebidoDe) {
		this.recebidoDe = recebidoDe;
	}
	public Pessoa getRepassadoA() {
		return repassadoA;
	}
	public void setRepassadoA(Pessoa repassadoA) {
		this.repassadoA = repassadoA;
	}
	public Date getDataLancamento() {
		return dataLancamento;
	}
	public void setDataLancamento(Date dataLancamento) {
		this.dataLancamento = dataLancamento;
	}


	
	
	
	
}
