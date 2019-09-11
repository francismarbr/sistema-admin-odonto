package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.agenciacrud.gestornegocio.model.enumeradores.TipoExecucaoProcessadorEnumerador;

@Entity
public class ExecucaoProcessador  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 10564L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.ORDINAL)
	private TipoExecucaoProcessadorEnumerador tipo;

	@Temporal(TemporalType.DATE)
	private Date data;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoExecucaoProcessadorEnumerador getTipo() {
		return tipo;
	}

	public void setTipo(TipoExecucaoProcessadorEnumerador tipo) {
		this.tipo = tipo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

}