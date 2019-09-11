package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.AnamnesePaciente;
import com.agenciacrud.gestornegocio.repositorio.AnamnesesPacientes;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = AnamnesePaciente.class)
public class AnamnesePacienteConverter implements Converter {
	
	//@Inject
	private AnamnesesPacientes anamneses;
	
	public AnamnesePacienteConverter(){
		anamneses = CDIServiceLocator.getBean(AnamnesesPacientes.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		AnamnesePaciente retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = anamneses.porId(id);
		}
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			AnamnesePaciente anamnesePaciente = (AnamnesePaciente) value;
			return anamnesePaciente.getId() == null ? null : anamnesePaciente.getId().toString();
		}
		
		return "";
	}
}
