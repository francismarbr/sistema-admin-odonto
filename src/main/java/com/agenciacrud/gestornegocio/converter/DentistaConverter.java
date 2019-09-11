package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Dentista;
import com.agenciacrud.gestornegocio.repositorio.Dentistas;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="dentistaConverter", forClass = Dentista.class)
public class DentistaConverter implements Converter {
	
	//@Inject
	private Dentistas dentistas;
	
	public DentistaConverter(){
		dentistas = CDIServiceLocator.getBean(Dentistas.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		Dentista retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = dentistas.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			Dentista dentista = (Dentista) value;
			return dentista.getId() == null ? null : dentista.getId().toString();
		}
		
		return "";
	}
}
