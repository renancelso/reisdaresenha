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
import javax.persistence.Transient;

/**
 * @author Renan Celso
 */
@Entity
@Table(name="rdrparticipante")
public class RDRParticipante implements Serializable {
	
	private static final long serialVersionUID = -371718413875208775L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
		
	@ManyToOne
	@JoinColumn(name = "time")
	private Time time;  
	
	@Column(name = "nome_time")
	private String nomeTime;
	
	@Column(name = "id_time_cartola")
	private Long idTimeCartola;
		
	/**
	 * A - APERTURA 
	 * C - CLAUSURA	
	 */
	@Column(name = "fase_liga", length=255)
	private String faseLiga;
	
	/**
	 * SA - SERIE A
	 * SB - SERIE B
	 */
	@Column(name = "serie_participante", length=255)
	private String serieParticipante;
	
	/**
	 * CA-SA  - Campeão Apertura Serie A
	 * VCA-SA - Vice Campeão Apertura Serie A
	 * CA-SB  - Campeão Apertura Serie B
	 * VCA-SB - Vice Campeão Apertura Serie B
	 * 
	 * CC-SA  - Campeão Clausura Serie A
	 * VCC-SA - Vice Campeão Clausura Serie A
	 * CC-SB  - Campeão Clausura Serie B
	 * VCC-SB - Vice Campeão Clausura Serie B
	 */
	@Column(name = "classif_final_copa", length=255)
	private String classificacaoFinalParaCopa;
	
	@Transient
	private boolean estaNaCopa;
	
	@Transient
	private boolean incluirNaCopa;
	
	@Transient
	private boolean incluirNoLugarCampeao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public String getNomeTime() {
		if(nomeTime != null)
			return nomeTime;
		else 
			return null;
		
	}

	public void setNomeTime(String nomeTime) {
		this.nomeTime = nomeTime;
	}

	public Long getIdTimeCartola() {
		return idTimeCartola;
	}

	public void setIdTimeCartola(Long idTimeCartola) {
		this.idTimeCartola = idTimeCartola;
	}

	public String getFaseLiga() {
		return faseLiga;
	}

	public void setFaseLiga(String faseLiga) {
		this.faseLiga = faseLiga;
	}

	public String getSerieParticipante() {
		return serieParticipante;
	}

	public void setSerieParticipante(String serieParticipante) {
		this.serieParticipante = serieParticipante;
	}
	
	public boolean isEstaNaCopa() {
		return estaNaCopa;
	}

	public void setEstaNaCopa(boolean estaNaCopa) {
		this.estaNaCopa = estaNaCopa;
	}	

	public boolean isIncluirNaCopa() {
		return incluirNaCopa;
	}

	public void setIncluirNaCopa(boolean incluirNaCopa) {
		this.incluirNaCopa = incluirNaCopa;
	}	

	public String getClassificacaoFinalParaCopa() {
		return classificacaoFinalParaCopa;
	}

	public void setClassificacaoFinalParaCopa(String classificacaoFinalParaCopa) {
		this.classificacaoFinalParaCopa = classificacaoFinalParaCopa;
	}
	
	public boolean isIncluirNoLugarCampeao() {
		return incluirNoLugarCampeao;
	}

	public void setIncluirNoLugarCampeao(boolean incluirNoLugarCampeao) {
		this.incluirNoLugarCampeao = incluirNoLugarCampeao;
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
		RDRParticipante other = (RDRParticipante) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
