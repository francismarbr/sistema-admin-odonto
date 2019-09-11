package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.agenciacrud.gestornegocio.model.AnamneseModelo;
import com.agenciacrud.gestornegocio.model.AnamnesePaciente;
import com.agenciacrud.gestornegocio.model.Banco;
import com.agenciacrud.gestornegocio.model.Despesa;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class AnamnesesModelos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	
	public AnamneseModelo guardarModelo(AnamneseModelo anamneseModelo) {
		return this.manager.merge(anamneseModelo);
	}

	public AnamneseModelo porId(Long id) {
		return manager.find(AnamneseModelo.class, id);
	}
	
	public List<AnamneseModelo> todasModelosAnamneses(Empresa empresa) {
		return this.manager.createQuery("from AnamneseModelo where empresa = :empresa", 
				AnamneseModelo.class).setParameter("empresa", empresa)
				.getResultList();
	}
	
	public List<AnamneseModelo> porModelo(Long id, Empresa empresa) {
		return this.manager.createQuery("from AnamneseModelo where id = :id and empresa = :empresa", 
				AnamneseModelo.class)
				.setParameter("id", id)
				.setParameter("empresa", empresa)
				.getResultList();
	}
	
	@Transactional
	public void remover(AnamneseModelo modelo) {
		try{
			modelo = porId(modelo.getId());
			manager.remove(modelo);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Este modelo não pode ser excluído");
		}
	}
	
}