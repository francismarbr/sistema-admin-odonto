package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.ReceitaMedica;
import com.agenciacrud.gestornegocio.repositorio.ReceitasMedicas;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = ReceitaMedica.class)
public class ReceitaMedicaConverter implements Converter {
	
	//@Inject
	private ReceitasMedicas receitas;
	
	public ReceitaMedicaConverter(){
		receitas = CDIServiceLocator.getBean(ReceitasMedicas.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		ReceitaMedica retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = receitas.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			ReceitaMedica receita = (ReceitaMedica) value;
			return receita.getId() == null ? null : receita.getId().toString();
		}
		
		return "";
	}
}
