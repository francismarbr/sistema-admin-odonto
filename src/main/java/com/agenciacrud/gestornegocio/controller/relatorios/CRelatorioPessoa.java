package com.agenciacrud.gestornegocio.controller.relatorios;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
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
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.repositorio.Clientes;
import com.agenciacrud.gestornegocio.repositorio.RGeral;
import com.agenciacrud.gestornegocio.util.jsf.FacesUtil;
import com.agenciacrud.gestornegocio.util.relatorio.ExecutorRelatorio;

@Named("CRelatorioPessoa")
@RequestScoped
public class CRelatorioPessoa extends CGeral implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Clientes clientes;
	
	@Inject
	private RGeral rGeral;
	
	@Inject
	private FacesContext facesContext;
	
	@Inject
	private HttpServletResponse response;
	
	private List<Pessoa> listaPessoa;
	
	private Pessoa pessoa;

	
	private List<RegimeTributario> listaRegimesTributarios;
	
	public CRelatorioPessoa() {
		limpar();
	}
	
	public void inicializar() {
		if(FacesUtil.isNotPostback()){
			setListaRegimesTributarios(rGeral.buscaTodos(RegimeTributario.class, getEmpresaLogada()));
		}
		
	}
	
	
	public void limpar() {
		pessoa = new Pessoa();
		listaRegimesTributarios = null;
		listaPessoa = null;
	}
	
	public List<Pessoa> obterItensRelatorio() {
		listaPessoa = clientes.filtroRelatorio(getEmpresaLogada(), pessoa.getId(), pessoa.getStatus());
		return listaPessoa;
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void gerarRelatorio() {
		
		try {
			Map map = new HashMap();
		
			String relatorio = getCaminhoRaiz()+"WEB-INF\\classes\\relatorios\\relatorio_pessoa.jasper";
			
			// troca \ por /
			relatorio = relatorio.replace("\\", "/");
			
			//chama método que realiza consulta no banco de dados para os filtros selecionados
			List<Pessoa> itensRelatorio = obterItensRelatorio();
			
			if (itensRelatorio.size() == 0) {
				FacesUtil.addInfoMessage("Nenhum registro foi encontrado para os parâmetros informados");
			}
			
			//recebe um coleção de itens 
			JRDataSource jrd = new JRBeanCollectionDataSource(itensRelatorio);
			adicionaCabecalhoFiltros(map);

			JasperPrint print = JasperFillManager.fillReport(relatorio, map, jrd);
			
			//cria formato de data para ser impresso junto com o nome do relatório
			SimpleDateFormat sdf = new SimpleDateFormat("_dd_MM_yyyy_hh_mm_SS");
			
			//Nome do arquivo a ser gerado em pdf
			String arquivo = "relatorio_pessoa" + sdf.format(new Date())+ ".pdf";
			
			//instancia a classe de execução do relatório e passa parâmetros para o construtor
			ExecutorRelatorio executor = new ExecutorRelatorio(this.response, arquivo, print);
			
			//chama método que cria o arquivo pdf
			executor.execute();

			facesContext.responseComplete();
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

		if(pessoa.getStatus() == "Ativo"){
			map.put("status", "Ativo");
		} else if  (pessoa.getStatus()=="Inativo") {
			map.put("status", "Inativo");
		} else if (pessoa.getStatus()=="Paralisada") {
			map.put("status", "Paralisada");
		} else if (pessoa.getStatus()=="Suspensa") {
			map.put("status", "Suspensa");
		} else if (pessoa.getStatus()=="Baixada"){
			map.put("status", "Baixada");
		} else {
			map.put("status", "Todos");
		} 

	}
	
	public List<Pessoa> getListaPessoa() {
		return listaPessoa;
	}
	public void setListaPessoa(List<Pessoa> listaPessoa) {
		this.listaPessoa = listaPessoa;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public List<RegimeTributario> getListaRegimesTributarios() {
		return listaRegimesTributarios;
	}

	public void setListaRegimesTributarios(
			List<RegimeTributario> listaRegimesTributarios) {
		this.listaRegimesTributarios = listaRegimesTributarios;
	}

}
