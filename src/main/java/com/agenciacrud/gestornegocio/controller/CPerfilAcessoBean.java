package com.agenciacrud.gestornegocio.controller;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.Banco;
import com.agenciacrud.gestornegocio.model.Dentista;
import com.agenciacrud.gestornegocio.model.ItemPerfilAcesso;
import com.agenciacrud.gestornegocio.model.MovimentacaoEstoque;
import com.agenciacrud.gestornegocio.model.PerfilAcesso;
import com.agenciacrud.gestornegocio.model.ItemAnamneseModelo;
import com.agenciacrud.gestornegocio.model.Permissao;
import com.agenciacrud.gestornegocio.model.Produto;
import com.agenciacrud.gestornegocio.model.enumeradores.PlanoEmpresa;
import com.agenciacrud.gestornegocio.repositorio.PerfisAcesso;
import com.agenciacrud.gestornegocio.repositorio.Permissoes;
import com.agenciacrud.gestornegocio.repositorio.Procedimentos;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.repositorio.filtro.PedidoFilter;
import com.agenciacrud.gestornegocio.service.PerfilAcessoService;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CPerfilAcessoBean")
@ViewScoped
public class CPerfilAcessoBean extends CGeral implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private Permissoes permissoes;
	
	@Inject
	private PerfilAcessoService perfilAcessoService;
	
	@Inject
	private PerfisAcesso perfis;
	
	private PerfilAcesso perfilAcesso;
	private PerfilAcesso perfilSelecionado;
	
	private List<PerfilAcesso> buscaUltimoRegistro;
	private List<PerfilAcesso> listaPerfisAcesso;
	private List<Permissao> listaPermissoes;
	
	
	
	private int itemLinha;
	private Permissao permissao;
	private ItemPerfilAcesso itemPerfil;
	private Long chaveRegistroEdicao;
	private List<PerfilAcesso> buscaRegistroEdicao;
	
	public CPerfilAcessoBean() {
		itemPerfil = new ItemPerfilAcesso();
		listaPerfisAcesso = new ArrayList<>();
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()) {
			System.out.println("Dentro do inicializar");
			listaPermissoes = permissoes.buscaTudo(Permissao.class);

			for(Permissao p : listaPermissoes) {
				if(getEmpresaLogada().getPlano().getDescricao().equals(PlanoEmpresa.INICIAL)
						&& !p.isPlanoInicial()) {
					listaPermissoes.remove(p);
				}
				if(getEmpresaLogada().getPlano().getDescricao().equals(PlanoEmpresa.MEGA)
						&& !p.isPlanoMega()) {
					listaPermissoes.remove(p);
				}
				if(getEmpresaLogada().getPlano().getDescricao().equals(PlanoEmpresa.COMPLETO)
						&& !p.isPlanoCompleto()) {
					listaPermissoes.remove(p);
				}
			}
		}
		pesquisar();	
	}
	
	private void limpar() {
		perfilAcesso = new PerfilAcesso();
	}
	
	public void pesquisar() {
		listaPerfisAcesso = rGeral.buscaTodos(PerfilAcesso.class, getEmpresaLogada());
	}
	
	public void salvar() {
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.perfilAcesso.getId())){
			PerfilAcesso chaveRegistro = null;
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<PerfilAcesso>) rGeral.buscaUltimoRegistroPorEmpresa(PerfilAcesso.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				perfilAcesso.setChaveRegistro(new Long(1)); 
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				perfilAcesso.setChaveRegistro(codigo);
			}
		}
		perfilAcesso.setEmpresa(getEmpresaLogada());
		this.perfilAcesso = this.perfilAcessoService.salvar(this.perfilAcesso);
		
		FacesUtil.addInfoMessage("Modelo salvo com sucesso!");

	}
	
	public void adicionarItemLista(Permissao permissao) {
		itemPerfil.setPermissao(permissao);
		itemPerfil.setPerfilAcesso(perfilAcesso);
		perfilAcesso.getPermissoes().add(itemPerfil);
		listaPermissoes.remove(permissao);
		itemPerfil = new ItemPerfilAcesso();
	}
	
	public void removerItemPerfil() {
		if (Numero.isMaiorZero(itemLinha)) {
			perfilAcesso.getPermissoes().remove(itemLinha);
			itemLinha = 0;
		}
	}
	
	//busca produtos por nome e atualiza o autocomplete
		public List<Permissao> completarPermissao(String nome) {
			return this.permissoes.porNome(nome);
		}
	
	public void excluir() {
		perfis.remover(perfilSelecionado);
		pesquisar();
		FacesUtil.addInfoMessage("O perfil " + perfilSelecionado.getNome()
				+ " foi excluído com sucesso.");
	}


	public List<PerfilAcesso> getListaPerfisAcesso() {
		return listaPerfisAcesso;
	}

	public PerfilAcesso getPerfilAcesso() {
		return perfilAcesso;
	}

	public void setPerfilAcesso(PerfilAcesso perfilAcesso) {
		this.perfilAcesso = perfilAcesso;
	}

	public PerfilAcesso getPerfilSelecionado() {
		return perfilSelecionado;
	}

	public void setPerfilSelecionado(PerfilAcesso perfilSelecionado) {
		this.perfilSelecionado = perfilSelecionado;
	}

	public List<Permissao> getListaPermissoes() {
		return listaPermissoes;
	}

	public void setListaPermissoes(List<Permissao> listaPermissoes) {
		this.listaPermissoes = listaPermissoes;
	}


	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			PerfilAcesso itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<PerfilAcesso>) rGeral.porChaveRegistro(PerfilAcesso.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.perfilAcesso = itemEdicao;
			
			listaPermissoes = permissoes.buscaTudo(Permissao.class);
			

			for(ItemPerfilAcesso item : this.perfilAcesso.getPermissoes()) {
				//verifica se a permissão do sistema já foi adicionada no perfil
				if(listaPermissoes.contains(item.getPermissao())){
					listaPermissoes.remove(item.getPermissao());
				}
			}
		}
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.perfilAcesso.getId() != null;
	}

	public List<PerfilAcesso> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<PerfilAcesso> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

	public int getItemLinha() {
		return itemLinha;
	}

	public void setItemLinha(int itemLinha) {
		this.itemLinha = itemLinha;
	}

	public Permissao getPermissao() {
		return permissao;
	}

	public void setPermissao(Permissao permissao) {
		this.permissao = permissao;
	}

	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<PerfilAcesso> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<PerfilAcesso> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}
	
}

