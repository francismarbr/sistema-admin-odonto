package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.agenciacrud.gestornegocio.model.Banco;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Bancos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	@Transactional
	public Banco guardar(Banco banco) {
		return manager.merge(banco);
	}
	
	@Transactional
	public void remover(Banco banco) {
		try{
			banco = porId(banco.getId());
			manager.remove(banco);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Banco não pode ser excluído.");
		}
	}

	@SuppressWarnings("unchecked")
	public List<Banco> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Banco.class);
		
		criteria.add(Restrictions.eq("empresa", empresa));
		
		if(filtro.getId() != null) {
			criteria.add(Restrictions.eq("chaveRegistro", filtro.getId()));
		}
		
		if(StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}
		
		return criteria.addOrder(Order.asc("nome")).list();
	}

	public Banco porId(Long id) {
		return manager.find(Banco.class, id);
	}
	
	public List<Banco> todosBanco() {
		return this.manager.createQuery("from Banco", Banco.class)
				.getResultList();
	}
	/*
	public List obterUltimoPorEmpresa(Class classe, Long idEmpresa) {
		List results = null;
		return this.manager.createQuery("SELECT o from " + classe.getSimpleName() + 
				"o WHERE o.chaveRegistro=(SELECT MAX(x.chaveRegistro) FROM "
				+ classe.getSimpleName()
				+ "x WHERE x.empresa.id = ")
				
				
	}
*/
}
