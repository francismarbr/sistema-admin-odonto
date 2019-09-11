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
import com.agenciacrud.gestornegocio.model.ReceitaMedica;
import com.agenciacrud.gestornegocio.repositorio.ReceitasMedicas;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;
import com.agenciacrud.gestornegocio.util.relatorio.ExecutorRelatorio;

@Named("CImpressaoReceitaMedica")
@ViewScoped
public class CImpressaoReceitaMedica extends CGeral implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ReceitasMedicas receitas;
		
	@Inject
	private FacesContext facesContext;
	
	@Inject
	private HttpServletResponse response;
	
	private List<ReceitaMedica> listaReceitas;
	
	private ReceitaMedica receita;

	public CImpressaoReceitaMedica() {
		limpar();
	}
	
	public void limpar() {
		receita = new ReceitaMedica();
		listaReceitas = null;
	}
	
	public List<ReceitaMedica> obterItensRelatorio() {
		listaReceitas = receitas.porPaciente(getEmpresaLogada(), receita.getId());
		return listaReceitas;
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void gerarRelatorio(ReceitaMedica receita) {
		this.receita = receita;
		try {
			Map map = new HashMap();
		
			String relatorio = getCaminhoRaiz()+"WEB-INF\\classes\\relatorios\\impressao_receita_medica.jasper";
			
			// troca \ por /
			relatorio = relatorio.replace("\\", "/");
			
			//chama método que realiza consulta no banco de dados para os filtros selecionados
			List<ReceitaMedica> itensRelatorio = obterItensRelatorio();
			
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
				String arquivo = "receita_medica_" + sdf.format(new Date())+ ".pdf";
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
	
	public List<ReceitaMedica> getListaReceitas() {
		return listaReceitas;
	}

	public void setListaReceitas(List<ReceitaMedica> listaReceitas) {
		this.listaReceitas = listaReceitas;
	}

	public ReceitaMedica getReceita() {
		return receita;
	}
}
