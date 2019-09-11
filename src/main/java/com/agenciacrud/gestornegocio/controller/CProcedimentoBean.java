package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import com.agenciacrud.gestornegocio.model.Categoria;
import com.agenciacrud.gestornegocio.model.CategoriaProcedimento;
import com.agenciacrud.gestornegocio.model.Procedimento;
import com.agenciacrud.gestornegocio.repositorio.CategoriasProcedimentos;
import com.agenciacrud.gestornegocio.repositorio.Procedimentos;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.ProcedimentoService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CProcedimentoBean")
@ViewScoped
public class CProcedimentoBean extends CGeral implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject //chama método produtor
	private CategoriasProcedimentos categorias;
	
	@Inject
	private ProcedimentoService procedimentoService;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private Procedimentos procedimentos;
	
	private Procedimento procedimento;
	private List<Procedimento> buscaUltimoRegistro;
	

	private List<CategoriaProcedimento> categoriasRaizes;
	private List<CategoriaProcedimento> subcategorias;
	private List<Procedimento> procedimentosFiltrados;
	
	private ConsultaFilter filtro;
	private Procedimento procedimentoSelecionado;
	
	private Long chaveRegistroEdicao;
	private List<Procedimento> buscaRegistroEdicao;
	
	
	public CProcedimentoBean(){
		limpar();
	}
	
	public void inicializar() {		
		if(FacesUtil.isNotPostback()){
			categoriasRaizes = categorias.raizes(getEmpresaLogada());
		}
	}
	

	
	private void limpar() {
		filtro = new ConsultaFilter();
		procedimento = new Procedimento();
		subcategorias = new ArrayList<>();
	}
	
	public void salvar(){
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.procedimento.getId())){
			Procedimento chaveRegistro = null;
			
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<Procedimento>) rGeral.buscaUltimoRegistroPorEmpresa(Procedimento.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				procedimento.setChaveRegistro(new Long(1));  
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				procedimento.setChaveRegistro(codigo);
			}
		}
		this.procedimento.setEmpresa(getEmpresaLogada());
		//vai até a classe de serviço verificar se já existe o login cadastrado, pois não pode ser igual
		this.procedimento = procedimentoService.salvar(this.procedimento);
		limpar();
		
		FacesUtil.addInfoMessage("Procedimento salvo com sucesso");
	}
	
	public void excluir() {
		procedimentos.remover(procedimentoSelecionado);
		procedimentosFiltrados.remove(procedimentoSelecionado);
		
		FacesUtil.addInfoMessage("O Procedimento de código " + procedimentoSelecionado.getChaveRegistro()
				+ "excluído com sucesso.");
	}

	public void pesquisar() {
		procedimentosFiltrados = procedimentos.filtrados(filtro, getEmpresaLogada());
	}

	public List<Procedimento> getProcedimentosFiltrados() {
		return procedimentosFiltrados;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}
	
	public void setFiltro(ConsultaFilter filtro) {
		this.filtro = filtro;
	}

	public Procedimento getProcedimentoSelecionado() {
		return procedimentoSelecionado;
	}

	public void setProcedimentoSelecionado(Procedimento procedimentoSelecionado) {
		this.procedimentoSelecionado = procedimentoSelecionado;
	}
	
	public Procedimento getProcedimento(){
		return procedimento;
	}

	public void setProcedimento(Procedimento procedimento) {
		this.procedimento = procedimento;
	}

	public List<CategoriaProcedimento> getCategoriasRaizes() {
		return categoriasRaizes;
	}

	public List<CategoriaProcedimento> getSubcategorias() {
		return subcategorias;
	}
	
	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Procedimento> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Procedimento> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			Procedimento itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Procedimento>) rGeral.porChaveRegistro(Procedimento.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.procedimento = itemEdicao;			
		}
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.procedimento.getId() != null;
	}

	public List<Procedimento> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Procedimento> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

}
