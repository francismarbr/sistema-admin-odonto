package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Agendamento;
import com.agenciacrud.gestornegocio.repositorio.Agendamentos;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class AgendamentoService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Agendamentos agendamentos;
	
	@Transactional
	public Agendamento salvar(Agendamento agendamento) {
		agendamento = this.agendamentos.guardar(agendamento);
		return agendamento;
	}

}
