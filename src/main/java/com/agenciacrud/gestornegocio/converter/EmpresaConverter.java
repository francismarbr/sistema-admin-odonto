package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.repositorio.Empresas;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="empresaConverter", forClass = Empresa.class)
public class EmpresaConverter implements Converter {
	
	//@Inject
	private Empresas empresas;
	
	public EmpresaConverter(){
		empresas = CDIServiceLocator.getBean(Empresas.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		Empresa retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = empresas.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			Empresa empresa = (Empresa) value;
			return empresa.getId() == null ? null : empresa.getId().toString();
		}
		
		return "";
	}
}
