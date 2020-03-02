package br.com.reisdaresenha.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 * @author Renan Celso
 */
@Entity
@Table(name="liga")
public class Liga implements Serializable {
	
	private static final long serialVersionUID = 7169807674565231857L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "nome_liga", nullable = false, length=255)
	private String nomeLiga;
	
	@Column(name = "slug_liga",  length=255)
	private String slugLiga;
	
	@Column(name = "brasao_liga",  length=255)
	private String brasaoLiga;
		
	@ManyToOne
	@JoinColumn(name = "ano")	
	private Ano ano;
	
	@Column(name = "id_user_atu", nullable = false, length=255)
	private String idUserAtu;	
	
	@Column(name = "login_user_atu", nullable = false, length=255)
	private String loginUserAtu;	
	
	@Column(name = "dh_atu")   
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dhAtu;		
	
	@Transient
	private List<Time> listaTimes;
	
	@Transient
	private List<Rodada> listaRodadas;
	
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

	public String getNomeLiga() {
		return nomeLiga;
	}

	public void setNomeLiga(String nomeLiga) {
		this.nomeLiga = nomeLiga;
	}

	public Ano getAno() {
		return ano;
	}

	public void setAno(Ano ano) {
		this.ano = ano;
	}	
	
	public String getIdUserAtu() {
		return idUserAtu;
	}

	public void setIdUserAtu(String idUserAtu) {
		this.idUserAtu = idUserAtu;
	}

	public String getLoginUserAtu() {
		return loginUserAtu;
	}

	public void setLoginUserAtu(String loginUserAtu) {
		this.loginUserAtu = loginUserAtu;
	}

	public Date getDhAtu() {
		return dhAtu;
	}

	public void setDhAtu(Date dhAtu) {
		this.dhAtu = dhAtu;
	}
	
	public String getSlugLiga() {
		return slugLiga;
	}

	public void setSlugLiga(String slugLiga) {
		this.slugLiga = slugLiga;
	}

	public List<Time> getListaTimes() {
		return listaTimes;
	}

	public void setListaTimes(List<Time> listaTimes) {
		this.listaTimes = listaTimes;
	}	
	
	public String getBrasaoLiga() {
		return brasaoLiga;
	}

	public void setBrasaoLiga(String brasaoLiga) {
		this.brasaoLiga = brasaoLiga;
	}	
	
	public List<Rodada> getListaRodadas() {
		return listaRodadas;
	}

	public void setListaRodadas(List<Rodada> listaRodadas) {
		this.listaRodadas = listaRodadas;
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
		Liga other = (Liga) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
		
	
}
