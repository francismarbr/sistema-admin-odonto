package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.model.Cidade;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.ItemPerfilAcesso;
import com.agenciacrud.gestornegocio.model.PerfilAcesso;
import com.agenciacrud.gestornegocio.model.Permissao;
import com.agenciacrud.gestornegocio.model.Receita;
import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.model.Socio;
import com.agenciacrud.gestornegocio.model.Usuario;
import com.agenciacrud.gestornegocio.model.enumeradores.PlanoEmpresa;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoCategoriaContaEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoPessoaEnumerador;
import com.agenciacrud.gestornegocio.repositorio.Cidades;
import com.agenciacrud.gestornegocio.repositorio.Empresas;
import com.agenciacrud.gestornegocio.repositorio.Permissoes;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.Usuarios;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.seguranca.UsuarioSistema;
import com.agenciacrud.gestornegocio.service.CadastroUsuarioService;
import com.agenciacrud.gestornegocio.service.CategoriaContaService;
import com.agenciacrud.gestornegocio.service.EmpresaService;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.service.PerfilAcessoService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;
import com.sun.faces.util.Util;


@Named("CEmpresaBean")
@ViewScoped
public class CEmpresaBean extends CGeral implements Serializable  {
	

	private static final long serialVersionUID = 1L;

	private Empresa empresa;
	
	@Inject
	private Empresas empresas;
	
	@Inject
	private Cidades cidades;
	
	@Inject
	private Usuarios usuarios;
	
	private Socio socio;
		
	@Inject
	private EmpresaService empresaService;

	@Inject
	private CadastroUsuarioService usuarioService;
	
	@Inject
	private CategoriaContaService categoriaContaService;
	
	@Inject
	private PerfilAcessoService perfilAcessoService;
	
	@Inject
	private RGeral rGeral;
		
	private ConsultaFilter filtro;
	private List<Empresa> empresasFiltradas;
	private List<Cidade> listaCidades;
	
	private List<RegimeTributario> listaRegimesTributarios;

	private Empresa empresaSelecionada;
	
	private Usuario usuario;
	
	private Socio socioSelecionado;
	
	private Long chaveRegistroEdicao;
	private List<Empresa> buscaRegistroEdicao;
	
	private boolean verSenha = false;
	private boolean empresaCriada = false;

