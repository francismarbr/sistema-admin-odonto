package com.agenciacrud.gestornegocio.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.agenciacrud.gestornegocio.model.enumeradores.StatusAgendamentoEnumerador;
import com.agenciacrud.gestornegocio.model.enumeradores.TipoMensalAnualEnumerador;
import com.agenciacrud.gestornegocio.util.DataUtil;


@Entity
public class Agendamento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 297311852567564716L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long chaveRegistro;
	
	@Temporal(TemporalType.DATE)
	private Date data;

	@Temporal(TemporalType.TIME)
	private Date horaInicial;

	@Temporal(TemporalType.TIME)
	private Date horaFinal;

	@Temporal(TemporalType.DATE)
	private Date dataDoRegistro;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private StatusAgendamentoEnumerador status;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private TipoMensalAnualEnumerador tipoIntervalo = TipoMensalAnualEnumerador.MENSAL;

	@ManyToOne
	private Pessoa cliente;
		
	@ManyToOne
	private Empresa empresa;
	
	@ManyToOne(optional=true)
	private Dentista dentista;

	private String lembrete;

	private String observacao;

	@Column
	private boolean presente;

	public int getDiasParaVencer() {
		int dias = 0;
		if (getId() != null && getId().intValue() > 0) {
			dias = DataUtil.diferencaDias(new Date(), getData());
		}
		return dias;
	}

	public String getDiasParaVencerTexto() {
		int dias = getDiasParaVencer();
		if (dias > 0 && dias < 2) {
			return dias + " dia";
		} else if(dias > 0){
			return dias + " dias";
		} else if (dias < 0) {
			return "Há " + (dias * -1) + " dia(s)";
		} else {
			return "Hoje";
		}
	}

	public String getNumeroFormatado() {
		return String.format("%06d", getId());
	}

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
		
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	public Date getHoraInicial() {
		return horaInicial;
	}

	public void setHoraInicial(Date horaInicial) {
		this.horaInicial = horaInicial;
	}

	public Date getHoraFinal() {
		return horaFinal;
	}

	public void setHoraFinal(Date horaFinal) {
		this.horaFinal = horaFinal;
	}

	public Date getDataDoRegistro() {
		return dataDoRegistro;
	}

	public void setDataDoRegistro(Date dataDoRegistro) {
		this.dataDoRegistro = dataDoRegistro;
	}

	public Pessoa getCliente() {
		return cliente;
	}

	public void setCliente(Pessoa cliente) {
		this.cliente = cliente;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
		
	public Dentista getDentista() {
		return dentista;
	}

	public void setDentista(Dentista dentista) {
		this.dentista = dentista;
	}

	public StatusAgendamentoEnumerador getStatus() {
		return status;
	}

	public void setStatus(StatusAgendamentoEnumerador status) {
		this.status = status;
	}

	public TipoMensalAnualEnumerador getTipoIntervalo() {
		return tipoIntervalo;
	}

	public void setTipoIntervalo(TipoMensalAnualEnumerador tipoIntervalo) {
		this.tipoIntervalo = tipoIntervalo;
	}

	public String getLembrete() {
		return lembrete;
	}

	public void setLembrete(String lembrete) {
		this.lembrete = lembrete;
	}

	public boolean isPresente() {
		return presente;
	}

	public void setPresente(boolean presente) {
		this.presente = presente;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
}