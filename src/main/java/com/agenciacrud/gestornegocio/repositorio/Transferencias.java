package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.TransferenciaConta;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;

public class Transferencias implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	public TransferenciaConta guardar(TransferenciaConta transferencia) {
		return manager.merge(transferencia);
	}

	public TransferenciaConta porId(Long id) {
		return manager.find(TransferenciaConta.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<TransferenciaConta> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(TransferenciaConta.class);
		
		if(filtro.getId() != null) {
			criteria.add(Restrictions.eq("chaveRegistro", filtro.getId()));
		}

		if (filtro.getDataInicial() != null) {
			criteria.add(Restrictions.ge("dataLancamento", filtro.getDataInicial()));
		}
		
		if (filtro.getDataFinal() != null) {
			criteria.add(Restrictions.le("dataLancamento", filtro.getDataFinal()));
		}
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.desc("chaveRegistro")).list();
	}
	
}
