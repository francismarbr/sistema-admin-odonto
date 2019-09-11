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
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.repositorio.Parcelas;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;
import com.agenciacrud.gestornegocio.util.relatorio.ExecutorRelatorio;

@Named("CRelatorioReceita")
@ViewScoped
public class CRelatorioReceita extends CGeral implements Serializable {

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
	
	private BigDecimal totalReceitas = BigDecimal.ZERO;
	private BigDecimal totalRecebido = BigDecimal.ZERO;
	private BigDecimal totalAReceber = BigDecimal.ZERO;

	
	public CRelatorioReceita() {
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
		listaParcela = parcelas.filtroRelatorioReceitas(getEmpresaLogada(), parcela.getReceita().getCliente(), 
				parcela.getReceita().getCategoriaConta(), parcela.getSituacao(),
				getDataVencimentoInicial(), getDataVencimentoFinal(), getDataLancamentoInicial(), getDataLancamentoFinal());
		
		totalReceitas = BigDecimal.ZERO;
		totalRecebido = BigDecimal.ZERO;
		totalAReceber = BigDecimal.ZERO;
		
		for (Parcela p : listaParcela) {
			totalReceitas = totalReceitas.add(p.getValor()).subtract(p.getValorTotalCanceladoPorParcela());
			totalRecebido = totalRecebido.add(p.getValorTotalRecebidoPorParcela());
		}
		
		totalAReceber = totalReceitas.subtract(totalRecebido);
		
		return listaParcela;
	}
	
	//busca pessoas por nome e atualiza o autocomplete
		public List<Pessoa> completarCliente(String nome) {
			return this.rGeral.porNome(Pessoa.class, nome, getEmpresaLogada());
		}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void gerarRelatorio() {
		
		try {
			Map map = new HashMap();
		
			String relatorio = getCaminhoRaiz()+"WEB-INF\\classes\\relatorios\\relatorio_receita.jasper";
			
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
				String arquivo = "relatorio_receita" + sdf.format(new Date())+ ".pdf";
				
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
		
		if (Numero.isMaiorZero(parcela.getReceita().getCliente().getId())) {
			map.put("cliente", parcela.getReceita().getCliente().getNome());
		} else {
			map.put("cliente", "Todos");
		}

		if (parcela.getReceita().getCategoriaConta() != null) {
			map.put("categoria", parcela.getReceita().getCategoriaConta().getNome());
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
			map.put("dataInicioReceita", dataLancamentoInicial);
		}

		if (dataLancamentoFinal != null) {
			map.put("dataFimReceita", dataLancamentoFinal);
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

	public BigDecimal getTotalReceitas() {
		return totalReceitas;
	}

	public BigDecimal getTotalRecebido() {
		return totalRecebido;
	}

	public BigDecimal getTotalAReceber() {
		return totalAReceber;
	}
}
