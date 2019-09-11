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

import com.agenciacrud.gestornegocio.model.AnamnesePaciente;
import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.model.ContaBancaria;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.ItemAnamnesePaciente;
import com.agenciacrud.gestornegocio.model.MovimentacaoConta;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.Receita;
import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoMovimentacao;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoOperacao;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.util.Numero;

public class Movimentacoes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;

	
	
	public List<MovimentacaoConta> filtroRelatorioMovimentacao(Empresa empresa, Date dataInicial, Date dataFinal) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<MovimentacaoConta> criteriaQuery = builder.createQuery(MovimentacaoConta.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<MovimentacaoConta> movimentacao = criteriaQuery.from(MovimentacaoConta.class);
					
		if (dataInicial != null) {
			predicates.add(builder.greaterThanOrEqualTo(movimentacao.<Date> get("dataLancamento"), dataInicial));
		}
		if (dataFinal != null) {
			predicates.add(builder.lessThanOrEqualTo(movimentacao.<Date> get("dataLancamento"), dataFinal));
		}
		//seleciona movimentação de crédito e débito para a empresa logada
		predicates.add(builder.equal(movimentacao.get("empresa"), empresa));
		
		//seleciona movimentação onde apenas está especificado débito ou crédito
		predicates.add(builder.or(builder.equal(movimentacao.get("tipoOperacao"), TipoOperacao.CREDITO), 
				builder.equal(movimentacao.get("tipoOperacao"), TipoOperacao.DEBITO)));
		
		//não seleciona movimentação que está anotada como transferência, não há necessidade dela aparecer no relatório
		predicates.add(builder.notEqual(movimentacao.get("tipoMovimentacao"), TipoMovimentacao.TRANSFERENCIA));
		
		//não seleciona movimentações que foram estornadas
		predicates.add(builder.equal(movimentacao.get("estornado"), false));
		
		criteriaQuery.select(movimentacao);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(movimentacao.get("dataLancamento")));
		
		TypedQuery<MovimentacaoConta> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
	//Filtro para imprimir relatório de extrato de contas
	public List<MovimentacaoConta> filtroRelatorioExtratoConta(Empresa empresa, Date dataInicial, Date dataFinal, ContaBancaria conta) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<MovimentacaoConta> criteriaQuery = builder.createQuery(MovimentacaoConta.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<MovimentacaoConta> movimentacao = criteriaQuery.from(MovimentacaoConta.class);
					
		if (dataInicial != null) {
			predicates.add(builder.greaterThanOrEqualTo(movimentacao.<Date> get("dataLancamento"), dataInicial));
		}
		if (dataFinal != null) {
			predicates.add(builder.lessThanOrEqualTo(movimentacao.<Date> get("dataLancamento"), dataFinal));
		}
		predicates.add(builder.equal(movimentacao.get("conta"), conta));
		//seleciona movimentação de crédito e débito para a empresa logada
		predicates.add(builder.equal(movimentacao.get("empresa"), empresa));
		
		
		criteriaQuery.select(movimentacao);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.desc(movimentacao.get("id")));
		
		TypedQuery<MovimentacaoConta> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
	public List<MovimentacaoConta> recebimentoPorPaciente(Empresa empresa, Pessoa pessoa) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<MovimentacaoConta> criteriaQuery = builder.createQuery(MovimentacaoConta.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<MovimentacaoConta> parcelaRoot = criteriaQuery.from(MovimentacaoConta.class);
				
		if (pessoa != null) {
			predicates.add(builder.equal(parcelaRoot.get("pessoa"), pessoa ));
		}
		predicates.add(builder.equal(parcelaRoot.get("estornado"), false));
		predicates.add(builder.equal(parcelaRoot.get("empresa"), empresa));
		
		criteriaQuery.select(parcelaRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(parcelaRoot.get("dataLancamento")));
		
		TypedQuery<MovimentacaoConta> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
	public List<MovimentacaoConta> filtroReceitasQuitadas(Empresa empresa, ConsultaFilter filtro) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<MovimentacaoConta> criteriaQuery = builder.createQuery(MovimentacaoConta.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<MovimentacaoConta> movimentacaoRoot = criteriaQuery.from(MovimentacaoConta.class);
		
		Join<MovimentacaoConta, Parcela> parcela = movimentacaoRoot.join("parcela");
		
		//Join<Parcela, Receita> receita = movimentacaoRoot.join("receita");
	
		criteriaQuery.select(movimentacaoRoot);
				
		if (filtro.getDataLancamentoInicial() != null) {
			predicates.add(builder.greaterThanOrEqualTo(movimentacaoRoot.<Date> get("dataLancamento"), filtro.getDataLancamentoInicial()));
		}
		if (filtro.getDataLancamentoFinal() != null) {
			predicates.add(builder.greaterThanOrEqualTo(movimentacaoRoot.<Date> get("dataLancamento"), filtro.getDataLancamentoFinal()));
		}
		
		predicates.add(builder.equal(movimentacaoRoot.get("empresa"), empresa));
		
		criteriaQuery.select(movimentacaoRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(movimentacaoRoot.get("descricao")));
		
		TypedQuery<MovimentacaoConta> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}

}
