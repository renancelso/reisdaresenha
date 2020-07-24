package br.com.reisdaresenha.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Renan Celso
 */
@Entity
@Table(name="osbpontuacao")
public class OSBPontuacao implements Serializable, Comparable<OSBPontuacao>{

	private static final long serialVersionUID = -5543199361380820801L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "vr_pontuacao")
	private Double vrPontuacao;	
			
	@ManyToOne
	@JoinColumn(name="osb_rodada_time_participante")
	private OSBRodadaTimeParticipante osbRodadaTimeParticipante;
	
	@Column(name = "nome_time")
	private String nomeTime;
	
	@ManyToOne
	@JoinColumn(name="osb_rodada")
	private OSBRodada osbRodada;
	
	@Column(name = "situacao_final_rodada")
	private String situacaoFinalRodada;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getVrPontuacao() {
		return vrPontuacao;
	}

	public void setVrPontuacao(Double vrPontuacao) {
		this.vrPontuacao = vrPontuacao;
	}

	public OSBRodadaTimeParticipante getOsbRodadaTimeParticipante() {
		return osbRodadaTimeParticipante;
	}

	public void setOsbRodadaTimeParticipante(OSBRodadaTimeParticipante osbRodadaTimeParticipante) {
		this.osbRodadaTimeParticipante = osbRodadaTimeParticipante;
	}

	public String getNomeTime() {
		return nomeTime;
	}

	public void setNomeTime(String nomeTime) {
		this.nomeTime = nomeTime;
	}

	public OSBRodada getOsbRodada() {
		return osbRodada;
	}

	public void setOsbRodada(OSBRodada osbRodada) {
		this.osbRodada = osbRodada;
	}

	public String getSituacaoFinalRodada() {
		return situacaoFinalRodada;
	}

	public void setSituacaoFinalRodada(String situacaoFinalRodada) {
		this.situacaoFinalRodada = situacaoFinalRodada;
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
		OSBPontuacao other = (OSBPontuacao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
	
	@Override
	public int compareTo(OSBPontuacao other) {		
		
		if (this.vrPontuacao > other.getVrPontuacao()) { 
			return -1; 
		} 	
		
		if (this.vrPontuacao < other.getVrPontuacao()) { 
			return 1; 
		} 	
		
		return 0; 
		 
	}
	
}
