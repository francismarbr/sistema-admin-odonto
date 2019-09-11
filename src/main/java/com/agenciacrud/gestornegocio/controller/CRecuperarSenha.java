package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.Locale;
import java.util.Random;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.Usuario;
import com.agenciacrud.gestornegocio.repositorio.Usuarios;
import com.agenciacrud.gestornegocio.service.CadastroUsuarioService;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;
import com.agenciacrud.gestornegocio.util.mail.Mailer;
import com.agenciacrud.gestornegocio.util.mail.VelocityTema;
import com.outjected.email.api.MailMessage;

@Named("CRecuperarSenha")
@ViewScoped
public class CRecuperarSenha implements Serializable {
	

	private static final long serialVersionUID = 1L;
		
	@Inject
	private Usuarios usuarios;
	
	@Inject
	private CadastroUsuarioService cadastroUsuarioService;
	
	@Inject
	private Mailer mailer;
	
	private String nomeUsuario;
	private String novaSenha;
	private String tokenUrl = "";
	
	private boolean solicitacaoValida = false;
		
	private Usuario usuarioEdicao;

	public void salvarNovaSenha() {
		if(solicitacaoValida) {
			usuarioEdicao.setSenha(novaSenha);
			usuarioEdicao.setTokenTemporario(null);
			cadastroUsuarioService.salvar(usuarioEdicao);

			FacesUtil.addInfoMessage("Senha Alterada com sucesso! Clique no link abaixo para fazer login.");
			solicitacaoValida = false;
		} else {
			FacesUtil.addErrorMessage("Solicitação de alteração inválida.");
		}
	}

	public void recuperar()  {
		Usuario usuarioEncontrado;
		usuarioEncontrado = usuarios.porLogin(nomeUsuario);
		
		if (usuarioEncontrado != null) {
			
			Random random = new Random();
			int numAleatorio = random.nextInt(100001) + 1001;
			
			String codTemporario = String.valueOf(numAleatorio);
			usuarioEncontrado.setTokenTemporario(codTemporario);
					
			//Salva código temporário para o usuário
			usuarioEncontrado = cadastroUsuarioService.salvar(usuarioEncontrado);
			
			if(usuarioEncontrado.getEmail().equals("")){
				throw new NegocioException("Não existe email cadastrado para esse usuário");
			} else {
			MailMessage message = mailer.novaMensagem();
			
			message
					.from("contato@adminodonto.com")
					.to(usuarioEncontrado.getEmail())
					.subject("Recuperação de Senha")
					.bodyHtml(new VelocityTema(getClass().getResourceAsStream("/emails/recupsenha.template")))
					.put("usuario", usuarioEncontrado)
					.put("locale", new Locale("pt", "BR"))
					.send();
			
			FacesUtil.addInfoMessage("Acesse seu email para completar o processo de recuperação de senha!");
			} 
		} else {
			FacesUtil.addErrorMessage("Nenhum usuário encontrado!");
		}
	}
	
	public boolean isAlterarSenha() {
		if(!tokenUrl.equals(null) || !tokenUrl.isEmpty()){
			//Verfica usuário que possui o token
			setUsuarioEdicao(usuarios.porToken(tokenUrl));
			if(getUsuarioEdicao() == null){
				FacesUtil.addErrorMessage("Solicitação de alteração inválida.");
				return false;
			}
			solicitacaoValida = true;
		}
		//retorna se usuario não existir, retorna falso, caso contrário, verdadeiro.
		return this.usuarioEdicao.getId() != null;
	}
		
	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}
	
	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}
	

	public Usuario getUsuarioEdicao() {
		return usuarioEdicao;
	}

	public void setUsuarioEdicao(Usuario usuarioEdicao) {
		this.usuarioEdicao = usuarioEdicao;
	}

	public String getTokenUrl() {
		return tokenUrl;
	}

	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}

	public boolean isSolicitacaoValida() {
		return solicitacaoValida;
	}

	

}
