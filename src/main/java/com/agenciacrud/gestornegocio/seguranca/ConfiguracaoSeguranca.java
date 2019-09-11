package com.agenciacrud.gestornegocio.seguranca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class ConfiguracaoSeguranca extends WebSecurityConfigurerAdapter {

	@Bean
	public AppUsuarioDetalhesService userDetalhesService(){
		return new  AppUsuarioDetalhesService();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		JsfLoginUrlAuthenticationEntryPoint jsfLoginEntry = new JsfLoginUrlAuthenticationEntryPoint();
		jsfLoginEntry.setLoginFormUrl("/login.xhtml");
		jsfLoginEntry.setRedirectStrategy(new JsfRedirectStrategy());
		
		JsfAccessDeniedHandler jsfDeniedEntry = new JsfAccessDeniedHandler();
		jsfDeniedEntry.setLoginPath("/AcessoNegado.xhtml");
		jsfDeniedEntry.setContextRelative(true);
		
		http
			.csrf().disable()
			.headers().frameOptions().sameOrigin()
			.and()
		
			
		.authorizeRequests()
			.antMatchers("/login.xhtml", "/cadastro.xhtml", "/recuperar-senha.xhtml", "cadastrar-nova-senha.xhtml",  "/javax.faces.resource/**").permitAll()
			.antMatchers("/home.xhtml", "/Erro.xhtml", "/AcessoNegado.xhtml").authenticated()	
			.antMatchers("/clientes/CadastroCliente.xhtml", "/clientes/PesquisaCliente.xhtml").hasRole("GERENCIAR_CLIENTES")
			.antMatchers("/clientes/Anamnese.xhtml", "/clientes/PesquisaAnamnesesPacientes.xhtml").hasRole("GERAR_ANAMNESE")
			.antMatchers("/pedidos/**").hasRole("GERENCIAR_TRATAMENTOS")
			.antMatchers("/receitasatestados/**").hasRole("GERENCIAR_RECEITAS_ATESTADO")
			.antMatchers("/modeloanamnese/**").hasRole("CRIAR_MODELO_ANAMNESE")
			.antMatchers("/agendamento/**").hasRole("GERENCIAR_AGENDA")
			.antMatchers("/dentistas/**").hasRole("GERENCIAR_DENTISTAS")
			.antMatchers("/fornecedores/**").hasRole("GERENCIAR_FORNECEDORES")
			.antMatchers("/categoriasprocedimentos/**").hasRole("GERENCIAR_CATEGORIAS_PROCEDIMENTOS")
			.antMatchers("/usuarios/**").hasRole("GERENCIAR_USUARIOS")
			.antMatchers("/procedimentos/**").hasRole("GERENCIAR_PROCEDIMENTOS")
			.antMatchers("/produtos/**").hasRole("GERENCIAR_PRODUTOS")
			.antMatchers("/regimes/**").hasRole("GERENCIAR_REGIMES")
			.antMatchers("/banco/**").hasRole("GERENCIAR_BANCOS")
			.antMatchers("/contabancaria/**").hasRole("GERENCIAR_CONTAS_BANCARIAS")
			.antMatchers("/categoriacontas/**").hasRole("GERENCIAR_CATEGORIA_CONTAS")
			.antMatchers("/movimentacaoestoque/**").hasRole("GERENCIAR_MOVIMENTACAO_ESTOQUE")
			.antMatchers("/receita/**").hasRole("GERENCIAR_RECEITAS")
			.antMatchers("/despesa/**").hasRole("GERENCIAR_DESPESAS")
			.antMatchers("/contas/**").hasRole("GERENCIAR_CONTAS")
			.antMatchers("/formapagamento/**").hasRole("GERENCIAR_FORMAS_PAGAMENTO")
			.antMatchers("/empresa/**").hasRole("GERENCIAR_EMPRESA")
			.antMatchers("/relatorios/relatorioComissao.xhtml").hasRole("VISUALIZAR_REL_COMISSAO_DENTISTA")
			.antMatchers("/relatorios/relatorioContasPagarReceber.xhtml").hasRole("VISUALIZAR_REL_CONTAS_PAGAR_RECEBER")
			.antMatchers("/relatorios/relatorioDespesa.xhtml").hasRole("VISUALIZAR_REL_DESPESA")
			.antMatchers("/relatorios/relatorioExtratoConta.xhtml").hasRole("VISUALIZAR_REL_EXTRATO_CONTA")
			.antMatchers("/relatorios/relatorioPessoa.xhtml").hasRole("VISUALIZAR_REL_PACIENTE")
			.antMatchers("/relatorios/relatorioRecebimentosPagamentos.xhtml").hasRole("VISUALIZAR_REL_RECEBIMENTOS_PAGAMENTOS")
			.antMatchers("/relatorios/relatorioReceita.xhtml").hasRole("VISUALIZAR_REL_RECEITA")
			.antMatchers("/perfilacesso/PesquisaPerfilAcesso.xhtml", "/perfilacesso/PerfilAcesso.xhtml").hasRole("GERENCIAR_PERFIL_ACESSO")		
			.antMatchers("/perfilacesso/CadastroPermissao.xhtml", "/perfilacesso/PesquisaPermissao.xhtml").hasRole("MASTER")
			.antMatchers("/perfilacesso/**").hasAnyRole("INICIAL", "MEGA", "COMPLETO", "MASTER")
			.antMatchers("/ClientesAdminOdonto/**").hasRole("MASTER")
			.antMatchers("/unidademedida/**").hasRole("MASTER")
			/*.anyRequest()
			.authenticated()*/
		.and()
			
		
		.formLogin()
			.loginPage("/login.xhtml")
			.failureUrl("/login.xhtml?invalid=true")
			.and()
			
		.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.and()
			
		.exceptionHandling()
			.accessDeniedPage("/AcessoNegado.xhtml")
			.authenticationEntryPoint(jsfLoginEntry)
			.accessDeniedHandler(jsfDeniedEntry);
			
		
		
	}
	
}
