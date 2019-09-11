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
import com.agenciacrud.gestornegocio.model.Dentista;
import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoPessoaEnumerador;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Dentistas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public Dentista guardar(Dentista dentista) {
		return manager.merge(dentista);
	}
	
	@Transactional
	public void remover(Dentista dentista) {
		try{
			dentista = porId(dentista.getId());
			manager.remove(dentista);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Dentista não pode ser excluído.");
		}
	}

	public Dentista porCpf(String cpf, Empresa empresa) {
		try {
			return manager.createQuery("from Dentista where upper(cpf) = :cpf and empresa = :empresa", Dentista.class)
				.setParameter("cpfj", cpf.toUpperCase())
				.setParameter("empresa", empresa)
				.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Dentista> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Dentista.class);
		
		if (StringUtils.isNotBlank(filtro.getCpfCnpj())) {
			criteria.add(Restrictions.ilike("cpf", filtro.getCpfCnpj(), MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq("empresa", empresa));
		
		return criteria.addOrder(Order.asc("chaveRegistro")).list();
	}
	
	public List<Dentista> filtroRelatorio(Empresa empresa, Long idPessoa, String status) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Dentista> criteriaQuery = builder.createQuery(Dentista.class);
		List<Predicate> predicates = new ArrayList<>();
		
		Root<Dentista> pessoaRoot = criteriaQuery.from(Dentista.class);
		
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
		
		TypedQuery<Dentista> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}

	public Dentista porId(Long id) {
		return manager.find(Dentista.class, id);
	}
	
	public List<Dentista> porNome(String nome, Empresa empresa) {
		return this.manager.createQuery("from Dentista " +
				"where upper(nome) like :nome and empresa = :empresa", Dentista.class)
				.setParameter("nome", "%" + nome.toUpperCase() + "%")
				.setParameter("empresa", empresa)
				.getResultList();
	}
	
	

}
