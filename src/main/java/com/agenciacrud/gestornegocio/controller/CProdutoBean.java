package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import com.agenciacrud.gestornegocio.model.Categoria;
import com.agenciacrud.gestornegocio.model.Produto;
import com.agenciacrud.gestornegocio.model.UnidadeMedida;
import com.agenciacrud.gestornegocio.repositorio.Categorias;
import com.agenciacrud.gestornegocio.repositorio.Produtos;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ProdutoFilter;
import com.agenciacrud.gestornegocio.service.CadastroProdutoService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CProdutoBean")
@ViewScoped
public class CProdutoBean extends CGeral implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Inject //chama método produtor
	private Categorias categorias;
	
	@Inject
	private CadastroProdutoService cadastroProdutoService;
	
	@Inject
	private RGeral rGeral;
	
	private List<Produto> buscaUltimoRegistro;
	
	private Produto produto;
	
	@NotNull
	private Categoria categoriaPai;	
	
	private List<Categoria> categoriasRaizes;
	private List<Categoria> subcategorias;
	
	@Inject
	private Produtos produtos;
	
	private ProdutoFilter filtro;
	private List<Produto> produtosFiltrados;
	private List<UnidadeMedida> listaUnidadesMedida;
	
	private Produto produtoSelecionado;
	
	private Long chaveRegistroEdicao;
	private List<Produto> buscaRegistroEdicao;
	
	
	public CProdutoBean(){
		filtro = new ProdutoFilter();
		limpar();
	}
	
	public void inicializar() {
		System.out.println("Inicializando...");
		
		if(FacesUtil.isNotPostback()){
			categoriasRaizes = categorias.raizes();
			listaUnidadesMedida = rGeral.obterTodos(UnidadeMedida.class);
			
			if (this.categoriaPai != null) {
				carregarSubcategorias();
			}
		}
	}
	
	//passa valor para a método subcategoriasDe na classe Categorias dentro de repositorio
	public void carregarSubcategorias() {
		subcategorias = categorias.subcategoriasDe(categoriaPai);
	}
	
	private void limpar() {
		produto = new Produto();
		categoriaPai = null;
		subcategorias = new ArrayList<>();
	}
	
	public void salvar(){
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.produto.getId())){
			Produto chaveRegistro = null;
			
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<Produto>) rGeral.buscaUltimoRegistroPorEmpresa(Produto.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				produto.setChaveRegistro(new Long(1));  
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				produto.setChaveRegistro(codigo);
			}
			produto.setQuantidadeEstoque(new BigDecimal(0));
		}
		this.produto.setEmpresa(getEmpresaLogada());
		//vai até a classe de serviço verificar se já existe o login cadastrado, pois não pode ser igual
		this.produto = cadastroProdutoService.salvar(this.produto);
		limpar();
		
		FacesUtil.addInfoMessage("Produto salvo com sucesso");
	}
	
	public void excluir() {
		produtos.remover(produtoSelecionado);
		produtosFiltrados.remove(produtoSelecionado);
		
		FacesUtil.addInfoMessage("Produto  " + produtoSelecionado.getNome()
				+ "excluído com sucesso.");
	}

	public void pesquisar() {
		produtosFiltrados = produtos.filtrados(filtro, getEmpresaLogada());
	}

	public List<Produto> getProdutosFiltrados() {
		return produtosFiltrados;
	}
	
	public List<UnidadeMedida> getListaUnidadesMedida() {
		return listaUnidadesMedida;
	}

	public void setListaUnidadesMedida(List<UnidadeMedida> listaUnidadesMedida) {
		this.listaUnidadesMedida = listaUnidadesMedida;
	}

	public ProdutoFilter getFiltro() {
		return filtro;
	}

	public Produto getProdutoSelecionado() {
		return produtoSelecionado;
	}

	public void setProdutoSelecionado(Produto produtoSelecionado) {
		this.produtoSelecionado = produtoSelecionado;
	}
	
	public Produto getProduto(){
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public List<Categoria> getCategoriasRaizes() {
		return categoriasRaizes;
	}

	public Categoria getCategoriaPai() {
		return categoriaPai;
	}

	public void setCategoriaPai(Categoria categoriaPai) {
		this.categoriaPai = categoriaPai;
	}

	public List<Categoria> getSubcategorias() {
		return subcategorias;
	}
	
	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Produto> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Produto> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			Produto itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Produto>) rGeral.porChaveRegistro(Produto.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.produto = itemEdicao;			
		}
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.produto.getId() != null;
	}

	public List<Produto> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Produto> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

}
