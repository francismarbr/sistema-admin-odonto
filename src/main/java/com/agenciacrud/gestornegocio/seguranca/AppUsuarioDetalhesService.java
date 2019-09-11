package com.agenciacrud.gestornegocio.seguranca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.agenciacrud.gestornegocio.model.ItemPerfilAcesso;
import com.agenciacrud.gestornegocio.model.PerfilAcesso;
import com.agenciacrud.gestornegocio.model.Permissao;
import com.agenciacrud.gestornegocio.model.Usuario;
import com.agenciacrud.gestornegocio.model.enumeradores.PlanoEmpresa;
import com.agenciacrud.gestornegocio.repositorio.Usuarios;
import com.agenciacrud.gestornegocio.util.cdi.CDIServiceLocator;

public class AppUsuarioDetalhesService implements UserDetailsService {
	
	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		Usuarios usuarios = CDIServiceLocator.getBean(Usuarios.class);
		Usuario usuario = usuarios.porLogin(login);
		UsuarioSistema user = null;
		
		if (usuario != null && usuario.isAtivo()) {
			user = new UsuarioSistema(usuario, getGrupos(usuario));
		} else {
			throw new UsernameNotFoundException("Usuário não encontrado");
		}
		
		return user;
	}

	private Collection<? extends GrantedAuthority> getGrupos(Usuario usuario) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		
		authorities.add(new SimpleGrantedAuthority("ROLE_"+usuario.getEmpresa().getPlano().getDescricao()));
		
		
		for (PerfilAcesso perfil : usuario.getPerfisAcesso()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + perfil.getNome().toUpperCase()));
			
			//TODO implementar regra se o usuário fizer parte da empresa master, possui acesso total
			
			//lista as permissões do perfil e passa para o spring verificar se tem autorização de acesso
			for(ItemPerfilAcesso item : perfil.getPermissoes()) {
				
				//verifica se o plano inicial é válido
				if(usuario.getEmpresa().getPlano().getDescricao().equals(PlanoEmpresa.INICIAL.getDescricao()) && item.getPermissao().isPlanoInicial()) {
					authorities.add(new SimpleGrantedAuthority("ROLE_" +item.getPermissao().getNome().toUpperCase()));
				}
				//verifica se o plano mega é válido
				if(usuario.getEmpresa().getPlano().getDescricao().equals(PlanoEmpresa.MEGA.getDescricao())&& item.getPermissao().isPlanoMega()) {
					authorities.add(new SimpleGrantedAuthority("ROLE_" +item.getPermissao().getNome().toUpperCase()));
				}
				//verifica se o plano completo é válido
				if(usuario.getEmpresa().getPlano().getDescricao().equals(PlanoEmpresa.COMPLETO.getDescricao()) && item.getPermissao().isPlanoCompleto()) {
					authorities.add(new SimpleGrantedAuthority("ROLE_" +item.getPermissao().getNome().toUpperCase()));
				}
				//verifica é a empresa master
				if(usuario.getEmpresa().getPlano().getDescricao().equals(PlanoEmpresa.MASTER.getDescricao()) && item.getPermissao().isEmpresaMaster()) {
					authorities.add(new SimpleGrantedAuthority("ROLE_" +item.getPermissao().getNome().toUpperCase()));
				}
			}
		}
		
		return authorities;
	}

}
