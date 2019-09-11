package com.agenciacrud.gestornegocio.controller;



import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.agenciacrud.gestornegocio.model.Dentista;
import com.agenciacrud.gestornegocio.model.FormaPagamento;
import com.agenciacrud.gestornegocio.model.ItemPedido;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.Parente;
import com.agenciacrud.gestornegocio.model.Pedido;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.Procedimento;
import com.agenciacrud.gestornegocio.model.Receita;
import com.agenciacrud.gestornegocio.model.StatusPedido;
import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoIntervaloTempoEnumerador;
import com.agenciacrud.gestornegocio.repositorio.Clientes;
import com.agenciacrud.gestornegocio.repositorio.Pedidos;
import com.agenciacrud.gestornegocio.repositorio.Procedimentos;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.repositorio.Usuarios;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.repositorio.filtro.PedidoFilter;
import com.agenciacrud.gestornegocio.service.CadastroPedidoService;
import com.agenciacrud.gestornegocio.service.EmissaoPedidoService;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;
import com.agenciacrud.gestornegocio.validation.SKU;


@Named("CPedidoBean")
@ViewScoped
public class CPedidoBean extends CGeral implements Serializable {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private Clientes clientes;
	
	@Inject
	private Procedimentos procedimentos;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private CadastroPedidoService cadastroPedidoService;
	
	@Inject
	private EmissaoPedidoService emissaoPedidoService;
	
	@Inject
	private Usuarios usuarios;
	
	@Inject
	private Pedidos pedidos;
	
	@Produces
	@PedidoEdicao
	private Pedido pedido;
	
	@Inject
	private ItemPedido itemPedido;
	
	@Inject
	private Event<PedidoAlteradoEvent> pedidoAlteradoEvent;
	
	@SKU
	private String sku;
	
	private List<Dentista> listaDentistas;
	private List<Pessoa> listaClientes;
	private List<Pedido> buscaUltimoRegistro;
	private List<Receita> buscaUltimoRegistroReceita;
	private List<Pedido> pedidosFiltrados;	
	private List<FormaPagamento> listaFormasPagamento;
	
	private int itemLinha;
	
	private PedidoFilter filtro;
	private ConsultaFilter filtroClientes;
	
	private Procedimento procedimentoLinhaEditavel;
	
	private Receita receita;
	
	private String nomePesquisaCliente;
	private String cpfCnpjPesquisaCliente;
	private BigDecimal valorParcela;
	private BigDecimal valorEntrada;
	
	private Long chaveRegistroEdicao;
	private List<Pedido> buscaRegistroEdicao;
	