	public CEmpresaBean(){
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()){
			//setListaRegimesTributarios(rGeral.buscaTodos(RegimeTributario.class, getEmpresa()));
			if (Numero.isMaiorZero(empresa.getId())) {
				//setExcluivel(true);
			}
		}		
	}
	
	public void limpar() {
		empresa = new Empresa();
		socio = new Socio();
		filtro = new ConsultaFilter();
		usuario = new Usuario();
		empresa.setTipo(TipoPessoaEnumerador.FISICA);
	}
	
	public String novoEmpresa() {
		empresa = new Empresa();
		return "FormEmpresa?faces-redirect=true";
	}
	
	//este plano é chamado ao iniciar página de cadastro com plano inicial
	public void planoInicial() {
		empresa.setPlano(PlanoEmpresa.INICIAL);
	}
	
	//este plano é chamado ao iniciar página de cadastro com plano mega
		public void planoMega() {
			empresa.setPlano(PlanoEmpresa.MEGA);
		}
	
	public void salvar() {
		empresa.setChaveRegistro(new Long(1)); 
		this.empresa = empresaService.salvar(this.empresa);
		FacesUtil.addInfoMessage("Registro salvo com sucesso");
	}
	
	
	public void criarEmpresa() {
		if(usuario.getLogin()!=null && empresa.getNomeFantasia()!=null && empresa.getPlano()!=null
				 && empresa.getTelefone1()!=null && usuario.getEmail()!=null && 
				usuario.getSenha()!=null) {
		Usuario loginExistente = usuarios.porLogin(usuario.getLogin());
		
		
		if(loginExistente !=null && !loginExistente.equals(usuario)) {
			FacesUtil.addErrorMessage("O nome de usuário que você escolheu já existe, escolha outro, por favor!");
		} else {
			empresa.setTipo(TipoPessoaEnumerador.FISICA);
			empresa.setNome(empresa.getNomeFantasia());
			empresa.setDataCadastro(new Date());
			empresa.setChaveRegistro(new Long(1));
			empresa.setAtiva(true);
			empresa.setEmail(usuario.getEmail());
			this.empresa = empresaService.salvar(this.empresa);
			
			List<Permissao> permissoes = rGeral.obterTodos(Permissao.class);
			PerfilAcesso obj = new PerfilAcesso();
			obj.setNome("Administrador");
			obj.setEmpresa(empresa);
			for (Permissao permissao : permissoes){
				ItemPerfilAcesso item = new ItemPerfilAcesso();
				item.setPerfilAcesso(obj);
				item.setPermissao(permissao);
				obj.getPermissoes().add(item);			
			}
			obj.setChaveRegistro(new Long(1));
			obj = perfilAcessoService.salvar(obj);
			usuario.getPerfisAcesso().add(obj);
			usuario.setAtivo(true);
			//gerarPerfilAcesso();
			usuario.setChaveRegistro(new Long(1));
			usuario.setEmpresa(empresa);
			usuario.setNome(empresa.getNomeFantasia());
			
			this.usuario = usuarioService.salvar(this.usuario);
			
			gerarPlanoContas();
					
			FacesUtil.addInfoMessage("Empresa criada com sucesso");
			setEmpresaCriada(true);
		}
		} else {
			FacesUtil.addErrorMessage("Preencha todos os campos");
		}
		//return "login?faces-redirect=true";
	}
	
	public void adicionarSocio() {
		if (socio != null) {
			empresa.getSocios().add(socio);
			socio.setSocioNa(empresa);
			socio = null;
		}
	}
	
	public void removerSocio() {
		if (socio != null) {
			empresa.getSocios().remove(socio);
			socio = null;
		}
	}
	
	public void excluir() {
		empresas.remover(empresaSelecionada);
		
		FacesUtil.addInfoMessage("Empresa " + empresaSelecionada.getNome()
				+ " excluído com sucesso.");
	}
	
	public void pesquisar() {
		empresasFiltradas = empresas.empLogada(getEmpresaLogada().getId());
	}
	
	public void buscarTodas() {
		empresasFiltradas = empresas.filtrados(filtro);
	}
	
	public void obterCidades() {
		//busca todas cidades por uf selecionada na view
		listaCidades = cidades.cidadesPorUf(filtro);
	}
	
	public Empresa getEmpresa(){
		return empresa;
	}
	
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public List<Empresa> getEmpresasFiltradas() {
		return empresasFiltradas;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public Empresa getEmpresaSelecionada() {
		return empresaSelecionada;
	}

	public void setEmpresaSelecionada(Empresa empresaSelecionada) {
		this.empresaSelecionada = empresaSelecionada;
	}
	
	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Empresa> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Empresa> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isVerSenha() {
		return verSenha;
	}

	public void setVerSenha(boolean verSenha) {
		this.verSenha = verSenha;
	}

	public boolean isEmpresaCriada() {
		return empresaCriada;
	}

	public void setEmpresaCriada(boolean empresaCriada) {
		this.empresaCriada = empresaCriada;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			Empresa itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Empresa>) rGeral.empresaPrinciaplPorChaveRegistro(
					Empresa.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.empresa = itemEdicao;
			setEmpresaCriada(true);
			if(empresa.getEndereco().getCidade()!=null) {
				filtro.setUf(empresa.getEndereco().getCidade().getUf());
				this.listaCidades = this.cidades.cidadesPorUf(filtro);
			}
		}
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.empresa.getId() != null;
	}
	
	public boolean isEditandoEmpresaCliente() {
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.empresa.getId() != null;
	}

	public Socio getSocio() {
		return socio;
	}

	public void setSocio(Socio socio) {
		this.socio = socio;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Socio getSocioSelecionado() {
		return socioSelecionado;
	}

	public void setSocioSelecionado(Socio socioSelecionado) {
		this.socioSelecionado = socioSelecionado;
	}

	public List<Cidade> getListaCidades() {
		return listaCidades;
	}

	public void setListaCidades(List<Cidade> listaCidades) {
		this.listaCidades = listaCidades;
	}

	public List<RegimeTributario> getListaRegimesTributarios() {
		return listaRegimesTributarios;
	}

	public void setListaRegimesTributarios(List<RegimeTributario> listaRegimesTributarios) {
		this.listaRegimesTributarios = listaRegimesTributarios;
	}

	//Lista os tipos de pessoa(Física ou Jurídica)
	public List<SelectItem> getComboTipoPessoa() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		TipoPessoaEnumerador tipos[] = TipoPessoaEnumerador.values();
		for(int i = 0; i < tipos.length; i++) {
			itens.add(new SelectItem(tipos[i]));
		}
		return itens;
	}
		
	//Combo Plano da empresa
	public List<SelectItem> getComboPlanoEmpresa(){
		List<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(PlanoEmpresa.INICIAL));
		itens.add(new SelectItem(PlanoEmpresa.MEGA));
		return itens;
	}
	
	
	//Método para gerar automaticamente plano de contas na criação de uma nova empresa
		private void gerarPlanoContas() {
			CategoriaConta obj = new CategoriaConta();
			obj.setEmpresa(empresa);
			obj.setNome("RECEITAS");
			obj.setChaveRegistro(new Long(1));
			obj.setPadrao(true);
			obj.setTipo(TipoCategoriaContaEnumerador.ENTRADA);
			obj = categoriaContaService.salvar(obj);
			
				CategoriaConta subCategoriaNivel1 = new CategoriaConta();
				subCategoriaNivel1.setEmpresa(empresa);
				subCategoriaNivel1.setNome("Tratamentos");
				subCategoriaNivel1.setChaveRegistro(new Long(2));
				subCategoriaNivel1.setCategoriaSuperior(obj);
				subCategoriaNivel1.setPadrao(true);
				subCategoriaNivel1.setTipo(TipoCategoriaContaEnumerador.ENTRADA);
				subCategoriaNivel1 = categoriaContaService.salvar(subCategoriaNivel1);
				empresa.setCategoriaConta(subCategoriaNivel1);
				this.empresa = empresaService.salvar(this.empresa);
						
			obj = new CategoriaConta();
			obj.setEmpresa(empresa);
			obj.setNome("DESPESAS");
			obj.setChaveRegistro(new Long(3));
			obj.setPadrao(true);
			obj.setTipo(TipoCategoriaContaEnumerador.SAIDA);
			obj = categoriaContaService.salvar(obj);
			
				subCategoriaNivel1 = new CategoriaConta();
				subCategoriaNivel1.setEmpresa(empresa);
				subCategoriaNivel1.setNome("Energia");
				subCategoriaNivel1.setChaveRegistro(new Long(4));
				subCategoriaNivel1.setCategoriaSuperior(obj);
				subCategoriaNivel1.setTipo(TipoCategoriaContaEnumerador.SAIDA);
				categoriaContaService.salvar(subCategoriaNivel1);
		}
		

}
