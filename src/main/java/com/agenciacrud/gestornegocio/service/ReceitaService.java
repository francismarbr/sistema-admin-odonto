package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Receita;
import com.agenciacrud.gestornegocio.repositorio.Receitas;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class ReceitaService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Receitas receitas;
	
	@Transactional
	public Receita salvar(Receita receita) {
		receita = this.receitas.guardar(receita);
		return receita;
	}

}
