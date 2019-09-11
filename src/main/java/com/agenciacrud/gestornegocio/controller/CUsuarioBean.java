package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.ItemPerfilAcesso;
import com.agenciacrud.gestornegocio.model.PerfilAcesso;
import com.agenciacrud.gestornegocio.model.Permissao;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.Usuario;
import com.agenciacrud.gestornegocio.model.enumeradores.PlanoEmpresa;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.Usuarios;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.CadastroUsuarioService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CUsuarioBean")
@ViewScoped
public class CUsuarioBean extends CGeral implements Serializable {
	

	private static final long serialVersionUID = 1L;

	private Usuario usuario;
		
	@Inject
	private CadastroUsuarioService cadastroUsuarioService;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private Usuarios usuarios;
	
	private List<Usuario> buscaUltimoRegistro;
	private List<Usuario> usuariosFiltrados;
	
	private PerfilAcesso perfil;
	
	private ConsultaFilter filtro;
	private Usuario usuarioSelecionado;
	
	private Long chaveRegistroEdicao;
	private List<Usuario> buscaRegistroEdicao;
	private List<PerfilAcesso> listaPerfis;
	
	private int itemLinha;
	private String novaSenha;
	private boolean alterarSenha = true;
	private boolean bloquearAlteracaoUsuario = false;
	private boolean limitarCadastro = false;
	
		
	public CUsuarioBean(){
		perfil = new PerfilAcesso();
		filtro = new ConsultaFilter();
		usuario = new Usuario();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()) {
			listaPerfis= rGeral.buscaTodos(PerfilAcesso.class, getEmpresaLogada());
		}
		if(Numero.isMaiorZero(chaveRegistroEdicao)) {
			setExcluivel(true);
		}
		pesquisar();
		int qtdUsuarios = usuariosFiltrados.size();
		if(getEmpresaLogada().getPlano().equals(PlanoEmpresa.INICIAL) && qtdUsuarios == 1 && !Numero.isMaiorZero(usuario.getId()) ){
			limitarCadastro = true;
		}
		if(getEmpresaLogada().getPlano().equals(PlanoEmpresa.MEGA) && qtdUsuarios == 4 && !Numero.isMaiorZero(usuario.getId())){
			limitarCadastro = true;
		}
		
	}
	
	
	public void limpar() {
		usuario = new Usuario();
	}
	
	public void salvar() {
		
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.usuario.getId())){
			Usuario chaveRegistro = null;
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<Usuario>) rGeral.buscaUltimoRegistroPorEmpresa(Usuario.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				usuario.setChaveRegistro(new Long(1));  
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				usuario.setChaveRegistro(codigo);
			}
		}
		this.usuario.setEmpresa(getEmpresaLogada());
		this.usuario = cadastroUsuarioService.salvar(this.usuario);
		limpar();
		
		FacesUtil.addInfoMessage("Usuário foi salvo com sucesso!");
		
	}
	
	public void alterarSenha() {
		this.usuario = getUsuarioLogado().getUsuario();
		this.usuario.setSenha(getNovaSenha());
		cadastroUsuarioService.salvar(this.usuario);
		FacesUtil.addInfoMessage("Senha alterada com sucesso!");
		setNovaSenha("");
	}
	
	public void adicionarItemLista(PerfilAcesso perfil) {
		//itemPerfil.setPermissao(permissao);
		usuario.getPerfisAcesso().add(perfil);
		listaPerfis.remove(perfil);
		perfil = new PerfilAcesso();
	}
	
	public void removerPerfil() {
		if (Numero.isMaiorZero(itemLinha)) {
			usuario.getPerfisAcesso().remove(itemLinha);
			itemLinha = 0;
		}
	}
	
	public void excluir() {
		usuarios.remover(usuarioSelecionado);
		usuariosFiltrados.remove(usuarioSelecionado);
		
		FacesUtil.addInfoMessage("Usuário" + usuarioSelecionado.getLogin()
				+ "excluído com sucesso.");
	}

	public void pesquisar() {
		usuariosFiltrados = usuarios.filtrados(filtro, getEmpresaLogada());
	}

	public List<Usuario> getUsuariosFiltrados() {
		return usuariosFiltrados;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}
	
	public void setFiltro(ConsultaFilter filtro) {
		this.filtro = filtro;
	}

	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}
	
	public Usuario getUsuario(){
		return usuario;
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Usuario> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Usuario> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Usuario> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Usuario> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public List<PerfilAcesso> getListaPerfis() {
		return listaPerfis;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public int getItemLinha() {
		return itemLinha;
	}

	public void setItemLinha(int itemLinha) {
		this.itemLinha = itemLinha;
	}

	public boolean isAlterarSenha() {
		return alterarSenha;
	}

	public void setAlterarSenha(boolean alterarSenha) {
		this.alterarSenha = alterarSenha;
	}

	public boolean isBloquearAlteracaoUsuario() {
		return bloquearAlteracaoUsuario;
	}

	public void setBloquearAlteracaoUsuario(boolean bloquearAlteracaoUsuario) {
		this.bloquearAlteracaoUsuario = bloquearAlteracaoUsuario;
	}

	public boolean isLimitarCadastro() {
		return limitarCadastro;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			Usuario itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Usuario>) rGeral.porChaveRegistro(Usuario.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.usuario = itemEdicao;
			setAlterarSenha(false);
			setBloquearAlteracaoUsuario(true);
			limitarCadastro = false;
		}
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.usuario.getId() != null;
	}
	

}
