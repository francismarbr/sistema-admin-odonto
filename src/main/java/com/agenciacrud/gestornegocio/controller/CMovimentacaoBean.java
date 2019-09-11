package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.Banco;
import com.agenciacrud.gestornegocio.model.Cheque;
import com.agenciacrud.gestornegocio.model.ContaBancaria;
import com.agenciacrud.gestornegocio.model.Fornecedor;
import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.ParcelaCancelamento;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoContaEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoMovimentacao;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoMovimentacaoBaixaCancelamento;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoOperacao;
import com.agenciacrud.gestornegocio.repositorio.Parcelas;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.MovimentacaoFilter;
import com.agenciacrud.gestornegocio.service.ContaBancariaService;
import com.agenciacrud.gestornegocio.service.ParcelaService;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CMovimentacaoBean")
@ViewScoped
public class CMovimentacaoBean extends CGeral implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Parcelas parcelas;
	@Inject
	private RGeral rGeral;

	@Inject
	private ParcelaService parcelaService;
	
	@Inject
	private ContaBancariaService contaBancariaService;
	
	private List<Parcela> parcelasFiltradas;
	private List<TipoContaEnumerador> listaTiposConta;
	private List<ItemParcela> listaParcelasSelecionadas;
	private List<Banco> listaBancos;
	private List<ContaBancaria> listaContasBancarias;
	private MovimentacaoFilter filtro;
	private TipoContaEnumerador tipoConta;
	private TipoMovimentacaoBaixaCancelamento tipoMovimentacao;
	private MovimentacaoConta movimentacao;
	private ContaBancaria conta;
	private Parcela parcela;
	private boolean somenteVencidas;
	private boolean baixarComCheque;
	private ItemParcela itemParcela;
	private Cheque cheque;
	private BigDecimal valorTotalBaixar = BigDecimal.ZERO;
	private String descricaoCancelamento;
	
	public CMovimentacaoBean() {
		filtro = new MovimentacaoFilter();
		parcelasFiltradas = new ArrayList<>();
		setTipoMovimentacao(TipoMovimentacaoBaixaCancelamento.BAIXA);
		movimentacao = new MovimentacaoConta();
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()) {
			this.setListaBancos(this.rGeral.buscaTodos(Banco.class, getEmpresaLogada()));
			this.setListaContasBancarias(this.rGeral.buscaTodos(ContaBancaria.class, getEmpresaLogada()));
		}
	}
	public void limpar() {
		listaParcelasSelecionadas = null;
		//cheque = new Cheque();
		setBaixarComCheque(false);
	}
	
	public void pesquisar() {
		//busca todas parcelas conforme filtro na view
		parcelasFiltradas = parcelas.filtrados(filtro, getEmpresaLogada());
		
		setListaParcelasSelecionadas(new ArrayList<ItemParcela>());
		ItemParcela item = null;
		
		//passa a busca do filtro para uma lista parcelas, para melhor controle dos itens
		for(Parcela parcela : parcelasFiltradas) {
			item = new ItemParcela();
			if(isSomenteVencidas()) {
				if(parcela.getDiasParaVencer() <= 0) {
					item.setParcela(parcela);
					getListaParcelasSelecionadas().add(item);
				}
			} else {
				item.setParcela(parcela);
				getListaParcelasSelecionadas().add(item);
			}
		}
		//Collections.sort(getListaParcelasSelecionadas(), new OrdenacaoItemParcela());
	}
	
	public boolean isAtivarBotaoBaixar() {
		if(getListaParcelasSelecionadas()!=null){
			for (ItemParcela item : getListaParcelasSelecionadas()) {
				if(item.isSelecionada()) {
					return true;
				}
			}
		}
		return false;
		
	}
		
	public void baixarParcelasSelecionadas() {
		
		MovimentacaoConta mov = null;
		
		for(ItemParcela item : getListaParcelasSelecionadas()) {
			//se a parcela da view foi selecionada na view, então entra no if
			if(item.isSelecionada()) {
				mov = new MovimentacaoConta();
				Parcela parcela = item.getParcela();
				
				mov.setParcela(parcela);
				mov.setDataLancamento(new Date());
				mov.setValorMovimentado(parcela.getValorPagamento());
				
				//identifica se é uma receita ou despesa para baixar
				if(filtro.getTipoConta().equals(TipoContaEnumerador.CONTA_RECEBER)) {
					mov.setTipoMovimentacao(TipoMovimentacao.RECEBIMENTO);
					mov.setTipoOperacao(TipoOperacao.CREDITO);
					mov.setPessoa(parcela.getReceita().getCliente());
					mov.setDescricao(parcela.getReceita().getNome());
					mov.setConta(getConta());
					mov.setSaldo(conta.getSaldo().add(mov.getValorMovimentado()));
					//atualiza o saldo da conta
					getConta().setSaldo(conta.getSaldo().add(mov.getValorMovimentado()));
				} else {
					mov.setTipoMovimentacao(TipoMovimentacao.PAGAMENTO);
					mov.setTipoOperacao(TipoOperacao.DEBITO);
					mov.setFornecedor(parcela.getDespesa().getFornecedor());
					mov.setDescricao(parcela.getDespesa().getNome());
					mov.setConta(getConta());
					mov.setSaldo(conta.getSaldo().subtract(mov.getValorMovimentado()));
					//atualiza o saldo da conta
					getConta().setSaldo(conta.getSaldo().subtract(mov.getValorMovimentado()));
				}
				mov.setEmpresa(getEmpresaLogada());
				mov.setCheque(getCheque());
				
				/*se a situação está como quitada na view e o 
				 * valor de pagamento é menor que o valor total da parcela,
				 * calcula-se o desconto e altera o status da parcela para quitada
				 */
				if(parcela.getNovaSituacao().equals(SituacaoPagamentoEnumerador.QUITADA)) {
					parcela.setSituacao(SituacaoPagamentoEnumerador.QUITADA);
					BigDecimal valorDesconto = new BigDecimal(0);
					if(parcela.getValorPagamento().compareTo(parcela.getValorTotalSaldoPorParcela()) == 1) {
						valorDesconto = parcela.getValorTotalSaldoPorParcela().subtract(parcela.getValorPagamento());
					}
					mov.setValorDesconto(valorDesconto);
				} else {
					parcela.setSituacao(SituacaoPagamentoEnumerador.PAGA_PARCIALMENTE);
				}
				
				parcela.setNovaSituacao(SituacaoPagamentoEnumerador.QUITADA);
				parcela.getRecebimentos().add(mov);
				contaBancariaService.salvar(conta);
				parcelaService.salvar(parcela);
				
			}
		}
		FacesUtil.addInfoMessage("Parcelas baixadas com sucesso!");
		limpar();
		
	}
	//Cancela parcelas
	public void cancelarParcelas() {
		ParcelaCancelamento cancelamento = null;
		for (ItemParcela item : getListaParcelasSelecionadas()) {
			//verifica se o item está marcado para cancelamento
			if (item.isCancelarParcela()) {
				
				//recebe a parcela selecionada para cancelamento
				Parcela parcela = item.getParcela();
				cancelamento = new ParcelaCancelamento();

				cancelamento.setData(new Date());
				cancelamento.setUsuario(getUsuario());
				cancelamento.setObservacao(getDescricaoCancelamento());
				cancelamento.setParcela(parcela);
				parcela.setCancelamento(cancelamento);
				//atualiza nova situação da parcela
				parcela.setSituacao(SituacaoPagamentoEnumerador.CANCELADA);
				parcelaService.salvar(parcela);
				FacesUtil.addInfoMessage("Parcela foi cancelada com sucesso!");
				limpar();

			}
		}

		
	}
		
	//busca pessoas por nome e atualiza o autocomplete
	public List<Pessoa> completarPessoa(String nome) {
		return this.rGeral.porNome(Pessoa.class, nome, getEmpresaLogada());
	}
	
	//busca pessoas por nome e atualiza o autocomplete
		public List<Fornecedor> completarFornecedor(String nome) {
			return this.rGeral.porNome(Fornecedor.class, nome, getEmpresaLogada());
		}
	
	public MovimentacaoFilter getFiltro() {
		return filtro;
	}

	public List<Parcela> getParcelasFiltradas() {
		return parcelasFiltradas;
	}

	public List<TipoContaEnumerador> getListaTiposConta() {
		return listaTiposConta;
	}

	public void setListaTiposConta(List<TipoContaEnumerador> listaTiposConta) {
		this.listaTiposConta = listaTiposConta;
	}

	public TipoContaEnumerador getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(TipoContaEnumerador tipoConta) {
		this.tipoConta = tipoConta;
	}

	public List<ItemParcela> getListaParcelasSelecionadas() {
		return listaParcelasSelecionadas;
	}

	public void setListaParcelasSelecionadas(
			List<ItemParcela> listaParcelasSelecionadas) {
		this.listaParcelasSelecionadas = listaParcelasSelecionadas;
	}

	public boolean isSomenteVencidas() {
		return somenteVencidas;
	}

	public void setSomenteVencidas(boolean somenteVencidas) {
		this.somenteVencidas = somenteVencidas;
	}

	public TipoMovimentacaoBaixaCancelamento getTipoMovimentacao() {
		return tipoMovimentacao;
	}

	public void setTipoMovimentacao(TipoMovimentacaoBaixaCancelamento tipoMovimentacao) {
		this.tipoMovimentacao = tipoMovimentacao;
	}

	public Parcela getParcela() {
		return parcela;
	}

	public void setParcela(Parcela parcela) {
		this.parcela = parcela;
	}

	public ItemParcela getItemParcela() {
		return itemParcela;
	}

	public void setItemParcela(ItemParcela itemParcela) {
		this.itemParcela = itemParcela;
	}

	public MovimentacaoConta getMovimentacao() {
		return movimentacao;
	}

	public void setMovimentacao(MovimentacaoConta movimentacao) {
		this.movimentacao = movimentacao;
	}

	public List<Banco> getListaBancos() {
		return listaBancos;
	}

	public void setListaBancos(List<Banco> listaBancos) {
		this.listaBancos = listaBancos;
	}

	public ContaBancaria getConta() {
		return conta;
	}

	public void setConta(ContaBancaria conta) {
		this.conta = conta;
	}

	public List<ContaBancaria> getListaContasBancarias() {
		return listaContasBancarias;
	}

	public void setListaContasBancarias(List<ContaBancaria> listaContasBancarias) {
		this.listaContasBancarias = listaContasBancarias;
	}

	public Cheque getCheque() {
		return cheque;
	}

	public void setCheque(Cheque cheque) {
		this.cheque = cheque;
	}

	public boolean isBaixarComCheque() {
		return baixarComCheque;
	}

	public void setBaixarComCheque(boolean baixarComCheque) {
		this.baixarComCheque = baixarComCheque;
	}

	public BigDecimal getValorTotalBaixar() {
		valorTotalBaixar = new BigDecimal(0);
		if(getListaParcelasSelecionadas() != null) {
		for (ItemParcela item : getListaParcelasSelecionadas()) {
			if (item.isSelecionada()) {
				Parcela parcela = item.getParcela();
				valorTotalBaixar = parcela.getValorPagamento().add(valorTotalBaixar);
			}
			
		}}
		return valorTotalBaixar;
	}

	public void setValorTotalBaixar(BigDecimal valorTotalBaixar) {
		this.valorTotalBaixar = valorTotalBaixar;
	}

	public String getDescricaoCancelamento() {
		return descricaoCancelamento;
	}

	public void setDescricaoCancelamento(String descricaoCancelamento) {
		this.descricaoCancelamento = descricaoCancelamento;
	}

	public ContaBancariaService getContaBancariaService() {
		return contaBancariaService;
	}

	public void setContaBancariaService(ContaBancariaService contaBancariaService) {
		this.contaBancariaService = contaBancariaService;
	}

	
}