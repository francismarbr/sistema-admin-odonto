package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import com.agenciacrud.gestornegocio.model.Categoria;
import com.agenciacrud.gestornegocio.model.Dentista;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.Atestado;
import com.agenciacrud.gestornegocio.repositorio.Categorias;
import com.agenciacrud.gestornegocio.repositorio.Atestados;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.AtestadoService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CAtestadoBean")
@ViewScoped
public class CAtestadoBean extends CGeral implements Serializable {
	

	private static final long serialVersionUID = 1L;

	
	@Inject
	private AtestadoService atestadoService;
	
	@Inject
	private RGeral rGeral;
	
	private List<Atestado> buscaUltimoRegistro;
	
	private Atestado atestado;
	
	@Inject
	private Atestados atestados;
	
	private ConsultaFilter filtro;
	private List<Atestado> atestadosFiltrados;
	
	private Atestado atestadoSelecionado;
	
	private Long chaveRegistroEdicao;
	private List<Atestado> buscaRegistroEdicao;
	private List<Dentista> listaDentistas;
	
	public CAtestadoBean(){
		filtro = new ConsultaFilter();
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()){
			this.listaDentistas = this.rGeral.buscaDentistas(Dentista.class, getEmpresaLogada());
		}
	}

	private void limpar() {
		atestado = new Atestado();
	}
	
	public String salvar(){
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.atestado.getId())){
			this.atestado.setDataRegistro(new Date());
			Atestado chaveRegistro = null;
			
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<Atestado>) rGeral.buscaUltimoRegistroPorEmpresa(Atestado.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				atestado.setChaveRegistro(new Long(1));
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				atestado.setChaveRegistro(codigo);
			}
		}
		this.atestado.setEmpresa(getEmpresaLogada());
		//vai até a classe de serviço verificar se já existe o login cadastrado, pois não pode ser igual
		this.atestado = atestadoService.salvar(this.atestado);
		limpar();
		
		FacesUtil.addInfoMessage("Atestado criado com sucesso");
		return "PesquisaAtestado?faces-redirect=true";
	}
	
	public void excluir() {
		atestados.remover(atestadoSelecionado);
		atestadosFiltrados.remove(atestadoSelecionado);
		
		FacesUtil.addInfoMessage("Atestado excluído com sucesso.");
	}

	public void pesquisar() {
		atestadosFiltrados = atestados.filtrados(filtro, getEmpresaLogada());
	}
	
	//busca pessoas por nome e atualiza o autocomplete
	public List<Pessoa> completarCliente(String nome) {
		return this.rGeral.porNome(Pessoa.class, nome, getEmpresaLogada());
	}

	public List<Atestado> getAtestadosFiltrados() {
		return atestadosFiltrados;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public Atestado getAtestadoSelecionado() {
		return atestadoSelecionado;
	}

	public void setAtestadoSelecionado(Atestado atestadoSelecionado) {
		this.atestadoSelecionado = atestadoSelecionado;
	}
	
	public Atestado getAtestado(){
		return atestado;
	}

	public void setAtestado(Atestado atestado) {
		this.atestado = atestado;
	}
	
	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Atestado> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Atestado> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			Atestado itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Atestado>) rGeral.porChaveRegistro(Atestado.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.atestado = itemEdicao;			
		}
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.atestado.getId() != null;
	}

	public List<Atestado> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Atestado> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

	public List<Dentista> getListaDentistas() {
		return listaDentistas;
	}

	public void setListaDentistas(List<Dentista> listaDentistas) {
		this.listaDentistas = listaDentistas;
	}

}
