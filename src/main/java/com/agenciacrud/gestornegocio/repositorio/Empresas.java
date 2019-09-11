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
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Empresas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public Empresa guardar(Empresa empresa) {
		return manager.merge(empresa);
	}
	
	@Transactional
	public void remover(Empresa empresa) {
		try{
			empresa= porId(empresa.getId());
			manager.remove(empresa);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Esta empresa não pode ser excluída.");
		}
	}

	public Empresa porCpfcnpj(String cpfCnpj) {
		try {
			return manager.createQuery("from Empresa where upper(cpfCnpj) = :cpfCnpj", Empresa.class)
				.setParameter("cpfCnpj", cpfCnpj.toUpperCase())
				.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Empresa> filtrados(ConsultaFilter filtro) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Empresa.class);
		
		criteria.add(Restrictions.eq("ativa", filtro.isAtivo()));
		
		if (StringUtils.isNotBlank(filtro.getCpfCnpj())) {
			criteria.add(Restrictions.eq("cpfCnpj", filtro.getCpfCnpj()));
		}
				
		
		if (StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}
		
		if(StringUtils.isNotBlank(filtro.getDescricao())) {
			criteria.add(Restrictions.ilike("descricao", filtro.getDescricao(), MatchMode.ANYWHERE));
		}
		
		return criteria.addOrder(Order.asc("id")).list();
	}

	public Empresa porId(Long id) {
		return manager.find(Empresa.class, id);
	}
	
	public List<Empresa> buscaTudo() {
		return  manager.createQuery("from Empresa",Empresa.class).getResultList();
	}
	
	public List<Empresa> porNome(String nome, Empresa empresa) {
		return this.manager.createQuery("from Empresa " +
				"where upper(nome) like :nome", Empresa.class)
				.setParameter("nome", nome.toUpperCase() + "%")
				.getResultList();
	}
	
	public List<Empresa> empLogada(Long idEmpresaLogada) {
		return this.manager.createQuery("from Empresa " +
				"where id = :idEmpresaLogada", Empresa.class)
				.setParameter("idEmpresaLogada", idEmpresaLogada)
				.getResultList();
	}
	
	

}
