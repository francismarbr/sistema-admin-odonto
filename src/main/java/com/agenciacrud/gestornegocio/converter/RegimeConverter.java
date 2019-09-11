package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.repositorio.RegimesTributarios;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="regimeConverter", forClass = RegimeTributario.class)
public class RegimeConverter implements Converter {
	
	//@Inject
	private RegimesTributarios regimesTributarios;
	
	public RegimeConverter(){
		regimesTributarios = CDIServiceLocator.getBean(RegimesTributarios.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		RegimeTributario retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = regimesTributarios.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			RegimeTributario regimeTributario = (RegimeTributario) value;
			return regimeTributario.getId() == null ? null : regimeTributario.getId().toString();
		}
		
		return "";
	}
}
