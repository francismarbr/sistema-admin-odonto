package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Cidade;
import com.agenciacrud.gestornegocio.repositorio.Cidades;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Cidade.class)
public class cidadeConverter implements Converter {
	
	//@Inject
	private Cidades cidades;
	
	public cidadeConverter(){
		cidades = CDIServiceLocator.getBean(Cidades.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		Cidade retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = cidades.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			Cidade cidade = (Cidade) value;
			return cidade.getId() == null ? null : cidade.getId().toString();
		}
		
		return "";
	}
}
