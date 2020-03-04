package br.com.reisdaresenha.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * @author Renan Celso
 */
@Entity
@Table(name="premiacao")
public class Premiacao implements Serializable {

	private static final long serialVersionUID = -5446664163515613246L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "ano")	
	private Ano ano;
	
	@ManyToOne
	@JoinColumn(name = "liga")	
	private Liga liga;
	
	@Column(name = "descricao_premio", nullable = false, length=255)
	private String descricaoPremio;	
		
	@Column(name = "vr_percentual_premio")
	private Double vrPercentualPremio;
		
	@Column(name = "vr_premio")
	private Double vrPremio;
	
	/**
	 * Vai ser um valor fixo em todos os registros. 
	 * Vai servir pra fazer o calculo do valor do premio usando o percentual e vice-versa
	 */
	@Column(name = "vr_soma_premio_total")
	private Double vrSomaPremioTotal;
	
	@Column(name = "id_user_atu", nullable = false, length=255)
	private String idUserAtu;	
	
	@Column(name = "login_user_atu", nullable = false, length=255)
	private String loginUserAtu;	
	
	@Column(name = "dh_atu")   
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dhAtu;		
	
	@Column(name = "ordem")
	private Integer ordem;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDescricaoPremio() {
		return descricaoPremio;
	}

	public void setDescricaoPremio(String descricaoPremio) {
		this.descricaoPremio = descricaoPremio;
	}

	public Double getVrPercentualPremio() {
		return vrPercentualPremio;
	}

	public void setVrPercentualPremio(Double vrPercentualPremio) {
		this.vrPercentualPremio = vrPercentualPremio;
	}

	public Double getVrPremio() {
		return vrPremio;
	}

	public void setVrPremio(Double vrPremio) {
		this.vrPremio = vrPremio;
	}

	public Double getVrSomaPremioTotal() {
		return vrSomaPremioTotal;
	}

	public void setVrSomaPremioTotal(Double vrSomaPremioTotal) {
		this.vrSomaPremioTotal = vrSomaPremioTotal;
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
		
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
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
		Premiacao other = (Premiacao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
		
}
