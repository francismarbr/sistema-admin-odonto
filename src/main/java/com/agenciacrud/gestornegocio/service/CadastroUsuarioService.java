package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Usuario;
import com.agenciacrud.gestornegocio.repositorio.Usuarios;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;
/*
 * Classe responsável pelas regras de negócio de cadastro de usuário
 */
public class CadastroUsuarioService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Usuarios usuarios;
	
	@Transactional
	public Usuario salvar(Usuario usuario) {
		//TODO implementar regra de negócio
		Usuario loginExistente = usuarios.porLogin(usuario.getLogin());
		
		if(loginExistente !=null && !loginExistente.equals(usuario)) {
			throw new NegocioException("Este nome de usuário não está disponível");
		}

		return usuarios.guardar(usuario);
	}
}
