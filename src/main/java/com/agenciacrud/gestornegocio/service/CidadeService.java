package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Cidade;
import com.agenciacrud.gestornegocio.repositorio.Cidades;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class CidadeService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Cidades cidades;
	
	@Transactional
	public Cidade salvar(Cidade cidade) {
		cidade = this.cidades.guardar(cidade);
		return cidade;
	}

}
