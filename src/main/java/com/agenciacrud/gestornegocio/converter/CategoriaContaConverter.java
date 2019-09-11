package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.repositorio.CategoriasContas;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="categoriaContaConverter", forClass = CategoriaConta.class)
public class CategoriaContaConverter implements Converter {
	
	//@Inject
	private CategoriasContas categorias;
	
	public CategoriaContaConverter(){
		categorias = CDIServiceLocator.getBean(CategoriasContas.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		CategoriaConta retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = categorias.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			CategoriaConta categoria = (CategoriaConta) value;
			return categoria.getId() == null ? null : categoria.getId().toString();
		}
		
		return "";
	}
}
