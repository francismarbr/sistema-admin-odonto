package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.agenciacrud.gestornegocio.model.Agendamento;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.enumeradores.StatusAgendamentoEnumerador;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Agendamentos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	public Agendamento guardar(Agendamento agendamento) {
		return manager.merge(agendamento);
	}
	
	@Transactional
	public void remover(Agendamento agendamento) {
		try{
			agendamento = porId(agendamento.getId());
			manager.remove(agendamento);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Agendamento não pode ser excluída.");
		}
	}

	@SuppressWarnings("unchecked")
	public List<Agendamento> filtrados(ConsultaFilter filtro, Empresa empresa, StatusAgendamentoEnumerador status, boolean agendamentoProgramado) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Agendamento.class);
		
		if(filtro.getId() != null) {
			criteria.add(Restrictions.eq("chaveRegistro", filtro.getId()));
		}
		
		if(filtro.getPessoa() != null){
			criteria.add(Restrictions.eq("cliente", filtro.getPessoa()));
		}
		
		if(filtro.getDentista() != null){
			criteria.add(Restrictions.eq("dentista", filtro.getDentista()));
		}
		
		criteria.add(Restrictions.ge("data", filtro.getDataInicial()));
		criteria.add(Restrictions.le("data", filtro.getDataFinal()));
		
		
		/*if(!agendamentoProgramado) {
			
			if(status!=null){
				criteria.add(Restrictions.eq("status", status));
			}
			
			if (filtro.getDataInicial() != null) {
				criteria.add(Restrictions.ge("data", filtro.getDataInicial()));
			} else {
				criteria.add(Restrictions.ge("data", DataUtil.setDataAnterior(0)));
			}
		
		
			if (filtro.getDataFinal() != null) {
				criteria.add(Restrictions.le("data", filtro.getDataFinal()));
			}
		} else {
			criteria.add(Restrictions.eq("repetir", agendamentoProgramado));
		}*/
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.asc("id")).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Agendamento> agendaDoDia(ConsultaFilter filtro, Empresa empresa, StatusAgendamentoEnumerador status, boolean agendamentoProgramado) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Agendamento.class);
		
		if(filtro.getId() != null) {
			criteria.add(Restrictions.eq("chaveRegistro", filtro.getId()));
		}
		
		if(filtro.getPessoa() != null){
			criteria.add(Restrictions.eq("cliente", filtro.getPessoa()));
		}
		
		if(filtro.getDentista() != null){
			criteria.add(Restrictions.eq("dentista", filtro.getDentista()));
		}
		
		if(!agendamentoProgramado) {
			
			if(status!=null){
				criteria.add(Restrictions.eq("status", status));
			}
			
			if (filtro.getDataInicial() != null) {
				criteria.add(Restrictions.ge("data", filtro.getDataInicial()));
			} else {
				criteria.add(Restrictions.ge("data", DataUtil.setDataAnterior(0)));
			}
		
		
			if (filtro.getDataFinal() != null) {
				criteria.add(Restrictions.le("data", filtro.getDataFinal()));
			}
		}
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.asc("horaInicial")).list();
	}

	public Agendamento porId(Long id) {
		return manager.find(Agendamento.class, id);
	}

}
