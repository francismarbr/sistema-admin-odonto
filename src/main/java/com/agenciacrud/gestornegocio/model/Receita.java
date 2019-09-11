package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.agenciacrud.gestornegocio.util.Numero;

@Entity
public class Receita implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2434586207988482045L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long chaveRegistro;
	
	@NotBlank
	private String nome;

	@Temporal(TemporalType.DATE)
	private Date dataLancamento;
	
	@NotNull
	@Temporal(TemporalType.DATE)
	private Date dataVencimento;
	
	private BigDecimal desconto = BigDecimal.ZERO;
	
	@NotNull
	private BigDecimal valorAReceber = BigDecimal.ZERO;
	
	@NotNull
	private Integer quantidadeParcelas;

	@ManyToOne
	public Pessoa cliente;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "receita", orphanRemoval = true, fetch = FetchType.LAZY)
	public List<Parcela> parcelas;

	@ManyToOne
	private CategoriaConta categoriaConta;
	
	@ManyToOne(optional=true)
	private Dentista dentista;
	
	@ManyToOne
	private Empresa empresa;

	@OneToOne
	@JoinColumn(name = "pedidovenda_id")
	public Pedido pedidoVenda;
	
	@Column(columnDefinition = "text")
	private String obs;
	
	private boolean repetir = false;
	private Integer diaRealizacao;

	private BigDecimal valorTotal = BigDecimal.ZERO;
	
	public boolean isPossuiBaixaOUCancelamento() {
		for (Parcela parcela : getParcelas()) {
			if (parcela.getCancelamento() != null
					|| parcela.getRecebimentos().size() > 0)
				return true;

		}
		return false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(Date dataLancamento) {
		this.dataLancamento = dataLancamento;
	}

	public BigDecimal getValorAReceber() {
		BigDecimal total = new BigDecimal(0);
		
		if (getId() != null) {
			for (Parcela parcela : getParcelas()) {
				total = total.add(parcela.getValor());
			}
			return total;
		} else {
			if(this.getValorTotal() == null)
				return null;
			valorAReceber = this.getValorTotal().subtract(this.getDesconto());
			return valorAReceber;
		}
	}

	public void setValorAReceber(BigDecimal valorAReceber) {
		this.valorAReceber = valorAReceber;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public Integer getQuantidadeParcelas() {
		return quantidadeParcelas;
	}

	public void setQuantidadeParcelas(Integer quantidadeParcelas) {
		this.quantidadeParcelas = quantidadeParcelas;
	}

	public Pessoa getCliente() {
		if (cliente == null)
			cliente = new Pessoa();
		return cliente;
	}

	public void setCliente(Pessoa cliente) {
		this.cliente = cliente;
	}


	public List<Parcela> getParcelas() {
		if (parcelas == null)
			parcelas = new ArrayList<Parcela>();
		
		return parcelas;
	}

	public void setParcelas(List<Parcela> parcelas) {
		this.parcelas = parcelas;
	}

	public CategoriaConta getCategoriaConta() {
		return categoriaConta;
	}

	public void setCategoriaConta(
			CategoriaConta categoriaReceitaDespesa) {
		this.categoriaConta = categoriaReceitaDespesa;
	}

	public Dentista getDentista() {
		return dentista;
	}

	public void setDentista(Dentista dentista) {
		this.dentista = dentista;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public boolean isRepetir() {
		return repetir;
	}

	public void setRepetir(boolean repetir) {
		this.repetir = repetir;
	}

	public Integer getDiaRealizacao() {
		return diaRealizacao;
	}

	public void setDiaRealizacao(Integer diaRealizacao) {
		this.diaRealizacao = diaRealizacao;
	}

	public BigDecimal getValorTotal() {
		//valorTotal = getValorAReceber().add(getDesconto());
		if(!Numero.isMaiorZero(valorTotal)) {
			return null;
		} else {
			return valorTotal;
		}
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Pedido getPedidoVenda() {
		return pedidoVenda;
	}

	public void setPedidoVenda(Pedido pedidoVenda) {
		this.pedidoVenda = pedidoVenda;
	}

	public Long getChaveRegistro() {
		return chaveRegistro;
	}

	public void setChaveRegistro(Long chaveRegistro) {
		this.chaveRegistro = chaveRegistro;
	}

	public BigDecimal getDesconto() {
		if(!Numero.isMaiorZero(desconto)) {
			setDesconto(new BigDecimal(0));
			return desconto;
		} else {
			return desconto;
		}
		
	}

	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
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
		Receita other = (Receita) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	

}