package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.CategoriaProcedimento;
import com.agenciacrud.gestornegocio.repositorio.CategoriasProcedimentos;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class CategoriaProcedimentoService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private CategoriasProcedimentos categorias;
	
	@Transactional
	public CategoriaProcedimento salvar(CategoriaProcedimento categoria) {
		categoria = this.categorias.guardar(categoria);
		return categoria;
	}

}
