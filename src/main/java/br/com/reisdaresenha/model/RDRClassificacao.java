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

@Entity
@Table(name="rdrclassificacao")
public class RDRClassificacao implements Serializable {
	
	private static final long serialVersionUID = -6032370699526664125L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
		
	@ManyToOne
	@JoinColumn(name="rdr_participante")
	private RDRParticipante rdrParticipante;
	
	@Column(name = "nome_time")
	private String nomeTime;
	
	@Column(name = "nome_dono_time")
	private String nomeDonoTime;
	
	@Column(name = "id_cartola_time")
	private Long idCartolaTime;	
	
	@Column(name = "vr_pontos")
	private Double vrPontos;	
	
	@Column(name = "qtd_vitorias")
	private Long qtdVitorias;	
	
	@Column(name = "qtd_empates")
	private Long qtdEmpates;	
	
	@Column(name = "qtd_derrotas")
	private Long qtdDerrotas;	
	
	@Column(name = "qtd_jogos_disputados")
	private Long qtdJogosDisputados;	
		
	/**
	 * A - APERTURA 
	 * C - CLAUSURA	
	 */
	@Column(name = "fase_liga", length=255)
	private String faseLiga;
	
	/**
	 * SA - SERIE A
	 * SB - SERIE B
	 */
	@Column(name = "serie", length=255)
	private String serie;
	
	@Column(name = "nr_rodada_atual")
	private Long nrRodadaAtual;	
		
	@Column(name = "vr_pontos_liga_principal")
	private Double vrPontuacaoAtualLigaPrincipalCartola;	
	
	private Double vrGolsPro;
	
	private Double vrGolsContra;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RDRParticipante getRdrParticipante() {
		return rdrParticipante;
	}

	public void setRdrParticipante(RDRParticipante rdrParticipante) {
		this.rdrParticipante = rdrParticipante;
	}

	public Double getVrPontos() {
		return vrPontos;
	}

	public void setVrPontos(Double vrPontos) {
		this.vrPontos = vrPontos;
	}

	public Long getQtdVitorias() {
		return qtdVitorias;
	}

	public void setQtdVitorias(Long qtdVitorias) {
		this.qtdVitorias = qtdVitorias;
	}

	public Long getQtdEmpates() {
		return qtdEmpates;
	}

	public void setQtdEmpates(Long qtdEmpates) {
		this.qtdEmpates = qtdEmpates;
	}

	public Long getQtdDerrotas() {
		return qtdDerrotas;
	}

	public void setQtdDerrotas(Long qtdDerrotas) {
		this.qtdDerrotas = qtdDerrotas;
	}

	public Long getQtdJogosDisputados() {
		return qtdJogosDisputados;
	}

	public void setQtdJogosDisputados(Long qtdJogosDisputados) {
		this.qtdJogosDisputados = qtdJogosDisputados;
	}

	public String getFaseLiga() {
		return faseLiga;
	}

	public void setFaseLiga(String faseLiga) {
		this.faseLiga = faseLiga;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}
	
	public String getNomeTime() {
		return nomeTime;
	}

	public void setNomeTime(String nomeTime) {
		this.nomeTime = nomeTime;
	}

	public String getNomeDonoTime() {
		return nomeDonoTime;
	}

	public void setNomeDonoTime(String nomeDonoTime) {
		this.nomeDonoTime = nomeDonoTime;
	}

	public Long getIdCartolaTime() {
		return idCartolaTime;
	}

	public void setIdCartolaTime(Long idCartolaTime) {
		this.idCartolaTime = idCartolaTime;
	}
		
	public Long getNrRodadaAtual() {
		return nrRodadaAtual;
	}

	public void setNrRodadaAtual(Long nrRodadaAtual) {
		this.nrRodadaAtual = nrRodadaAtual;
	}
	
	public Double getVrPontuacaoAtualLigaPrincipalCartola() {
		return vrPontuacaoAtualLigaPrincipalCartola;
	}

	public void setVrPontuacaoAtualLigaPrincipalCartola(Double vrPontuacaoAtualLigaPrincipalCartola) {
		this.vrPontuacaoAtualLigaPrincipalCartola = vrPontuacaoAtualLigaPrincipalCartola;
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
		RDRClassificacao other = (RDRClassificacao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Double getVrGolsPro() {
		return vrGolsPro;
	}

	public void setVrGolsPro(Double vrGolsPro) {
		this.vrGolsPro = vrGolsPro;
	}

	public Double getVrGolsContra() {
		return vrGolsContra;
	}

	public void setVrGolsContra(Double vrGolsContra) {
		this.vrGolsContra = vrGolsContra;
	}	
	
}
