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
import com.agenciacrud.gestornegocio.model.Procedimento;
import com.agenciacrud.gestornegocio.model.Produto;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Procedimentos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public Procedimento guardar(Procedimento procedimento) {
		return manager.merge(procedimento);
	}
	
	@Transactional
	public void remover(Procedimento procedimento) {
		try{
			procedimento = porId(procedimento.getId());
			manager.remove(procedimento);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Serviço não pode ser excluído.");
		}
	}

	@SuppressWarnings("unchecked")
	public List<Procedimento> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Procedimento.class);
		
		if(StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.asc("nome")).list();
	}

	public Procedimento porId(Long id) {
		return manager.find(Procedimento.class, id);
	}
	
	public List<Procedimento> porNome(String nome, Empresa empresa) {
		return this.manager.createQuery("from Procedimento where upper(nome) like :nome and empresa = :empresa", Procedimento.class)
			.setParameter("nome", "%" + nome.toUpperCase() + "%")
			.setParameter("empresa", empresa).getResultList();
	}

}
