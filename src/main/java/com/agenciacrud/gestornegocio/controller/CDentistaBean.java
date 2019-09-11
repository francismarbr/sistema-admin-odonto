package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.Cidade;
import com.agenciacrud.gestornegocio.model.Dentista;
import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.model.Parente;
import com.agenciacrud.gestornegocio.model.enumeradores.PlanoEmpresa;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoPessoaEnumerador;
import com.agenciacrud.gestornegocio.repositorio.Cidades;
import com.agenciacrud.gestornegocio.repositorio.Dentistas;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.DentistaService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CDentistaBean")
@ViewScoped
public class CDentistaBean extends CGeral implements Serializable  {
	

	private static final long serialVersionUID = 1L;

	private Dentista dentista;
	
	@Inject
	private Dentistas dentistas;
	
	@Inject
	private Cidades cidades;
	
	@Inject
	private DentistaService dentistaService;
	
	@Inject
	private RGeral rGeral;
		
	private ConsultaFilter filtro;
	private List<Dentista> dentistasFiltrados;
	private List<Cidade> listaCidades;
	
	private List<Dentista> buscaUltimoRegistro;
	
	private Long chaveRegistroEdicao;
	private List<Dentista> buscaRegistroEdicao;

	private Dentista dentistaSelecionado;
	private boolean limitarCadastro = false;

	public CDentistaBean(){
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()){
			if (Numero.isMaiorZero(dentista.getId())) {
				setExcluivel(true);
			}
		}
		pesquisar();
		int qtdDentista = dentistasFiltrados.size();
		if(getEmpresaLogada().getPlano().equals(PlanoEmpresa.INICIAL) && qtdDentista == 1 && !Numero.isMaiorZero(dentista.getId()) ){
			limitarCadastro = true;
		}
		if(getEmpresaLogada().getPlano().equals(PlanoEmpresa.MEGA) && qtdDentista == 4 && !Numero.isMaiorZero(dentista.getId())){
			limitarCadastro = true;
		}
	}
	
	public void limpar() {
		dentista = new Dentista();
		filtro = new ConsultaFilter();
		dentistasFiltrados = null;
	}
	
	public String novoPessoa() {
		dentista = new Dentista();
		return "CadastroDentista?faces-redirect=true";
	}
	
	public void salvar() {
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.dentista.getId())){
			Dentista chaveRegistro = null;
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<Dentista>) rGeral.buscaUltimoRegistroPorEmpresa(Dentista.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				dentista.setChaveRegistro(new Long(1)); 
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				dentista.setChaveRegistro(codigo);
			}
		}
		this.dentista.setEmpresa(getEmpresaLogada());
		this.dentista = dentistaService.salvar(this.dentista);
		limpar();
		
		FacesUtil.addInfoMessage("Registro salvo com sucesso");
	}
	
	public void excluir() {
		dentistas.remover(dentistaSelecionado);
		dentistasFiltrados.remove(dentistaSelecionado);
		
		FacesUtil.addInfoMessage("Pessoa " + dentistaSelecionado.getNome()
				+ " excluído com sucesso.");
	}
	
	public void pesquisar() {
		dentistasFiltrados = dentistas.filtrados(filtro, getEmpresaLogada());
	}
	
	public void obterCidades() {
		//busca todas cidades por uf selecionada na view
		listaCidades = cidades.cidadesPorUf(filtro);
	}
	
	public Dentista getDentista(){
		return dentista;
	}
	
	public void setDentista(Dentista dentista) {
		this.dentista = dentista;
	}
	
	public List<Dentista> getDentistasFiltrados() {
		return dentistasFiltrados;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public Dentista getDentistaSelecionado() {
		return dentistaSelecionado;
	}

	public void setDentistaSelecionado(Dentista dentistaSelecionado) {
		this.dentistaSelecionado = dentistaSelecionado;
	}
	
	public boolean isLimitarCadastro() {
		return limitarCadastro;
	}

	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Dentista> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Dentista> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			Dentista itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Dentista>) rGeral.porChaveRegistro(Dentista.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.dentista = itemEdicao;
			limitarCadastro = false;
			if(dentista.getEndereco().getCidade()!=null) {
				filtro.setUf(dentista.getEndereco().getCidade().getUf());
				this.listaCidades = this.cidades.cidadesPorUf(filtro);
			}
		}
		//retorna se banco não existir, retorna falso, caso contrário, verdadeiro.
		return this.dentista.getId() != null;
	}

	public List<Dentista> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Dentista> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

	public List<Cidade> getListaCidades() {
		return listaCidades;
	}

	public void setListaCidades(List<Cidade> listaCidades) {
		this.listaCidades = listaCidades;
	}

}
