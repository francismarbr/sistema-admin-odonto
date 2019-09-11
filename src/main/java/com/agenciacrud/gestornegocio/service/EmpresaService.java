package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.repositorio.Empresas;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

/*
 * Classe responsável pelas regras de negócio de cadastro de produto
 */
public class EmpresaService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Empresas empresas;
	
	@Transactional
	public Empresa salvar(Empresa empresa) {
		return empresas.guardar(empresa);
	}

}
