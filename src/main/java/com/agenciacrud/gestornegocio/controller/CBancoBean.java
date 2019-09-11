package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.Banco;
import com.agenciacrud.gestornegocio.repositorio.Bancos;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CBancoBean")
@ViewScoped
public class CBancoBean extends CGeral implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private Banco banco;
	
	@Inject
	private Bancos bancos;
	
	@Inject
	private RGeral rGeral; //repositório Geral
		
	private ConsultaFilter filtro;
	private List<Banco> bancosFiltrados;
	private List<Banco> buscaUltimoRegistro;
	
	private Banco bancoSelecionado;
	
	private Long chaveRegistroEdicao;
	private List<Banco> buscaRegistroEdicao;
	
	public CBancoBean() {
		limpar();
	}
	
	private void limpar() {
		banco = new Banco();
		filtro = new ConsultaFilter();
	}
	
	public void salvar(){
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.banco.getId())){
			Banco chaveRegistro = null;
			
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<Banco>) rGeral.buscaUltimoRegistroPorEmpresa(Banco.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				banco.setChaveRegistro(new Long(1));  
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				banco.setChaveRegistro(codigo);
			}
		}
		this.banco.setEmpresa(getEmpresaLogada());
		this.banco = bancos.guardar(this.banco);
		limpar();
		
		FacesUtil.addInfoMessage("Banco salvo com sucesso");
	}
	
	public void excluir() {
		bancos.remover(bancoSelecionado);
		bancosFiltrados.remove(bancoSelecionado);
		
		FacesUtil.addInfoMessage("O Tipo de evento de código " + bancoSelecionado.getChaveRegistro()
				+ " foi excluído com sucesso.");
	}
	
	public void editar(){
		
		
	}

	public void pesquisar() {
		bancosFiltrados = bancos.filtrados(filtro, getEmpresaLogada());
	}


	public Banco getBanco(){
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}
	
	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			Banco itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Banco>) rGeral.porChaveRegistro(Banco.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.banco = itemEdicao;			
		}
		//retorna se banco não existir, retorna falso, caso contrário, verdadeiro.
		return this.banco.getId() != null;
	}
	
	public List<Banco> getBancosFiltrados() {
		return bancosFiltrados;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public Banco getBancoSelecionado() {
		return bancoSelecionado;
	}

	public void setBancoSelecionado(Banco bancoSelecionado) {
		this.bancoSelecionado = bancoSelecionado;
	}

	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Banco> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Banco> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public List<Banco> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Banco> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

}
