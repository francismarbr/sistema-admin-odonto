package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Dentista;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.repositorio.Clientes;
import com.agenciacrud.gestornegocio.repositorio.Dentistas;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

/*
 * Classe responsável pelas regras de negócio de cadastro de produto
 */
public class DentistaService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Dentistas dentistas;
	
	@Transactional
	public Dentista salvar(Dentista dentista) {
		Dentista dentistaExistente = null;
		if(dentista.getCpf() != "") {
			dentistaExistente = dentistas.porCpf(dentista.getCpf(), dentista.getEmpresa());
		}
		//verifica se existe o cliente cadastrado, caso cnpj ou cpf em branco, permite o cadastro
		if(dentistaExistente !=null && !dentistaExistente.equals(dentista) ) {
			throw new NegocioException("Já existe um dentista com o CPF informado.");
		}
		return dentistas.guardar(dentista);
	}

}
