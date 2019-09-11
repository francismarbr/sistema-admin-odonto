package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Procedimento;
import com.agenciacrud.gestornegocio.repositorio.Procedimentos;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

/*
 * Classe responsável pelas regras de negócio de cadastro de produto
 */
public class ProcedimentoService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Procedimentos servicos;
	
	@Transactional
	public Procedimento salvar(Procedimento procedimento) {
		return servicos.guardar(procedimento);
	}

}
