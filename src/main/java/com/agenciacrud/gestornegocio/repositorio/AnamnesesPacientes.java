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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.agenciacrud.gestornegocio.model.AnamneseModelo;
import com.agenciacrud.gestornegocio.model.AnamnesePaciente;
import com.agenciacrud.gestornegocio.model.Atestado;
import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.ItemAnamnesePaciente;
import com.agenciacrud.gestornegocio.model.MovimentacaoEstoque;
import com.agenciacrud.gestornegocio.model.Parcela;
import com.agenciacrud.gestornegocio.model.Pessoa;
import com.agenciacrud.gestornegocio.model.Receita;
import com.agenciacrud.gestornegocio.model.enumeradores.SituacaoPagamentoEnumerador;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.DataUtil;

public class AnamnesesPacientes implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	
	public AnamnesePaciente guardar(AnamnesePaciente anamnesePaciente) {
		return this.manager.merge(anamnesePaciente);
	}

	public AnamnesePaciente porId(Long id) {
		return manager.find(AnamnesePaciente.class, id);
	}
	
	public List<AnamnesePaciente> filtro(Empresa empresa, Date dataInicio, Date dataFim, Pessoa pessoa) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<AnamnesePaciente> criteriaQuery = builder.createQuery(AnamnesePaciente.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<AnamnesePaciente> anamneseRoot = criteriaQuery.from(AnamnesePaciente.class);
		
		if (dataInicio != null) {
			predicates.add(builder.greaterThanOrEqualTo(anamneseRoot.<Date> get("dataRegistro"), dataInicio));
		} else {
			predicates.add(builder.greaterThanOrEqualTo(anamneseRoot.<Date> get("dataRegistro"),DataUtil.setDataAnterior(30)));
		}
		if (dataFim != null) {
			predicates.add(builder.lessThanOrEqualTo(anamneseRoot.<Date> get("dataRegistro"), dataFim));
		}
		
		if(pessoa != null){
			predicates.add(builder.equal(anamneseRoot.get("pessoa"), pessoa));
		}
		
		predicates.add(builder.equal(anamneseRoot.get("empresa"), empresa));
		
		criteriaQuery.select(anamneseRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.desc(anamneseRoot.get("dataRegistro")));
		
		TypedQuery<AnamnesePaciente> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
	public List<ItemAnamnesePaciente> anamnesesPaciente(Empresa empresa, Pessoa paciente) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ItemAnamnesePaciente> criteriaQuery = builder.createQuery(ItemAnamnesePaciente.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<ItemAnamnesePaciente> anamnesesRoot = criteriaQuery.from(ItemAnamnesePaciente.class);
		
		Join<ItemAnamnesePaciente, AnamnesePaciente> anamnesePaciente = anamnesesRoot.join("anamnesePaciente");
				
		if (paciente != null) {
			predicates.add(builder.equal(anamnesePaciente.get("pessoa"), paciente ));
		}		
		predicates.add(builder.equal(anamnesePaciente.get("empresa"), empresa));
		
		criteriaQuery.select(anamnesesRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.desc(anamnesePaciente.get("dataRegistro")));
		
		TypedQuery<ItemAnamnesePaciente> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}

	public void remover(AnamnesePaciente anamneseSelecionada) {
		try {
			System.out.println("A anamnse selecionada é "+anamneseSelecionada.getId());
			anamneseSelecionada = porId(anamneseSelecionada.getId());
			manager.remove(anamneseSelecionada);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Esta Anamnese não pode ser excluída");
		}
	}
	
	public List<ItemAnamnesePaciente> porAnamnese(AnamnesePaciente id) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<ItemAnamnesePaciente> criteriaQuery = builder.createQuery(ItemAnamnesePaciente.class);
		
		List<Predicate> predicates = new ArrayList<>();
		
		Root<ItemAnamnesePaciente> anamenese = criteriaQuery.from(ItemAnamnesePaciente.class);
		
		//não seleciona movimentações que foram estornadas
		predicates.add(builder.equal(anamenese.get("anamnesePaciente"), id));
		
		criteriaQuery.select(anamenese);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(anamenese.get("ordem")));
		TypedQuery<ItemAnamnesePaciente> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
		
}