package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.agenciacrud.gestornegocio.service.NegocioException;
import com.agenciacrud.gestornegocio.util.Numero;
import com.agenciacrud.gestornegocio.validation.SKU;

@Entity
public class Produto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long chaveRegistro;
	
	//não aceita campo em branco
	@NotBlank 
	@Size(max = 80)
	@Column(nullable = false, length = 255)
	private String nome;
	
	@Column(name = "valor_unitario", precision = 10, scale = 2)
	private BigDecimal valorUnitario;
	
	@NotNull(message = "é obrigatório") @Min(0) //@Max(value = 9999, message = "é um valor muito alto")
	@Column(nullable = false)
	private BigDecimal quantidadeEstoque;
	
	private Date dataUltimaCompra;
	
	private BigDecimal minimo;
	
	@ManyToOne
	private Empresa empresa;
	
	@OneToOne
	private UnidadeMedida unidadeMedida;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getChaveRegistro() {
		return chaveRegistro;
	}
	public void setChaveRegistro(Long chaveRegistro) {
		this.chaveRegistro = chaveRegistro;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}
	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	public BigDecimal getQuantidadeEstoque() {
		return quantidadeEstoque;
	}
	public void setQuantidadeEstoque(BigDecimal quantidadeEstoque) {
		this.quantidadeEstoque = quantidadeEstoque;
	}

	public Date getDataUltimaCompra() {
		return dataUltimaCompra;
	}
	public void setDataUltimaCompra(Date dataUltimaCompra) {
		this.dataUltimaCompra = dataUltimaCompra;
	}
	public BigDecimal getMinimo() {
		return minimo;
	}
	public void setMinimo(BigDecimal minimo) {
		this.minimo = minimo;
	}
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	public UnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}
	
	public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Produto other = (Produto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public void baixarEstoque(BigDecimal quantidade) {
		BigDecimal novaQuantidade = new BigDecimal(0);
		novaQuantidade = this.getQuantidadeEstoque().subtract(quantidade);
		
		if(novaQuantidade.compareTo(new BigDecimal(0)) < 0) {
			throw new NegocioException("Não há disponibilidade no estoque de "
					+ quantidade + "itens do produto " + this.getNome() + ".");
		}
		
		this.setQuantidadeEstoque(novaQuantidade);
	}
	
	public void adicionarEstoque(BigDecimal quantidade) {
		this.setQuantidadeEstoque(getQuantidadeEstoque().add(quantidade));
	}
	
	

}
