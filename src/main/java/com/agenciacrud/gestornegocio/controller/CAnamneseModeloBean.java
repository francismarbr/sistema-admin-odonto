package com.agenciacrud.gestornegocio.controller;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.AnamneseModelo;
import com.agenciacrud.gestornegocio.model.ItemAnamneseModelo;
import com.agenciacrud.gestornegocio.repositorio.AnamnesesModelos;
import com.agenciacrud.gestornegocio.repositorio.Procedimentos;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.repositorio.filtro.PedidoFilter;
import com.agenciacrud.gestornegocio.service.AnamneseService;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CAnamneseModeloBean")
@ViewScoped
public class CAnamneseModeloBean extends CGeral implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private AnamneseService anamneseService;
	
	@Inject
	private AnamnesesModelos anamneses;
	
	private AnamneseModelo anamneseModelo;
	private AnamneseModelo modeloSelecionado;
	

	private List<AnamneseModelo> buscaUltimoRegistro;
	private List<AnamneseModelo> listaModelosAnamneses;	
	
	
	private int itemLinha;
	
	private Long chaveRegistroEdicao;
	private List<AnamneseModelo> buscaRegistroEdicao;
	
	public CAnamneseModeloBean() {
		listaModelosAnamneses = new ArrayList<>();
		limpar();
	}
	
	public void inicializar() {
		pesquisar();	
	}
	
	private void limpar() {
		anamneseModelo = new AnamneseModelo();
	}
	
	public void pesquisar() {
		listaModelosAnamneses = anamneses.todasModelosAnamneses(getEmpresaLogada());
	}
	
	public void salvar() {
			//geração de sequência para chave de registro
			if(!Numero.isMaiorZero(this.anamneseModelo.getId())){
				AnamneseModelo chaveRegistro = null;
				
				//Recebe em uma lista o último registro da empresa
				setBuscaUltimoRegistro((List<AnamneseModelo>) rGeral.buscaUltimoRegistroPorEmpresa(AnamneseModelo.class, getEmpresaLogada()));
				
				if (getBuscaUltimoRegistro()==null) {
					//se a lista for vazia, soma 1 ao novo objeto
					anamneseModelo.setChaveRegistro(new Long(1));  
				} else {
					//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
					chaveRegistro = buscaUltimoRegistro.get(0);
					Long codigo = (Long) chaveRegistro.getChaveRegistro();
					codigo += 1;
					anamneseModelo.setChaveRegistro(codigo);
				}
			}
			this.anamneseModelo.removerItemVazio();
			
			this.anamneseModelo.setEmpresa(getEmpresaLogada());		
			this.anamneseModelo = this.anamneseService.salvarModelo(this.anamneseModelo);
			
			FacesUtil.addInfoMessage("Modelo salvo com sucesso!");

	}
	
	public void removerItemPedido() {
		if (Numero.isMaiorZero(itemLinha)) {
			System.out.println("procedimento na linha "+itemLinha);
			anamneseModelo.getItens().remove(itemLinha);
			itemLinha = 0;
		}
	}
	
	public void excluir() {
		anamneses.remover(modeloSelecionado);
		pesquisar();
		FacesUtil.addInfoMessage("O modelo " + modeloSelecionado.getNome()
				+ " foi excluído com sucesso.");
	}


	public List<AnamneseModelo> getListaModelosAnamneses() {
		return listaModelosAnamneses;
	}

	public AnamneseModelo getAnamneseModelo() {
		return anamneseModelo;
	}

	public void setAnamneseModelo(AnamneseModelo anamneseModelo) {
		this.anamneseModelo = anamneseModelo;
	}

	public AnamneseModelo getModeloSelecionado() {
		return modeloSelecionado;
	}

	public void setModeloSelecionado(AnamneseModelo modeloSelecionado) {
		this.modeloSelecionado = modeloSelecionado;
	}

	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<AnamneseModelo> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<AnamneseModelo> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			AnamneseModelo itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<AnamneseModelo>) rGeral.porChaveRegistro(AnamneseModelo.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.anamneseModelo = itemEdicao;			
		}

		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.anamneseModelo.getId() != null;
	}

	public List<AnamneseModelo> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<AnamneseModelo> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

	public int getItemLinha() {
		return itemLinha;
	}

	public void setItemLinha(int itemLinha) {
		this.itemLinha = itemLinha;
	}
	
}

