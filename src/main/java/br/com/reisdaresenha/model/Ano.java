package br.com.reisdaresenha.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;


/**
 * @author Renan Celso
 */
@Entity
@Table(name="ano")
public class Ano implements Serializable {
	
	private static final long serialVersionUID = 2354668924315843687L;
	
	@Id	
	@Column(name = "id")
	private Long id;
		
	@Column(name = "id_user_atu", nullable = false, length=255)
	private String idUserAtu;	
	
	@Column(name = "login_user_atu", nullable = false, length=255)
	private String loginUserAtu;	
	
	@Column(name = "dh_atu", nullable = false)   
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dhAtu;		 
	
	@Transient
	private List<Liga> listaLigas;
	
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
	
	public List<Liga> getListaLigas() {
		return listaLigas;
	}

	public void setListaLigas(List<Liga> listaLigas) {
		this.listaLigas = listaLigas;
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
		Ano other = (Ano) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
		 
}
