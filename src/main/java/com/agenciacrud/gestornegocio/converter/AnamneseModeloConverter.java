package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.AnamneseModelo;
import com.agenciacrud.gestornegocio.repositorio.AnamnesesModelos;
import com.agenciacrud.gestornegocio.repositorio.AnamnesesModelos;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = AnamneseModelo.class)
public class AnamneseModeloConverter implements Converter {
	
	//@Inject
	private AnamnesesModelos anamneses;
	
	public AnamneseModeloConverter(){
		anamneses = CDIServiceLocator.getBean(AnamnesesModelos.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		AnamneseModelo retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = anamneses.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			AnamneseModelo anamneseModelo = (AnamneseModelo) value;
			return anamneseModelo.getId() == null ? null : anamneseModelo.getId().toString();
		}
		
		return "";
	}
}
