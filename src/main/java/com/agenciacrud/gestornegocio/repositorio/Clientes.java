package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoPessoaEnumerador;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Clientes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public Pessoa guardar(Pessoa pessoa) {
		return manager.merge(pessoa);
	}
	
	@Transactional
	public void remover(Pessoa pessoa) {
		try{
			pessoa = porId(pessoa.getId());
			manager.remove(pessoa);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Cliente não pode ser excluído.");
		}
	}

	public Pessoa porCpfcnpj(String cpf, Empresa empresa) {
		try {
			return manager.createQuery("from Pessoa where upper(cpf) = :cpf and empresa = :empresa", Pessoa.class)
				.setParameter("cpf", cpf.toUpperCase())
				.setParameter("empresa", empresa)
				.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Pessoa> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Pessoa.class);
		
		if (StringUtils.isNotBlank(filtro.getCpfCnpj())) {
			criteria.add(Restrictions.ilike("cpf", filtro.getCpfCnpj(), MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq("empresa", empresa));
		
		return criteria.addOrder(Order.asc("chaveRegistro")).list();
	}
	
	public List<Pessoa> filtroRelatorio(Empresa empresa, Long idPessoa, String status) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Pessoa> criteriaQuery = builder.createQuery(Pessoa.class);
		List<Predicate> predicates = new ArrayList<>();
		
		Root<Pessoa> pessoaRoot = criteriaQuery.from(Pessoa.class);
		
		if (idPessoa != null) {
			predicates.add(builder.equal(pessoaRoot.get("id"), idPessoa ));
		}
		
		if (status != null) {
			predicates.add(builder.equal(pessoaRoot.get("status"), status ));
		}
		
		predicates.add(builder.equal(pessoaRoot.get("empresa"), empresa));
		
		criteriaQuery.select(pessoaRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(pessoaRoot.get("nome")));
		
		TypedQuery<Pessoa> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}

	public Pessoa porId(Long id) {
		return manager.find(Pessoa.class, id);
	}
	
	public List<Pessoa> porNome(String nome, Empresa empresa) {
		return this.manager.createQuery("from Pessoa " +
				"where upper(nome) like :nome and empresa = :empresa", Pessoa.class)
				.setParameter("nome", "%" + nome.toUpperCase() + "%")
				.setParameter("empresa", empresa)
				.getResultList();
	}
	
	

}
