package com.agenciacrud.gestornegocio.seguranca;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Named
@RequestScoped
public class Seguranca {
	@Inject
	private ExternalContext externalContext;
	
	public String getNomeUsuario() {
		String nome = null;
		
		UsuarioSistema usuarioLogado = getUsuarioLogado();
		
		if (usuarioLogado != null) {
			nome = usuarioLogado.getUsuario().getNome();			
		}
		
		return nome;
	}
	
	@Produces
	@UsuarioLogado
	public UsuarioSistema getUsuarioLogado() {
		UsuarioSistema usuario = null;
		
		//classe do  spring security, usa o método getUserPrinciapal
		UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken)
				FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();

		if (auth != null && auth.getPrincipal() != null) {
			usuario = (UsuarioSistema) auth.getPrincipal(); //retorna o usuário sistema, se não tiver ningúem logado retorna nulo
		}
		
		return usuario;
	}
	
	
	//método para exibir ou ocultar menu
	public boolean isLiberarMenuEmpresaPlanoMCM() {
		return externalContext.isUserInRole("MEGA") || externalContext.isUserInRole("COMPLETO") || externalContext.isUserInRole("MASTER");
	}
	
	public boolean isLiberarMenuEmpresaPlanoCM() {
		return externalContext.isUserInRole("COMPLETO") || externalContext.isUserInRole("MASTER");
	}
	public boolean isLiberarMenuEmpresaPlanoMaster() {
		return externalContext.isUserInRole("MASTER");
	}
	
	public boolean isCancelarPedidoPermitido() {
		return externalContext.isUserInRole("ADMINISTRADORES") || externalContext.isUserInRole("VENDEDORES");
	}
	
	public boolean isEmitirPedidoPermitido() {
		return externalContext.isUserInRole("ADMINISTRADORES") || externalContext.isUserInRole("VENDEDORES");
	}
}
