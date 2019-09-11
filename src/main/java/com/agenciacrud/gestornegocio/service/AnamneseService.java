package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.AnamneseModelo;
import com.agenciacrud.gestornegocio.model.AnamnesePaciente;
import com.agenciacrud.gestornegocio.model.StatusPedido;
import com.agenciacrud.gestornegocio.repositorio.AnamnesesModelos;
import com.agenciacrud.gestornegocio.repositorio.AnamnesesPacientes;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class AnamneseService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private AnamnesesModelos anamneses;
	
	@Inject
	private AnamnesesPacientes anamnesesPacientes;
	
	
	//guarda modelos de anamneses
	@Transactional
	public AnamneseModelo salvarModelo(AnamneseModelo anamneseModelo) {
		if(anamneseModelo.getItens().isEmpty()) {
			throw new NegocioException("Insira perguntas para esse modelo");
		}
		anamneseModelo = this.anamneses.guardarModelo(anamneseModelo);
		return anamneseModelo;
	}
	
	//guarda anamneses dos pacientes
	@Transactional
	public AnamnesePaciente salvarAnamnesePaciente(AnamnesePaciente anamnesePaciente) {
		if(anamnesePaciente.isNovo()) {
			anamnesePaciente.setDataRegistro(new Date());
		}
		
		anamnesePaciente = this.anamnesesPacientes.guardar(anamnesePaciente);
		return anamnesePaciente;
	}

}
