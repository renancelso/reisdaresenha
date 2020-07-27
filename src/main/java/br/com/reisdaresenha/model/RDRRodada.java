package br.com.reisdaresenha.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="rdrrodada")
public class RDRRodada implements Serializable {
	
	private static final long serialVersionUID = -4620246525743079162L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "nr_rodada_cartola", nullable = false, length=255)
	private Long nrRodadaCartola;
	
	@Column(name = "nr_rdr_rodada", nullable = false, length=255)
	private Long nrRDRRodada;	
	
	/**
	 * Em Andamento - EA	 
	 * Passada      - PS
	 * Passada      - FT		
	 */
	@Column(name = "status_rodada", length=255)
	private String statusRodada;
	
	/** 	 
	 * Liga RDR = Liga ID 2 	 
	 **/
	@ManyToOne
	@JoinColumn(name="liga")
	private Liga liga;
	
	/**
	 * A - APERTURA 
	 * C - CLAUSURA	
	 */
	@Column(name = "tipo_rodada", length=255)
	private String tipoRodada; //FASE
	
	/**
	 * SA - SERIE A
	 * SB - SERIE B
	 */
	@Column(name = "serie_rodada", length=255)
	private String serieRodada;
	
	@Transient
	private List<RDRPontuacao> listaRDRPontuacao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getNrRodadaCartola() {
		return nrRodadaCartola;
	}

	public void setNrRodadaCartola(Long nrRodadaCartola) {
		this.nrRodadaCartola = nrRodadaCartola;
	}

	public Long getNrRDRRodada() {
		return nrRDRRodada;
	}

	public void setNrRDRRodada(Long nrRDRRodada) {
		this.nrRDRRodada = nrRDRRodada;
	}

	public String getStatusRodada() {
		return statusRodada;
	}

	public void setStatusRodada(String statusRodada) {
		this.statusRodada = statusRodada;
	}

	public Liga getLiga() {
		return liga;
	}

	public void setLiga(Liga liga) {
		this.liga = liga;
	}

	public String getTipoRodada() {
		return tipoRodada;
	}

	public void setTipoRodada(String tipoRodada) {
		this.tipoRodada = tipoRodada;
	}

	public List<RDRPontuacao> getListaRDRPontuacao() {
		return listaRDRPontuacao;
	}

	public void setListaRDRPontuacao(List<RDRPontuacao> listaRDRPontuacao) {
		this.listaRDRPontuacao = listaRDRPontuacao;
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
		RDRRodada other = (RDRRodada) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getSerieRodada() {
		return serieRodada;
	}

	public void setSerieRodada(String serieRodada) {
		this.serieRodada = serieRodada;
	}
	
}
