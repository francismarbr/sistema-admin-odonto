package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Atestado;
import com.agenciacrud.gestornegocio.model.Produto;
import com.agenciacrud.gestornegocio.model.ReceitaMedica;
import com.agenciacrud.gestornegocio.repositorio.Atestados;
import com.agenciacrud.gestornegocio.repositorio.ReceitasMedicas;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class AtestadoService implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Atestados atestados;
	
	@Transactional
	public Atestado salvar(Atestado atestado) {
		atestado = this.atestados.guardar(atestado);
		return atestado;
	}
}
