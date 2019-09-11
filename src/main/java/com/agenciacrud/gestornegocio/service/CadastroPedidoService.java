package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.Pedido;
import com.agenciacrud.gestornegocio.model.StatusPedido;
import com.agenciacrud.gestornegocio.repositorio.Pedidos;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class CadastroPedidoService implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Pedidos pedidos;
	
	@Transactional
	public Pedido salvar(Pedido pedido) {
		if(pedido.isNovo()) {
			pedido.setDataCriacao(new Date());
			pedido.setStatus(StatusPedido.ORCAMENTO);
		}
		
		pedido.recalcularValorToral();
		
		if (pedido.isNaoAlteravel()) {
			throw new NegocioException("Pedido não pode ser alterado no status "
					+ pedido.getStatus().getDescricao());
		}
		
		if(pedido.getItens().isEmpty()) {
			throw new NegocioException("O pedido deve possuir pelo menos um item.");
		}
		
		if (pedido.isValorTotalNegativo()) {
			throw new NegocioException("Valor total do pedido não pode ser negativo");
		}
		
		pedido = this.pedidos.guardar(pedido);
		return pedido;
	}

}
