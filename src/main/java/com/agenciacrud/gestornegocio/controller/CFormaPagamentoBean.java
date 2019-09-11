package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.FormaPagamento;
import com.agenciacrud.gestornegocio.repositorio.FormasPagamentos;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CFormaPagamentoBean")
@ViewScoped
public class CFormaPagamentoBean extends CGeral implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private FormaPagamento formaPagamento;
	
	@Inject
	private FormasPagamentos formasPagamentos;
	
	@Inject
	private RGeral rGeral;
	
	private List<FormaPagamento> buscaUltimoRegistro;
	private List<FormaPagamento> formasPagamentosFiltradas;
	
	private ConsultaFilter filtro;
	private FormaPagamento formaPagamentoSelecionado;
	
	private Long chaveRegistroEdicao;
	private List<FormaPagamento> buscaRegistroEdicao;

	
	public CFormaPagamentoBean() {
		filtro = new ConsultaFilter();
		limpar();
	}
	
	private void limpar() {
		formaPagamento = new FormaPagamento();
	}
	
	public void salvar(){
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.formaPagamento.getId())){
			FormaPagamento chaveRegistro = null;
			
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<FormaPagamento>) rGeral.buscaUltimoRegistroPorEmpresa(FormaPagamento.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				formaPagamento.setChaveRegistro(new Long(1));  
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				formaPagamento.setChaveRegistro(codigo);
			}
		}
		this.formaPagamento.setEmpresa(getEmpresaLogada());
		this.formaPagamento = formasPagamentos.guardar(this.formaPagamento);
		limpar();
		
		FacesUtil.addInfoMessage("Forma de Pagamento criada com sucesso");
	}
	
	public void excluir() {
		formasPagamentos.remover(formaPagamentoSelecionado);
		formasPagamentosFiltradas.remove(formaPagamentoSelecionado);
		
		FacesUtil.addInfoMessage("A forma de pagamento de código " + formaPagamentoSelecionado.getChaveRegistro()
				+ " foi excluído com sucesso.");
	}

	public void pesquisar() {
		formasPagamentosFiltradas = formasPagamentos.filtrados(filtro, getEmpresaLogada());
	}

	public List<FormaPagamento> getFormasPagamentosFiltradas() {
		return formasPagamentosFiltradas;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public FormaPagamento getFormaPagamentoSelecionado() {
		return formaPagamentoSelecionado;
	}

	public void setFormaPagamentoSelecionado(FormaPagamento formaPagamentoSelecionado) {
		this.formaPagamentoSelecionado = formaPagamentoSelecionado;
	}
	
	public FormaPagamento getFormaPagamento(){
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}
	
	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<FormaPagamento> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<FormaPagamento> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			FormaPagamento itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<FormaPagamento>) rGeral.porChaveRegistro(FormaPagamento.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.formaPagamento = itemEdicao;			
		}
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.formaPagamento.getId() != null;
	}

	public List<FormaPagamento> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<FormaPagamento> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

}
