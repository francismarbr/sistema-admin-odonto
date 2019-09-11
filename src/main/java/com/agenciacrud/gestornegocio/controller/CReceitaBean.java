package com.agenciacrud.gestornegocio.controller;



import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.model.ContaBancaria;
import com.agenciacrud.gestornegocio.model.Dentista;
import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.ParcelaCancelamento;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.Receita;
import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoMovimentacao;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoOperacao;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.Receitas;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.ContaBancariaService;
import com.agenciacrud.gestornegocio.service.ReceitaService;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CReceitaBean")
@ViewScoped
public class CReceitaBean extends CGeral implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private ReceitaService receitaService;
	
	@Inject
	private Receitas receitas;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private ContaBancariaService contaBancariaService;
	
	private Receita receita;
	private ContaBancaria contaBancaria;
	
	private List<Receita> listaReceitas;
	private List<Receita> buscaUltimoRegistro;
	private List<MovimentacaoConta> listaRecebimentos;
	private List<ParcelaCancelamento> listaCancelamentos;
	private List<ContaBancaria> listaContasBancarias;
	private List<CategoriaConta> listaCategoriaContas;
	private List<Receita> receitasFiltradas;
	private List<Dentista> listaDentistas;
	
	private BigDecimal valorParcela;
	private boolean calculoParcela;
	private boolean totalMaiorZero;
	private boolean receitaQuitada;
	
	private ConsultaFilter filtro;
	
	private Receita receitaSelecionada;
	
	private Long chaveRegistroEdicao;
	private List<Receita> buscaRegistroEdicao;
	
	public CReceitaBean() {
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()) {
			this.listaDentistas = this.rGeral.buscaDentistas(Dentista.class, getEmpresaLogada());
			this.listaReceitas = this.rGeral.buscaTodos(Receita.class, getEmpresaLogada());
			this.listaContasBancarias = this.rGeral.buscaTodos(ContaBancaria.class, getEmpresaLogada());
			this.listaCategoriaContas = this.rGeral.buscaTodos(CategoriaConta.class, getEmpresaLogada());
			
			if (Numero.isMaiorZero(chaveRegistroEdicao)) {
				setExcluivel(true);
			}
		}
		
	}
	
	private void limpar() {
		receita = new Receita();
		contaBancaria = new ContaBancaria();
		listaContasBancarias = null;
		listaCategoriaContas = null;
		listaReceitas = null;
		setTotalMaiorZero(false);
		setExcluivel(false);
		filtro = new ConsultaFilter();
		receitasFiltradas = new ArrayList<>();
		receita.setDataLancamento(new Date());
		setReceitaQuitada(false);
	}
	
	public void salvar() {
		if(!Numero.isMaiorZero(receita.getValorTotal())) {
			FacesUtil.addErrorMessage("O campo valor total deve ser maior que zero!");
		} else if(receita.getDesconto().compareTo(receita.getValorTotal()) == 1) {
			FacesUtil.addErrorMessage("O valor do desconto não pode ser maior que o valor total!");
		} else {
			//se tiver quitada gera uma movimentação do tipo recebimento
			if (isReceitaQuitada()) {
				MovimentacaoConta mov = new MovimentacaoConta();
				Parcela parcela = receita.getParcelas().get(0);
				mov.setPessoa(receita.getCliente());
				mov.setParcela(parcela);
				mov.setDataLancamento(new Date());
				mov.setValorMovimentado(parcela.getValor());
				mov.setConta(getContaBancaria());
				mov.setTipoMovimentacao(TipoMovimentacao.RECEBIMENTO);
				mov.setTipoOperacao(TipoOperacao.CREDITO);
				mov.setSaldo(getContaBancaria().getSaldo().add(mov.getValorMovimentado()));
				mov.setEmpresa(getEmpresaLogada());
				mov.getConta().setSaldo(getContaBancaria().getSaldo().add(mov.getValorMovimentado()));
				
				parcela.getRecebimentos().add(mov);
				parcela.setSituacao(SituacaoPagamentoEnumerador.QUITADA);
				
				//atualiza o saldo da conta				
				contaBancariaService.salvar(contaBancaria);
			}
			//geração de sequência para chave de registro
			if(!Numero.isMaiorZero(this.receita.getId())){
				Receita chaveRegistro = null;
				
				//Recebe em uma lista o último registro da empresa
				setBuscaUltimoRegistro((List<Receita>) rGeral.buscaUltimoRegistroPorEmpresa(Receita.class, getEmpresaLogada()));
				
				if (getBuscaUltimoRegistro()==null) {
					//se a lista for vazia, soma 1 ao novo objeto
					receita.setChaveRegistro(new Long(1));  
				} else {
					//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
					chaveRegistro = buscaUltimoRegistro.get(0);
					Long codigo = (Long) chaveRegistro.getChaveRegistro();
					codigo += 1;
					receita.setChaveRegistro(codigo);
				}
			}
			receita.setEmpresa(getEmpresaLogada());
			this.receita = this.receitaService.salvar(this.receita);
			limpar();
			FacesUtil.addInfoMessage("Receita salva com sucesso!");
		}
		
	}
	
	//busca pessoas por nome e atualiza o autocomplete
	public List<Pessoa> completarCliente(String nome) {
		return this.rGeral.porNome(Pessoa.class, nome, getEmpresaLogada());
	}
	
	public void pesquisar() {
		receitasFiltradas = receitas.filtrados(filtro, getEmpresaLogada());
	}
	
	public void excluir() {
		receitas.remover(receitaSelecionada);
		pesquisar();
		FacesUtil.addInfoMessage("A Receita de código " + receitaSelecionada.getChaveRegistro()
				+ " foi excluída com sucesso.");
	}
	
	//
	public void ativarReceitaQuitada() {
		receita.setQuantidadeParcelas(new Integer(1));
		gerarParcelas();
	}
	
	//gera parcelas e a quantidade de linhas na view Cadastro Receita
	public void gerarParcelas() {
	
		if(!Numero.isMaiorZero(receita.getValorTotal())) {
			//FacesUtil.addErrorMessage("O campo valor total deve ser maior que zero!");
			return;
		}else if (receita.getDataVencimento() != null
				&& Numero.isMaiorZero(receita.getQuantidadeParcelas())) {
			setCalculoParcela(true);

			Date dataVencimento = receita.getDataVencimento();
			int qtParcelas = receita.getQuantidadeParcelas();
			
			setValorParcela((receita.getValorTotal().subtract(receita.getDesconto())).divide(new BigDecimal(qtParcelas), 2, RoundingMode.HALF_UP));
			
			//se não existir id de receita,ou seja, não foi criada ainda
			if (!Numero.isMaiorZero(receita.getId())) {
				receita.setParcelas(null);
				Parcela parcela = null;
				for (int nrParcela = 1; nrParcela <= qtParcelas; nrParcela++) {
					parcela = new Parcela();
					if (nrParcela > 1) {
						//aumenta um mês na data de vencimento
						dataVencimento = DataUtil.addMeses(receita.getDataVencimento(), (nrParcela - 1));
					}

					parcela.setNumero(nrParcela);
					parcela.setDataVencimento(dataVencimento);
					parcela.setSituacao(SituacaoPagamentoEnumerador.ABERTA);
					parcela.setValor(valorParcela);
					parcela.setReceita(receita);
					receita.getParcelas().add(parcela);

				}
			} else {
				for (Parcela parcela : receita.getParcelas()) {
					if (qtParcelas > 1) {
						dataVencimento = DataUtil.addMeses(receita.getDataVencimento(), (parcela.getNumero() - 1));
					}
					parcela.setDataVencimento(dataVencimento);
					parcela.setValor(valorParcela);
				}
			}

		}
	}
	
	//busca todas as parcelas recebidas para a receita selecionada dentro de movimentacao
	private void carregarRecebimentos() {
		listaRecebimentos  = new ArrayList<MovimentacaoConta>();
		if(Numero.isMaiorZero(receita.getId())) {
			for (Parcela p : receita.getParcelas()) {
				for(MovimentacaoConta recebimento : p.getRecebimentos()) {
					listaRecebimentos.add(recebimento);
				}
			}
		}
	}
	
	//verifica se existe parcela cancelada para a receita, caso sim adiciona na lista
	private void carregarCancelamentos() {
		listaCancelamentos  = new ArrayList<ParcelaCancelamento>();
		if(Numero.isMaiorZero(receita.getId())) {
			for (Parcela p : receita.getParcelas()) {
				
				if(p.getCancelamento() != null) {
					listaCancelamentos.add(p.getCancelamento());
					
				}
			}
		}
	}

	public Receita getReceita() {
		return receita;
	}

	public void setReceita(Receita receita) {
		this.receita = receita;
	}

	public ContaBancaria getContaBancaria() {
		return contaBancaria;
	}

	public void setContaBancaria(ContaBancaria contaBancaria) {
		this.contaBancaria = contaBancaria;
	}

	public List<Receita> getListaReceitas() {
		return listaReceitas;
	}

	public void setListaReceitas(List<Receita> listaReceitas) {
		this.listaReceitas = listaReceitas;
	}
		
	public List<ContaBancaria> getListaContasBancarias() {
		return listaContasBancarias;
	}

	public void setListaContasBancarias(List<ContaBancaria> listaContasBancarias) {
		this.listaContasBancarias = listaContasBancarias;
	}

	public List<CategoriaConta> getListaCategoriaContas() {
		return listaCategoriaContas;
	}

	public void setListaCategoriaContas(List<CategoriaConta> listaCategoriaContas) {
		this.listaCategoriaContas = listaCategoriaContas;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Receita> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Receita> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			Receita itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Receita>) rGeral.porChaveRegistro(Receita.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.receita = itemEdicao;
			carregarRecebimentos();
			carregarCancelamentos();
		}
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.receita.getId() != null;
	}

	public boolean isCalculoParcela() {
		return calculoParcela;
	}

	public void setCalculoParcela(boolean calculoParcela) {
		this.calculoParcela = calculoParcela;
	}

	public boolean isTotalMaiorZero() {
		return totalMaiorZero;
	}

	public void setTotalMaiorZero(boolean totalMaiorZero) {
		this.totalMaiorZero = totalMaiorZero;
	}

	public List<Receita> getReceitasFiltradas() {
		return receitasFiltradas;
	}

	public void setReceitasFiltradas(List<Receita> receitasFiltradas) {
		this.receitasFiltradas = receitasFiltradas;
	}

	public List<Dentista> getListaDentistas() {
		return listaDentistas;
	}

	public void setListaDentistas(List<Dentista> listaDentistas) {
		this.listaDentistas = listaDentistas;
	}

	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(ConsultaFilter filtro) {
		this.filtro = filtro;
	}

	public Receita getReceitaSelecionada() {
		return receitaSelecionada;
	}

	public void setReceitaSelecionada(Receita receitaSelecionada) {
		this.receitaSelecionada = receitaSelecionada;
	}

	public List<MovimentacaoConta> getListaRecebimentos() {
		return listaRecebimentos;
	}

	public void setListaRecebimentos(List<MovimentacaoConta> listaRecebimentos) {
		this.listaRecebimentos = listaRecebimentos;
	}

	public List<ParcelaCancelamento> getListaCancelamentos() {
		return listaCancelamentos;
	}

	public void setListaCancelamentos(List<ParcelaCancelamento> listaCancelamentos) {
		this.listaCancelamentos = listaCancelamentos;
	}

	public boolean isReceitaQuitada() {
		return receitaQuitada;
	}

	public void setReceitaQuitada(boolean receitaQuitada) {
		this.receitaQuitada = receitaQuitada;
	}

	public List<Receita> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Receita> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}
	
	

	
}

