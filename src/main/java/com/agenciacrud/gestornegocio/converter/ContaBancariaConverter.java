package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.ContaBancaria;
import com.agenciacrud.gestornegocio.repositorio.ContasBancarias;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="contaBancariaConverter",forClass = ContaBancaria.class)
public class ContaBancariaConverter implements Converter {
	
	//@Inject
	private ContasBancarias contas;
	
	public ContaBancariaConverter(){
		contas = CDIServiceLocator.getBean(ContasBancarias.class);
		
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		ContaBancaria retorno = null;
		if(value != null) {
			Long id = new Long(value);
			retorno = contas.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			
			ContaBancaria conta = (ContaBancaria) value;
			return conta.getId() == null ? null : conta.getId().toString();
		}
		
		return "";
	}
}
