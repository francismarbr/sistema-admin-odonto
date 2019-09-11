package com.agenciacrud.gestornegocio.util.jsf;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JsfUtil {
	
	//@Inject
	

    public static FacesContext getFC(){
        return FacesContext.getCurrentInstance();
    }

    public static ExternalContext getEC(){
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    public static HttpServletRequest getRequest(){
        return (HttpServletRequest) getEC().getRequest();
    }

    public static HttpServletResponse getResponse(){
        return (HttpServletResponse) getEC().getResponse();
    }


    public static void forwardTo(String url) throws ServletException, IOException {
        getRequest().getRequestDispatcher(url).forward(getRequest(), getResponse());
        //responseComplete();
    }
}