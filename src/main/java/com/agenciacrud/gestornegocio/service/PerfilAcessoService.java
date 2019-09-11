package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.PerfilAcesso;
import com.agenciacrud.gestornegocio.model.AnamnesePaciente;
import com.agenciacrud.gestornegocio.model.StatusPedido;
import com.agenciacrud.gestornegocio.repositorio.PerfisAcesso;
import com.agenciacrud.gestornegocio.repositorio.AnamnesesPacientes;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class PerfilAcessoService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private PerfisAcesso perfisAcesso;
	
	//guarda perfil de acesso
	@Transactional
	public PerfilAcesso salvar(PerfilAcesso perfilAcesso) {
		if(perfilAcesso.getPermissoes().isEmpty()) {
			throw new NegocioException("Insira permissões para este perfil!");
		}
		if(perfilAcesso.getNome() == null) {
			throw new NegocioException("O nome do perfil é obrigatório");
		}
		perfilAcesso = this.perfisAcesso.guardar(perfilAcesso);
		return perfilAcesso;
	}

}
