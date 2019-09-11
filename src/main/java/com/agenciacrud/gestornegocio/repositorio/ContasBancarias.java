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

import com.agenciacrud.gestornegocio.model.ContaBancaria;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.repositorio.filtro.ContaBancariaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class ContasBancarias implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public ContaBancaria guardar(ContaBancaria contaBancaria) {
		return manager.merge(contaBancaria);
	}
	
	@Transactional
	public void remover(ContaBancaria contaBancaria) {
		try{
			contaBancaria = porId(contaBancaria.getId());
			manager.remove(contaBancaria);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Conta Bancária não pode ser excluída.");
		}
	}

	@SuppressWarnings("unchecked")
	public List<ContaBancaria> filtrados(ContaBancariaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(ContaBancaria.class);
		
		if(StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("conta", filtro.getNome(), MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.asc("conta")).list();
	}

	public ContaBancaria porId(Long id) {
		return manager.find(ContaBancaria.class, id);
	}
	
	public List<ContaBancaria> todasContas() {
		return this.manager.createQuery("from ContaBancaria", ContaBancaria.class)
				.getResultList();
	}

}
