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

import com.agenciacrud.gestornegocio.model.CategoriaConta;
import com.agenciacrud.gestornegocio.model.Categoria;
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoCategoriaContaEnumerador;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class Categorias implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Inject //chama método produtor
	private EntityManager manager;
	
	@Transactional
	public Categoria guardar(Categoria categoria) {
		return manager.merge(categoria);
	}
	
	@Transactional
	public void remover(Categoria categoriaConta) {
		try{
			categoriaConta = porId(categoriaConta.getId());
			manager.remove(categoriaConta);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Esta categoria não pode ser excluída, deve estar vinculado a algum outro registro.");
		}
	}
	
	public List<Categoria> porNome(String nome, Empresa empresa) {
		return this.manager.createQuery("from Categoria " +
				"where empresa = :empresa and upper(nome) like :nome", Categoria.class)
				.setParameter("nome", nome.toUpperCase() + "%")
				.setParameter("empresa", empresa)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(Categoria.class);
		
		if(filtro.getId() != null) {
			criteria.add(Restrictions.eq("chaveRegistro", filtro.getId()));
		}
		
		if(filtro.getTipoCategoriaConta() != null) {
			criteria.add(Restrictions.eq("tipo", filtro.getTipoCategoriaConta()));
		}
		
		if(StringUtils.isNotBlank(filtro.getNome())) {
			criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.eq("empresa", empresa));
		return criteria.addOrder(Order.asc("nome")).list();
	}
	

	
	public List<Categoria> todasCategorias(TipoCategoriaContaEnumerador tipo, Empresa empresa) {
		return this.manager.createQuery("from Categoria where empresa = :empresa "
				//+ "and tipo = :tipo  and categoriaSuperior is null", CategoriaConta.class)
				+ "and tipo = :tipo  and categoriaSuperior is null", Categoria.class)
				.setParameter("empresa", empresa)
				.setParameter("tipo", tipo)
				.getResultList();
	}

	
	public Categoria porId(Long id) {
		return manager.find(Categoria.class, id);
	}
	
	public List<Categoria> raizes() {
		return manager.createQuery("from Categoria where categoriaPai is null", 
				Categoria.class).getResultList();
	}
	
	//espera um parâmetro categoriaPai e passa o valor para raiz
	public List<Categoria> subcategoriasDe(Categoria categoriaPai) {
		return manager.createQuery("from Categoria where categoriaPai = :raiz", 
				Categoria.class).setParameter("raiz", categoriaPai).getResultList();
	}

}
