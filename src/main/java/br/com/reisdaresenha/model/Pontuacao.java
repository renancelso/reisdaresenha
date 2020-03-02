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
@Table(name="pontuacao")
public class Pontuacao implements Serializable {

	private static final long serialVersionUID = -3179427642055715125L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "vr_pontuacao")
	private Double vrPontuacao;
	
	@Column(name = "vr_cartoletas")
	private Double vrCartoletas;
	
	@ManyToOne
	@JoinColumn(name="ano")
	private Ano ano;	
	
	@ManyToOne
	@JoinColumn(name="liga")
	private Liga liga;
	
	@ManyToOne
	@JoinColumn(name="time")
	private Time time;
	
	@ManyToOne
	@JoinColumn(name="rodada")
	private Rodada rodada;

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

	public Double getVrCartoletas() {
		return vrCartoletas;
	}

	public void setVrCartoletas(Double vrCartoletas) {
		this.vrCartoletas = vrCartoletas;
	}

	public Ano getAno() {
		return ano;
	}

	public void setAno(Ano ano) {
		this.ano = ano;
	}

	public Liga getLiga() {
		return liga;
	}

	public void setLiga(Liga liga) {
		this.liga = liga;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public Rodada getRodada() {
		return rodada;
	}

	public void setRodada(Rodada rodada) {
		this.rodada = rodada;
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
		Pontuacao other = (Pontuacao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
