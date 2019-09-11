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
import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoCategoriaContaEnumerador;
import com.agenciacrud.gestornegocio.repositorio.filtro.ConsultaFilter;
import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.jpa.Transactional;

public class CategoriasContas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Inject //chama método produtor
	private EntityManager manager;
	
	@Transactional
	public CategoriaConta guardar(CategoriaConta categoria) {
		return manager.merge(categoria);
	}
	
	@Transactional
	public void remover(CategoriaConta categoriaConta) {
		try{
			categoriaConta = porId(categoriaConta.getId());
			manager.remove(categoriaConta);
			manager.flush(); //Se tiver algo pendente de execução no banco de dados
		} catch (PersistenceException e) {
			throw new NegocioException("Esta categoria não pode ser excluída, deve estar vinculado a algum outro registro.");
		}
	}
	
	public List<CategoriaConta> porNome(String nome, Empresa empresa) {
		return this.manager.createQuery("from CategoriaConta " +
				"where empresa = :empresa and upper(nome) like :nome", CategoriaConta.class)
				.setParameter("nome", nome.toUpperCase() + "%")
				.setParameter("empresa", empresa)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<CategoriaConta> filtrados(ConsultaFilter filtro, Empresa empresa) {
		//desempacota o session do hibernate e coloca na variável session
		Session session = (Session) manager;
		Criteria criteria = session.createCriteria(CategoriaConta.class);
		
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
	
	public CategoriaConta porId(Long id) {
		return manager.find(CategoriaConta.class, id);
	}
	
	public List<CategoriaConta> todasCategorias(TipoCategoriaContaEnumerador tipo, Empresa empresa) {
		return this.manager.createQuery("from CategoriaConta where empresa = :empresa "
				//+ "and tipo = :tipo  and categoriaSuperior is null", CategoriaConta.class)
				+ "and tipo = :tipo  and categoriaSuperior is null", CategoriaConta.class)
				.setParameter("empresa", empresa)
				.setParameter("tipo", tipo)
				.getResultList();
	}
	
	//espera um parâmetro categoriaPai e passa o valor para superior
	public List<CategoriaConta> subcategoriasDe(CategoriaConta categoriaSuperior) {
		return manager.createQuery("from CategoriaConta where categoriaSuperior = :superior", 
				CategoriaConta.class).setParameter("superior", categoriaSuperior).getResultList();
	}
	
	/*Lista os bancos cadastrados
	public List<SelectItem> getComboCategoriaConta() {
		List<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add(new SelectItem(null, "-- Selecione --"));
		List<CategoriaConta> lista = this.todasCategorias();
		for(CategoriaConta categoria : lista) {
			itens.add(new SelectItem(categoria.getId(), categoria.getNome()));
		}
		return itens;
	}*/

}
