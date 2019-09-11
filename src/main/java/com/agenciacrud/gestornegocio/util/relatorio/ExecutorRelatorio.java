package com.agenciacrud.gestornegocio.util.relatorio;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;

public class ExecutorRelatorio {
	
	
	private HttpServletResponse response;

	private String nomeArquivoSaida;
	
	private JasperPrint print;
	
	private boolean relatorioGerado;
	
	
	public ExecutorRelatorio(HttpServletResponse response,
			String nomeArquivoSaida, JasperPrint print) {
		
		this.response = response;
		this.nomeArquivoSaida = nomeArquivoSaida;
		this.print = print;
	}
	
	public void execute() {
		try {
			//InputStream relatorioStream = this.getClass().getResourceAsStream(this.caminhoRelatorio);
			
			//JasperPrint print = JasperFillManager.fillReport(relatorioStream, this.parametros);
			this.relatorioGerado = print.getPages().size() > 0;
			
			if (this.relatorioGerado) {
				JRExporter exportador = new JRPdfExporter();
				exportador.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
				exportador.setParameter(JRExporterParameter.JASPER_PRINT, print);
				
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + this.nomeArquivoSaida + "\"");
				
				exportador.exportReport();
			}
		} catch (Exception e) {
			FacesUtil.addErrorMessage("Erro ao executar relatório ");
		}
	}
	public boolean isRelatorioGerado() {
		return relatorioGerado;
	}

}
