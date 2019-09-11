package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Despesa;
import com.agenciacrud.gestornegocio.repositorio.Despesas;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class DespesaService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Despesas despesas;
	
	@Transactional
	public Despesa salvar(Despesa despesa) {
		despesa = this.despesas.guardar(despesa);
		return despesa;
	}

}
