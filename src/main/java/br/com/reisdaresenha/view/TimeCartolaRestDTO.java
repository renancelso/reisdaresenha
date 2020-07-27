package br.com.reisdaresenha.view;

import java.io.Serializable;

import br.com.reisdaresenha.model.OSBPontuacao;

public class TimeCartolaRestDTO implements Serializable, Comparable<TimeCartolaRestDTO>{
	
	private static final long serialVersionUID = 1814635572420449643L;

	private Long idCartola;
	
	private String nomeTime;
	
	private String nomeDonoTime;
	
	private String slug;
	
	private Double pontosCapitao; 
	
	private Long rodada;
	
	private String urlEscudoSvg;
	
	private String urlEscudoPng;

	public Long getIdCartola() {
		return idCartola;
	}

	public void setIdCartola(Long idCartola) {
		this.idCartola = idCartola;
	}
	public String getNomeTime() {
		return nomeTime;
	}

	public void setNomeTime(String nomeTime) {
		this.nomeTime = nomeTime;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public Double getPontosCapitao() {
		return pontosCapitao;
	}

	public void setPontosCapitao(Double pontosCapitao) {
		this.pontosCapitao = pontosCapitao;
	}

	public Long getRodada() {
		return rodada;
	}

	public void setRodada(Long rodada) {
		this.rodada = rodada;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idCartola == null) ? 0 : idCartola.hashCode());
		result = prime * result + ((rodada == null) ? 0 : rodada.hashCode());
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
		TimeCartolaRestDTO other = (TimeCartolaRestDTO) obj;
		if (idCartola == null) {
			if (other.idCartola != null)
				return false;
		} else if (!idCartola.equals(other.idCartola))
			return false;
		if (rodada == null) {
			if (other.rodada != null)
				return false;
		} else if (!rodada.equals(other.rodada))
			return false;
		return true;
	}

	public String getUrlEscudoSvg() {
		return urlEscudoSvg;
	}

	public void setUrlEscudoSvg(String urlEscudoSvg) {
		this.urlEscudoSvg = urlEscudoSvg;
	}

	@Override
	public int compareTo(TimeCartolaRestDTO other) {	
		return new Integer(this.nomeTime.compareTo(other.getNomeTime())).intValue();	
	}

	public String getNomeDonoTime() {
		return nomeDonoTime;
	}

	public void setNomeDonoTime(String nomeDonoTime) {
		this.nomeDonoTime = nomeDonoTime;
	}

	public String getUrlEscudoPng() {
		return urlEscudoPng;
	}

	public void setUrlEscudoPng(String urlEscudoPng) {
		this.urlEscudoPng = urlEscudoPng;
	}
	
}
