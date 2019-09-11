package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.Cidade;
import com.agenciacrud.gestornegocio.model.ItemAnamnesePaciente;
import com.agenciacrud.gestornegocio.model.ItemPedido;
import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.model.Parente;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoPessoaEnumerador;
import com.agenciacrud.gestornegocio.repositorio.AnamnesesPacientes;
import com.agenciacrud.gestornegocio.repositorio.Cidades;
import com.agenciacrud.gestornegocio.repositorio.Clientes;
import com.agenciacrud.gestornegocio.repositorio.Movimentacoes;
import com.agenciacrud.gestornegocio.repositorio.Parcelas;
import com.agenciacrud.gestornegocio.repositorio.Pedidos;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.CadastroClienteService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;


@Named("CClienteBean")
@ViewScoped
public class CClienteBean extends CGeral implements Serializable  {
	

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Pessoa pessoa;
	
	@Inject
	private Clientes clientes;
	
	@Inject
	private AnamnesesPacientes anamneses;
	
	@Inject
	private Movimentacoes movimentacoes;
	
	@Inject
	private Parcelas parcelas;
	
	@Inject
	private Cidades cidades;
	
	private Parente parente;
	
	@Inject
	private Pedidos pedidos;
	
	@Inject
	private CadastroClienteService cadastroClienteService;
	
	@Inject
	private RGeral rGeral;
		
	private ConsultaFilter filtro;
	private List<Pessoa> clientesFiltrados;
	List<Pessoa> listaAniversariantes = new ArrayList<Pessoa>();
	private List<Cidade> listaCidades;
	
	private List<Pessoa> buscaUltimoRegistro;
	private List<ItemAnamnesePaciente> anamnesesPaciente;
	private List<MovimentacaoConta> pagamentos;
	private List<Parcela> aReceber;
	private List<ItemPedido> pedidosTratamentos;
	
	private Long chaveRegistroEdicao;
	private List<Pessoa> buscaRegistroEdicao;

	private Pessoa pessoaSelecionado;
	private Parente parenteSelecionado;
	
	Date aniversariantesDoDia = null;
	String aniversariantesMes = "";

