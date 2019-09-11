package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.Produto;
import com.agenciacrud.gestornegocio.repositorio.Produtos;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="produtoConverter", forClass = Produto.class)
public class ProdutoConverter implements Converter {
	
	//@Inject
	private Produtos produtos;
	
	public ProdutoConverter(){
		produtos = CDIServiceLocator.getBean(Produtos.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		Produto retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = produtos.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			Produto produto = (Produto) value;
			return produto.getId() == null ? null : produto.getId().toString();
		}
		
		return "";
	}
}
