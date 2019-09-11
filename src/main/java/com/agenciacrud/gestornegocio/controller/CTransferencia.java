package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.ContaBancaria;
import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.model.TransferenciaConta;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoMovimentacao;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoOperacao;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.Transferencias;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.ContaBancariaService;
import com.agenciacrud.gestornegocio.service.MovimentacaoService;
import com.agenciacrud.gestornegocio.service.TransferenciaService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CTransferenciaBean")
@ViewScoped
public class CTransferencia extends CGeral implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TransferenciaConta transferencia;
	
	@Inject
	private TransferenciaService transferenciaService;
	
	@Inject
	private ContaBancariaService contaBancariaService;
	
	@Inject
	private MovimentacaoService movimentacaoService;
	
	@Inject
	private Transferencias transferencias;
	
	@Inject
	private RGeral rGeral;
	
	private List<ContaBancaria> listaContaBancaria;
	private List<TransferenciaConta> buscaUltimoRegistro;
	
	private List<TransferenciaConta> transferenciasFiltradas;	
	private ConsultaFilter filtro;
	
	public CTransferencia() {
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()) {
			this.listaContaBancaria = this.rGeral.buscaTodos(ContaBancaria.class, getEmpresaLogada());
		}
	}
	
	public void limpar() {
		filtro = new ConsultaFilter();
		transferencia = new TransferenciaConta();
		listaContaBancaria = null;
	}
	
	public void salvar() throws Exception {
		if(!Numero.isMaiorZero(transferencia.getValor())){
			FacesUtil.addErrorMessage("O valor a ser transferido deve ser maior que zero.");
			return;
		}
		if(transferencia.getContaOrigem().equals(transferencia.getContaDestino())){
			FacesUtil.addErrorMessage("A conta de destino deve ser diferente da conta de origem");
			return;
		}
		BigDecimal limiteTotal = transferencia.getContaOrigem().getLimiteSaque().add(transferencia.getContaOrigem().getSaldo());
		if(limiteTotal.compareTo(transferencia.getValor()) == 1) {
			
			//geração de sequência para chave de registro
			if(!Numero.isMaiorZero(this.transferencia.getId())){
				TransferenciaConta chaveRegistro = null;
				
				//Recebe em uma lista o último registro da empresa
				setBuscaUltimoRegistro((List<TransferenciaConta>) rGeral.buscaUltimoRegistroPorEmpresa(TransferenciaConta.class, getEmpresaLogada()));
				
				if (getBuscaUltimoRegistro()==null) {
					//se a lista for vazia, soma 1 ao novo objeto
					transferencia.setChaveRegistro(new Long(1));  
				} else {
					//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
					chaveRegistro = buscaUltimoRegistro.get(0);
					Long codigo = (Long) chaveRegistro.getChaveRegistro();
					codigo += 1;
					transferencia.setChaveRegistro(codigo);
				}
			}
			
			transferencia.setRealizadoPor(getUsuario());
			transferencia.setDataLancamento(new Date());
			transferencia.setEmpresa(getEmpresaLogada());
			this.transferencia = transferenciaService.salvar(transferencia);
			movimentacaoDebitoContaOrigem();
			movimentacaoCreditoContaDestino();
			limpar();
			FacesUtil.addInfoMessage("Transferência realizada com sucesso");
		} else{
			FacesUtil.addErrorMessage("A conta de origem não possui saldo suficiente para realizar essa transferência.");
		}
	}
	
	//Método que debita valor de transferencia da conta de origem
	private void movimentacaoDebitoContaOrigem() throws Exception {
		MovimentacaoConta mov = new MovimentacaoConta();
		mov.setDataLancamento(new Date());
		mov.setDescricao("Transf: " + transferencia.getHistorico());
		mov.setConta(transferencia.getContaOrigem());
		mov.setValorMovimentado(transferencia.getValor());
		mov.setEmpresa(getEmpresaLogada());
		mov.setSaldo(transferencia.getContaOrigem().getSaldo().subtract(transferencia.getValor()));
				
		mov.setTipoMovimentacao(TipoMovimentacao.TRANSFERENCIA);
		mov.setTipoOperacao(TipoOperacao.DEBITO);

		ContaBancaria conta = transferencia.getContaOrigem();
		conta.setSaldo(transferencia.getContaOrigem().getSaldo().subtract(transferencia.getValor()));
		
		movimentacaoService.salvar(mov);
		contaBancariaService.salvar(conta);
	}
	
	//Método que credita valor de transferencia da conta de destino
	private void movimentacaoCreditoContaDestino() throws Exception {
		MovimentacaoConta mov = new MovimentacaoConta();
		mov.setDataLancamento(new Date());
		mov.setDescricao("Transf: " + transferencia.getHistorico());
		mov.setConta(transferencia.getContaDestino());
		mov.setValorMovimentado(transferencia.getValor());
		mov.setEmpresa(getEmpresaLogada());
		mov.setSaldo(transferencia.getContaDestino().getSaldo().add(transferencia.getValor()));
				
		mov.setTipoMovimentacao(TipoMovimentacao.TRANSFERENCIA);
		mov.setTipoOperacao(TipoOperacao.CREDITO);

		ContaBancaria conta = transferencia.getContaDestino();
		conta.setSaldo(transferencia.getContaDestino().getSaldo().add(transferencia.getValor()));
		
		movimentacaoService.salvar(mov);
		contaBancariaService.salvar(conta);
	}
	
	public void pesquisar() {	
		transferenciasFiltradas = transferencias.filtrados(filtro, getEmpresaLogada());
	}

	public TransferenciaConta getTransferencia() {
		return transferencia;
	}

	public void setTransferencia(TransferenciaConta transferencia) {
		this.transferencia = transferencia;
	}

	public List<ContaBancaria> getListaContaBancaria() {
		return listaContaBancaria;
	}

	public void setListaContaBancaria(List<ContaBancaria> listaContaBancaria) {
		listaContaBancaria = listaContaBancaria;
	}

	public List<TransferenciaConta> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<TransferenciaConta> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

	public List<TransferenciaConta> getTransferenciasFiltradas() {
		return transferenciasFiltradas;
	}

	public void setTransferenciasFiltradas(
			List<TransferenciaConta> transferenciasFiltradas) {
		this.transferenciasFiltradas = transferenciasFiltradas;
	}

	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public void setFiltro(ConsultaFilter filtro) {
		this.filtro = filtro;
	}


}
