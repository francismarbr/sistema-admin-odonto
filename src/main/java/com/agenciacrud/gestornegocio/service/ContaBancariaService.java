package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.ContaBancaria;
import com.agenciacrud.gestornegocio.repositorio.ContasBancarias;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

/*
 * Classe responsável pelas regras de negócio de cadastro de produto
 */
public class ContaBancariaService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ContasBancarias contasBancarias;
	
	@Transactional
	public ContaBancaria salvar(ContaBancaria contaBancaria) {
		//TODO implementar regra de negócio
		//Servico servicoExistente = servicos.porId(servico.getId());
		
		//if(servicoExistente != null && !servicoExistente.equals(servico)) {
		//	throw new NegocioException("Já existe um serviço com este mesmo código cadastrado.");
		//}
		return contasBancarias.guardar(contaBancaria);
	}

}
