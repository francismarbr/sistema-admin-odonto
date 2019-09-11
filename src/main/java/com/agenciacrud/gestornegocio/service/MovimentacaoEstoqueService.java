package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.MovimentacaoEstoque;
import com.agenciacrud.gestornegocio.repositorio.MovimentacoesProdutos;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class MovimentacaoEstoqueService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private MovimentacoesProdutos movimentacoes;
	
	@Transactional
	public MovimentacaoEstoque salvar(MovimentacaoEstoque movimentacao) {
		/*if(movimentacao.isNovo()) {
			movimentacao.setDataCriacao(new Date());
			movimentacao.setStatus(StatusPedido.ORCAMENTO);
		}
		
		movimentacao.recalcularValorToral();
		
		if (movimentacao.isNaoAlteravel()) {
			throw new NegocioException("Pedido não pode ser alterado no status "
					+ movimentacao.getStatus().getDescricao());
		}
		
		if(movimentacao.getItens().isEmpty()) {
			throw new NegocioException("O pedido deve possuir pelo menos um item.");
		}
		
		if (movimentacao.isValorTotalNegativo()) {
			throw new NegocioException("Valor total do pedido não pode ser negativo");
		}*/
		
		movimentacao = this.movimentacoes.guardar(movimentacao);
		return movimentacao;
	}

}
