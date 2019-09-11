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

import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class RegimesTributarios implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public RegimeTributario guardar(RegimeTributario regimeTributario) {
		return manager.merge(regimeTributario);
	}
	
	@Transactional
	public void remover(RegimeTributario regimeTributario) {
		try{
			regimeTributario = porId(regimeTributario.getId());
			manager.remove(regimeTributario);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Serviço não pode ser excluído.");
		}
	}

	@SuppressWarnings("unchecked")
	public List<RegimeTributario> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(RegimeTributario.class);
		
		if(StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}
		
		if(filtro.getId() != null) {
			criteria.add(Restrictions.eq("chaveRegistro", filtro.getId()));
		}
		
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.asc("nome")).list();
	}

	public RegimeTributario porId(Long id) {
		return manager.find(RegimeTributario.class, id);
	}
	
	public List<RegimeTributario> regimes(Empresa empresa) {
		return this.manager.createQuery("from RegimeTributario where empresa = :empresa", RegimeTributario.class)
				.setParameter("empresa", empresa)
				.getResultList();
	}

}
