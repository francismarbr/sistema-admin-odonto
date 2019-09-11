package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.Pedido;
import com.agenciacrud.gestornegocio.service.EmissaoPedidoService;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;



@Named
@RequestScoped
public class EmissaoPedidoBean extends CGeral implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EmissaoPedidoService emissaoPedidoService;
	
	@Inject
	@PedidoEdicao
	private Pedido pedido;
	
	
	
	@Inject
	private Event<PedidoAlteradoEvent> pedidoAlteradoEvent;
	
	@Transactional
	public void emitirPedido() {
		this.pedido.removerItemVazio();
		
		try {
			this.pedido = this.emissaoPedidoService.emitir(this.pedido);
			this.pedidoAlteradoEvent.fire(new PedidoAlteradoEvent(this.pedido));//lançar um evento CDI atualiza objeto pedido
			
			
			
			
			FacesUtil.addInfoMessage("Pedido Emitido com sucesso!");
		} finally {
			this.pedido.adicionarItemVazio();
		}
	}
}
