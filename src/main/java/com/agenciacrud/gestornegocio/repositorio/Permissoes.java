package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.agenciacrud.gestornegocio.model.Banco;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.Permissao;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Permissoes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	@Transactional
	public Permissao guardar(Permissao permissao) {
		return manager.merge(permissao);
	}
	
	@Transactional
	public void remover(Permissao permissao) {
		try{
			permissao = porId(permissao.getId());
			manager.remove(permissao);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Permissão não pode ser excluído.");
		}
	}


	public Permissao porId(Long id) {
		return manager.find(Permissao.class, id);
	}
	
	public List<Permissao> porNome(String nome) {
		return this.manager.createQuery("from Permissao where upper(descricao) like :nome").setParameter("nome", "%" + nome.toUpperCase() + "%")
				.getResultList();
	}
	
	public List buscaTudo(Class classe) {
		return this.manager.createQuery("from "+ classe.getSimpleName()).getResultList();
	}
	
	public List<Permissao> todasPermissoes() {
		return this.manager.createQuery("from Permissao", Permissao.class)
				.getResultList();
	}
	
}
