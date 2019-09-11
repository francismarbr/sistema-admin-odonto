package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.TransferenciaConta;
import com.agenciacrud.gestornegocio.repositorio.Transferencias;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class TransferenciaService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Transferencias transferencias;
	
	@Transactional
	public TransferenciaConta salvar(TransferenciaConta transferencia) {
		transferencia = this.transferencias.guardar(transferencia);
		return transferencia;
	}

}
