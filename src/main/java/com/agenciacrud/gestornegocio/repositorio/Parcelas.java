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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.model.Despesa;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.Fornecedor;
import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.Receita;
import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoContaEnumerador;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.repositorio.filtro.MovimentacaoFilter;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.Numero;

public class Parcelas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	public MovimentacaoConta guardarMovimentacao(MovimentacaoConta movimentacao) {
		return manager.merge(movimentacao);
	}
	
	public Parcela guardarParcela(Parcela parcela) {
		return manager.merge(parcela);
	}

	@SuppressWarnings("unchecked")
	public List<Parcela> filtrados(MovimentacaoFilter filtro, Empresa empresa) {
		Session session = (Session) manager;
		
		Date dataFinal = DataUtil.addDias(DataUtil.setHoraFinal(new Date()), 90);
		Criteria criteria = null;
		
		if(filtro.getTipoConta().equals(TipoContaEnumerador.CONTA_RECEBER)) {
		
			 criteria = session.createCriteria(Parcela.class)
				// fazemos uma associação (join) com receita e nomeamos como "r"
				.createAlias("receita", "r");
			 criteria.add(Restrictions.eq("r.empresa", empresa));
		} else {
			 criteria = session.createCriteria(Parcela.class)
				// fazemos uma associação (join) com despesa e nomeamos como "d"
				 .createAlias("despesa", "d");
			 criteria.add(Restrictions.eq("d.empresa", empresa));
		}

		if (filtro.getDataInicial() != null) {
			criteria.add(Restrictions.ge("dataVencimento", filtro.getDataInicial()));
		}
		
		if (filtro.getDataFinal() != null) {
			criteria.add(Restrictions.le("dataVencimento", filtro.getDataFinal()));
		} else {
			//Busca todas as parcelas com vencimento em até 60 dias a partir da data de hoje
			criteria.add(Restrictions.le("dataVencimento", DataUtil.addDias(
					DataUtil.setHoraFinal(new Date()), 60)));
		}
		
		if (filtro.getPessoa() != null && filtro.getTipoConta().equals(TipoContaEnumerador.CONTA_RECEBER)) {
			// acessamos o nome do cliente associado ao pedido pelo alias "p", criado anteriormente
			criteria.add(Restrictions.eqOrIsNull("r.cliente", filtro.getPessoa()));
		}
		
		if (filtro.getPessoa() != null && filtro.getTipoConta().equals(TipoContaEnumerador.CONTA_PAGAR)) {
			// acessamos o nome do cliente associado ao pedido pelo alias "p", criado anteriormente
			criteria.add(Restrictions.eqOrIsNull("d.fornecedor", filtro.getPessoa()));
		}
		
		//verifica através do eq se situacao é igual a quitada ou (or) igual paga parcialmente
		criteria.add(Restrictions.or((Restrictions.eq("situacao", SituacaoPagamentoEnumerador.ABERTA)),
				(Restrictions.eq("situacao", SituacaoPagamentoEnumerador.PAGA_PARCIALMENTE))));
		
		return criteria.addOrder(Order.asc("id")).list();
	}
	
	public List<Parcela> filtroRelatorio(Empresa empresa, Pessoa pessoa, CategoriaConta categoriaConta, SituacaoPagamentoEnumerador situacao,
			Date dataInicioParcela, Date dataFimParcela) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Parcela> criteriaQuery = builder.createQuery(Parcela.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<Parcela> parcelaRoot = criteriaQuery.from(Parcela.class);
				
		if (pessoa != null) {
			predicates.add(builder.equal(parcelaRoot.get("receita.cliente"), pessoa ));
		}
		if (categoriaConta != null) {
			predicates.add(builder.equal(parcelaRoot.get("receita.categoriaConta"), categoriaConta ));
		}
		if (situacao != null) {
			predicates.add(builder.equal(parcelaRoot.get("situacao"), situacao));
		}
		if (dataInicioParcela != null) {
			predicates.add(builder.greaterThanOrEqualTo(parcelaRoot.<Date> get("dataVencimento"), dataInicioParcela));
		}
		if (dataFimParcela != null) {
			predicates.add(builder.lessThanOrEqualTo(parcelaRoot.<Date> get("dataVencimento"), dataFimParcela));
		}
		
		predicates.add(builder.equal(parcelaRoot.get("empresa"), empresa));
		
		criteriaQuery.select(parcelaRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(parcelaRoot.get("nome")));
		
		TypedQuery<Parcela> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
	public List<Parcela> filtroRelatorioReceitas(Empresa empresa, Pessoa pessoa, CategoriaConta categoriaConta, SituacaoPagamentoEnumerador situacao,
			Date dataVencimentoInicial, Date dataVencimentoFinal, Date dataLancamentoInicial, Date dataLancamentoFinal) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Parcela> criteriaQuery = builder.createQuery(Parcela.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<Parcela> parcelaRoot = criteriaQuery.from(Parcela.class);
		
		Join<Parcela, Receita> receita = parcelaRoot.join("receita");
		
		criteriaQuery.select(parcelaRoot);
				
		if (Numero.isMaiorZero(pessoa.getId())) {
			predicates.add(builder.equal(receita.get("cliente"), pessoa ));
		}
		if (categoriaConta != null) {
			predicates.add(builder.equal(receita.get("categoriaConta"), categoriaConta ));
		}
		if (situacao != null) {
			if(situacao.equals(SituacaoPagamentoEnumerador.QUITADA)) {
				predicates.add(builder.equal(parcelaRoot.get("situacao"), situacao));
			}
			else {
				predicates.add(builder.or(builder.equal(parcelaRoot.get("situacao"), SituacaoPagamentoEnumerador.PAGA_PARCIALMENTE), 
						builder.equal(parcelaRoot.get("situacao"), SituacaoPagamentoEnumerador.ABERTA)));
			}
		}
		if (dataVencimentoInicial != null) {
			predicates.add(builder.greaterThanOrEqualTo(receita.<Date> get("dataVencimento"), dataVencimentoInicial));
		}
		if (dataVencimentoFinal != null) {
			predicates.add(builder.lessThanOrEqualTo(receita.<Date> get("dataVencimento"), dataVencimentoFinal));
		}
		
		if (dataLancamentoInicial != null) {
			predicates.add(builder.greaterThanOrEqualTo(receita.<Date> get("dataLancamento"), dataLancamentoInicial));
		}
		if (dataLancamentoFinal != null) {
			predicates.add(builder.lessThanOrEqualTo(receita.<Date> get("dataLancamento"), dataLancamentoFinal));
		}
		
		predicates.add(builder.equal(receita.get("empresa"), empresa));
		
		criteriaQuery.select(parcelaRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(receita.get("nome")));
		
		TypedQuery<Parcela> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
	public List<Parcela> filtroRelatorioDespesas(Empresa empresa, Fornecedor fornecedor, CategoriaConta categoriaConta, SituacaoPagamentoEnumerador situacao,
			Date dataVencimentoInicial, Date dataVencimentoFinal, Date dataLancamentoInicial, Date dataLancamentoFinal) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Parcela> criteriaQuery = builder.createQuery(Parcela.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<Parcela> parcelaRoot = criteriaQuery.from(Parcela.class);
		
		Join<Parcela, Despesa> despesa = parcelaRoot.join("despesa");
		
		criteriaQuery.select(parcelaRoot);
				
		if (Numero.isMaiorZero(fornecedor.getId())) {
			predicates.add(builder.equal(despesa.get("fornecedor"), fornecedor ));
		}
		if (categoriaConta != null) {
			predicates.add(builder.equal(despesa.get("categoriaConta"), categoriaConta ));
		}
		if (situacao != null) {
			if(situacao.equals(SituacaoPagamentoEnumerador.QUITADA)) {
				predicates.add(builder.equal(parcelaRoot.get("situacao"), situacao));
			}
			else {
				predicates.add(builder.or(builder.equal(parcelaRoot.get("situacao"), SituacaoPagamentoEnumerador.PAGA_PARCIALMENTE), 
						builder.equal(parcelaRoot.get("situacao"), SituacaoPagamentoEnumerador.ABERTA)));
			}
		}
		if (dataVencimentoInicial != null) {
			predicates.add(builder.greaterThanOrEqualTo(despesa.<Date> get("dataVencimento"), dataVencimentoInicial));
		}
		if (dataVencimentoFinal != null) {
			predicates.add(builder.lessThanOrEqualTo(despesa.<Date> get("dataVencimento"), dataVencimentoFinal));
		}
		
		if (dataLancamentoInicial != null) {
			predicates.add(builder.greaterThanOrEqualTo(despesa.<Date> get("dataLancamento"), dataLancamentoInicial));
		}
		if (dataLancamentoFinal != null) {
			System.out.println("A data de vencimento é " +dataLancamentoFinal);
			predicates.add(builder.lessThanOrEqualTo(despesa.<Date> get("dataLancamento"), dataLancamentoFinal));
		}
		
		predicates.add(builder.equal(despesa.get("empresa"), empresa));
		
		criteriaQuery.select(parcelaRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(despesa.get("nome")));
		
		TypedQuery<Parcela> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
	//Realiza filtro na tabela parcela para contas do tipo a receber
	public List<Parcela> filtroRelatorioContasReceber(Empresa empresa, Pessoa pessoa, CategoriaConta categoriaConta,
			Date dataVencimentoInicial, Date dataVencimentoFinal) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Parcela> criteriaQuery = builder.createQuery(Parcela.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<Parcela> parcelaRoot = criteriaQuery.from(Parcela.class);
		
		Join<Parcela, Receita> receita = parcelaRoot.join("receita");
		
		criteriaQuery.select(parcelaRoot);
				
		if (Numero.isMaiorZero(pessoa.getId())) {
			predicates.add(builder.equal(receita.get("cliente"), pessoa ));
		}
		if (categoriaConta != null) {
			predicates.add(builder.equal(receita.get("categoriaConta"), categoriaConta ));
		}

		if (dataVencimentoInicial != null) {
			predicates.add(builder.greaterThanOrEqualTo(parcelaRoot.<Date> get("dataVencimento"), dataVencimentoInicial));
		}
		if (dataVencimentoFinal != null) {
			predicates.add(builder.lessThanOrEqualTo(parcelaRoot.<Date> get("dataVencimento"), dataVencimentoFinal));
		}
		

		predicates.add(builder.and(builder.equal(receita.get("empresa"), empresa)));
		predicates.add(builder.or(builder.equal(parcelaRoot.get("situacao"), SituacaoPagamentoEnumerador.PAGA_PARCIALMENTE), 
				builder.equal(parcelaRoot.get("situacao"), SituacaoPagamentoEnumerador.ABERTA)));		
		
		criteriaQuery.select(parcelaRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(receita.get("nome")));
		
		TypedQuery<Parcela> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}	
	//Realiza filtro na tabela parcela para contas do tipo a pagar
	public List<Parcela> filtroRelatorioContasPagar(Empresa empresa, Pessoa pessoa, CategoriaConta categoriaConta,
			Date dataVencimentoInicial, Date dataVencimentoFinal) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Parcela> criteriaQuery = builder.createQuery(Parcela.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<Parcela> parcelaRoot = criteriaQuery.from(Parcela.class);
		
		Join<Parcela, Despesa> despesa = parcelaRoot.join("despesa");
		
		criteriaQuery.select(parcelaRoot);
				
		if (Numero.isMaiorZero(pessoa.getId())) {
			predicates.add(builder.equal(despesa.get("fornecedor"), pessoa ));
		}
		if (categoriaConta != null) {
			predicates.add(builder.equal(despesa.get("categoriaConta"), categoriaConta ));
		}

		if (dataVencimentoInicial != null) {
			predicates.add(builder.greaterThanOrEqualTo(parcelaRoot.<Date> get("dataVencimento"), dataVencimentoInicial));
		}
		if (dataVencimentoFinal != null) {
			predicates.add(builder.lessThanOrEqualTo(parcelaRoot.<Date> get("dataVencimento"), dataVencimentoFinal));
		}
		

		predicates.add(builder.and(builder.equal(despesa.get("empresa"), empresa)));
		predicates.add(builder.or(builder.equal(parcelaRoot.get("situacao"), SituacaoPagamentoEnumerador.PAGA_PARCIALMENTE), 
				builder.equal(parcelaRoot.get("situacao"), SituacaoPagamentoEnumerador.ABERTA)));		
		
		criteriaQuery.select(parcelaRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(despesa.get("nome")));
		
		TypedQuery<Parcela> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<MovimentacaoConta> filtroBaixadas(MovimentacaoFilter filtro, Empresa empresa) {
		Session session = (Session) manager;

		Date dataFinal = DataUtil.addDias(DataUtil.setHoraFinal(new Date()), 90);
		Criteria criteria = session.createCriteria(MovimentacaoConta.class);

		if (filtro.getDataInicial() != null) {
			criteria.add(Restrictions.ge("dataLancamento", filtro.getDataInicial()));
		}
		
		if (filtro.getDataFinal() != null) {
			criteria.add(Restrictions.le("dataLancamento", filtro.getDataFinal()));
		}
		if(filtro.getPessoa() != null){
			criteria.add(Restrictions.eqOrIsNull("pessoa", filtro.getPessoa()));
		}
		
		criteria.add(Restrictions.eq("empresa", empresa));
		criteria.add(Restrictions.eq("estornado", false));
		criteria.add(Restrictions.isNotNull("parcela"));
		return criteria.addOrder(Order.asc("id")).list();
	}
	
	//Realiza filtro na tabela parcela para contas do tipo a receber
		public List<Parcela> filtroReceitaPorDentista(Empresa empresa, ConsultaFilter filtro) {
			
			CriteriaBuilder builder = manager.getCriteriaBuilder();
			
			CriteriaQuery<Parcela> criteriaQuery = builder.createQuery(Parcela.class);
			
			List<Predicate> predicates = new ArrayList<>();
			
			Root<Parcela> parcelaRoot = criteriaQuery.from(Parcela.class);
			
			Join<Parcela, Receita> receita = parcelaRoot.join("receita");
			
			criteriaQuery.select(parcelaRoot);
					
			if (filtro.getDentista() != null && Numero.isMaiorZero(filtro.getDentista().getId())) {
				predicates.add(builder.equal(receita.get("dentista"), filtro.getDentista() ));
			}
			

			if (filtro.getDataInicial() != null) {
				predicates.add(builder.greaterThanOrEqualTo(parcelaRoot.<Date> get("dataVencimento"), filtro.getDataInicial()));
			}
			if (filtro.getDataFinal() != null) {
				predicates.add(builder.lessThanOrEqualTo(parcelaRoot.<Date> get("dataVencimento"), filtro.getDataFinal()));
			}
			

			predicates.add(builder.and(builder.equal(receita.get("empresa"), empresa)));
			predicates.add(builder.or(builder.equal(parcelaRoot.get("situacao"), SituacaoPagamentoEnumerador.PAGA_PARCIALMENTE), 
					builder.equal(parcelaRoot.get("situacao"), SituacaoPagamentoEnumerador.ABERTA)));		
			
			criteriaQuery.select(parcelaRoot);
			criteriaQuery.where(predicates.toArray(new Predicate[0]));
			criteriaQuery.orderBy(builder.asc(receita.get("nome")));
			
			TypedQuery<Parcela> query = manager.createQuery(criteriaQuery);
			return query.getResultList();
		}
	

}
