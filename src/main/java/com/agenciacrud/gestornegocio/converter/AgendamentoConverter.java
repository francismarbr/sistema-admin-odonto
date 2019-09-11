package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Agendamento;
import com.agenciacrud.gestornegocio.repositorio.Agendamentos;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="agendamentoConverter", forClass = Agendamento.class)
public class AgendamentoConverter implements Converter {
	
	//@Inject
	private Agendamentos agendamentos;
	
	public AgendamentoConverter(){
		agendamentos = CDIServiceLocator.getBean(Agendamentos.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		Agendamento retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = agendamentos.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			Agendamento agendamento = (Agendamento) value;
			return agendamento.getId() == null ? null : agendamento.getId().toString();
		}
		
		return "";
	}
}
