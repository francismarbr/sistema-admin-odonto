package com.agenciacrud.gestornegocio.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;

import com.agenciacrud.gestornegocio.model.Banco;
import com.agenciacrud.gestornegocio.model.ContaBancaria;
import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.repositorio.ContasBancarias;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.filtro.ContaBancariaFilter;
import com.agenciacrud.gestornegocio.service.ContaBancariaService;
import com.agenciacrud.gestornegocio.service.MovimentacaoService;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

@Named("CContaBancariaBean")
@ViewScoped
public class CContaBancariaBean extends CGeral implements Serializable {
	

	private static final long serialVersionUID = 1L;

	@Inject //chama método produtor
	private ContasBancarias contasBancarias;
	
	private ContaBancariaFilter filtro;
	private List<ContaBancaria> contasFiltradas;
	
	private ContaBancaria contaSelecionada;
	
	@Inject
	private ContaBancariaService contaBancariaService;
	
	@Inject
	private RGeral rGeral; //repositório Geral
	
	@Inject
	private MovimentacaoService movimentacaoService;
	
	private ContaBancaria contaBancaria;
	private BigDecimal saldoOriginalConta = BigDecimal.ZERO;
	
	@NotNull
	private Banco banco;	

	private List<Banco> listaBancos;
	private List<ContaBancaria> buscaUltimoRegistro;
	
	private Long chaveRegistroEdicao;
	private List<ContaBancaria> buscaRegistroEdicao;
	
	public CContaBancariaBean(){
		limpar();
	}
	
	public void inicializar() {
		//Verifica se já há um carregamento ativo, caso sim não executa novamente a busca
		if(FacesUtil.isNotPostback()){
			listaBancos = rGeral.buscaTodos(Banco.class, getEmpresaLogada());
		}
	}
	
	private void limpar() {
		contaBancaria = new ContaBancaria();
		listaBancos = null;
		filtro = new ContaBancariaFilter();
	}
	
	public void salvar(){
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.contaBancaria.getId())){
			ContaBancaria chaveRegistro = null;
			
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<ContaBancaria>) rGeral.buscaUltimoRegistroPorEmpresa(ContaBancaria.class, getEmpresaLogada()));
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				contaBancaria.setChaveRegistro(new Long(1));  
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				contaBancaria.setChaveRegistro(codigo);
			}
		}
		
		this.contaBancaria.setEmpresa(getEmpresaLogada());
		//vai até a classe conta verificar se já existe a conta cadastrada , pois não pode ser igual
		this.contaBancaria = contaBancariaService.salvar(this.contaBancaria);
		
		//Se o saldo original for diferente do novo saldo, houve alguma alterção e por isso registra uma nova movimentação
		if(!getSaldoOriginalConta().equals(contaBancaria.getSaldo())) {
			MovimentacaoConta mov = null;
			
			mov = new MovimentacaoConta();
						
			mov.setDataLancamento(new Date());
			mov.setValorMovimentado(new BigDecimal(0));
			mov.setDescricao("Alteração de conta no sistema");
			mov.setConta(contaBancaria);
			mov.setSaldo(contaBancaria.getSaldo());
			mov.setEmpresa(getEmpresaLogada());
			movimentacaoService.salvar(mov);
		}			
		
		limpar();
		FacesUtil.addInfoMessage("Conta Bancária salva com sucesso");		
	}
	
	public void excluir() {
		contasBancarias.remover(contaSelecionada);
		contasFiltradas.remove(contaSelecionada);
		
		FacesUtil.addInfoMessage("A conta bancária de código " + contaSelecionada.getChaveRegistro()
				+ "excluído com sucesso.");
	}

	public void pesquisar() {
		contasFiltradas = contasBancarias.filtrados(filtro, getEmpresaLogada());
	}

	public List<ContaBancaria> getContasFiltradas() {
		return contasFiltradas;
	}
	
	public ContaBancariaFilter getFiltro() {
		return filtro;
	}

	public ContaBancaria getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(ContaBancaria contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}
	
	public ContaBancaria getContaBancaria(){
		return contaBancaria;
	}

	public void setContaBancaria(ContaBancaria contaBancaria) {
		this.contaBancaria = contaBancaria;
	}

	public List<Banco> getlistaBancos() {
		return listaBancos;
	}
	
	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<ContaBancaria> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<ContaBancaria> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){
			ContaBancaria itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<ContaBancaria>) rGeral.porChaveRegistro(ContaBancaria.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.contaBancaria = itemEdicao;			
		}
		
		setSaldoOriginalConta(this.contaBancaria.getSaldo());
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.contaBancaria.getId() != null;
	}
	
	
	public BigDecimal getSaldoOriginalConta() {
		return saldoOriginalConta;
	}

	public void setSaldoOriginalConta(BigDecimal saldoOriginalConta) {
		this.saldoOriginalConta = saldoOriginalConta;
	}

	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public List<ContaBancaria> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<ContaBancaria> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

}
