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
@Table(name="time")
public class Time implements Serializable {

	private static final long serialVersionUID = -4129229888068232244L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "nome_time", nullable = false, length=255)
	private String nometime;
	
	@Column(name = "slug_time", length=255)
	private String slugTime;
		
	@Column(name = "brasao_time", length=255)
	private String brasaoTime;
	
	@Column(name = "vr_cartoletasAtuais")
	private Double vrCartoletasAtuais;
		
	@Column(name = "id_user_atu", nullable = false, length=255)
	private String idUserAtu;	
	
	@Column(name = "login_user_atu", nullable = false, length=255)
	private String loginUserAtu;	
	
	@Column(name = "dh_atu")   
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dhAtu;	
		
	@JoinColumn(name = "liga")
	@ManyToOne
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

	public String getNometime() {
		return nometime;
	}

	public void setNometime(String nometime) {
		this.nometime = nometime;
	}

	public String getSlugTime() {
		return slugTime;
	}

	public void setSlugTime(String slugTime) {
		this.slugTime = slugTime;
	}

	public String getBrasaoTime() {
		return brasaoTime;
	}

	public void setBrasaoTime(String brasaoTime) {
		this.brasaoTime = brasaoTime;
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
		Time other = (Time) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	

}
