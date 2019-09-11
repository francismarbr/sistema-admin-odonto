package com.agenciacrud.gestornegocio.seguranca;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
	
	public SecurityWebApplicationInitializer() {
		super(ConfiguracaoSeguranca.class);
	}

}
