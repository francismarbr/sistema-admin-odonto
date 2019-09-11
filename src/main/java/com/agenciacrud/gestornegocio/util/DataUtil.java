package com.agenciacrud.gestornegocio.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.agenciacrud.gestornegocio.model.enumeradores.TipoIntervaloTempoEnumerador;

public class DataUtil {

	public static Date setHoraInicial(Date data) {
		Calendar c = new GregorianCalendar();
		c.setTime(data);
		c.set(GregorianCalendar.HOUR, 0);
		c.set(GregorianCalendar.MINUTE, 0);
		c.set(GregorianCalendar.SECOND, 0);
		return c.getTime();
	}

	public static Date setHoraFinal(Date data) {
		Calendar c = new GregorianCalendar();
		c.setTime(data);
		c.set(GregorianCalendar.HOUR, 23);
		c.set(GregorianCalendar.MINUTE, 59);
		c.set(GregorianCalendar.SECOND, 59);
		return c.getTime();
	}

	public static Date setPrimeiroDiaMes(Date data) {
		Calendar c = new GregorianCalendar();
		c.setTime(setHoraInicial(data));
		c.set(GregorianCalendar.DAY_OF_MONTH, 1);
		c.set(GregorianCalendar.MILLISECOND, 0);
		return c.getTime();
	}

	public static Date setUltimoDiaMes(Date data) {
		Calendar c = new GregorianCalendar();
		c.setTime(setHoraFinal(data));
		c.set(GregorianCalendar.DAY_OF_MONTH,
				c.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
		return c.getTime();
	}

	public static Date setPrimeiroDiaAno(Date data) {
		Calendar c = new GregorianCalendar();
		c.setTime(setHoraInicial(data));
		c.set(GregorianCalendar.DAY_OF_MONTH, 1);
		c.set(GregorianCalendar.MONTH, Calendar.JANUARY);
		c.set(GregorianCalendar.MILLISECOND, 0);
		return c.getTime();
	}

	public static Date setUltimoDiaAno(Date data) {
		Calendar c = new GregorianCalendar();
		c.setTime(setHoraFinal(data));
		c.set(GregorianCalendar.MONTH, Calendar.DECEMBER);
		c.set(GregorianCalendar.DAY_OF_MONTH, 31);
		c.set(GregorianCalendar.MILLISECOND, 999);
		return c.getTime();
	}
	
	//recebe um número de dias e calcula uma data anterior a esse número
	public static Date setDataAnterior(int numDiasAntes) {
		Calendar c = new GregorianCalendar();
		c.add(Calendar.DATE, -numDiasAntes);
		return c.getTime();
	}

	public static Date obterDataDiaInformadoMesAtual(int dia) {
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		c.set(GregorianCalendar.DAY_OF_MONTH, dia);
		return c.getTime();
	}
	
	public static Date obterMes(int mes) {
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		c.set(GregorianCalendar.MONTH, mes);
		return c.getTime();
	}

	public static Integer getAnoAtual() {
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		return c.get(GregorianCalendar.YEAR);
	}

	public static Integer getDiaAtual() {
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		return c.get(GregorianCalendar.DAY_OF_MONTH);
	}

	public static void main(String[] args) {
		System.out.println(DataUtil.obterDataDiaInformadoMesAtual(25));
		System.out.println(DataUtil.setPrimeiroDiaMes(new Date()));
		System.out.println(DataUtil.setUltimoDiaMes(new Date()));
	}

	public static String formatoPadrao(Date data) {
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		return formatador.format(data);
	}
	
	public static String formatoPadraoHora(Date data) {
		SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");
		return formatador.format(data);
	}

	public static Date getDataAntiga() {
		Calendar c = new GregorianCalendar();
		c.setTime(setHoraInicial(new Date()));
		c.set(GregorianCalendar.DAY_OF_MONTH, 1);
		c.set(GregorianCalendar.MONTH, 1);
		c.set(GregorianCalendar.YEAR, 2000);
		return c.getTime();
	}

	public static Date fromString(String data) {
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		try {
			return formatador.parse(data);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Date addPeriodoPorTipoInvertvalo(Date data,
			TipoIntervaloTempoEnumerador tipo, int quantidade) {
		Calendar c = new GregorianCalendar();
		c.setTime(data);
		if (tipo.equals(TipoIntervaloTempoEnumerador.DIA)) {
			//c.add(GregorianCalendar.DAY_OF_MONTH, quantidade);
			c.add(GregorianCalendar.DAY_OF_MONTH,
					quantidade * tipo.getQuantidadeDias());
		} else if (tipo.equals(TipoIntervaloTempoEnumerador.MENSAL)) {
			c.add(GregorianCalendar.MONTH, quantidade);
		} else if (tipo.equals(TipoIntervaloTempoEnumerador.ANUAL)) {
			c.add(GregorianCalendar.YEAR, quantidade);
		} else {
			c.add(GregorianCalendar.DAY_OF_MONTH,
					quantidade * tipo.getQuantidadeDias());
		}

		return c.getTime();
	}

	public static Date addDias(Date data, int dias) {
		Calendar c = new GregorianCalendar();
		c.setTime(data);
		c.add(GregorianCalendar.DAY_OF_MONTH, dias);
		return c.getTime();
	}

	public static Date addMeses(Date data, int meses) {
		Calendar c = new GregorianCalendar();
		c.setTime(data);
		c.add(GregorianCalendar.MONTH, meses);
		return c.getTime();
	}

	public static Date addAnos(Date data, int anos) {
		Calendar c = new GregorianCalendar();
		c.setTime(data);
		c.add(GregorianCalendar.YEAR, anos);
		return c.getTime();
	}

	public static Integer diferencaDias(Date inicio, Date fim) {
		inicio = setHoraInicial(inicio);
		fim = setHoraInicial(fim);
		long diferenca = fim.getTime() - inicio.getTime();
		Double dias = Math.ceil(diferenca / 1000d / 86400d);
		return new Integer(dias.intValue());
	}

}
