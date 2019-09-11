package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.ReceitaMedica;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoMovimentacao;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoOperacao;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class ReceitasMedicas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public ReceitaMedica guardar(ReceitaMedica receitaMedica) {
		return manager.merge(receitaMedica);
	}
	
	@Transactional
	public void remover(ReceitaMedica receitaMedica) {
		try{
			receitaMedica = porId(receitaMedica.getId());
			manager.remove(receitaMedica);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Receita não pode ser excluída.");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ReceitaMedica> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(ReceitaMedica.class);
		
		if(filtro.getPessoa()!=null) {
			criteria.add(Restrictions.eq("paciente", filtro.getPessoa()));
		} else {
			criteria.add(Restrictions.ge("dataRegistro", DataUtil.setDataAnterior(20)));
		}
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.desc("dataRegistro")).list();
	}
	
	public ReceitaMedica porId(Long id) {
		return manager.find(ReceitaMedica.class, id);
	}

	public List<ReceitaMedica> porNome(String descricao, Empresa empresa) {
		return this.manager.createQuery("from ReceitaMedica where upper(descricao) like :descricao", ReceitaMedica.class)
			.setParameter("descricao", descricao.toUpperCase() + "%").getResultList();
	}
	
	public List<ReceitaMedica> porPaciente(Empresa empresa, Long id) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<ReceitaMedica> criteriaQuery = builder.createQuery(ReceitaMedica.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<ReceitaMedica> receita = criteriaQuery.from(ReceitaMedica.class);
					
		//seleciona movimentação de crédito e débito para a empresa logada
		predicates.add(builder.equal(receita.get("empresa"), empresa));
		
		//não seleciona movimentações que foram estornadas
		predicates.add(builder.equal(receita.get("id"), id));
		
		criteriaQuery.select(receita);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		
		TypedQuery<ReceitaMedica> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}

}