	public CClienteBean(){
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()){
			if (Numero.isMaiorZero(pessoa.getId())) {
				setExcluivel(true);
			}
		}
	}
	
	public void limpar() {
		pessoa = new Pessoa();
		parente = new Parente();
		filtro = new ConsultaFilter();
		clientesFiltrados = null;
	}
	
	public String novoPessoa() {
		pessoa = new Pessoa();
		return "CadastroCliente?faces-redirect=true";
	}
	
	public void salvar() {
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.pessoa.getId())){
			Pessoa chaveRegistro = null;
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<Pessoa>) rGeral.buscaUltimoRegistroPorEmpresa(Pessoa.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				pessoa.setChaveRegistro(new Long(1)); 
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				pessoa.setChaveRegistro(codigo);
			}
		}
		this.pessoa.setEmpresa(getEmpresaLogada());
		this.pessoa = cadastroClienteService.salvar(this.pessoa);
		limpar();
		
		FacesUtil.addInfoMessage("Registro salvo com sucesso");
	}
	
	public void adicionarParente() {
			System.out.println("Pessoa é "+pessoa.getNome());
			parente.setPaciente(pessoa);
			pessoa.getParentes().add(parente);
			parente = new Parente();
			System.out.println("A pessoa aqui depois do novo parente é " + pessoa.getId());
			
	}
	
	public void removerParente() {
		if (parente != null) {
			pessoa.getParentes().remove(parente);
			parente = new Parente();
			
		}
	}
	
	public void excluir() {
		clientes.remover(pessoaSelecionado);
		clientesFiltrados.remove(pessoaSelecionado);
		
		FacesUtil.addInfoMessage("Pessoa " + pessoaSelecionado.getNome()
				+ " excluído com sucesso.");
	}
	
	public void pesquisar() {
		List<Pessoa> listaAniversariantes = new ArrayList<Pessoa>();
		clientesFiltrados = clientes.filtrados(filtro, getEmpresaLogada());
		//verifica se foi feito filtro por dia de aniversário
		if(aniversariantesDoDia != null) {
			//recebe uma lista de todos clientes da base
			for(Pessoa p : clientesFiltrados) {
				//verifica se o cliente possui data de nascimento cadastrada
				if(p.getDataNascimento() != null) {
					
					Date dataAniversario = p.getDataNascimento();
					
					//particiona a data de aniversario para pegar apenas dia e mês
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
					String diaMesAniversario = sdf.format(dataAniversario);
					String diaMesAtual = sdf.format(aniversariantesDoDia);
					//compara a data de nascimento cadastrada com a data informada no filtro
					if(diaMesAniversario.equals(diaMesAtual)){
						listaAniversariantes.add(p);
						clientesFiltrados = listaAniversariantes;
					}
				}
			}
		} else if (aniversariantesMes != "") {
			//recebe uma lista de todos clientes da base
			for(Pessoa p : clientesFiltrados) {
				//verifica se o cliente possui data de nascimento cadastrada
				if(p.getDataNascimento() != null) {
					
					Date dataAniversario = p.getDataNascimento();
					//particiona a data de aniversario para pegar apenas o mês
					SimpleDateFormat sdf = new SimpleDateFormat("MM");
					String mesAniversario = sdf.format(dataAniversario);
					//compara a data de nascimento cadastrada com o mês informado no filtro
					if(aniversariantesMes.equals(mesAniversario)){
						listaAniversariantes.add(p);
						clientesFiltrados = listaAniversariantes;
					} 
				}
			}
			/* Se a lista de aniversariantes for vazia, significa que a 
			* pesquisa não encontrou resultados para os filtros informados
			*/
			if(listaAniversariantes.isEmpty()) {
				FacesUtil.addErrorMessage("Não existe aniversariantes desse mês");
				clientesFiltrados = null;
			}
		}
	}
	
	public void aniversariantesDeHoje() {
		clientesFiltrados = clientes.filtrados(filtro, getEmpresaLogada());
		//verifica se foi feito filtro por dia de aniversário
		aniversariantesDoDia = new Date();
			//recebe uma lista de todos clientes da base
			for(Pessoa p : clientesFiltrados) {
				//verifica se o cliente possui data de nascimento cadastrada
				if(p.getDataNascimento() != null) {
					
					Date dataAniversario = p.getDataNascimento();
					
					//particiona a data de aniversario para pegar apenas dia e mês
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
					String diaMesAniversario = sdf.format(dataAniversario);
					String diaMesAtual = sdf.format(aniversariantesDoDia);
					
					//compara a data de nascimento cadastrada com a data informada no filtro
					if(diaMesAniversario.equals(diaMesAtual)){
						System.out.println("Entrei no if");
						listaAniversariantes.add(p);
					}
				}
			} 
	}
	
	public void verHistoricoFinanceiro() {
		pagamentos = movimentacoes.recebimentoPorPaciente(getEmpresaLogada(), pessoa);
		aReceber = parcelas.filtroRelatorioContasReceber(getEmpresaLogada(), pessoa, null, null, null);
	}
	
	public void verAnamneses() {
		//lista anamneses já realizadas para esse paciente
		anamnesesPaciente = anamneses.anamnesesPaciente(getEmpresaLogada(), pessoa);
	}
	
	public void verTratamentos() {
		//lista tratamentos já realizadas para esse paciente ou apenas orçados
		pedidosTratamentos = pedidos.porPaciente(getEmpresaLogada(), pessoa);
	}
	
	public void obterCidades() {
		//busca todas cidades por uf selecionada na view
		listaCidades = cidades.cidadesPorUf(filtro);
	}
	
	public Pessoa getPessoa(){
		return pessoa;
	}
	
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	public List<Pessoa> getClientesFiltrados() {
		return clientesFiltrados;
	}
	
	public List<Pessoa> getListaAniversariantes() {
		return listaAniversariantes;
	}
	
	public ConsultaFilter getFiltro() {
		return filtro;
	}

	public Pessoa getPessoaSelecionado() {
		return pessoaSelecionado;
	}

	public void setPessoaSelecionado(Pessoa pessoaSelecionado) {
		this.pessoaSelecionado = pessoaSelecionado;
	}
	
	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Pessoa> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Pessoa> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
	
			Pessoa itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Pessoa>) rGeral.porChaveRegistro(Pessoa.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.pessoa = itemEdicao;
			if(pessoa.getEndereco().getCidade()!=null) {
				filtro.setUf(pessoa.getEndereco().getCidade().getUf());
				this.listaCidades = this.cidades.cidadesPorUf(filtro);
			}
		}
		//retorna se banco não existir, retorna falso, caso contrário, verdadeiro.
		return this.pessoa.getId() != null;
	}

	public Parente getParente() {
		return parente;
	}

	public void setParente(Parente parente) {
		this.parente = parente;
	}

	public Parente getParenteSelecionado() {
		return parenteSelecionado;
	}

	public void setParenteSelecionado(Parente parenteSelecionado) {
		this.parenteSelecionado = parenteSelecionado;
	}

	public List<Pessoa> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Pessoa> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

	public List<Cidade> getListaCidades() {
		return listaCidades;
	}

	public void setListaCidades(List<Cidade> listaCidades) {
		this.listaCidades = listaCidades;
	}

	public List<ItemAnamnesePaciente> getAnamnesesPaciente() {
		return anamnesesPaciente;
	}

	public List<MovimentacaoConta> getPagamentos() {
		return pagamentos;
	}

	public List<Parcela> getaReceber() {
		return aReceber;
	}

	public List<ItemPedido> getPedidosTratamentos() {
		return pedidosTratamentos;
	}
	
	public Date getAniversariantesDoDia() {
		return aniversariantesDoDia;
	}

	public void setAniversariantesDoDia(Date aniversariantesDoDia) {
		this.aniversariantesDoDia = aniversariantesDoDia;
	}

	public String getAniversariantesMes() {
		return aniversariantesMes;
	}

	public void setAniversariantesMes(String aniversariantesMes) {
		this.aniversariantesMes = aniversariantesMes;
	}

}
