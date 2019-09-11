package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.repositorio.RegimesTributarios;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

/*
 * Classe responsável pelas regras de negócio de cadastro de produto
 */
public class CadastroRegimeTributarioService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private RegimesTributarios regimes;
	
	@Transactional
	public RegimeTributario salvar(RegimeTributario regime) {
		//TODO implementar regra de negócio
		//Servico servicoExistente = servicos.porId(servico.getId());
		
		//if(servicoExistente != null && !servicoExistente.equals(servico)) {
		//	throw new NegocioException("Já existe um serviço com este mesmo código cadastrado.");
		//}
		return regimes.guardar(regime);
	}

}
