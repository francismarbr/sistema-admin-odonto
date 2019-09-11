package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.Atestado;
import com.agenciacrud.gestornegocio.model.ReceitaMedica;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Atestados implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public Atestado guardar(Atestado atestado) {
		return manager.merge(atestado);
	}
	
	@Transactional
	public void remover(Atestado atestado) {
		try{
			atestado = porId(atestado.getId());
			manager.remove(atestado);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Atestado não pode ser excluída.");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Atestado> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Atestado.class);
		
		if(filtro.getPessoa()!=null) {
			criteria.add(Restrictions.eq("paciente", filtro.getPessoa()));
		} else {
			criteria.add(Restrictions.ge("dataRegistro", DataUtil.setDataAnterior(20)));
		}
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.desc("dataRegistro")).list();
	}
	
	public Atestado porId(Long id) {
		return manager.find(Atestado.class, id);
	}
	
	public List<Atestado> porAtestado(Empresa empresa, Long id) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Atestado> criteriaQuery = builder.createQuery(Atestado.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<Atestado> atestado = criteriaQuery.from(Atestado.class);
					
		//seleciona movimentação de crédito e débito para a empresa logada
		predicates.add(builder.equal(atestado.get("empresa"), empresa));
		
		//não seleciona movimentações que foram estornadas
		predicates.add(builder.equal(atestado.get("id"), id));
		
		criteriaQuery.select(atestado);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		
		TypedQuery<Atestado> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
}
