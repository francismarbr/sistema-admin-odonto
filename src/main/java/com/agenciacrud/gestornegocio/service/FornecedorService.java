package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Fornecedor;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.repositorio.Clientes;
import com.agenciacrud.gestornegocio.repositorio.Fornecedores;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

/*
 * Classe responsável pelas regras de negócio de cadastro de produto
 */
public class FornecedorService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Fornecedores fornecedores;
	
	@Transactional
	public Fornecedor salvar(Fornecedor fornecedor) {
		Fornecedor fornecedorExistente = null;
		if(fornecedor.getCpfCnpj() != "") {
			fornecedorExistente = fornecedores.porCpfcnpj(fornecedor.getCpfCnpj(), fornecedor.getEmpresa());
		}
		//verifica se existe o cliente cadastrado, caso cnpj ou cpf em branco, permite o cadastro
		if(fornecedorExistente !=null && !fornecedorExistente.equals(fornecedor) ) {
			throw new NegocioException("Já existe uma fornecedor com o CPF/CNPJ informado.");
		}
		return fornecedores.guardar(fornecedor);
	}

}
