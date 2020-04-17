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

/**
 * @author Renan Celso
 */
@Entity
@Table(name="rodada")
public class Rodada implements Serializable {

	private static final long serialVersionUID = -3179427642055715125L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "nr_rodada", nullable = false, length=255)
	private Long nrRodada;
	
	/**
	 * Em Andamento - EA	 
	 * Passada      - PS
	 * Futura       - FT
	 */
	@Column(name = "status_rodada", length=255)
	private String statusRodada;
	
	@ManyToOne
	@JoinColumn(name="liga")
	private Liga liga;
	
	@Transient
	private List<Pontuacao> listaPontuacao;
	
	public List<Pontuacao> getListaPontuacao() {
		return listaPontuacao;
	}

	public void setListaPontuacao(List<Pontuacao> listaPontuacao) {
		this.listaPontuacao = listaPontuacao;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getNrRodada() {
		return nrRodada;
	}

	public void setNrRodada(Long nrRodada) {
		this.nrRodada = nrRodada;
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
		Rodada other = (Rodada) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
		
}
