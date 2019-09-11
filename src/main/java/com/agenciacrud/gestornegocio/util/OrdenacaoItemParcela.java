package com.agenciacrud.gestornegocio.util;

import java.util.Comparator;

import com.agenciacrud.gestornegocio.model.Parcela;

public class OrdenacaoItemParcela implements Comparator<Parcela> {

	@Override
	public int compare(Parcela parcela1, Parcela parcela2) {

		if (parcela1.getNumero().equals(parcela2.getNumero())) {
			return 0;
		} else if (parcela1.getNumero().longValue() > parcela2.getNumero()
				.longValue()) {
			return 1;
		} else {
			return -1;
		}
	}

}
