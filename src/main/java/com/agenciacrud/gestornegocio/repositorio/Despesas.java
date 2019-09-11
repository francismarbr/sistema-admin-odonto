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

import com.agenciacrud.gestornegocio.model.Despesa;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Despesas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public Despesa guardar(Despesa despesa) {
		return manager.merge(despesa);
	}
	
	@Transactional
	public void remover(Despesa despesa) {
		try{
			despesa = porId(despesa.getId());
			manager.remove(despesa);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Esta despesa não pode ser excluída, deve estar vinculado a algum outro registro.");
		}
	}


	public Despesa porId(Long id) {
		return manager.find(Despesa.class, id);
	}
	
	public List<Despesa> porNome(String nome) {
		return this.manager.createQuery("from Despesa " +
				"where upper(nome) like :nome", Despesa.class)
				.setParameter("nome", nome.toUpperCase() + "%")
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Despesa> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Despesa.class);
				
		if(filtro.getId() == null && filtro.getFornecedor() == null) {
			criteria.add(Restrictions.ge("dataLancamento", DataUtil.setDataAnterior(60)));
		}
		
		if(filtro.getFornecedor() != null) {
			criteria.add(Restrictions.eq("fornecedor", filtro.getFornecedor()));
		}
		
		if(filtro.getId() != null) {
			criteria.add(Restrictions.eq("chaveRegistro", filtro.getId()));
		}
		
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.desc("chaveRegistro")).list();
	}
	
	public List<Despesa> todasDespesas() {
		return this.manager.createQuery("from Despesas ", Despesa.class).getResultList();
	}

	
	

}
