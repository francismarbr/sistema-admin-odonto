package com.agenciacrud.gestornegocio.controller;

import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.model.Parcela;

public class ItemParcela {
	
	private boolean selecionada;
	private Parcela parcela;
	private MovimentacaoConta parcelaBaixada; //é usado no bean EstornoBean
	private boolean cancelarParcela;
	private boolean estornarParcela;
	
	
	public boolean isSelecionada() {
		return selecionada;
	}
	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}
	public Parcela getParcela() {
		return parcela;
	}
	public void setParcela(Parcela parcela) {
		this.parcela = parcela;
	}
	public MovimentacaoConta getParcelaBaixada() {
		return parcelaBaixada;
	}
	public void setParcelaBaixada(MovimentacaoConta parcelaBaixada) {
		this.parcelaBaixada = parcelaBaixada;
	}
	public boolean isCancelarParcela() {
		return cancelarParcela;
	}
	public void setCancelarParcela(boolean cancelarParcela) {
		if(cancelarParcela==false)
			cancelarParcela=true;
		this.cancelarParcela = cancelarParcela;
	}
	public boolean isEstornarParcela() {
		if(estornarParcela==false)
			estornarParcela=true;
		return estornarParcela;
	}
	public void setEstornarParcela(boolean estornarParcela) {
		this.estornarParcela = estornarParcela;
	}
	
}
