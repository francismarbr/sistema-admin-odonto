package com.agenciacrud.gestornegocio.util.jsf;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

public class FacesProducer {

	@Produces
	@RequestScoped
	public FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	
	@Produces
	@RequestScoped
	public ExternalContext getExternalContext() {
		return getFacesContext().getExternalContext();
	}
	
	//@Produces
	//@RequestScoped
	//public HttpServletRequest getHttpServletRequest() {
		//return ((HttpServletRequest) getExternalContext().getRequest());
		//return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
	//}
	
	@Produces
	@RequestScoped
	public HttpServletResponse getHttpServletResponse() {
		return ((HttpServletResponse) getExternalContext().getResponse());	
		
	}
	
	
}