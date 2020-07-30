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
 * - Rodada 35
 *		
 *		Jogo 1. Campeão serie A Apertura x vice-campeao Serie B Clausura
 *		Jogo 2. Campeão Serie B  Clausura x Vice Campeão Serie A Apertura
 *  	Jogo 3. Campeão serie A Clausura x vice-campeao Serie B Apertura
 *		Jogo 4. Campeão Serie B Apertura x Vice Campeão Serie A Clausura
 *
 *	- Rodada 36 - IDA 
 *		
 *		Jogo 5. Vencedor Jogo 1 x Vencedor Jogo 2
 *		Jogo 6. Vencedor Jogo 3 x Vencedor Jogo 4
 *
 *	- Rodada 37 - VOLTA
 *		
 *		Jogo 7. Vencedor Jogo 2 x Vencedor Jogo 1
 *		Jogo 8. Vencedor Jogo 4 x Vencedor Jogo 3
 *
 *	- Rodada 38
 *
 *		Final = Vencedor (Jogo 5 + 7) x Vencedor (Jogo 6 + 8)
 *
 *	@author Renan Celso
 */
@Entity
@Table(name="rdrcopapontuacao")
public class RDRCopaPontuacao implements Serializable {
	
	private static final long serialVersionUID = -6256502675670409444L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
		
	/**
	 * QF - QUARTAS DE FINAL
	 * SFI - SEMI-FINAL-IDA
	 * SFV - SEMI-VOLTA-VOLTA
	 * F  - FINAL
	 */	
	@Column(name="fase")
	private String fase;
	
	@Column(name = "nr_jogo_copa", nullable = false, length=255)
	private Long nrJogoCopa;
	
	@Column(name = "nr_rodada_cartola", nullable = false, length=255)
	private Long nrRodadaCartola;
		
	@ManyToOne
	@JoinColumn(name="time_casa")
	private RDRParticipante rdrParticipanteTimeCasa;		
	@Column(name = "vr_pont_time_casa")
	private Double vrPontuacaoTimeCasa;		
	@Column(name = "vr_pont_time_casa_arr")
	private Double vrPontuacaoTimeCasaArredondada;	
	@Column(name="nome_time_casa")
	private String nomeTimeCasa;
	
	@Column(name="descricao_time_rodada_casa")
	private String descricaoTimeRodadaCasa;
	
	@ManyToOne
	@JoinColumn(name="time_fora")
	private RDRParticipante rdrParticipanteTimeFora;	
	@Column(name = "vr_pont_time_fora")
	private Double vrPontuacaoTimeFora;		
	@Column(name = "vr_pont_time_fora_arr")
	private Double vrPontuacaoTimeForaArredondada;	
	@Column(name="nome_time_fora")
	private String nomeTimeFora;
	
	@Column(name="descricao_time_rodada_fora")
	private String descricaoTimeRodadaFora;
	
	@ManyToOne
	@JoinColumn(name="time_vencedor")
	private RDRParticipante rdrParticipanteTimeVencedor;
	
	@Column(name="nome_time_vencedor")
	private String nomeTimeVencedor;
	
	@ManyToOne
	@JoinColumn(name="time_perdedor")
	private RDRParticipante rdrParticipanteTimePerdedor;
	
	@Column(name="nome_time_perdedor")
	private String nomeTimePerdedor;
	
	/**
	 * S - sim
	 * N - nao
	 */
	@Column(name="empate")
	private String empate;
				
	@ManyToOne
	@JoinColumn(name="time_empate_casa")
	private RDRParticipante rdrParticipanteTimeEmpateEmCasa;
	
	@ManyToOne
	@JoinColumn(name="time_empate_fora")
	private RDRParticipante rdrParticipanteTimeEmpateFora;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFase() {
		return fase;
	}

	public void setFase(String fase) {
		this.fase = fase;
	}

	public Long getNrJogoCopa() {
		return nrJogoCopa;
	}

	public void setNrJogoCopa(Long nrJogoCopa) {
		this.nrJogoCopa = nrJogoCopa;
	}

	public Long getNrRodadaCartola() {
		return nrRodadaCartola;
	}

	public void setNrRodadaCartola(Long nrRodadaCartola) {
		this.nrRodadaCartola = nrRodadaCartola;
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

	public String getNomeTimeCasa() {
		return nomeTimeCasa;
	}

	public void setNomeTimeCasa(String nomeTimeCasa) {
		this.nomeTimeCasa = nomeTimeCasa;
	}

	public String getDescricaoTimeRodadaCasa() {
		return descricaoTimeRodadaCasa;
	}

	public void setDescricaoTimeRodadaCasa(String descricaoTimeRodadaCasa) {
		this.descricaoTimeRodadaCasa = descricaoTimeRodadaCasa;
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

	public String getNomeTimeFora() {
		return nomeTimeFora;
	}

	public void setNomeTimeFora(String nomeTimeFora) {
		this.nomeTimeFora = nomeTimeFora;
	}

	public String getDescricaoTimeRodadaFora() {
		return descricaoTimeRodadaFora;
	}

	public void setDescricaoTimeRodadaFora(String descricaoTimeRodadaFora) {
		this.descricaoTimeRodadaFora = descricaoTimeRodadaFora;
	}

	public RDRParticipante getRdrParticipanteTimeVencedor() {
		return rdrParticipanteTimeVencedor;
	}

	public void setRdrParticipanteTimeVencedor(RDRParticipante rdrParticipanteTimeVencedor) {
		this.rdrParticipanteTimeVencedor = rdrParticipanteTimeVencedor;
	}

	public String getNomeTimeVencedor() {
		return nomeTimeVencedor;
	}

	public void setNomeTimeVencedor(String nomeTimeVencedor) {
		this.nomeTimeVencedor = nomeTimeVencedor;
	}

	public RDRParticipante getRdrParticipanteTimePerdedor() {
		return rdrParticipanteTimePerdedor;
	}

	public void setRdrParticipanteTimePerdedor(RDRParticipante rdrParticipanteTimePerdedor) {
		this.rdrParticipanteTimePerdedor = rdrParticipanteTimePerdedor;
	}

	public String getNomeTimePerdedor() {
		return nomeTimePerdedor;
	}

	public void setNomeTimePerdedor(String nomeTimePerdedor) {
		this.nomeTimePerdedor = nomeTimePerdedor;
	}

	public String getEmpate() {
		return empate;
	}

	public void setEmpate(String empate) {
		this.empate = empate;
	}

	public RDRParticipante getRdrParticipanteTimeEmpateEmCasa() {
		return rdrParticipanteTimeEmpateEmCasa;
	}

	public void setRdrParticipanteTimeEmpateEmCasa(RDRParticipante rdrParticipanteTimeEmpateEmCasa) {
		this.rdrParticipanteTimeEmpateEmCasa = rdrParticipanteTimeEmpateEmCasa;
	}

	public RDRParticipante getRdrParticipanteTimeEmpateFora() {
		return rdrParticipanteTimeEmpateFora;
	}

	public void setRdrParticipanteTimeEmpateFora(RDRParticipante rdrParticipanteTimeEmpateFora) {
		this.rdrParticipanteTimeEmpateFora = rdrParticipanteTimeEmpateFora;
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
		RDRCopaPontuacao other = (RDRCopaPontuacao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	

}
