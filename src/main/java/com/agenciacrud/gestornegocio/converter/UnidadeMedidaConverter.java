package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.UnidadeMedida;
import com.agenciacrud.gestornegocio.repositorio.UnidadesMedidas;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="unidadeMedidaConverter", forClass = UnidadeMedida.class)
public class UnidadeMedidaConverter implements Converter {
	
	//@Inject
	private UnidadesMedidas unidadesMedidas;
	
	public UnidadeMedidaConverter(){
		unidadesMedidas = CDIServiceLocator.getBean(UnidadesMedidas.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		UnidadeMedida retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = unidadesMedidas.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			UnidadeMedida unidadeMedida = (UnidadeMedida) value;
			return unidadeMedida.getId() == null ? null : unidadeMedida.getId().toString();
		}
		
		return "";
	}
}
