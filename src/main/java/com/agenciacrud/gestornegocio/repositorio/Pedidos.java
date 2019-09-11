package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.ItemPedido;
import com.agenciacrud.gestornegocio.model.Pedido;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.StatusPedido;
import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.model.vo.DataValor;
import com.agenciacrud.gestornegocio.repositorio.filtro.PedidoFilter;
import com.agenciacrud.gestornegocio.util.DataUtil;

public class Pedidos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	
	@SuppressWarnings({ "unchecked" })
	public Map<Date, Integer> valoresTotaisPorData(Integer numeroDeDias, Empresa empresa) {
		Session session = manager.unwrap(Session.class);
		
		numeroDeDias -= 1;
		
		StatusPedido status = StatusPedido.APROVADO;
		
		Calendar dataInicial = Calendar.getInstance();
		//Data truncada para pegar apenas dia
		dataInicial = DateUtils.truncate(dataInicial, Calendar.DAY_OF_MONTH);
		//não tem método de subtrair no calendar, por isso o número negativo, para subtrair.
		dataInicial.add(Calendar.DAY_OF_MONTH, numeroDeDias * -1); 
		
		Map<Date, Integer> resultado = criarMapaVazio(numeroDeDias, dataInicial);
		
		Criteria criteria = session.createCriteria(Pedido.class);
		criteria.add(Restrictions.eq("empresa", empresa));
		criteria.add(Restrictions.eq("status", status));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlGroupProjection("date(data_criacao) as data", 
						"date(data_criacao)", new String[] { "data" }, 
						new Type[] { StandardBasicTypes.DATE } ))
				.add(Projections.count("receita").as("quantidade")) //retorna um long
			)
			.add(Restrictions.ge("dataCriacao", dataInicial.getTime()));
			
		
		//Transforma em uma lista de DataValor
		List<DataValor> valoresPorData = criteria.setResultTransformer(Transformers.aliasToBean(DataValor.class)).list();
		
		for (DataValor dataValor : valoresPorData) {
			Integer quantidade = Integer.valueOf(dataValor.getQuantidade().toString());
			resultado.put(dataValor.getData(), quantidade);
		}
		
		return resultado;
	}

	private Map<Date, Integer> criarMapaVazio(Integer numeroDeDias, Calendar dataInicial) {
		//clona a data para que os valores alterados aqui não sejam alterados fora desse método
		dataInicial = (Calendar) dataInicial.clone();
		
		Map<Date, Integer> mapaInicial = new TreeMap<>();

		//Esse for é para garantir que o dia que não teve tratamento, ele retorne zero
		for (int i = 0; i <= numeroDeDias; i++) {
			mapaInicial.put(dataInicial.getTime(), 0);
			dataInicial.add(Calendar.DAY_OF_MONTH, 1); //adiciona um dia na data inicial
		}
		
		return mapaInicial;
	}

	@SuppressWarnings("unchecked")
	public List<Pedido> filtrados(PedidoFilter filtro) {
		Session session = (Session) manager;
		
		Criteria criteria = session.createCriteria(Pedido.class)
				// fazemos uma associação (join) com cliente e nomeamos como "c"
				.createAlias("pessoa", "p")
				// fazemos uma associação (join) com usuário e nomeamos como "v"
				.createAlias("usuario", "u");
		
		if (filtro.getNumeroDe() != null) {
			// id deve ser maior ou igual (ge = greater or equals) a filtro.numeroDe
			criteria.add(Restrictions.ge("id", filtro.getNumeroDe()));
		}

		if (filtro.getNumeroAte() != null) {
			// id deve ser menor ou igual (le = lower or equal) a filtro.numeroDe
			criteria.add(Restrictions.le("id", filtro.getNumeroAte()));
		}

		if (filtro.getDataCriacaoDe() != null) {
			criteria.add(Restrictions.ge("dataCriacao", filtro.getDataCriacaoDe()));
		} else {
			criteria.add(Restrictions.ge("dataCriacao", DataUtil.setDataAnterior(30)));
		}
		
		if (filtro.getDataCriacaoAte() != null) {
			criteria.add(Restrictions.le("dataCriacao", filtro.getDataCriacaoAte()));
		}
		
		if (StringUtils.isNotBlank(filtro.getNomeCliente())) {
			// acessamos o nome do cliente associado ao pedido pelo alias "p", criado anteriormente
			criteria.add(Restrictions.ilike("p.nome", filtro.getNomeCliente(), MatchMode.ANYWHERE));
		}
		
		if (StringUtils.isNotBlank(filtro.getNomeUsuario())) {
			// acessamos o nome do usuário associado ao pedido pelo alias "u", criado anteriormente
			criteria.add(Restrictions.ilike("u.nome", filtro.getNomeUsuario(), MatchMode.ANYWHERE));
		}
		
		if (filtro.getStatuses() != null && filtro.getStatuses().length > 0) {
			// adicionamos uma restrição "in", passando um array de constantes da enum StatusPedido
			criteria.add(Restrictions.in("status", filtro.getStatuses()));
		}
		
		criteria.add(Restrictions.eq("empresa", filtro.getEmpresa()));
		
		return criteria.addOrder(Order.desc("chaveRegistro")).list();
	}
	
	public List<ItemPedido> porPaciente(Empresa empresa, Pessoa pessoa) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ItemPedido> criteriaQuery = builder.createQuery(ItemPedido.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<ItemPedido> itemRoot = criteriaQuery.from(ItemPedido.class);
		
		Join<ItemPedido, Pedido> pedido = itemRoot.join("pedido");
				
		if (pessoa != null) {
			predicates.add(builder.equal(pedido.get("pessoa"), pessoa ));
		}
		predicates.add(builder.equal(pedido.get("empresa"), empresa));
		
		criteriaQuery.select(itemRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(pedido.get("dataCriacao")));
		
		TypedQuery<ItemPedido> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
public List<Pedido> porPedido(Long id) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Pedido> criteriaQuery = builder.createQuery(Pedido.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<Pedido> tratamento = criteriaQuery.from(Pedido.class);
		
		//não seleciona movimentações que foram estornadas
		predicates.add(builder.equal(tratamento.get("id"), id));
		
		criteriaQuery.select(tratamento);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		
		TypedQuery<Pedido> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
	public Pedido guardar(Pedido pedido) {
		return this.manager.merge(pedido);
	}

	public Pedido porId(Long id) {
		return manager.find(Pedido.class, id);
	}
	
}