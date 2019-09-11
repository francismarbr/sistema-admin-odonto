package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.agenciacrud.gestornegocio.model.Fornecedor;
import com.agenciacrud.gestornegocio.model.MovimentacaoEstoque;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.Produto;
import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoCategoriaContaEnumerador;
import com.agenciacrud.gestornegocio.repositorio.Movimentacoes;
import com.agenciacrud.gestornegocio.repositorio.MovimentacoesProdutos;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.service.MovimentacaoEstoqueService;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.service.ProdutoService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CMovimentacaoEstoqueBean")
@ViewScoped
public class CMovimentacaoEstoqueBean  extends CGeral implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private MovimentacaoEstoqueService movimentacaoEstoqueService;
	
	@Inject
	private ProdutoService produtoService;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private MovimentacoesProdutos movimentacoes;
	
	private MovimentacaoEstoque itemProdutoMovimentado;
	
	private Produto produto;
	private Fornecedor fornecedor;
	
	private List<MovimentacaoEstoque> listaItensComprados;
	private List<MovimentacaoEstoque> listaMovimentacoes;
	private List<MovimentacaoEstoque> buscaUltimoRegistro;
	
	private Date dataInicialMovimentacao;
	private Date dataFinalMovimentacao;

	public CMovimentacaoEstoqueBean() {
		itemProdutoMovimentado = new MovimentacaoEstoque();
		limpar();
	}
	
	public void inicializar() {
		pesquisar();
	}
	
	public void limpar(){
		//setListaItensComprados(new ArrayList<MovimentacaoEstoque>());
		fornecedor = new Fornecedor();
	}
	
	
	public MovimentacaoEstoque getItemProdutoMovimentado() {
		return itemProdutoMovimentado;
	}


	public void setItemProdutoMovimentado(MovimentacaoEstoque itemProdutoMovimentado) {
		this.itemProdutoMovimentado = itemProdutoMovimentado;
	}


