package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Categoria;
import com.agenciacrud.gestornegocio.model.CategoriaProcedimento;
import com.agenciacrud.gestornegocio.repositorio.CategoriasProcedimentos;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="categoriaProcedimentoConverter", forClass = CategoriaProcedimento.class)
public class CategoriaProcedimentoConverter implements Converter {
	
	//@Inject
	private CategoriasProcedimentos categorias;
	
	public CategoriaProcedimentoConverter(){
		categorias = CDIServiceLocator.getBean(CategoriasProcedimentos.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		CategoriaProcedimento retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = categorias.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			return ((CategoriaProcedimento) value).getId().toString();
		}
		
		return "";
	}
}
