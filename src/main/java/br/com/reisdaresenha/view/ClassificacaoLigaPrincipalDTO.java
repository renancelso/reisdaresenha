package br.com.reisdaresenha.view;

public class ClassificacaoLigaPrincipalDTO {
		
	private Integer colocacao;
	
	private String escudoTime;
	
	private String time;
	
	private Integer jogos;
	
	private Double cartoletas;
	
	private Double pontuacao;
	
	private Long idTimeCartola;
	
	private boolean serieA;
	
	private boolean serieB;
	
	private boolean sobrevivente;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getJogos() {
		return jogos;
	}

	public void setJogos(Integer jogos) {
		this.jogos = jogos;
	}

	public Double getCartoletas() {
		return cartoletas;
	}

	public void setCartoletas(Double cartoletas) {
		this.cartoletas = cartoletas;
	}

	public Double getPontuacao() {
		return pontuacao;
	}

	public void setPontuacao(Double pontuacao) {
		this.pontuacao = pontuacao;
	}	

	public Integer getColocacao() {
		return colocacao;
	}

	public void setColocacao(Integer colocacao) {
		this.colocacao = colocacao;
	}

	public String getEscudoTime() {
		return escudoTime;
	}

	public void setEscudoTime(String escudoTime) {
		this.escudoTime = escudoTime;
	}
	
	public boolean isSerieA() {
		return serieA;
	}

	public void setSerieA(boolean serieA) {
		this.serieA = serieA;
	}

	public boolean isSerieB() {
		return serieB;
	}

	public void setSerieB(boolean serieB) {
		this.serieB = serieB;
	}

	public boolean isSobrevivente() {
		return sobrevivente;
	}

	public void setSobrevivente(boolean sobrevivente) {
		this.sobrevivente = sobrevivente;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		ClassificacaoLigaPrincipalDTO other = (ClassificacaoLigaPrincipalDTO) obj;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	public Long getIdTimeCartola() {
		return idTimeCartola;
	}

	public void setIdTimeCartola(Long idTimeCartola) {
		this.idTimeCartola = idTimeCartola;
	}
	
}
