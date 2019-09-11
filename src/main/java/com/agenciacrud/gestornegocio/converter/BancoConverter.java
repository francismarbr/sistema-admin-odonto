package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Banco;
import com.agenciacrud.gestornegocio.repositorio.Bancos;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="bancoConverter", forClass = Banco.class)
public class BancoConverter implements Converter {
	
	//@Inject
	private Bancos bancos;
	
	public BancoConverter(){
		bancos = CDIServiceLocator.getBean(Bancos.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		Banco retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = bancos.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			Banco banco = (Banco) value;
			return banco.getId() == null ? null : banco.getId().toString();
		}
		
		return "";
	}
}