/*	
 * Salva entrada
 * 
 * public void salvar() {
		if(!(getListaItensComprados().size()==0)) {
			for(MovimentacaoEstoque movProduto : getListaItensComprados()) {
				
				itemProdutoMovimentado = new MovimentacaoEstoque();
				
				itemProdutoMovimentado.setDataLancamento(new Date());
				itemProdutoMovimentado.setProduto(movProduto.getProduto());
				itemProdutoMovimentado.setEstoqueAnterior(movProduto.getProduto().getQuantidadeEstoque());
				itemProdutoMovimentado.setQuantidade(movProduto.getQuantidade());
				itemProdutoMovimentado.setEstoquePosterior(movProduto.getProduto().getQuantidadeEstoque().add(movProduto.getQuantidade()));
				itemProdutoMovimentado.setDataCompra(movProduto.getDataCompra());
				itemProdutoMovimentado.setFornecedor(movProduto.getFornecedor());
				itemProdutoMovimentado.setValorUnitario(movProduto.getValorUnitario());
				itemProdutoMovimentado.setTipoMovimentacao(TipoCategoriaContaEnumerador.ENTRADA);
				itemProdutoMovimentado.setEmpresa(getEmpresaLogada());
				
				//geração de sequência para chave de registro
				if(!Numero.isMaiorZero(this.itemProdutoMovimentado.getId())){
					MovimentacaoEstoque chaveRegistro = null;
					//Recebe em uma lista o último registro da empresa
					setBuscaUltimoRegistro((List<MovimentacaoEstoque>) rGeral.buscaUltimoRegistroPorEmpresa(MovimentacaoEstoque.class, getEmpresaLogada()));
					
					if (getBuscaUltimoRegistro()==null) {
						//se a lista for vazia, soma 1 ao novo objeto
						itemProdutoMovimentado.setChaveRegistro(new Long(1)); 
					} else {
						//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
						chaveRegistro = buscaUltimoRegistro.get(0);
						Long codigo = (Long) chaveRegistro.getChaveRegistro();
						codigo += 1;
						itemProdutoMovimentado.setChaveRegistro(codigo);
					}
				}
				
				movimentacaoEstoqueService.salvar(itemProdutoMovimentado);
				
				Produto produto = movProduto.getProduto();
				if(movProduto.getDataCompra() != null)
					produto.setDataUltimaCompra(movProduto.getDataCompra());
				produto.adicionarEstoque(movProduto.getQuantidade());
				
				produtoService.salvar(produto);
			}
			FacesUtil.addInfoMessage("Movimentação criada com sucesso!");
		} else {
			FacesUtil.addErrorMessage("Nenhum item foi adicionado para entrada!");
		}
	}*/
	
	public void salvar() {
		if(itemProdutoMovimentado != null) {
			if(!Numero.isMaiorZero(itemProdutoMovimentado.getValorUnitario()) || !Numero.isMaiorZero(itemProdutoMovimentado.getQuantidade()) ) {
				throw new NegocioException("O campo valor unitário e quantidade devem ter valor maior que zero.");
			}
			if(Numero.isMaiorZero(itemProdutoMovimentado.getQuantidade())){
				
				itemProdutoMovimentado.setDataLancamento(new Date());
				itemProdutoMovimentado.setEstoqueAnterior(itemProdutoMovimentado.getProduto().getQuantidadeEstoque());
				itemProdutoMovimentado.setEstoquePosterior(itemProdutoMovimentado.getProduto().getQuantidadeEstoque().add(itemProdutoMovimentado.getQuantidade()));
				itemProdutoMovimentado.setTipoMovimentacao(TipoCategoriaContaEnumerador.ENTRADA);
				itemProdutoMovimentado.setEmpresa(getEmpresaLogada());
				
				//geração de sequência para chave de registro
				if(!Numero.isMaiorZero(this.itemProdutoMovimentado.getId())){
					MovimentacaoEstoque chaveRegistro = null;
					//Recebe em uma lista o último registro da empresa
					setBuscaUltimoRegistro((List<MovimentacaoEstoque>) rGeral.buscaUltimoRegistroPorEmpresa(MovimentacaoEstoque.class, getEmpresaLogada()));
					
					if (getBuscaUltimoRegistro()==null) {
						//se a lista for vazia, soma 1 ao novo objeto
						itemProdutoMovimentado.setChaveRegistro(new Long(1)); 
					} else {
						//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
						chaveRegistro = buscaUltimoRegistro.get(0);
						Long codigo = (Long) chaveRegistro.getChaveRegistro();
						codigo += 1;
						itemProdutoMovimentado.setChaveRegistro(codigo);
					}
				}
				//gera movimentação de estoque
				movimentacaoEstoqueService.salvar(itemProdutoMovimentado);
				
				//atualiza valores do objeto produto
				Produto produto = itemProdutoMovimentado.getProduto();
				if(itemProdutoMovimentado.getDataCompra() != null)
					produto.setDataUltimaCompra(itemProdutoMovimentado.getDataCompra());
				produto.adicionarEstoque(itemProdutoMovimentado.getQuantidade());
				
				//salva o produto atualizado
				produtoService.salvar(produto);

				itemProdutoMovimentado = new MovimentacaoEstoque();
				FacesUtil.addInfoMessage("Movimentação criada com sucesso!");
			} else {
				FacesUtil.addErrorMessage("A quantidade deve ser maior do que zero ");
			}
		}
	}
	
	public void lancarSaida() {
		if(itemProdutoMovimentado.getProduto() == null || itemProdutoMovimentado.getQuantidade() == null || itemProdutoMovimentado.getObservacao() == "") {
			FacesUtil.addErrorMessage("Preencha todos os campos para concretizar a saída!");
		} else{
			BigDecimal novaQuantidadeEstoque = new BigDecimal(0);
			novaQuantidadeEstoque = itemProdutoMovimentado.getProduto().getQuantidadeEstoque().subtract(itemProdutoMovimentado.getQuantidade());
			
			/*verifica se existe qtde suficiente no estoque para realizar a baixa. 
			 * Se o estoque for ficar negativo após a baixa, significa  que a qtd a ser
			 * baixada é maior do que a quantidade em estoque		 * 
			 */
			if(novaQuantidadeEstoque.compareTo(new BigDecimal(0)) < 0) {
				FacesUtil.addErrorMessage("Não há quantidade em estoque suficiente para realizar esta saída.");
			} else {
				itemProdutoMovimentado.setDataLancamento(new Date());
				itemProdutoMovimentado.setEstoqueAnterior(itemProdutoMovimentado.getProduto().getQuantidadeEstoque());
				itemProdutoMovimentado.setEstoquePosterior(itemProdutoMovimentado.getProduto().getQuantidadeEstoque().subtract(itemProdutoMovimentado.getQuantidade()));
				itemProdutoMovimentado.setTipoMovimentacao(TipoCategoriaContaEnumerador.SAIDA);
				itemProdutoMovimentado.setEmpresa(getEmpresaLogada());
				
				//geração de sequência para chave de registro
				if(!Numero.isMaiorZero(this.itemProdutoMovimentado.getId())){
					MovimentacaoEstoque chaveRegistro = null;
					//Recebe em uma lista o último registro da empresa
					setBuscaUltimoRegistro((List<MovimentacaoEstoque>) rGeral.buscaUltimoRegistroPorEmpresa(MovimentacaoEstoque.class, getEmpresaLogada()));
					
					if (getBuscaUltimoRegistro()==null) {
						//se a lista for vazia, soma 1 ao novo objeto
						itemProdutoMovimentado.setChaveRegistro(new Long(1)); 
					} else {
						//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
						chaveRegistro = buscaUltimoRegistro.get(0);
						Long codigo = (Long) chaveRegistro.getChaveRegistro();
						codigo += 1;
						itemProdutoMovimentado.setChaveRegistro(codigo);
					}
				}
				
				movimentacaoEstoqueService.salvar(itemProdutoMovimentado);
				
				Produto produto = itemProdutoMovimentado.getProduto();
				produto.baixarEstoque(itemProdutoMovimentado.getQuantidade());
				
				produtoService.salvar(produto);
				
				FacesUtil.addInfoMessage("Movimentação de saída realizada com sucesso!");
			}
		}
	}
	
	//busca produtos por nome e atualiza o autocomplete
	public List<Produto> completarProduto(String nome) {
		return this.rGeral.porNome(Produto.class, nome, getEmpresaLogada());
	}
	
	//busca produtos por nome e atualiza o autocomplete
	public List<Fornecedor> completarFornecedor(String nome) {
		return this.rGeral.porNome(Fornecedor.class, nome, getEmpresaLogada());
	}
	
	public void adicionarItemLista() {
		if(itemProdutoMovimentado != null) {
			if(!Numero.isMaiorZero(itemProdutoMovimentado.getValorUnitario()) || !Numero.isMaiorZero(itemProdutoMovimentado.getQuantidade()) ) {
				throw new NegocioException("O campo valor unitário e quantidade devem ter valor maior que zero.");
			}
			if(Numero.isMaiorZero(itemProdutoMovimentado.getQuantidade())){
				itemProdutoMovimentado.setFornecedor(getFornecedor());
				getListaItensComprados().add(itemProdutoMovimentado);
				itemProdutoMovimentado = new MovimentacaoEstoque();
				
			} else {
				FacesUtil.addErrorMessage("A quantidade deve ser maior do que zero ");
			}
		}
	}
	
	public void removerItemLista() {
		if(itemProdutoMovimentado!=null){
			listaItensComprados.remove(itemProdutoMovimentado);
			itemProdutoMovimentado = null;
		}
	}
	
	public void pesquisar() {
		listaMovimentacoes = null;
		listaMovimentacoes = movimentacoes.filtroMovimentacao(getEmpresaLogada(), dataInicialMovimentacao, dataFinalMovimentacao);
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public List<MovimentacaoEstoque> getListaItensComprados() {
		return listaItensComprados;
	}


	public void setListaItensComprados(List<MovimentacaoEstoque> listaItensComprados) {
		this.listaItensComprados = listaItensComprados;
	}

	public List<MovimentacaoEstoque> getListaMovimentacoes() {
		return listaMovimentacoes;
	}

	public void setListaMovimentacoes(List<MovimentacaoEstoque> listaMovimentacoes) {
		this.listaMovimentacoes = listaMovimentacoes;
	}

	public List<MovimentacaoEstoque> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<MovimentacaoEstoque> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

	public Date getDataInicialMovimentacao() {
		return dataInicialMovimentacao;
	}

	public void setDataInicialMovimentacao(Date dataInicialMovimentacao) {
		this.dataInicialMovimentacao = dataInicialMovimentacao;
	}

	public Date getDataFinalMovimentacao() {
		return dataFinalMovimentacao;
	}

	public void setDataFinalMovimentacao(Date dataFinalMovimentacao) {
		this.dataFinalMovimentacao = dataFinalMovimentacao;
	}
	
	
	
	
}
