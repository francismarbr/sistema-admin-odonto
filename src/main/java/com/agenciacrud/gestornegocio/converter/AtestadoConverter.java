package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Atestado;
import com.agenciacrud.gestornegocio.repositorio.Atestados;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Atestado.class)
public class AtestadoConverter implements Converter {
	
	//@Inject
	private Atestados atestados;
	
	public AtestadoConverter(){
		atestados = CDIServiceLocator.getBean(Atestados.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		Atestado retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = atestados.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			Atestado atestado = (Atestado) value;
			return atestado.getId() == null ? null : atestado.getId().toString();
		}
		
		return "";
	}
}
