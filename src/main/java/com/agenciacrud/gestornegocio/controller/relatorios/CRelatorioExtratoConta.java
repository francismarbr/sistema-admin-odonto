package com.agenciacrud.gestornegocio.controller.relatorios;

import java.io.Serializable;
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
import com.agenciacrud.gestornegocio.model.ContaBancaria;
import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.repositorio.Movimentacoes;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;
import com.agenciacrud.gestornegocio.util.relatorio.ExecutorRelatorio;

@Named("CRelatorioExtratoConta")
@ViewScoped
public class CRelatorioExtratoConta extends CGeral implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Movimentacoes movimentacoes;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private FacesContext facesContext;
	
	@Inject
	private HttpServletResponse response;
	
	private List<MovimentacaoConta> listaMovimentacoes;
	private List<ContaBancaria> listaContasBancarias;
	
	private MovimentacaoConta movimentacao;
	private Date dataInicial;
	private Date dataFinal;
	private ContaBancaria conta;


	
	public CRelatorioExtratoConta() {
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()){
			this.listaContasBancarias = this.rGeral.buscaTodos(ContaBancaria.class, getEmpresaLogada());
		}
		
	}
	
	public void limpar() {
		movimentacao = new MovimentacaoConta();
		listaMovimentacoes = null;
		setDataInicial(null);
		setDataFinal(null);
	}
	
	public List<MovimentacaoConta> obterItensRelatorio() {	
		listaMovimentacoes = movimentacoes.filtroRelatorioExtratoConta(getEmpresaLogada(),getDataInicial(), getDataFinal(), getConta());
		return listaMovimentacoes;
	}
	
	//busca pessoas por nome e atualiza o autocomplete
		public List<Pessoa> completarCliente(String nome) {
			return this.rGeral.porNome(Pessoa.class, nome, getEmpresaLogada());
		}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void gerarRelatorio() {
		
		try {
			Map map = new HashMap();
		
			String relatorio = getCaminhoRaiz()+"WEB-INF\\classes\\relatorios\\extrato_conta.jasper";
			
			// troca \ por /
			relatorio = relatorio.replace("\\", "/");
			
			//chama método que realiza consulta no banco de dados para os filtros selecionados
			List<MovimentacaoConta> itensRelatorio = obterItensRelatorio();
			
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
		
		if (dataInicial != null) {
			map.put("dataInicio", dataInicial);
		}

		if (dataFinal != null) {
			map.put("dataFim", dataFinal);
		}
		map.put("conta", getConta().getBanco().getNome()+ " - "+getConta().getAgencia()+"/"+getConta().getConta());
		map.put("usuario", getUsuarioLogado().getUsuario().getNome());

	}
	
	
	public List<MovimentacaoConta> getListaMovimentacoes() {
		return listaMovimentacoes;
	}

	public void setListaMovimentacoes(List<MovimentacaoConta> listaMovimentacoes) {
		this.listaMovimentacoes = listaMovimentacoes;
	}

	public List<ContaBancaria> getListaContasBancarias() {
		return listaContasBancarias;
	}

	public void setListaContasBancarias(List<ContaBancaria> listaContasBancarias) {
		this.listaContasBancarias = listaContasBancarias;
	}

	public MovimentacaoConta getMovimentacao() {
		return movimentacao;
	}

	public void setMovimentacao(MovimentacaoConta movimentacao) {
		this.movimentacao = movimentacao;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public ContaBancaria getConta() {
		return conta;
	}

	public void setConta(ContaBancaria conta) {
		this.conta = conta;
	}



}
