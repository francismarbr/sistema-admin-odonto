package com.agenciacrud.gestornegocio.controller;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.Usuario;
import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.StatusAgendamentoEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoCategoriaContaEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoContaEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoMensalAnualEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoPessoaEnumerador;
import com.agenciacrud.gestornegocio.seguranca.UsuarioLogado;
import com.agenciacrud.gestornegocio.seguranca.UsuarioSistema;


public class CGeral {
	
	//@Inject
	//private RGeral rGeral;
	
	@Inject
	@UsuarioLogado
	private UsuarioSistema usuarioLogado;
	
	protected boolean excluivel;	
	private Empresa empresaLogada;
	private Usuario usuario;
	
	@Inject
	HttpServletResponse response;
	
	protected Logger log = LogManager.getLogger(this.getClass());
	
	//Lista os tipos de pessoa(Física ou Jurídica)
	public List<SelectItem> getComboTipoPessoa() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		TipoPessoaEnumerador tipos[] = TipoPessoaEnumerador.values();
		for(int i = 0; i < tipos.length; i++) {
			itens.add(new SelectItem(tipos[i]));
		}
		return itens;
	}
	
	//Lista os tipos de status
	public List<SelectItem> getComboStatusAgendamento() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		StatusAgendamentoEnumerador status[] = StatusAgendamentoEnumerador.values();
		for(int i = 0; i < status.length; i++) {
			itens.add(new SelectItem(status[i]));
		}
		return itens;
	}
		
	public List<SelectItem> getComboIntervaloTempo(){
		List<SelectItem> itens = new ArrayList<SelectItem>();
		TipoMensalAnualEnumerador tipos[] = TipoMensalAnualEnumerador.values();
		for(int i = 0; i < tipos.length; i++) {
			itens.add(new SelectItem(tipos[i]));
		}
		return itens;
	}
	
	//Contas a Receber ou Contas a Pagar
	public List<SelectItem> getComboTipoConta() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		TipoContaEnumerador tipos[] = TipoContaEnumerador.values();
		for(int i = 0; i < tipos.length; i++) {
			itens.add(new SelectItem(tipos[i]));
		}
		return itens;
	}
	
	public List<SelectItem> getComboCategoriaConta() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		TipoCategoriaContaEnumerador tipos[] = TipoCategoriaContaEnumerador.values();
		for(int i = 0; i < tipos.length; i++) {
			itens.add(new SelectItem(tipos[i]));
		}
		return itens;
	}
		
	public List<SelectItem> getComboTipoBaixaParcela() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(SituacaoPagamentoEnumerador.QUITADA));
		//itens.add(new SelectItem(SituacaoPagamentoEnumerador.PAGA_PARCIALMENTE));
		itens.add(new SelectItem(SituacaoPagamentoEnumerador.ABERTA));
		return itens;
	}
	
	public List<SelectItem> getComboTipoBaixaParcelaQuitadaParcial() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(SituacaoPagamentoEnumerador.QUITADA));
		itens.add(new SelectItem(SituacaoPagamentoEnumerador.PAGA_PARCIALMENTE));
		return itens;
	}
	
	public List<SelectItem> getComboUFSigla() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(null, "--Selecione--"));
		itens.add(new SelectItem("AC", "AC"));
		itens.add(new SelectItem("AL", "AL"));
		itens.add(new SelectItem("AP", "AP"));
		itens.add(new SelectItem("AM", "AM"));
		itens.add(new SelectItem("BA", "BA"));
		itens.add(new SelectItem("CE", "CE"));
		itens.add(new SelectItem("DF", "DF"));
		itens.add(new SelectItem("ES", "ES"));
		itens.add(new SelectItem("GO", "GO"));
		itens.add(new SelectItem("MA", "MA"));
		itens.add(new SelectItem("MT", "MT"));
		itens.add(new SelectItem("MS", "MS"));
		itens.add(new SelectItem("MG", "MG"));
		itens.add(new SelectItem("PA", "PA"));
		itens.add(new SelectItem("PB", "PB"));
		itens.add(new SelectItem("PR", "PR"));
		itens.add(new SelectItem("PE", "PE"));
		itens.add(new SelectItem("PI", "PI"));
		itens.add(new SelectItem("RJ", "RJ"));
		itens.add(new SelectItem("RN", "RN"));
		itens.add(new SelectItem("RS", "RS"));
		itens.add(new SelectItem("RO", "RO"));
		itens.add(new SelectItem("RR", "RR"));
		itens.add(new SelectItem("SC", "SC"));
		itens.add(new SelectItem("SP", "SP"));
		itens.add(new SelectItem("SE", "SE"));
		itens.add(new SelectItem("TO", "TO"));
		return itens;
	}
	
	public String criptografar(String senha) {
		StringBuilder senhaCriptografada = new StringBuilder();
		try {
			MessageDigest algoritimo = MessageDigest.getInstance("SHA-256");
			byte digestMessage[] = algoritimo.digest(senha.getBytes("UTF-8"));
			for(byte aByte : digestMessage) {
				senhaCriptografada.append(String.format("%02X", 0xFF & aByte));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return senhaCriptografada.toString();
	}
		
	protected FacesContext context() {
		return (FacesContext.getCurrentInstance());
	}

	public boolean isExcluivel() {
		return excluivel;
	}

	public void setExcluivel(boolean excluivel) {
		this.excluivel = excluivel;
	}
   
	public Empresa getEmpresaLogada() {
		empresaLogada = usuarioLogado.getUsuario().getEmpresa();
		return empresaLogada;
	}

	public void setEmpresaLogada(Empresa empresa) {
		this.empresaLogada = empresa;
	}

	public UsuarioSistema getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(UsuarioSistema usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public Usuario getUsuario() {
		usuario = usuarioLogado.getUsuario();
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	/*
	public HttpServletResponse getResponse() {
		return (HttpServletResponse) context().getExternalContext()
				.getResponse();
	}*/
	
	public String getCaminhoRaiz() {
		return FacesContext.getCurrentInstance().getExternalContext().getRealPath("");
				
	}
	
	/*public void setResponseComplete() {
		FacesContext.getCurrentInstance().responseComplete();
	}*/
	
	/*Lista os bancos cadastrados
	public List<SelectItem> getComboBanco() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(null, "-- Selecione --"));
		List<Banco> lista = rGeral.buscaTodos(Banco.class);
		for(Banco banco : lista) {
			itens.add(new SelectItem(banco.getId(), banco.getNome()));
		}
		return itens;
	}*/
	
}
