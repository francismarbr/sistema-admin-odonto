package com.agenciacrud.gestornegocio.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.agenciacrud.gestornegocio.model.ItemPedido;
import com.agenciacrud.gestornegocio.model.Pedido;
import com.agenciacrud.gestornegocio.repositorio.Pedidos;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class EstoqueService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Pedidos pedidos;
	
	@Transactional
	public void baixarItensEstoque(Pedido pedido) {
		pedido = this.pedidos.porId(pedido.getId());
		
		for(ItemPedido item : pedido.getItens()) {
			//item.getProduto().baixarEstoque(item.getQuantidade());
				
		}
	}

	public void retornarItensEstoque(Pedido pedido) {
		pedido = this.pedidos.porId(pedido.getId());
		
		for(ItemPedido item : pedido.getItens()) {
			//item.getProduto().adicionarEstoque(item.getQuantidade());
		}
	}
}
