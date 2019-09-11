package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.repositorio.Parcelas;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class MovimentacaoService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Parcelas parcelas;
	
	@Transactional
	public MovimentacaoConta salvar(MovimentacaoConta movimentacao) {
		movimentacao = this.parcelas.guardarMovimentacao(movimentacao);
		return movimentacao;
	}

}
