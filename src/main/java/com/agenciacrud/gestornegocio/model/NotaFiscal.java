package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.agenciacrud.gestornegocio.model.enumeradores.TipoNotaFiscal;


@Entity
public class NotaFiscal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -37349705358518039L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.ORDINAL)
	private TipoNotaFiscal tipo;

	@Column(length = 10)
	private String numrSerie;

	@Column(length = 15)
	private String numrNotaFiscal;

	@Temporal(TemporalType.DATE)
	private Date dataEmissaoNF;

	@Column(length = 44)
	private String chaveAcesso;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumrSerie() {
		return numrSerie;
	}

	public void setNumrSerie(String numrSerie) {
		this.numrSerie = numrSerie;
	}

	public String getNumrNotaFiscal() {
		return numrNotaFiscal;
	}

	public void setNumrNotaFiscal(String numrNotaFiscal) {
		this.numrNotaFiscal = numrNotaFiscal;
	}

	public Date getDataEmissaoNF() {
		return dataEmissaoNF;
	}

	public void setDataEmissaoNF(Date dataEmissaoNF) {
		this.dataEmissaoNF = dataEmissaoNF;
	}

	public String getChaveAcesso() {
		return chaveAcesso;
	}

	public void setChaveAcesso(String chaveAcesso) {
		this.chaveAcesso = chaveAcesso;
	}

	public TipoNotaFiscal getTipo() {
		return tipo;
	}

	public void setTipo(TipoNotaFiscal tipo) {
		this.tipo = tipo;
	}

}