	public CPedidoBean() {
		filtro = new PedidoFilter();
		pedidosFiltrados = new ArrayList<>();
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()) {
			this.listaDentistas = this.rGeral.buscaDentistas(Dentista.class, getEmpresaLogada());
			this.listaFormasPagamento = this.rGeral.buscaTodos(FormaPagamento.class, getEmpresaLogada());
			
			this.pedido.adicionarItemVazio();
			
			this.recalcularPedido();
		}			
	}
	
	private void limpar() {
		pedido = new Pedido();
		//pedido.setEnderecoEntrega(new EnderecoEntrega());
	}
	
	public void pedidoAlterado(@Observes PedidoAlteradoEvent event) {
		this.pedido = event.getPedido();
	}
	
	public void recalcularPedido() {
		if(this.pedido != null) {
			this.pedido.recalcularValorToral();
		}
	}
	
	public void carregarProcedimentoLinhaEditavel() {
		ItemPedido item = this.pedido.getItens().get(0);
		
		if(this.procedimentoLinhaEditavel != null){
			//if(this.existeItemComProcedimento(this.procedimentoLinhaEditavel)){
				//FacesUtil.addErrorMessage("Já existe um item no pedido com o procedimento informado.");
			//} else {
				item.setProcedimento(this.procedimentoLinhaEditavel);
				item.setValor(this.procedimentoLinhaEditavel.getValorUnitario());
				
				this.pedido.adicionarItemVazio();
				this.procedimentoLinhaEditavel = null; //limpa produtoLinhaEditavel
				this.sku = null;
				
				this.pedido.recalcularValorToral();
			//}
			
		}
			
	}
	
	public void gerarReceita() {
		if(pedido.getCondicaoPagamento().getaVista().equals("s")) {
			pedido.getCondicaoPagamento().setValorEntrada(BigDecimal.ZERO);
		}
		setReceita(null);
		
		int quantidadeParcelas = pedido.getCondicaoPagamento().getQtParcelas();
		BigDecimal valorEntrada = pedido.getCondicaoPagamento().getValorEntrada();
		Date dataVencimento = new Date();

		List<Parcela> parcelas = new ArrayList<Parcela>();
		Parcela parcela = null;
		
		BigDecimal saldoReceita = pedido.getValorTotal();
		setValorEntrada(valorEntrada);

		saldoReceita = saldoReceita.subtract(getValorEntrada());

		BigDecimal valorPorParcela = new BigDecimal(0); 
		valorPorParcela = saldoReceita.divide(new BigDecimal(quantidadeParcelas),2, RoundingMode.HALF_UP);

		saldoReceita = saldoReceita.subtract(getValorEntrada());

		for (int nrParcela = 1; nrParcela <= quantidadeParcelas; nrParcela++) {
			Date dataParcela = null;
			dataParcela = DataUtil
					.addPeriodoPorTipoInvertvalo(dataVencimento, TipoIntervaloTempoEnumerador.MENSAL, nrParcela);

			parcela = new Parcela();
			parcela.setNumero(nrParcela);
			parcela.setDataVencimento(dataParcela);
			parcela.setReceita(getReceita());
			parcela.setValor(valorPorParcela);
			parcela.setSituacao(SituacaoPagamentoEnumerador.ABERTA);
			parcelas.add(parcela);
		}

		if (Numero.isMaiorZero(valorEntrada)) {
			parcela = new Parcela();
			parcela.setNumero(0);
			parcela.setDataVencimento(dataVencimento);
			parcela.setReceita(getReceita());
			parcela.setValor(getValorEntrada());
			parcela.setSituacao(SituacaoPagamentoEnumerador.ABERTA);
			parcelas.add(parcela);
			quantidadeParcelas++;
		}
		
		getReceita().setEmpresa(getEmpresaLogada());
		getReceita().setNome("Ref. Tratamento cod " + pedido.getChaveRegistro());
		getReceita().setValorTotal(pedido.getValorTotal());
		getReceita().setDataLancamento(new Date());
		getReceita().setDataVencimento(dataVencimento);
		getReceita().setQuantidadeParcelas(quantidadeParcelas);
		getReceita().setCliente(pedido.getPessoa());
		getReceita().setDentista(pedido.getDentista());
		getReceita().setPedidoVenda(pedido);
		getReceita().setCategoriaConta(pedido.getEmpresa().getCategoriaConta());
		
		getReceita().setParcelas(parcelas);
	}
	
	private boolean existeItemComProcedimento(Procedimento procedimento) {
		boolean existeItem = false;
		
		for(ItemPedido item : this.getPedido().getItens()) {
			if(procedimento.equals(item.getProcedimento())) {
				existeItem = true;
				break;
			}
		}
		
		return existeItem;
	}
	
	public List<Procedimento> completarProcedimento(String nome) {
		return this.procedimentos.porNome(nome, getEmpresaLogada());
	}
	
	public void atualizarQuantidade(ItemPedido item, int linha) {
		if(item.getQuantidade() <1 ) {
			if(linha == 0) {
				item.setQuantidade(1);
			} else {
				this.getPedido().getItens().remove(linha);
			}
		}
		
		this.pedido.recalcularValorToral();
	}
	
	public void getInicializarFatura(){
		System.out.println("Entrou no inicaliza fatura.");
		
		//gerarReceita();
	}
	
	public void pesquisar() {
		filtro.setEmpresa(getEmpresaLogada());
		pedidosFiltrados = pedidos.filtrados(filtro);
	}
	
	public String salvar() {
		
		//geração de sequência para chave de registro
		if(!Numero.isMaiorZero(this.pedido.getId())){
			Pedido chaveRegistro = null;
			
			//Recebe em uma lista o último registro da empresa
			setBuscaUltimoRegistro((List<Pedido>) rGeral.buscaUltimoRegistroPorEmpresa(Pedido.class, getEmpresaLogada())); 
			
			if (getBuscaUltimoRegistro()==null) {
				//se a lista for vazia, soma 1 ao novo objeto
				pedido.setChaveRegistro(new Long(1));  
			} else {
				//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
				chaveRegistro = buscaUltimoRegistro.get(0);
				Long codigo = (Long) chaveRegistro.getChaveRegistro();
				codigo += 1;
				pedido.setChaveRegistro(codigo);
			}
		}
		this.pedido.removerItemVazio();
		
		try{
			this.pedido.setUsuario(getUsuario());
			this.pedido.setEmpresa(getEmpresaLogada());
			this.pedido = this.cadastroPedidoService.salvar(this.pedido);
		
		FacesUtil.addInfoMessage("Pedido salvo com sucesso!");
		} finally {
			this.pedido.adicionarItemVazio();
			return "CadastroPedido?faces-redirect=true&pedido=" + pedido.getChaveRegistro();
		}
	}
	
	@Transactional
	public void emitirPedido() {
		this.pedido.removerItemVazio();
		pedido.setReceita(getReceita());
		
		
		try {
			
			//geração de sequência para chave de registro
			if(!Numero.isMaiorZero(this.pedido.getReceita().getId())){
				Receita chaveRegistro = null;
				
				//Recebe em uma lista o último registro da empresa
				setBuscaUltimoRegistroReceita((List<Receita>) rGeral.buscaUltimoRegistroPorEmpresa(Receita.class, getEmpresaLogada()));
				
				if (getBuscaUltimoRegistroReceita()==null) {
					//se a lista for vazia, soma 1 ao novo objeto
					pedido.getReceita().setChaveRegistro(new Long(1));  
				} else {
					//Pega o valor da chave de registro do objeto e soma + 1 e atribui ao novo Objeto
					chaveRegistro = buscaUltimoRegistroReceita.get(0);
					Long codigo = (Long) chaveRegistro.getChaveRegistro();
					codigo += 1;
					
					pedido.getReceita().setChaveRegistro(codigo);
				}
			}
			
			this.pedido = this.emissaoPedidoService.emitir(this.pedido);
			this.pedidoAlteradoEvent.fire(new PedidoAlteradoEvent(this.pedido));//lançar um evento CDI atualiza objeto pedido
			
			
			
			
			FacesUtil.addInfoMessage("Pedido Emitido com sucesso!");
		} finally {
			pedido.setId(this.pedido.getId());
			this.pedido.adicionarItemVazio();
		}
	}
	
	public void removerItemPedido() {
		if (Numero.isMaiorZero(itemLinha)) {
			System.out.println("procedimento na linha "+itemLinha);
			pedido.getItens().remove(itemLinha);
			itemLinha = 0;
		}
	}
	
	//retorna um array com status
	public StatusPedido[] getStatuses() {
		return StatusPedido.values(); //values retorna array do tipo da enumeração; no caso do tipo pedido
	}
	
	public PedidoFilter getFiltro() {
		return filtro;
	}

	public ConsultaFilter getFiltroClientes() {
		return filtroClientes;
	}

	public List<Pedido> getPedidosFiltrados() {
		return pedidosFiltrados;
	}
	
	public List<Pessoa> completarCliente(String nome) {
		return this.clientes.porNome(nome, getEmpresaLogada());
	}
	
	public void pesquisarClientes() {
		listaClientes = clientes.filtrados(filtroClientes, getEmpresaLogada());
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public List<Dentista> getListaDentistas() {
		return listaDentistas;
	}

	public void setListaDentistas(List<Dentista> listaDentistas) {
		this.listaDentistas = listaDentistas;
	}

	public List<FormaPagamento> getListaFormasPagamento() {
		return listaFormasPagamento;
	}

	public List<Pessoa> getListaClientes() {
		return listaClientes;
	}

	public void setListaClientes(List<Pessoa> listaClientes) {
		this.listaClientes = listaClientes;
	}

	public Long getChaveRegistroEdicao() {
		return chaveRegistroEdicao;
	}

	public void setChaveRegistroEdicao(Long chaveRegistroEdicao) {
		this.chaveRegistroEdicao = chaveRegistroEdicao;
	}

	public List<Pedido> getBuscaRegistroEdicao() {
		return buscaRegistroEdicao;
	}

	public void setBuscaRegistroEdicao(List<Pedido> buscaRegistroEdicao) {
		this.buscaRegistroEdicao = buscaRegistroEdicao;
	}

	public boolean isEditando() {
		if(Numero.isMaiorZero(chaveRegistroEdicao)){ 
			Pedido itemEdicao = null;
			//Recebe em uma lista o registro a ser editado
			setBuscaRegistroEdicao((List<Pedido>) rGeral.porChaveRegistro(Pedido.class, getChaveRegistroEdicao(), getEmpresaLogada()));
			
			if(getBuscaRegistroEdicao() == null){
				FacesUtil.addErrorMessage("O registro que você procura não foi encontrado.");
				return false;
			}
			//Pega o valor da chave de registro e atribui ao novo Objeto
			itemEdicao = buscaRegistroEdicao.get(0);
			this.pedido = itemEdicao;			
		}
		if(pedido.getId() != null && pedido.getStatus().equals(StatusPedido.ORCAMENTO))
			pedido.adicionarItemVazio();
		//se não existir, retorna falso, caso contrário, verdadeiro.
		return this.pedido.getId() != null;
	}
	
	public boolean isAvista() {
		if(pedido.getCondicaoPagamento().getaVista().equals("s")){
			this.pedido.getCondicaoPagamento().setQtParcelas(1);
			return true;
		} else{
			return false;
		}
	}

	public Procedimento getProcedimentoLinhaEditavel() {
		return procedimentoLinhaEditavel;
	}

	public void setProcedimentoLinhaEditavel(Procedimento procedimentoLinhaEditavel) {
		this.procedimentoLinhaEditavel = procedimentoLinhaEditavel;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Receita getReceita() {
		if(receita == null)
			receita = new Receita();
		return receita;
	}

	public void setReceita(Receita receita) {
		this.receita = receita;
	}

	public String getNomePesquisaCliente() {
		return nomePesquisaCliente;
	}

	public void setNomePesquisaCliente(String nomePesquisaCliente) {
		this.nomePesquisaCliente = nomePesquisaCliente;
	}

	public String getCpfCnpjPesquisaCliente() {
		return cpfCnpjPesquisaCliente;
	}

	public void setCpfCnpjPesquisaCliente(String cpfCnpjPesquisaCliente) {
		this.cpfCnpjPesquisaCliente = cpfCnpjPesquisaCliente;
	}


	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	public BigDecimal getValorEntrada() {
		return valorEntrada;
	}

	public void setValorEntrada(BigDecimal valorEntrada) {
		this.valorEntrada = valorEntrada;
	}

	public List<Pedido> getBuscaUltimoRegistro() {
		return buscaUltimoRegistro;
	}

	public void setBuscaUltimoRegistro(List<Pedido> buscaUltimoRegistro) {
		this.buscaUltimoRegistro = buscaUltimoRegistro;
	}

	public List<Receita> getBuscaUltimoRegistroReceita() {
		return buscaUltimoRegistroReceita;
	}

	public void setBuscaUltimoRegistroReceita(
			List<Receita> buscaUltimoRegistroReceita) {
		this.buscaUltimoRegistroReceita = buscaUltimoRegistroReceita;
	}

	public ItemPedido getItemPedido() {
		return itemPedido;
	}

	public void setItemPedido(ItemPedido itemPedido) {
		this.itemPedido = itemPedido;
	}

	public int getItemLinha() {
		return itemLinha;
	}

	public void setItemLinha(int itemLinha) {
		this.itemLinha = itemLinha;
	}


	
}

