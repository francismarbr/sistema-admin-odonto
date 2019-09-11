package com.agenciacrud.gestornegocio.util;

import java.math.BigDecimal;

public class Numero {

	public static boolean isMaiorZero(Double numr) {
		if (numr == null)
			return false;

		if (numr.intValue() < 0)
			return false;

		return true;
	}

	public static boolean isValido(String numero) {
		try {
			Double.parseDouble(numero);
			return true;
		} catch (Exception e) {

		}
		return false;
	}

	public static boolean isMaiorZero(Integer numr) {
		if (numr == null)
			return false;

		return isMaiorZero((double) numr);
	}

	public static boolean isMaiorZero(Long numr) {
		if (numr == null)
			return false;

		return isMaiorZero((double) numr);
	}
	
	public static boolean isMaiorZero(BigDecimal numr) {
		if (numr == null)
			return false;
		if (numr.compareTo(new BigDecimal(0)) > 0) {
			return true;
		} else {
			return false;
		}
		//return isMaiorZero(numr);
	}
}
