package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.TransferenciaConta;
import com.agenciacrud.gestornegocio.repositorio.Transferencias;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="transferenciaConverter", forClass = TransferenciaConta.class)
public class TransferenciaConverter implements Converter {
	
	//@Inject
	private Transferencias transferencias;
	
	public TransferenciaConverter(){
		transferencias = CDIServiceLocator.getBean(Transferencias.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		TransferenciaConta retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = transferencias.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			TransferenciaConta transferencia = (TransferenciaConta) value;
			return transferencia.getId() == null ? null : transferencia.getId().toString();
		}
		
		return "";
	}
}
