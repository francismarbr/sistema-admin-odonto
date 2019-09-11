package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Despesa;
import com.agenciacrud.gestornegocio.repositorio.Despesas;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Despesa.class)
public class DespesaConverter implements Converter {
	
	//@Inject
	private Despesas despesas;
	
	public DespesaConverter(){
		despesas = CDIServiceLocator.getBean(Despesas.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		Despesa retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = despesas.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			Despesa despesa= (Despesa) value;
			return despesa.getId() == null ? null : despesa.getId().toString();
		}
		
		return "";
	}
}
