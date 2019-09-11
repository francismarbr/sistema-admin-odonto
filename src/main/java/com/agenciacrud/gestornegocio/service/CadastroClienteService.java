package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.repositorio.Clientes;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

/*
 * Classe responsável pelas regras de negócio de cadastro de produto
 */
public class CadastroClienteService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Clientes clientes;
	
	@Transactional
	public Pessoa salvar(Pessoa pessoa) {
		Pessoa clienteExistente = null;
		if(pessoa.getCpf() != "") {
			clienteExistente = clientes.porCpfcnpj(pessoa.getCpf(), pessoa.getEmpresa());
		}
		//verifica se existe o cliente cadastrado, caso cnpj ou cpf em branco, permite o cadastro
		if(clienteExistente !=null && !clienteExistente.equals(pessoa) ) {
			throw new NegocioException("Já existe uma pessoa com o CPF/CNPJ informado.");
		}
		return clientes.guardar(pessoa);
	}

}
