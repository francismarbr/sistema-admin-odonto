package com.agenciacrud.gestornegocio.controller.relatorios;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.agenciacrud.gestornegocio.controller.CGeral;
import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.model.Fornecedor;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.repositorio.Parcelas;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;
import com.agenciacrud.gestornegocio.util.relatorio.ExecutorRelatorio;

@Named("CRelatorioDespesa")
@ViewScoped
public class CRelatorioDespesa extends CGeral implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Parcelas parcelas;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private FacesContext facesContext;
	
	@Inject
	private HttpServletResponse response;
	
	private List<Parcela> listaParcela;
	private List<CategoriaConta> listaCategoriaContas;
	private Parcela parcela;
	
	private Date dataVencimentoInicial;
	private Date dataVencimentoFinal;
	private Date dataLancamentoInicial;
	private Date dataLancamentoFinal;
	
	private BigDecimal totalDespesas = BigDecimal.ZERO;
	private BigDecimal totalPago = BigDecimal.ZERO;
	private BigDecimal totalAPagar = BigDecimal.ZERO;

	
	public CRelatorioDespesa() {
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()){
			this.listaCategoriaContas = this.rGeral.buscaTodos(CategoriaConta.class, getEmpresaLogada());
		}
		
	}
	
	public void limpar() {
		parcela = new Parcela();
		listaParcela = null;
		setDataVencimentoInicial(null);
		setDataVencimentoFinal(null);
		setDataLancamentoInicial(null);
		setDataLancamentoFinal(null);
	}
	
	public List<Parcela> obterItensRelatorio() {	
		listaParcela = parcelas.filtroRelatorioDespesas(getEmpresaLogada(), parcela.getDespesa().getFornecedor(), 
				parcela.getDespesa().getCategoriaConta(), parcela.getSituacao(),
				getDataVencimentoInicial(), getDataVencimentoFinal(), getDataLancamentoInicial(), getDataLancamentoFinal());
		
		totalDespesas = BigDecimal.ZERO;
		totalPago = BigDecimal.ZERO;
		totalAPagar = BigDecimal.ZERO;
		
		for (Parcela p : listaParcela) {
			totalDespesas = totalDespesas.add(p.getValor()).subtract(p.getValorTotalCanceladoPorParcela());
			totalPago = totalPago.add(p.getValorTotalRecebidoPorParcela());
		}
		
		totalAPagar = totalDespesas.subtract(totalPago);
		
		return listaParcela;
	}
	
	//busca pessoas por nome e atualiza o autocomplete
	public List<Fornecedor> completarFornecedor(String nome) {
		return this.rGeral.porNome(Fornecedor.class, nome, getEmpresaLogada());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void gerarRelatorio() throws Throwable {
		
		try {
			Map map = new HashMap();
		
			String relatorio = getCaminhoRaiz()+"WEB-INF\\classes\\relatorios\\relatorio_despesa.jasper";
			
			// troca \ por /
			relatorio = relatorio.replace("\\", "/");
			
			//chama método que realiza consulta no banco de dados para os filtros selecionados
			List<Parcela> itensRelatorio = obterItensRelatorio();
			
			if (itensRelatorio.size() == 0) {
				FacesUtil.addInfoMessage("Nenhum registro foi encontrado para os parâmetros informados");
			} else {
				//recebe um coleção de itens 
				JRDataSource jrd = new JRBeanCollectionDataSource(itensRelatorio);
				adicionaCabecalhoFiltros(map);
	
				JasperPrint print = JasperFillManager.fillReport(relatorio, map, jrd);
				
				//cria formato de data para ser impresso junto com o nome do relatório
				SimpleDateFormat sdf = new SimpleDateFormat("_dd_MM_yyyy_hh_mm_SS");
				
				//Nome do arquivo a ser gerado em pdf
				String arquivo = "relatorio_despesa" + sdf.format(new Date())+ ".pdf";
				
				//instancia a classe de execução do relatório e passa parâmetros para o construtor
				ExecutorRelatorio executor = new ExecutorRelatorio(this.response, arquivo, print);
				
				//chama método que cria o arquivo pdf
				executor.execute();
	
				facesContext.responseComplete();
			}
		} catch (JRException e) {
			FacesUtil.addErrorMessage("Falha ao gerar relatório.");
			log.error("Falha ao gerar relatório.", e);
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Falha ao gerar gerar PDF do relatório.");
			log.error("Falha ao gerar gerar PDF do relatório.", e);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void adicionaCabecalhoFiltros(Map map) {
		
		if (Numero.isMaiorZero(parcela.getDespesa().getFornecedor().getId())) {
			map.put("fornecedor", parcela.getDespesa().getFornecedor().getNome());
		} else {
			map.put("fornecedor", "Todos");
		}

		if (parcela.getDespesa().getCategoriaConta() != null) {
			map.put("categoria", parcela.getDespesa().getCategoriaConta().getNome());
		} else {
			map.put("categoria", "Todas");
		}

		if (dataVencimentoInicial != null) {
			map.put("dataInicioParcela", dataVencimentoInicial);
		}

		if (dataVencimentoFinal != null) {
			map.put("dataFimParcela", dataVencimentoFinal);
		}

		if (dataLancamentoInicial != null) {
			map.put("dataInicioDespesa", dataLancamentoInicial);
		}

		if (dataLancamentoFinal != null) {
			map.put("dataFimDespesa", dataLancamentoFinal);
		}

		if (parcela.getSituacao()!= null) {
			map.put("situacao", parcela.getSituacao().getDescricao());
		} else {
			map.put("situacao", "Todas");
		}

		
		map.put("usuario", getUsuarioLogado().getUsuario().getNome());



	}
	
	public List<Parcela> getListaParcela() {
		return listaParcela;
	}
	public void setListaParcela(List<Parcela> listaParcela) {
		this.listaParcela = listaParcela;
	}
	public List<CategoriaConta> getListaCategoriaContas() {
		return listaCategoriaContas;
	}

	public void setListaCategoriaContas(List<CategoriaConta> listaCategoriaContas) {
		this.listaCategoriaContas = listaCategoriaContas;
	}

	public Parcela getParcela() {
		return parcela;
	}

	public void setParcela(Parcela parcela) {
		this.parcela = parcela;
	}

	public Date getDataVencimentoInicial() {
		return dataVencimentoInicial;
	}

	public void setDataVencimentoInicial(Date dataVencimentoInicial) {
		this.dataVencimentoInicial = dataVencimentoInicial;
	}

	public Date getDataVencimentoFinal() {
		return dataVencimentoFinal;
	}

	public void setDataVencimentoFinal(Date dataVencimentoFinal) {
		this.dataVencimentoFinal = dataVencimentoFinal;
	}

	public Date getDataLancamentoInicial() {
		return dataLancamentoInicial;
	}

	public void setDataLancamentoInicial(Date dataLancamentoInicial) {
		this.dataLancamentoInicial = dataLancamentoInicial;
	}

	public Date getDataLancamentoFinal() {
		return dataLancamentoFinal;
	}

	public void setDataLancamentoFinal(Date dataLancamentoFinal) {
		this.dataLancamentoFinal = dataLancamentoFinal;
	}

	public BigDecimal getTotalDespesas() {
		return totalDespesas;
	}

	public BigDecimal getTotalPago() {
		return totalPago;
	}

	public BigDecimal getTotalAPagar() {
		return totalAPagar;
	}
}
