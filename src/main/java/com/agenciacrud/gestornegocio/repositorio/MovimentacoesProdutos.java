package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
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

import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.MovimentacaoEstoque;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.Pedido;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.repositorio.filtro.PedidoFilter;
import com.agenciacrud.gestornegocio.util.DataUtil;

public class MovimentacoesProdutos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	
	public MovimentacaoEstoque guardar(MovimentacaoEstoque movimentacao) {
		return this.manager.merge(movimentacao);
	}

	public MovimentacaoEstoque porId(Long id) {
		return manager.find(MovimentacaoEstoque.class, id);
	}
	
	public List<MovimentacaoEstoque> filtroMovimentacao(Empresa empresa, Date dataInicioMovimentacao, Date dataFimMovimentacao) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<MovimentacaoEstoque> criteriaQuery = builder.createQuery(MovimentacaoEstoque.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<MovimentacaoEstoque> parcelaRoot = criteriaQuery.from(MovimentacaoEstoque.class);
		
		if (dataInicioMovimentacao != null) {
			predicates.add(builder.greaterThanOrEqualTo(parcelaRoot.<Date> get("dataLancamento"), dataInicioMovimentacao));
		} else {
			predicates.add(builder.greaterThanOrEqualTo(parcelaRoot.<Date> get("dataLancamento"),DataUtil.setDataAnterior(60)));
		}
		if (dataFimMovimentacao != null) {
			predicates.add(builder.lessThanOrEqualTo(parcelaRoot.<Date> get("dataLancamento"), dataFimMovimentacao));
		}
		
		predicates.add(builder.equal(parcelaRoot.get("empresa"), empresa));
		
		criteriaQuery.select(parcelaRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.desc(parcelaRoot.get("chaveRegistro")));
		
		TypedQuery<MovimentacaoEstoque> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
}