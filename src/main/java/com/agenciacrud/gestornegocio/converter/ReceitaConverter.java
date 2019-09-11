package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Receita;
import com.agenciacrud.gestornegocio.repositorio.Receitas;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Receita.class)
public class ReceitaConverter implements Converter {
	
	//@Inject
	private Receitas receitas;
	
	public ReceitaConverter(){
		receitas = CDIServiceLocator.getBean(Receitas.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		Receita retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = receitas.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			Receita receita = (Receita) value;
			return receita.getId() == null ? null : receita.getId().toString();
		}
		
		return "";
	}
}
