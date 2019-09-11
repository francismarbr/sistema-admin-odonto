package com.agenciacrud.gestornegocio.repositorio;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.agenciacrud.gestornegocio.model.Empresa;
import com.agenciacrud.gestornegocio.model.ExecucaoProcessador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoExecucaoProcessadorEnumerador;
import com.agenciacrud.gestornegocio.util.DataUtil;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.util.excecoes.ConsultarException;
import com.agenciacrud.gestornegocio.util.excecoes.NaoEncontradoException;

public class RGeral implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	public List buscaTodos(Class classe, Empresa empresa) {
		return this.manager.createQuery("SELECT o FROM "+ classe.getSimpleName() + " o WHERE o.empresa = :empresa" )
				.setParameter("empresa", empresa).getResultList();
	}
	
	public List buscaDentistas(Class classe, Empresa empresa) {
		String status = "Ativo";
		return this.manager.createQuery("SELECT o FROM "+ classe.getSimpleName() + " o WHERE o.empresa = :empresa and status = :status" )
				.setParameter("empresa", empresa)
				.setParameter("status", new String("Ativo"))
				.getResultList();
	}
	
	
	public List porNome(Class classe, String nome, Empresa empresa) {
		return this.manager.createQuery("from " + classe.getSimpleName() +
				" where upper(nome) like :nome and empresa = :empresa")
				.setParameter("nome", "%" + nome.toUpperCase() + "%")
				.setParameter("empresa", empresa)
				.getResultList();
	}
		
	/**
	 * Busca o objeto uma vez passado sua chave como parâmetro.
	 * 
	 * @param chave
	 *            identificador
	 * @return Objeto do tipo T
	 * @throws ConsultarException
	 * @throws NaoEncontradoException
	 */
	public <T> T getObj(Long id, Class classe) {

		if (!Numero.isMaiorZero(id)) {
			return null;
		}
		String sql = "from " +classe.getSimpleName() + " o WHERE o.id = :id";
		return (T) this.manager.createQuery(sql).setParameter("id", id).getSingleResult();
	}
	
	public List buscaUltimoRegistroPorEmpresa(Class classe, Empresa empresa) {
		List resultado = null;
		String sql = "SELECT o FROM " + classe.getSimpleName() + " o WHERE o.chaveRegistro=(SELECT MAX(x.chaveRegistro) FROM "
				+ classe.getSimpleName() + " x WHERE x.empresa = :empresa)";
		resultado = this.manager.createQuery(sql).setParameter("empresa", empresa).getResultList();
		if(resultado.size() < 1) {
			return null;
		} else {
			return resultado;
		}
			
	}
	
	public List obterTodos(Class classe) {
		return this.manager.createQuery("SELECT o FROM "+ classe.getSimpleName() + " o").getResultList();
	}
	
	//Busca informações por chave de registro e empresa
	public List porChaveRegistro(Class classe, Long chaveRegistro, Empresa empresa) {
		List resultado = null;
		String sql = "SELECT o FROM " + classe.getSimpleName() + " o WHERE o.chaveRegistro = :chaveRegistro and o.empresa = :empresa)";
		resultado = this.manager.createQuery(sql)
				.setParameter("empresa", empresa)
				.setParameter("chaveRegistro", chaveRegistro)
				.getResultList();
		if(resultado.size() < 1) {
			return null;
		} else {
			return resultado;
		}
	}
	
	//Busca Empresa principal do sistema para o usuário
	public List empresaPrinciaplPorChaveRegistro(Class classe, Long chaveRegistro, Empresa empresa) {
		List resultado = null;
		String sql = "SELECT o FROM " + classe.getSimpleName() + " o WHERE o.chaveRegistro = :chaveRegistro and o = :empresa)";
		resultado = this.manager.createQuery(sql)
				.setParameter("empresa", empresa)
				.setParameter("chaveRegistro", chaveRegistro)
				.getResultList();
		if(resultado.size() < 1) {
			return null;
		} else {
			return resultado;
		}
	}
	
	public List obterAgendamentosAutomaticos(Class classe, boolean repetir){
		List resultado = null;
		String sql = "SELECT o FROM " + classe.getSimpleName() + " o WHERE o.repetir = :repetir)";
		resultado = this.manager.createQuery(sql).setParameter("repetir", repetir).getResultList();
		if(resultado.size() < 1) {
			return null;
		} else {
			return resultado;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ExecucaoProcessador> obterListaDeExecucaoProcessadorNoMesAtual(TipoExecucaoProcessadorEnumerador tipo){
		
		List<ExecucaoProcessador> resultado = null;
		Date inicio = DataUtil.setPrimeiroDiaMes(new Date());
		Date fim = DataUtil.setUltimoDiaMes(new Date());
		
		String sql = "SELECT o FROM ExecucaoProcessador o WHERE o.tipo = :tipo "
				+ " AND o.data between :inicio AND :fim ORDER BY o.id DESC)";
		
		resultado = this.manager.createQuery(sql)
				.setParameter("tipo", tipo)
				.setParameter("inicio", inicio)
				.setParameter("fim", fim)
				.getResultList();
		if(resultado.size() < 1) {
			System.out.println("Aqui o resultado é "+resultado.size());
			//throw new NoResultException();
		} 
		
		return resultado;
		
	}
}
