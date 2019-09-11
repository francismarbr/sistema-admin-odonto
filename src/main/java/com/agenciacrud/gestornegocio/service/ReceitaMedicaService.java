package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Produto;
import com.agenciacrud.gestornegocio.model.ReceitaMedica;
import com.agenciacrud.gestornegocio.repositorio.ReceitasMedicas;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class ReceitaMedicaService implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ReceitasMedicas receitasMedicas;
	
	@Transactional
	public ReceitaMedica salvar(ReceitaMedica receitaMedica) {
		receitaMedica = this.receitasMedicas.guardar(receitaMedica);
		return receitaMedica;
	}
}
