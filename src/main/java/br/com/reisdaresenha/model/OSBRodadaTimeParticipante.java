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
@Table(name="osbrodadatimeparticipante")
public class OSBRodadaTimeParticipante implements Serializable {

	private static final long serialVersionUID = -5919228342043742940L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "osb_rodada")
	private OSBRodada osbRodada;
	
	@ManyToOne
	@JoinColumn(name = "time")
	private Time time;  
	
	@Column(name = "nome_time")
	private String nomeTime;
	
	@Column(name = "id_time_cartola")
	private Long idTimeCartola;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OSBRodada getOsbRodada() {
		return osbRodada;
	}

	public void setOsbRodada(OSBRodada osbRodada) {
		this.osbRodada = osbRodada;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public String getNomeTime() {
		return nomeTime;
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
		OSBRodadaTimeParticipante other = (OSBRodadaTimeParticipante) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
}
