package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.agenciacrud.gestornegocio.model.PerfilAcesso;
import com.agenciacrud.gestornegocio.model.AnamnesePaciente;
import com.agenciacrud.gestornegocio.model.Banco;
import com.agenciacrud.gestornegocio.model.Despesa;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class PerfisAcesso implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;
	
	public PerfilAcesso guardar(PerfilAcesso perfilAcesso) {
		return this.manager.merge(perfilAcesso);
	}

	public PerfilAcesso porId(Long id) {
		return manager.find(PerfilAcesso.class, id);
	}
	
	public List<PerfilAcesso> todosPerfis() {
		return this.manager.createQuery("from PerfilAcesso", 
				PerfilAcesso.class).getResultList();
	}
		
	@Transactional
	public void remover(PerfilAcesso perfilAcesso) {
		try{
			perfilAcesso = porId(perfilAcesso.getId());
			manager.remove(perfilAcesso);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Este perfil não pode ser excluído");
		}
	}
	
}