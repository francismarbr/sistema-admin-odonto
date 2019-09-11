package com.agenciacrud.gestornegocio.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.agenciacrud.gestornegocio.model.PerfilAcesso;
import com.agenciacrud.gestornegocio.repositorio.PerfisAcesso;
import com.agenciacrud.gestornegocio.repositorio.PerfisAcesso;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

@FacesConverter(value="perfilAcessoConverter", forClass = PerfilAcesso.class)
public class PerfilAcessoConverter implements Converter {
	
	//@Inject
	private PerfisAcesso perfis;
	
	public PerfilAcessoConverter(){
		perfis = CDIServiceLocator.getBean(PerfisAcesso.class);
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value){
		PerfilAcesso retorno = null;
		
		if(value != null) {
			Long id = new Long(value);
			retorno = perfis.porId(id);
		}
		
		return retorno;
	}
	
	@Override
	public String getAsString(FacesContext contex, UIComponent component, Object value) {
		if (value != null) {
			PerfilAcesso perfilAcesso = (PerfilAcesso) value;
			return perfilAcesso.getId() == null ? null : perfilAcesso.getId().toString();
		}
		
		return "";
	}
}
