package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.velocity.tools.generic.NumberTool;

import com.agenciacrud.gestornegocio.model.Pedido;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;
import com.agenciacrud.gestornegocio.util.mail.Mailer;
import com.agenciacrud.gestornegocio.util.mail.VelocityTema;
import com.outjected.email.api.MailMessage;



@Named
@RequestScoped
public class EnvioPedidoEmailBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private Mailer mailer;
	
	@Inject
	@PedidoEdicao
	private Pedido pedido;
	
	public void enviarPedido() {
		if(this.pedido.getPessoa().getEmail().equals("")){
			throw new NegocioException("Não existe email cadastrado para esse cliente");
		} else {
		MailMessage message = mailer.novaMensagem();
		
		message
				.from("contato@adminodonto.com")
				.to(this.pedido.getPessoa().getEmail())
				.subject("Pedido " + this.pedido.getId())
				.bodyHtml(new VelocityTema(getClass().getResourceAsStream("/emails/pedido.template")))
				.put("pedido", this.pedido)
				.put("numberTool", new NumberTool())
				.put("locale", new Locale("pt", "BR"))
				.send();
		
		FacesUtil.addInfoMessage("Pedido enviado por e-mail com sucesso!");
		}
	}

}
