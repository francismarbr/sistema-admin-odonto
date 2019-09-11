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

import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.model.CategoriaProcedimento;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoCategoriaContaEnumerador;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class CategoriasProcedimentos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Inject //chama método produtor
	private EntityManager manager;
	
	@Transactional
	public CategoriaProcedimento guardar(CategoriaProcedimento categoria) {
		return manager.merge(categoria);
	}
	
	@Transactional
	public void remover(CategoriaProcedimento categoriaConta) {
		try{
			categoriaConta = porId(categoriaConta.getId());
			manager.remove(categoriaConta);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Esta categoria não pode ser excluída, deve estar vinculado a algum outro registro.");
		}
	}
	
	public List<CategoriaProcedimento> porNome(String nome, Empresa empresa) {
		return this.manager.createQuery("from CategoriaProcedimento " +
				"where empresa = :empresa and upper(nome) like :nome", CategoriaProcedimento.class)
				.setParameter("nome", nome.toUpperCase() + "%")
				.setParameter("empresa", empresa)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<CategoriaProcedimento> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(CategoriaProcedimento.class);
		
		if(filtro.getId() != null) {
			criteria.add(Restrictions.eq("chaveRegistro", filtro.getId()));
		}
		
		if(StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.asc("nome")).list();
	}
	
	public List<CategoriaProcedimento> todasCategorias(Empresa empresa) {
		return this.manager.createQuery("from CategoriaProcedimento where empresa = :empresa", CategoriaProcedimento.class)
				.setParameter("empresa", empresa)
				.getResultList();
	}

	
	public CategoriaProcedimento porId(Long id) {
		return manager.find(CategoriaProcedimento.class, id);
	}
	
	public List<CategoriaProcedimento> raizes(Empresa empresa) {
		return manager.createQuery("from CategoriaProcedimento where empresa = :empresa", 
				CategoriaProcedimento.class).setParameter("empresa", empresa).getResultList();
	}
	
	//espera um parâmetro categoriaPai e passa o valor para raiz
	public List<CategoriaProcedimento> subcategoriasDe(CategoriaProcedimento categoriaPai) {
		return manager.createQuery("from CategoriaProcedimento where categoriaPai = :raiz", 
				CategoriaProcedimento.class).setParameter("raiz", categoriaPai).getResultList();
	}

}
