package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.Usuario;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Usuarios implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public Usuario guardar(Usuario usuario) {
		return manager.merge(usuario);
	}
	
	@Transactional
	public void remover(Usuario usuario) {
		try{
			usuario = porId(usuario.getId());
			manager.remove(usuario);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Usuario não pode ser excluído.");
		}
	}

	public Usuario porLogin(String login) {
		Usuario usuario = null;
		//Hibernate.initialize(usuario.getGrupos());
		
		try {
			usuario = this.manager.createQuery("from Usuario where lower(login) = :login", Usuario.class)
				.setParameter("login", login.toLowerCase()).getSingleResult();
		} catch (NoResultException e) {
			//nenhum usuário encontrado com o login informado
		}
		
		return usuario;
	}
	
	public Usuario porToken(String token) {
		Usuario usuario = null;
		//Hibernate.initialize(usuario.getGrupos());
		
		try {
			usuario = this.manager.createQuery("from Usuario where lower(tokenTemporario) = :token", Usuario.class)
				.setParameter("token", token.toLowerCase()).getSingleResult();
		} catch (NoResultException e) {
			//nenhum usuário encontrado com o login informado
		}
		
		return usuario;
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Usuario.class);
		
		if(filtro.getId() != null) {
			criteria.add(Restrictions.eq("chaveRegistro", filtro.getId()));
		}
		
		if(StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.asc("nome")).list();
	}

	public Usuario porId(Long id) {
		return manager.find(Usuario.class, id);
	}
	
	public List<Usuario> users() {
		// TODO filtrar apenas vendedores (por um grupo específico)
		return this.manager.createQuery("from Usuario", Usuario.class)
				.getResultList();
	}

}
