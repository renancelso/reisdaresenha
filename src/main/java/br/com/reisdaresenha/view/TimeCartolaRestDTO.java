package br.com.reisdaresenha.view;

public class TimeCartolaRestDTO {

	private Long idCartola;
	
	private String nomeTime;
	
	private String slug;
	
	private Double pontosCapitao; 
	
	private Long rodada;

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
	
}
