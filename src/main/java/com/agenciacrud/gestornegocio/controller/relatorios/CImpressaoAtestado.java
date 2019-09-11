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
import com.agenciacrud.gestornegocio.model.Atestado;
import com.agenciacrud.gestornegocio.repositorio.Atestados;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;
import com.agenciacrud.gestornegocio.util.relatorio.ExecutorRelatorio;

@Named("CImpressaoAtestado")
@ViewScoped
public class CImpressaoAtestado extends CGeral implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Atestados atestados;
		
	@Inject
	private FacesContext facesContext;
	
	@Inject
	private HttpServletResponse response; 
	
	private List<Atestado> listaAtestados;
	
	private Atestado atestado;

	public CImpressaoAtestado() {
		limpar();
	}
	
	public void limpar() {
		atestado = new Atestado();
		listaAtestados = null;
	}
	
	public List<Atestado> obterItensRelatorio() {
		listaAtestados = atestados.porAtestado(getEmpresaLogada(), atestado.getId());
		return listaAtestados;
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void gerarRelatorio(Atestado atestado) {
		this.atestado = atestado;
		try {
			Map map = new HashMap();
		
			String relatorio = getCaminhoRaiz()+"WEB-INF\\classes\\relatorios\\impressao_atestado.jasper";
			
			// troca \ por /
			relatorio = relatorio.replace("\\", "/");
			
			//chama método que realiza consulta no banco de dados para os filtros selecionados
			List<Atestado> itensRelatorio = obterItensRelatorio();
			
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
				String arquivo = "atestado_" + sdf.format(new Date())+ ".pdf";
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
	
	public List<Atestado> getListaAtestados() {
		return listaAtestados;
	}

	public void setListaAtestados(List<Atestado> listaAtestados) {
		this.listaAtestados = listaAtestados;
	}

	public Atestado getAtestado() {
		return atestado;
	}
}
