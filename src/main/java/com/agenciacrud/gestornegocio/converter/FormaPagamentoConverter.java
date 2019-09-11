package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.FormaPagamento;
import com.agenciacrud.gestornegocio.repositorio.FormasPagamentos;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="formaPagamentoConverter", forClass = FormaPagamento.class)
public class FormaPagamentoConverter implements Converter {
	
	//@Inject
	private FormasPagamentos formasPagamentos;
	
	public FormaPagamentoConverter(){
		formasPagamentos = CDIServiceLocator.getBean(FormasPagamentos.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		FormaPagamento retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = formasPagamentos.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			FormaPagamento formaPagamento = (FormaPagamento) value;
			return formaPagamento.getId() == null ? null : formaPagamento.getId().toString();
		}
		
		return "";
	}
}
