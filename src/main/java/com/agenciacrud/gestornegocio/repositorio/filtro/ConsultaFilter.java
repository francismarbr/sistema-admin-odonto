package com.agenciacrud.gestornegocio.repositorio.filtro;

import java.io.Serializable;
import java.util.Date;

import com.agenciacrud.gestornegocio.model.Dentista;
import com.agenciacrud.gestornegocio.model.Fornecedor;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoCategoriaContaEnumerador;

public class ConsultaFilter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String nome;
	private String descricao;
	private String cpfCnpj;
	private String uf;
	private Pessoa pessoa;
	private Fornecedor fornecedor;
	private Date dataInicial;
	private Date dataFinal;
	private Date dataLancamentoInicial;
	private Date dataLancamentoFinal;
	private TipoCategoriaContaEnumerador tipoCategoriaConta;
	private Dentista dentista;
	private boolean ativo = true;
	
	
	/*public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id == null ? null : id.toUpperCase(); //se sku for nulo, atribui nulo, senão coloca em maiúsculo
	}*/
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getCpfCnpj() {
		return cpfCnpj;
	}
	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
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
	public Date getDataLancamentoInicial() {
		return dataLancamentoInicial;
	}
	public void setDataLancamentoInicial(Date dataLancamentoInicial) {
		this.dataLancamentoInicial = dataLancamentoInicial;
	}
	public Date getDataLancamentoFinal() {
		return dataLancamentoFinal;
	}
	public void setDataLancamentoFinal(Date dataLancamentoFinal) {
		this.dataLancamentoFinal = dataLancamentoFinal;
	}
	public TipoCategoriaContaEnumerador getTipoCategoriaConta() {
		return tipoCategoriaConta;
	}
	public void setTipoCategoriaConta(
			TipoCategoriaContaEnumerador tipoCategoriaConta) {
		this.tipoCategoriaConta = tipoCategoriaConta;
	}
	public Dentista getDentista() {
		return dentista;
	}
	public void setDentista(Dentista dentista) {
		this.dentista = dentista;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
