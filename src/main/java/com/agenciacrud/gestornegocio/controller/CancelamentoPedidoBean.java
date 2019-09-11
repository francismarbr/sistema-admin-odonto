package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.ItemPedido;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.ParcelaCancelamento;
import com.agenciacrud.gestornegocio.model.Pedido;
import com.agenciacrud.gestornegocio.model.Produto;
import com.agenciacrud.gestornegocio.model.Receita;
import com.agenciacrud.gestornegocio.model.StatusPedido;
import com.agenciacrud.gestornegocio.model.VendaCancelamento;
import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.service.CancelamentoPedidoService;
import com.agenciacrud.gestornegocio.service.ParcelaService;
import com.agenciacrud.gestornegocio.service.ProdutoService;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named
@RequestScoped
public class CancelamentoPedidoBean extends CGeral implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private CancelamentoPedidoService cancelamentoPedidoService;
	
	@Inject
	private Event<PedidoAlteradoEvent> pedidoAlteradoEvent;
	
	@Inject
	private ParcelaService parcelaService;
	
	@Inject
	private ProdutoService produtoService;
	
	@Inject
	private RGeral rGeral;
	
	private VendaCancelamento cancelamento;
	
	@Inject
	@PedidoEdicao
	private Pedido pedido;
		
	public void cancelarPedido(){
		getCancelamento().setUsuario(getUsuario());
		getCancelamento().setData(new Date());
		pedido.setStatus(StatusPedido.CANCELADO);
		pedido.setCancelamento(getCancelamento());
		this.pedido = this.cancelamentoPedidoService.cancelar(this.pedido);
		this.pedidoAlteradoEvent.fire(new PedidoAlteradoEvent(this.pedido));
		cancelarReceita();
		//reajustarEstoque();
		FacesUtil.addInfoMessage("Pedido cancelado com sucesso");
	}
	
	//Cancela a receita que foi gerada pela venda
	private void cancelarReceita() {
		Receita receita = null;
		receita = this.rGeral.getObj(pedido.getReceita().getId(), Receita.class);
		for(Parcela item : receita.getParcelas()) {
			Parcela itemCancelamento = montarCancelamentoParcela(item);
			parcelaService.salvar(itemCancelamento);
		}
	}
	
	//Nesse método, faz-se o lançamento das parcelas canceladas
	private Parcela montarCancelamentoParcela(Parcela parcela) {
		ParcelaCancelamento parcelaCancelamento = new ParcelaCancelamento();
		
		parcelaCancelamento.setData(new Date());
		parcelaCancelamento.setUsuario(getUsuario());
		parcelaCancelamento.setObservacao("Cancelamento da Venda de nº "+pedido.getChaveRegistro());
		parcelaCancelamento.setParcela(parcela);
		
		parcela.setCancelamento(parcelaCancelamento);
		parcela.setSituacao(SituacaoPagamentoEnumerador.CANCELADA);
		
		//implementar aqui lançamento de cancelamento na movimentação de contas
		//MovimentacaoConta mov = nul;
		
		
		return parcela;
	}
	
	/*private void reajustarEstoque(){
		for(ItemPedido item : pedido.getItens()){
			Produto produto = item.getProduto();
			produto.setQuantidadeEstoque(produto.getQuantidadeEstoque()+ item.getQuantidade());
			produtoService.salvar(produto);
		}
	}*/

	public VendaCancelamento getCancelamento() {
		if (cancelamento == null)
			cancelamento = new VendaCancelamento();
		return cancelamento;
	}

	public void setCancelamento(VendaCancelamento cancelamento) {
		this.cancelamento = cancelamento;
	}
	
	

}
