package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Procedimento;
import com.agenciacrud.gestornegocio.repositorio.Procedimentos;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(forClass = Procedimento.class)
public class procedimentoConverter implements Converter {
	
	//@Inject
	private Procedimentos servicos;
	
	public procedimentoConverter(){
		servicos = CDIServiceLocator.getBean(Procedimentos.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		Procedimento retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = servicos.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			Procedimento servico = (Procedimento) value;
			return servico.getId() == null ? null : servico.getId().toString();
		}
		
		return "";
	}
}
