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
import com.agenciacrud.gestornegocio.model.Despesa;
import com.agenciacrud.gestornegocio.model.Fornecedor;
import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.ParcelaCancelamento;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoMovimentacao;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoOperacao;
import com.agenciacrud.gestornegocio.repositorio.Despesas;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.ContaBancariaService;
import com.agenciacrud.gestornegocio.service.DespesaService;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CDespesaBean")
@ViewScoped
public class CDespesaBean extends CGeral implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private DespesaService despesaService;
	
	@Inject
	private Despesas despesas;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private ContaBancariaService contaBancariaService;
	
	private Despesa despesa;
	private ContaBancaria contaBancaria;
	
	private List<Despesa> listaDespesas;
	private List<Despesa> buscaUltimoRegistro;
	private List<MovimentacaoConta> listaPagamentos;
	private List<ParcelaCancelamento> listaCancelamentos;
	private List<ContaBancaria> listaContasBancarias;
	private List<CategoriaConta> listaCategoriaContas;
	private List<Despesa> despesasFiltradas;
	
	private BigDecimal valorParcela = BigDecimal.ZERO;
	private boolean calculoParcela;
	private boolean totalMaiorZero;
	private boolean despesaQuitada;
	
	private ConsultaFilter filtro;
	
	private Despesa despesaSelecionada;
	
	private Long chaveRegistroEdicao;
	private List<Despesa> buscaRegistroEdicao;
	
	public CDespesaBean() {
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()) {
			this.listaDespesas = this.rGeral.buscaTodos(Despesa.class, getEmpresaLogada());
			this.listaContasBancarias = this.rGeral.buscaTodos(ContaBancaria.class, getEmpresaLogada());
			this.listaCategoriaContas = this.rGeral.buscaTodos(CategoriaConta.class, getEmpresaLogada());
			
			if (Numero.isMaiorZero(chaveRegistroEdicao)) {
				setExcluivel(true);
			}
		}
		
	}
	
	private void limpar() {
		despesa = new Despesa();
		contaBancaria = new ContaBancaria();
		listaContasBancarias = null;
		listaCategoriaContas = null;
		listaDespesas = null;
		setTotalMaiorZero(false);
		setExcluivel(false);
		filtro = new ConsultaFilter();
		despesa.setDataLancamento(new Date());
		despesasFiltradas = new ArrayList<>();
		setDespesaQuitada(false);
	}
	
	public void salvar() {
		if(!Numero.isMaiorZero(despesa.getValorTotal())) {
			FacesUtil.addErrorMessage("O campo valor total deve ser maior que zero!");
		} else if(despesa.getDesconto().compareTo(despesa.getValorTotal()) == 1) {
			FacesUtil.addErrorMessage("O valor do desconto não pode ser maior que o valor total!");
		} else {
			//se tiver quitada gera uma movimentação do tipo pagamento
			if (isDespesaQuitada()) {

				MovimentacaoConta mov = new MovimentacaoConta();
				Parcela parcela = despesa.getParcelas().get(0);
				mov.setDescricao(despesa.getNome());
				mov.setFornecedor(despesa.getFornecedor());
				mov.setParcela(parcela);
				mov.setDataLancamento(new Date());
				mov.setValorMovimentado(parcela.getValor());
				mov.setConta(getContaBancaria());
				mov.setTipoMovimentacao(TipoMovimentacao.PAGAMENTO);
				mov.setTipoOperacao(TipoOperacao.DEBITO);
				mov.setSaldo(getContaBancaria().getSaldo().subtract(mov.getValorMovimentado()));
				mov.setEmpresa(getEmpresaLogada());
				mov.getConta().setSaldo(getContaBancaria().getSaldo().subtract(mov.getValorMovimentado()));
				
				parcela.getRecebimentos().add(mov);
				parcela.setSituacao(SituacaoPagamentoEnumerador.QUITADA);
				
				//atualiza Saldo da conta bancária
				//contaBancaria = mov.getConta();
				contaBancariaService.salvar(contaBancaria);
			}
			//geração de sequência para chave de registro
			if(!Numero.isMaiorZero(this.despesa.getId())){
				Despesa chaveRegistro = null;
				
				//Recebe em uma lista o último registro da empresa
				setBuscaUltimoRegistro((List<Despesa>) rGeral.buscaUltimoRegistroPorEmpresa(Despesa.class, getEmpresaLogada()));
				
				if (getBuscaUltimoRegistro()==null) {
					//se a lista for vazia, soma 1 ao novo objeto
					despesa.setChaveRegistro(new Long(1));  
				} else {
					//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
					chaveRegistro = buscaUltimoRegistro.get(0);
					Long codigo = (Long) chaveRegistro.getChaveRegistro();
					codigo += 1;
					despesa.setChaveRegistro(codigo);
				}
			}
			despesa.setEmpresa(getEmpresaLogada());
			this.despesa = this.despesaService.salvar(this.despesa);
			limpar();
			FacesUtil.addInfoMessage("Despesa salva com sucesso!");
		}
		
	}
	
	//busca pessoas por nome e atualiza o autocomplete
	public List<Fornecedor> completarFornecedor(String nome) {
		return this.rGeral.porNome(Fornecedor.class, nome, getEmpresaLogada());
	}
	
	public void pesquisar() {
		despesasFiltradas = despesas.filtrados(filtro, getEmpresaLogada());
	}
	
	public void excluir() {
		despesas.remover(despesaSelecionada);
		pesquisar();
		FacesUtil.addInfoMessage("A Despesa de código " + despesaSelecionada.getChaveRegistro()
				+ " foi excluída com sucesso.");
	}
	
	//
	public void ativarDespesaQuitada() {
		despesa.setQuantidadeParcelas(new Integer(1));
		gerarParcelas();
	}
	
	//gera parcelas e a quantidade de linhas na view Cadastro Despesa
	public void gerarParcelas() {
		if(!Numero.isMaiorZero(despesa.getValorTotal())) {
			//FacesUtil.addErrorMessage("O campo valor total deve ser maior que zero!");
			return;
		}else if (despesa.getDataVencimento() != null
				&& Numero.isMaiorZero(despesa.getQuantidadeParcelas())) {

			setCalculoParcela(true);

			Date dataVencimento = despesa.getDataVencimento();
			int qtParcelas = despesa.getQuantidadeParcelas();
			
			setValorParcela((despesa.getValorTotal().subtract(despesa.getDesconto())).divide(new BigDecimal(qtParcelas), 2, RoundingMode.HALF_UP));
			System.out.println("O valor total da despesa é "+despesa.getValorTotal());
			//se não existir id de despesa,ou seja, não foi criada ainda
			if (!Numero.isMaiorZero(despesa.getId())) {
				despesa.setParcelas(null);
				Parcela parcela = null;
				for (int nrParcela = 1; nrParcela <= qtParcelas; nrParcela++) {
					parcela = new Parcela();
					if (nrParcela > 1) {
						//aumenta um mês na data de vencimento
						dataVencimento = DataUtil.addMeses(despesa.getDataVencimento(), (nrParcela - 1));
					}

					parcela.setNumero(nrParcela);
					parcela.setDataVencimento(dataVencimento);
					parcela.setSituacao(SituacaoPagamentoEnumerador.ABERTA);
					parcela.setValor(valorParcela);
					parcela.setDespesa(despesa);
					despesa.getParcelas().add(parcela);

				}

			} else {
				for (Parcela parcela : despesa.getParcelas()) {
					if (qtParcelas > 1) {
						dataVencimento = DataUtil.addMeses(despesa.getDataVencimento(), (parcela.getNumero() - 1));
					}
					parcela.setDataVencimento(dataVencimento);
					parcela.setValor(valorParcela);
				}
			}

		}
	}
	
	//busca todas as parcelas pagas para a despesa selecionada dentro de movimentacao
	private void carregarPagamentos() {
		listaPagamentos  = new ArrayList<MovimentacaoConta>();
		if(Numero.isMaiorZero(despesa.getId())) {
			for (Parcela p : despesa.getParcelas()) {
				for(MovimentacaoConta pagamento : p.getRecebimentos()) {
					listaPagamentos.add(pagamento);
				}
			}
		}
	}
	
	//verifica se existe parcela cancelada para a receita, caso sim adiciona na lista
	private void carregarCancelamentos() {
		listaCancelamentos  = new ArrayList<ParcelaCancelamento>();
		if(Numero.isMaiorZero(despesa.getId())) {
			for (Parcela p : despesa.getParcelas()) {
				if(p.getCancelamento() != null) {
					listaCancelamentos.add(p.getCancelamento());
				}
			}
		}
	}

	public Despesa getDespesa() {
		return despesa;
	}

	public void setDespesa(Despesa despesa) {
		this.despesa = despesa;
	}

	public ContaBancaria getContaBancaria() {
		return contaBancaria;
	}

	public void setContaBancaria(ContaBancaria contaBancaria) {
		this.contaBancaria = contaBancaria;
	}

	public List<Despesa> getListaDespesas() {
		return listaDespesas;
	}

	public void setListaDespesas(List<Despesa> listaDespesas) {
		this.listaDespesas = listaDespesas;
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

	public List<Despesa> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Despesa> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			Despesa itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Despesa>) rGeral.porChaveRegistro(Despesa.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.despesa = itemEdicao;
			carregarPagamentos();
			carregarCancelamentos();
		}
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.despesa.getId() != null;
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

	public List<Despesa> getDespesasFiltradas() {
		return despesasFiltradas;
	}

	public void setDespesasFiltradas(List<Despesa> despesasFiltradas) {
		this.despesasFiltradas = despesasFiltradas;
	}

	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(ConsultaFilter filtro) {
		this.filtro = filtro;
	}

	public Despesa getDespesaSelecionada() {
		return despesaSelecionada;
	}

	public void setDespesaSelecionada(Despesa despesaSelecionada) {
		this.despesaSelecionada = despesaSelecionada;
	}

	public List<MovimentacaoConta> getListaPagamentos() {
		return listaPagamentos;
	}

	public void setListaPagamentos(List<MovimentacaoConta> listaPagamentos) {
		this.listaPagamentos = listaPagamentos;
	}

	public List<ParcelaCancelamento> getListaCancelamentos() {
		return listaCancelamentos;
	}

	public void setListaCancelamentos(List<ParcelaCancelamento> listaCancelamentos) {
		this.listaCancelamentos = listaCancelamentos;
	}

	public boolean isDespesaQuitada() {
		return despesaQuitada;
	}

	public void setDespesaQuitada(boolean despesaQuitada) {
		this.despesaQuitada = despesaQuitada;
	}

	public List<Despesa> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Despesa> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}	
}

