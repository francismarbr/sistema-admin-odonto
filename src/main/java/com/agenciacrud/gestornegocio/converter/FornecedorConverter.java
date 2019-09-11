package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Fornecedor;
import com.agenciacrud.gestornegocio.repositorio.Fornecedores;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="fornecedorConverter", forClass = Fornecedor.class)
public class FornecedorConverter implements Converter {
	
	//@Inject
	private Fornecedores fornecedores;
	
	public FornecedorConverter(){
		fornecedores = CDIServiceLocator.getBean(Fornecedores.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		Fornecedor retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = fornecedores.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			Fornecedor fornecedor = (Fornecedor) value;
			return fornecedor.getId() == null ? null : fornecedor.getId().toString();
		}
		
		return "";
	}
}
