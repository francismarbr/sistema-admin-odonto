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
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import com.agenciacrud.gestornegocio.controller.CGeral;
import com.agenciacrud.gestornegocio.model.Pedido;
import com.agenciacrud.gestornegocio.model.ItemAnamnesePaciente;
import com.agenciacrud.gestornegocio.repositorio.Pedidos;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;
import com.agenciacrud.gestornegocio.util.relatorio.ExecutorRelatorio;

@Named("CImpressaoTratamento")
@ViewScoped
public class CImpressaoTratamento extends CGeral implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Pedidos pedidos;
		
	@Inject
	private FacesContext facesContext;
	
	@Inject
	private HttpServletResponse response; 
	
	private List<Pedido> listaPedido;
	
	private Pedido pedido;

	public CImpressaoTratamento() {
		limpar();
	}
	
	public void limpar() {
		pedido = new Pedido();
		listaPedido = null;
	}
	
	public List<Pedido> obterItensRelatorio() {
		listaPedido = pedidos.porPedido(pedido.getId());
		return listaPedido;
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void gerarRelatorio(Pedido pedido) {
		this.pedido = pedido;
		try {
			Map map = new HashMap();
		
			String relatorio = getCaminhoRaiz()+"WEB-INF\\classes\\relatorios\\impressao_tratamento.jasper";
			
			// troca \ por /
			relatorio = relatorio.replace("\\", "/");
			
			//chama método que realiza consulta no banco de dados para os filtros selecionados
			List<Pedido> itensRelatorio = obterItensRelatorio();
			
			if (itensRelatorio.size() == 0) {
				FacesUtil.addInfoMessage("Nenhum registro foi encontrado para os parâmetros informados");
			} else {
				//recebe um coleção de itens 
				JRDataSource jrd = new JRBeanCollectionDataSource(itensRelatorio);
				adicionaCabecalhoFiltros(map); 
				
				//Seta caminho do subrelatório
				String subrelatorio_itens = getCaminhoRaiz()+"WEB-INF\\classes\\relatorios\\impressao_tratamento_itens.jasper";
				
				// troca \ por /
				subrelatorio_itens = subrelatorio_itens.replace("\\", "/"); 

				JasperReport subrelatorio_itens_jasper = (JasperReport) JRLoader.loadObjectFromFile(subrelatorio_itens);
				
				//inclui itens no relatório
				map.put("url_subrelatorio_itens",subrelatorio_itens_jasper);
	
				JasperPrint print = JasperFillManager.fillReport(relatorio, map, jrd);
				
				//cria formato de data para ser impresso junto com o nome do relatório
				SimpleDateFormat sdf = new SimpleDateFormat("_dd_MM_yyyy_hh_mm_SS");
				
				//Nome do arquivo a ser gerado em pdf
				String arquivo = "orçamento_tratamento_" + sdf.format(new Date())+ ".pdf";
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
		map.put("usuario", getUsuarioLogado().getUsuario().getNome());
	}
	
	public List<Pedido> getListaPedido() {
		return listaPedido;
	}

	public void setListaPedido(List<Pedido> listaPedido) {
		this.listaPedido = listaPedido;
	}

	public Pedido getPedido() {
		return pedido;
	}
}
