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
import com.agenciacrud.gestornegocio.model.Receita;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Receitas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public Receita guardar(Receita receita) {
		return manager.merge(receita);
	}
	
	@Transactional
	public void remover(Receita receita) {
		try{
			receita = porId(receita.getId());
			manager.remove(receita);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Esta obrigação não pode ser excluída, deve estar vinculado a algum outro registro.");
		}
	}


	public Receita porId(Long id) {
		return manager.find(Receita.class, id);
	}
	
	public List<Receita> porNome(String nome) {
		return this.manager.createQuery("from Receita " +
				"where upper(nome) like :nome", Receita.class)
				.setParameter("nome", nome.toUpperCase() + "%")
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Receita> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Receita.class);
		
		/*if(StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}*/ 
		
		if(filtro.getDataInicial() == null && filtro.getDataFinal() == null) {
			criteria.add(Restrictions.ge("dataLancamento", DataUtil.setDataAnterior(60)));
		}
		
		if(filtro.getPessoa() != null) {
			criteria.add(Restrictions.eq("cliente", filtro.getPessoa()));
		}
		
		if(filtro.getId() != null) {
			criteria.add(Restrictions.eq("chaveRegistro", filtro.getId()));
		}
		
		if(filtro.getDataInicial() != null) {
			criteria.add(Restrictions.ge("dataLancamento", filtro.getDataInicial()));
		}
		
		if(filtro.getDataFinal() != null) {
			criteria.add(Restrictions.le("dataLancamento", filtro.getDataFinal()));
		}
		
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.desc("chaveRegistro")).list();
	}
	
	public List<Receita> todasReceitas(Empresa empresa) {
		return this.manager.createQuery("from Receita where empresa = :empresa ", Receita.class)
				.setParameter("empresa", empresa)
				.getResultList();
	}
	
}
