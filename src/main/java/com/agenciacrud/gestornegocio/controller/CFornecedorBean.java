package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.Cidade;
import com.agenciacrud.gestornegocio.model.Fornecedor;
import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.model.Socio;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoPessoaEnumerador;
import com.agenciacrud.gestornegocio.repositorio.Cidades;
import com.agenciacrud.gestornegocio.repositorio.Fornecedores;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.FornecedorService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CFornecedorBean")
@ViewScoped
public class CFornecedorBean extends CGeral implements Serializable  {
	

	private static final long serialVersionUID = 1L;

	private Fornecedor fornecedor;
	
	@Inject
	private Fornecedores fornecedores;
	
	@Inject
	private Cidades cidades;
	
	@Inject
	private Socio socio;
	
	@Inject
	private FornecedorService fornecedorService;
	
	@Inject
	private RGeral rGeral;
		
	private ConsultaFilter filtro;
	private List<Fornecedor> fornecedoresFiltrados;
	private List<Cidade> listaCidades;
	
	private List<Fornecedor> buscaUltimoRegistro;
	private List<RegimeTributario> listaRegimesTributarios;
	
	private Long chaveRegistroEdicao;
	private List<Fornecedor> buscaRegistroEdicao;

	private Fornecedor fornecedorSelecionado;
	private Socio socioSelecionado;

	public CFornecedorBean(){
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()){
			setListaRegimesTributarios(rGeral.buscaTodos(RegimeTributario.class, getEmpresaLogada()));
			if (Numero.isMaiorZero(fornecedor.getId())) {
				setExcluivel(true);
			}
		}		
	}
	
	public void limpar() {
		fornecedor = new Fornecedor();
		filtro = new ConsultaFilter();
		listaRegimesTributarios = null;
		fornecedoresFiltrados = null;
		fornecedor.setTipo(TipoPessoaEnumerador.FISICA);
	}
	
	public String novoPessoa() {
		fornecedor = new Fornecedor();
		return "CadastroFornecedor?faces-redirect=true";
	}
	
	public void salvar() {
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.fornecedor.getId())){
			Fornecedor chaveRegistro = null;
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<Fornecedor>) rGeral.buscaUltimoRegistroPorEmpresa(Fornecedor.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				fornecedor.setChaveRegistro(new Long(1)); 
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				fornecedor.setChaveRegistro(codigo);
			}
		}
		this.fornecedor.setEmpresa(getEmpresaLogada());
		this.fornecedor = fornecedorService.salvar(this.fornecedor);
		limpar();
		
		FacesUtil.addInfoMessage("Registro salvo com sucesso");
	}
	
	public void adicionarSocio() {
		if (socio != null) {
			fornecedor.getSocios().add(socio);
			socio.setFornecedor(fornecedor);
			socio = null;
		}
	}
	
	public void removerSocio() {
		if (socio != null) {
			fornecedor.getSocios().remove(socio);
			socio = null;
		}
	}
	
	public void excluir() {
		fornecedores.remover(fornecedorSelecionado);
		fornecedoresFiltrados.remove(fornecedorSelecionado);
		
		FacesUtil.addInfoMessage("Pessoa " + fornecedorSelecionado.getNome()
				+ " excluído com sucesso.");
	}
	
	public void pesquisar() {
		fornecedoresFiltrados = fornecedores.filtrados(filtro, getEmpresaLogada());
	}
	
	public void obterCidades() {
		//busca todas cidades por uf selecionada na view
		listaCidades = cidades.cidadesPorUf(filtro);
	}
	
	public Fornecedor getFornecedor(){
		return fornecedor;
	}
	
	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	public List<Fornecedor> getFornecedoresFiltrados() {
		return fornecedoresFiltrados;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public Fornecedor getFornecedorSelecionado() {
		return fornecedorSelecionado;
	}

	public void setFornecedorSelecionado(Fornecedor fornecedorSelecionado) {
		this.fornecedorSelecionado = fornecedorSelecionado;
	}
	
	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Fornecedor> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Fornecedor> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			Fornecedor itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Fornecedor>) rGeral.porChaveRegistro(Fornecedor.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.fornecedor = itemEdicao;
			if(fornecedor.getEndereco().getCidade()!=null) {
				filtro.setUf(fornecedor.getEndereco().getCidade().getUf());
				this.listaCidades = this.cidades.cidadesPorUf(filtro);
			}
		}
		//retorna se banco não existir, retorna falso, caso contrário, verdadeiro.
		return this.fornecedor.getId() != null;
	}

	public Socio getSocio() {
		return socio;
	}

	public void setSocio(Socio socio) {
		this.socio = socio;
	}

	public Socio getSocioSelecionado() {
		return socioSelecionado;
	}

	public void setSocioSelecionado(Socio socioSelecionado) {
		this.socioSelecionado = socioSelecionado;
	}

	public List<Fornecedor> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Fornecedor> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
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
	
		

}
