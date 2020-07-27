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
@Table(name="rdrpontuacao")
public class RDRPontuacao implements Serializable {

	private static final long serialVersionUID = -6256502675670409444L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
						
	@ManyToOne
	@JoinColumn(name="rdr_rodada")
	private RDRRodada rdrRodada;
		
	@ManyToOne
	@JoinColumn(name="time_casa")
	private RDRParticipante rdrParticipanteTimeCasa;		
	@Column(name = "vr_pont_time_casa")
	private Double vrPontuacaoTimeCasa;		
	@Column(name = "vr_pont_time_casa_arr")
	private Double vrPontuacaoTimeCasaArredondada;	
	@Column(name="nome_time_casa")
	private String nomeTimeCasa;
	
	@ManyToOne
	@JoinColumn(name="time_fora")
	private RDRParticipante rdrParticipanteTimeFora;	
	@Column(name = "vr_pont_time_fora")
	private Double vrPontuacaoTimeFora;		
	@Column(name = "vr_pont_time_fora_arr")
	private Double vrPontuacaoTimeForaArredondada;	
	@Column(name="nome_time_fora")
	private String nomeTimeFora;
	
	@ManyToOne
	@JoinColumn(name="time_vencedor")
	private RDRParticipante rdrParticipanteTimeVencedor;
	
	@Column(name="nome_time_vencedor")
	private String nomeTimeVencedor;
	
	/**
	 * S - sim
	 * N - nao
	 */
	@Column(name="empate")
	private String empate;
		
	@Column(name="fase")
	private String fase;
	
	@Column(name="serie")
	private String serie;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RDRRodada getRdrRodada() {
		return rdrRodada;
	}

	public void setRdrRodada(RDRRodada rdrRodada) {
		this.rdrRodada = rdrRodada;
	}

	public RDRParticipante getRdrParticipanteTimeCasa() {
		return rdrParticipanteTimeCasa;
	}

	public void setRdrParticipanteTimeCasa(RDRParticipante rdrParticipanteTimeCasa) {
		this.rdrParticipanteTimeCasa = rdrParticipanteTimeCasa;
	}

	public Double getVrPontuacaoTimeCasa() {
		return vrPontuacaoTimeCasa;
	}

	public void setVrPontuacaoTimeCasa(Double vrPontuacaoTimeCasa) {
		this.vrPontuacaoTimeCasa = vrPontuacaoTimeCasa;
	}

	public Double getVrPontuacaoTimeCasaArredondada() {
		return vrPontuacaoTimeCasaArredondada;
	}

	public void setVrPontuacaoTimeCasaArredondada(Double vrPontuacaoTimeCasaArredondada) {
		this.vrPontuacaoTimeCasaArredondada = vrPontuacaoTimeCasaArredondada;
	}

	public RDRParticipante getRdrParticipanteTimeFora() {
		return rdrParticipanteTimeFora;
	}

	public void setRdrParticipanteTimeFora(RDRParticipante rdrParticipanteTimeFora) {
		this.rdrParticipanteTimeFora = rdrParticipanteTimeFora;
	}

	public Double getVrPontuacaoTimeFora() {
		return vrPontuacaoTimeFora;
	}

	public void setVrPontuacaoTimeFora(Double vrPontuacaoTimeFora) {
		this.vrPontuacaoTimeFora = vrPontuacaoTimeFora;
	}

	public Double getVrPontuacaoTimeForaArredondada() {
		return vrPontuacaoTimeForaArredondada;
	}

	public void setVrPontuacaoTimeForaArredondada(Double vrPontuacaoTimeForaArredondada) {
		this.vrPontuacaoTimeForaArredondada = vrPontuacaoTimeForaArredondada;
	}

	public RDRParticipante getRdrParticipanteTimeVencedor() {
		return rdrParticipanteTimeVencedor;
	}

	public void setRdrParticipanteTimeVencedor(RDRParticipante rdrParticipanteTimeVencedor) {
		this.rdrParticipanteTimeVencedor = rdrParticipanteTimeVencedor;
	}

	public String getEmpate() {
		return empate;
	}

	public void setEmpate(String empate) {
		this.empate = empate;
	}	

	public String getNomeTimeCasa() {
		return nomeTimeCasa;
	}

	public void setNomeTimeCasa(String nomeTimeCasa) {
		this.nomeTimeCasa = nomeTimeCasa;
	}

	public String getNomeTimeFora() {
		return nomeTimeFora;
	}

	public void setNomeTimeFora(String nomeTimeFora) {
		this.nomeTimeFora = nomeTimeFora;
	}	
	
	public String getNomeTimeVencedor() {
		return nomeTimeVencedor;
	}

	public void setNomeTimeVencedor(String nomeTimeVencedor) {
		this.nomeTimeVencedor = nomeTimeVencedor;
	}	

	public String getFase() {
		return fase;
	}

	public void setFase(String fase) {
		this.fase = fase;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
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
		RDRPontuacao other = (RDRPontuacao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
	
}
