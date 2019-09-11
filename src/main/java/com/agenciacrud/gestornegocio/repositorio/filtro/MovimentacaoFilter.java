package com.agenciacrud.gestornegocio.repositorio.filtro;

import java.io.Serializable;
import java.util.Date;

import com.agenciacrud.gestornegocio.model.Fornecedor;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoContaEnumerador;

public class MovimentacaoFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private Pessoa pessoa;
	private Fornecedor fornecedor;
	private Date dataInicial;
	private Date dataFinal;
	private TipoContaEnumerador tipoConta;
	
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	public Fornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public Date getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	public Date getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public TipoContaEnumerador getTipoConta() {
		return tipoConta;
	}
	
	public void setTipoConta(TipoContaEnumerador tipoConta) {
		this.tipoConta = tipoConta;
	}
}