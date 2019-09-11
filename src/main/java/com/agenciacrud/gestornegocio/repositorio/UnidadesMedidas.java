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
import com.agenciacrud.gestornegocio.model.UnidadeMedida;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class UnidadesMedidas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	@Transactional
	public UnidadeMedida guardar(UnidadeMedida unidadeMedida) {
		return manager.merge(unidadeMedida);
	}
	
	@Transactional
	public void remover(UnidadeMedida unidadeMedida) {
		try{
			unidadeMedida = porId(unidadeMedida.getId());
			manager.remove(unidadeMedida);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Unidade de Medida não pode ser excluído.");
		}
	}

	@SuppressWarnings("unchecked")
	public List<UnidadeMedida> filtrados(ConsultaFilter filtro) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(UnidadeMedida.class);
		
		
		if(StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}
		
		return criteria.addOrder(Order.asc("nome")).list();
	}

	public UnidadeMedida porId(Long id) {
		return manager.find(UnidadeMedida.class, id);
	}
	
	public List<UnidadeMedida> todosUnidades() {
		return this.manager.createQuery("from UnidadeMedida", UnidadeMedida.class)
				.getResultList();
	}
}
