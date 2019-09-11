package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.RegimesTributarios;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.CadastroRegimeTributarioService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CRegimeTributarioBean")
@ViewScoped
public class CRegimeTributarioBean extends CGeral implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Inject
	private CadastroRegimeTributarioService cadastroRegimeTributarioService;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private RegimesTributarios regimesTributarios;
	
	private List<RegimeTributario> buscaUltimoRegistro;
	private List<RegimeTributario> regimesFiltrados;
	
	private RegimeTributario regimeTributario;
	private ConsultaFilter filtro;
	private RegimeTributario regimeSelecionado;
	
	private Long chaveRegistroEdicao;
	private List<RegimeTributario> buscaRegistroEdicao;
		
	public CRegimeTributarioBean(){
		filtro = new ConsultaFilter();
		limpar();
	}
	

	private void limpar() {
		regimeTributario = new RegimeTributario();
	}
	
	public void salvar(){
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.regimeTributario.getId())){
			RegimeTributario chaveRegistro = null;
			
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<RegimeTributario>) rGeral.buscaUltimoRegistroPorEmpresa(RegimeTributario.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				regimeTributario.setChaveRegistro(new Long(1));  
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				regimeTributario.setChaveRegistro(codigo);
			}
		}
		this.regimeTributario.setEmpresa(getEmpresaLogada());
		//vai até a classe de serviço verificar se já existe o login cadastrado, pois não pode ser igual
		this.regimeTributario = cadastroRegimeTributarioService.salvar(this.regimeTributario);
		limpar();
		
		FacesUtil.addInfoMessage("Serviço salvo com sucesso");
	}
	
	public void excluir() {
		regimesTributarios.remover(regimeSelecionado);
		regimesFiltrados.remove(regimeSelecionado);
		
		FacesUtil.addInfoMessage("O Regime de código " + regimeSelecionado.getId()
				+ " foi excluído com sucesso.");
	}

	public void pesquisar() {
		regimesFiltrados = regimesTributarios.filtrados(filtro, getEmpresaLogada());
	}

	public List<RegimeTributario> getRegimesFiltrados() {
		return regimesFiltrados;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public RegimeTributario getRegimeSelecionado() {
		return regimeSelecionado;
	}

	public void setRegimeSelecionado(RegimeTributario regimeSelecionado) {
		this.regimeSelecionado = regimeSelecionado;
	}
	
	public RegimeTributario getRegimeTributario(){
		return regimeTributario;
	}

	public void setRegimeTributario(RegimeTributario regimeTributario) {
		this.regimeTributario = regimeTributario;
	}
	
	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<RegimeTributario> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<RegimeTributario> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			RegimeTributario itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<RegimeTributario>) rGeral.porChaveRegistro(RegimeTributario.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.regimeTributario = itemEdicao;			
		}
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.regimeTributario.getId() != null;
	}


	public List<RegimeTributario> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}


	public void setBuscaUltimoRegistro(List<RegimeTributario> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

}
