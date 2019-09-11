package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.agenciacrud.gestornegocio.model.Cidade;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Cidades implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public Cidade guardar(Cidade cidade) {
		return manager.merge(cidade);
	}
	
	@Transactional
	public void remover(Cidade cidade) {
		try{
			cidade = porId(cidade.getId());
			manager.remove(cidade);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Serviço não pode ser excluído.");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Cidade> cidadesPorUf(ConsultaFilter filtro) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Cidade.class);
		
		if(StringUtils.isNotBlank(filtro.getUf())) {
			criteria.add(Restrictions.eq("uf", filtro.getUf()));
		}
		return criteria.addOrder(Order.asc("nome")).list();
	}

	public Cidade porId(Long id) {
		return manager.find(Cidade.class, id);
	}

}
