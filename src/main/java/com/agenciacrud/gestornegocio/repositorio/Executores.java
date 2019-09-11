package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.agenciacrud.gestornegocio.model.ExecucaoProcessador;

public class Executores implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	public ExecucaoProcessador guardar(ExecucaoProcessador execucao) {
		return manager.merge(execucao);
	}
	

}
