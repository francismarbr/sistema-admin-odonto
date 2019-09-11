package com.agenciacrud.gestornegocio.controller.relatorios;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.controller.CGeral;
import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.model.ContaBancaria;
import com.agenciacrud.gestornegocio.model.Dentista;
import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.Receita;
import com.agenciacrud.gestornegocio.repositorio.Movimentacoes;
import com.agenciacrud.gestornegocio.repositorio.Parcelas;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CRelatorioComissao")
@ViewScoped
public class CRelatorioComissao extends CGeral implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private Parcela parcela;
	
	@Inject
	private Parcelas parcelas;
	
	@Inject
	private Movimentacoes movimentacoes;
	
	@Inject
	private RGeral rGeral; //repositório Geral
		
	private ConsultaFilter filtro;
	private List<Parcela> parcelasFiltradas;
	private List<MovimentacaoConta> parcelasFiltradasRecebidas;
	private List<Dentista> listaDentistas;
	
	BigDecimal totalComissao;
	BigDecimal totalReceitaDentista;
	boolean parcelasRecebidas;
	
	public CRelatorioComissao() {
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()) {
			this.listaDentistas = this.rGeral.buscaDentistas(Dentista.class, getEmpresaLogada());
		}
		
	}
	
	public void limpar() {
		parcela = new Parcela();
		filtro = new ConsultaFilter();
		parcelasFiltradas = new ArrayList<Parcela>();
		parcelasFiltradasRecebidas = new ArrayList<MovimentacaoConta>();
		totalComissao = BigDecimal.ZERO;
		totalReceitaDentista = BigDecimal.ZERO;
	}
	
	public void pesquisar() {
		if(isParcelasRecebidas()) {
			List<MovimentacaoConta> listaParcelasRecebidasPorDentista = new ArrayList<MovimentacaoConta>();
			parcelasFiltradasRecebidas = movimentacoes.filtroReceitasQuitadas(getEmpresaLogada(), filtro);
			totalComissao = new BigDecimal(0);
			totalReceitaDentista = new BigDecimal(0);
			BigDecimal porcentagem = new BigDecimal(100);
			for(MovimentacaoConta mov : parcelasFiltradasRecebidas) {

				if(mov.getParcela().getReceita().getDentista() != null && mov.getParcela().getReceita().getDentista().equals(filtro.getDentista())){
					totalReceitaDentista = totalReceitaDentista.add(mov.getValorMovimentado());
					totalComissao = totalComissao.add((mov.getValorMovimentado().multiply(
							mov.getParcela().getReceita().getDentista().getComissao().divide(porcentagem))));
					listaParcelasRecebidasPorDentista.add(mov);
					
				}
			}
			parcelasFiltradasRecebidas = listaParcelasRecebidasPorDentista;
		} else {
			parcelasFiltradas = parcelas.filtroReceitaPorDentista(getEmpresaLogada(), filtro);
			totalComissao = new BigDecimal(0);
			totalReceitaDentista = new BigDecimal(0);
			BigDecimal porcentagem = new BigDecimal(100);
			for(Parcela p : parcelasFiltradas) {
				totalComissao = totalComissao.add((p.getValor().multiply(p.getReceita().getDentista().getComissao().divide(porcentagem))));
				totalReceitaDentista = totalReceitaDentista.add(p.getValor());
			}
		}
	} 

	public Parcela getParcela(){
		return parcela;
	}

	public void setParcela(Parcela parcela) {
		this.parcela = parcela;
	}
	
	
	public List<Parcela> getParcelasFiltradas() {
		return parcelasFiltradas;
	}
	
	public List<MovimentacaoConta> getParcelasFiltradasRecebidas() {
		return parcelasFiltradasRecebidas;
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

	public BigDecimal getTotalComissao() {
		return totalComissao;
	}

	public void setTotalComissao(BigDecimal totalComissao) {
		this.totalComissao = totalComissao;
	}

	public BigDecimal getTotalReceitaDentista() {
		return totalReceitaDentista;
	}

	public void setTotalReceitaDentista(BigDecimal totalReceitaDentista) {
		this.totalReceitaDentista = totalReceitaDentista;
	}

	public boolean isParcelasRecebidas() {
		return parcelasRecebidas;
	}

	public void setParcelasRecebidas(boolean parcelasRecebidas) {
		this.parcelasRecebidas = parcelasRecebidas;
	}
}
