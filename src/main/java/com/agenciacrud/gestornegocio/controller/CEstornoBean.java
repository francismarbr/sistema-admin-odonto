package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.ContaBancaria;
import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoContaEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoMovimentacao;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoOperacao;
import com.agenciacrud.gestornegocio.repositorio.Parcelas;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.MovimentacaoFilter;
import com.agenciacrud.gestornegocio.service.ContaBancariaService;
import com.agenciacrud.gestornegocio.service.MovimentacaoService;
import com.agenciacrud.gestornegocio.service.ParcelaService;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CEstornoBean")
@ViewScoped
public class CEstornoBean extends CGeral implements Serializable {
	
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
	
	@Inject MovimentacaoService movimentacaoService;
	
	@Inject
	private ContaBancariaService contaBancariaService;
	
	private List<MovimentacaoConta> parcelasFiltradas;
	private List<TipoContaEnumerador> listaTiposConta;
	private List<ItemParcela> listaParcelasSelecionadas;
	private MovimentacaoFilter filtro;

	private ContaBancaria conta;
	
	public CEstornoBean() {
		filtro = new MovimentacaoFilter();
		parcelasFiltradas = new ArrayList<>();
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()) {
			completarPessoa("");
		}
	}
	public void limpar() {
		listaParcelasSelecionadas = null;
	}
	
	public void pesquisar() {
		
		//busca todas parcelas conforme filtro na view
		parcelasFiltradas = parcelas.filtroBaixadas(filtro, getEmpresaLogada());
		
		setListaParcelasSelecionadas(new ArrayList<ItemParcela>());
		ItemParcela item = null;
		
		//passa a busca do filtro para uma lista parcelas, para melhor controle dos itens
		for(MovimentacaoConta parcelaBaixada : parcelasFiltradas) {
			item = new ItemParcela();
			item.setParcelaBaixada(parcelaBaixada);
			getListaParcelasSelecionadas().add(item);
		}
		//Collections.sort(getListaParcelasSelecionadas(), new OrdenacaoItemParcela());
	}
		
	public void estornarBaixaSelecionada() {
		
		MovimentacaoConta mov = null;
		
		for(ItemParcela item : getListaParcelasSelecionadas()) {
			//se a parcela da view foi selecionada, então entra no if
			if(item.isEstornarParcela()) {
				mov = new MovimentacaoConta();
				MovimentacaoConta itemEstornar = item.getParcelaBaixada();
				Parcela parcela = new Parcela();
				
				mov.setParcela(itemEstornar.getParcela());
				mov.setDataLancamento(new Date());
				mov.setValorMovimentado(itemEstornar.getValorMovimentado());
				
				//identifica se é uma receita ou despesa para baixar
				if(itemEstornar.getTipoMovimentacao().equals(TipoMovimentacao.RECEBIMENTO)) {
					//mov.setTipoMovimentacao(TipoMovimentacao.PAGAMENTO);
					mov.setTipoOperacao(TipoOperacao.DEBITO);
					mov.setPessoa(itemEstornar.getPessoa());
					mov.setDescricao("Estorno: "+itemEstornar.getDescricao());
					mov.setConta(itemEstornar.getConta());
					mov.setSaldo(itemEstornar.getConta().getSaldo().subtract(itemEstornar.getValorMovimentado()));
					//atualiza o saldo da conta
					setConta(itemEstornar.getConta());
					getConta().setSaldo(conta.getSaldo().subtract(itemEstornar.getValorMovimentado()));
				} else {
					//mov.setTipoMovimentacao(TipoMovimentacao.RECEBIMENTO);
					mov.setTipoOperacao(TipoOperacao.CREDITO);
					mov.setPessoa(itemEstornar.getPessoa());
					mov.setDescricao("Estorno: "+itemEstornar.getDescricao());
					mov.setConta(itemEstornar.getConta());
					mov.setSaldo(itemEstornar.getConta().getSaldo().add(itemEstornar.getValorMovimentado()));
					//atualiza o saldo da conta
					setConta(itemEstornar.getConta());
					getConta().setSaldo(conta.getSaldo().add(itemEstornar.getValorMovimentado()));
				}
				mov.setEmpresa(itemEstornar.getEmpresa());
				mov.setCheque(null);
				mov.setEstornado(true);
				itemEstornar.setEstornado(true);
				
				/*seta os valores em parcela para estornar a baixa*/
				parcela.setDataVencimento(itemEstornar.getParcela().getDataVencimento());
				parcela.setNumero(itemEstornar.getParcela().getNumero());
				parcela.setSituacao(SituacaoPagamentoEnumerador.ABERTA);
				parcela.setValor(itemEstornar.getValorMovimentado());
				if(itemEstornar.getTipoMovimentacao().equals(TipoMovimentacao.RECEBIMENTO)) {
					parcela.setReceita(itemEstornar.getParcela().getReceita());
				} else {
					parcela.setDespesa(itemEstornar.getParcela().getDespesa());
				}
				
				
				contaBancariaService.salvar(conta);
				parcelaService.salvar(parcela);
				movimentacaoService.salvar(mov);
				movimentacaoService.salvar(itemEstornar);
				FacesUtil.addInfoMessage("Baixas Estornadas Com Sucesso!");
				limpar();
			}
		}
		
	}
	
	//busca pessoas por nome e atualiza o autocomplete
	public List<Pessoa> completarPessoa(String nome) {
		return this.rGeral.porNome(Pessoa.class, nome, getEmpresaLogada());
	}
	
	public MovimentacaoFilter getFiltro() {
		return filtro;
	}

	public List<MovimentacaoConta> getParcelasFiltradas() {
		return parcelasFiltradas;
	}

	public List<TipoContaEnumerador> getListaTiposConta() {
		return listaTiposConta;
	}

	public void setListaTiposConta(List<TipoContaEnumerador> listaTiposConta) {
		this.listaTiposConta = listaTiposConta;
	}

	public List<ItemParcela> getListaParcelasSelecionadas() {
		return listaParcelasSelecionadas;
	}

	public void setListaParcelasSelecionadas(
			List<ItemParcela> listaParcelasSelecionadas) {
		this.listaParcelasSelecionadas = listaParcelasSelecionadas;
	}

	public ContaBancaria getConta() {
		return conta;
	}

	public void setConta(ContaBancaria conta) {
		this.conta = conta;
	}

	public ContaBancariaService getContaBancariaService() {
		return contaBancariaService;
	}

	public void setContaBancariaService(ContaBancariaService contaBancariaService) {
		this.contaBancariaService = contaBancariaService;
	}

	
}