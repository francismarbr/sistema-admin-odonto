package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Permissao;
import com.agenciacrud.gestornegocio.model.Permissao;
import com.agenciacrud.gestornegocio.repositorio.Permissoes;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="permissaoConverter", forClass = Permissao.class)
public class PermissaoConverter implements Converter {
	
	//@Inject
	private Permissoes permissoes;
	
	public PermissaoConverter(){
		permissoes = CDIServiceLocator.getBean(Permissoes.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		Permissao retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = permissoes.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			Permissao permissao = (Permissao) value;
			return permissao.getId() == null ? null : permissao.getId().toString();
		}
		
		return "";
	}
}
