package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.repositorio.Parcelas;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class ParcelaService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Parcelas parcelas;
	
	@Transactional
	public Parcela salvar(Parcela parcela) {
		parcela = this.parcelas.guardarParcela(parcela);
		return parcela;
	}

}
