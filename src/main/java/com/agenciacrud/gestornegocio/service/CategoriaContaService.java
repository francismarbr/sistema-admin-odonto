package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.repositorio.CategoriasContas;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class CategoriaContaService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private CategoriasContas categorias;
	
	@Transactional
	public CategoriaConta salvar(CategoriaConta categoria) {
		categoria = this.categorias.guardar(categoria);
		return categoria;
	}

}
