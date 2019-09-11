package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.ExecucaoProcessador;
import com.agenciacrud.gestornegocio.repositorio.Executores;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class ExecutorService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Executores executores;
	
	@Transactional
	public ExecucaoProcessador salvar(ExecucaoProcessador execucao) {
		execucao = this.executores.guardar(execucao);
		return execucao;
	}

}
