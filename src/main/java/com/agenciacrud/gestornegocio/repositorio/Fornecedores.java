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
import com.agenciacrud.gestornegocio.model.Fornecedor;
import com.agenciacrud.gestornegocio.model.RegimeTributario;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoPessoaEnumerador;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Fornecedores implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public Fornecedor guardar(Fornecedor fornecedor) {
		return manager.merge(fornecedor);
	}
	
	@Transactional
	public void remover(Fornecedor fornecedor) {
		try{
			fornecedor = porId(fornecedor.getId());
			manager.remove(fornecedor);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Fornecedor não pode ser excluído.");
		}
	}

	public Fornecedor porCpfcnpj(String cpfCnpj, Empresa empresa) {
		try {
			return manager.createQuery("from Fornecedor where upper(cpfCnpj) = :cpfCnpj and empresa = :empresa", Fornecedor.class)
				.setParameter("cpfCnpj", cpfCnpj.toUpperCase())
				.setParameter("empresa", empresa)
				.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Fornecedor> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Fornecedor.class);
		
		if (StringUtils.isNotBlank(filtro.getCpfCnpj())) {
			criteria.add(Restrictions.ilike("cpfCnpj", filtro.getCpfCnpj(), MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq("empresa", empresa));
		
		return criteria.addOrder(Order.asc("chaveRegistro")).list();
	}
	
	public List<Fornecedor> filtroRelatorio(Empresa empresa, TipoPessoaEnumerador tipo, Long idPessoa,
			boolean cliente, boolean fornecedor, boolean transportadora, RegimeTributario regime, String status) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Fornecedor> criteriaQuery = builder.createQuery(Fornecedor.class);
		List<Predicate> predicates = new ArrayList<>();
		
		Root<Fornecedor> pessoaRoot = criteriaQuery.from(Fornecedor.class);
		
		if (idPessoa != null) {
			predicates.add(builder.equal(pessoaRoot.get("id"), idPessoa ));
		}
		if (cliente == true) {
			predicates.add(builder.equal(pessoaRoot.get("cliente"), cliente ));
		}
		if (fornecedor == true) {
			predicates.add(builder.equal(pessoaRoot.get("fornecedor"), fornecedor ));
		}
		if (regime != null) {
			predicates.add(builder.equal(pessoaRoot.get("regime"), regime ));
		}
		if (status != null) {
			predicates.add(builder.equal(pessoaRoot.get("status"), status ));
		}
		if (tipo != null) {
			predicates.add(builder.equal(pessoaRoot.get("tipo"), tipo));
		}
		
		predicates.add(builder.equal(pessoaRoot.get("empresa"), empresa));
		
		criteriaQuery.select(pessoaRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(pessoaRoot.get("nome")));
		
		TypedQuery<Fornecedor> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}

	public Fornecedor porId(Long id) {
		return manager.find(Fornecedor.class, id);
	}
	
	public List<Fornecedor> porNome(String nome, Empresa empresa) {
		return this.manager.createQuery("from Pessoa " +
				"where upper(nome) like :nome and empresa = :empresa", Fornecedor.class)
				.setParameter("nome", "%" + nome.toUpperCase() + "%")
				.setParameter("empresa", empresa)
				.getResultList();
	}
	
	

}
