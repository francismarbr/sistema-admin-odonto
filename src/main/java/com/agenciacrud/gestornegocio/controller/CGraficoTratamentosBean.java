package com.agenciacrud.gestornegocio.controller;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.velocity.runtime.parser.node.GetExecutor;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;

import com.agenciacrud.gestornegocio.repositorio.Pedidos;

@Named("CGraficoTratamentoBean")
@RequestScoped
public class CGraficoTratamentosBean extends CGeral {
	
	private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM");
	
	@Inject
	private Pedidos pedidos; 
	
	private LineChartModel model;
	
	public void preRender() {
		this.model = new LineChartModel();
		this.model.setTitle("Tratamentos/Procedimentos Aprovados");
		//this.model.setLegendPosition("e");
		this.model.setAnimate(true);
		this.model.setShowPointLabels(false);
		this.model.setShowDatatip(false);
		
		this.model.getAxes().put(AxisType.X, new CategoryAxis());
		
		adicionarSerie("Tratamentos/Procedimentos");
	}
	
	private void adicionarSerie(String rotulo) {
		
		Map<Date, Integer> valoresPorData = this.pedidos.valoresTotaisPorData(15, getEmpresaLogada());
		
		ChartSeries series = new ChartSeries(rotulo);
		
		for(Date data : valoresPorData.keySet()) {
			series.set(DATE_FORMAT.format(data), valoresPorData.get(data));
		}
		
		this.model.addSeries(series);
	}
	
	public LineChartModel getModel() {
		return model;
	}
}
