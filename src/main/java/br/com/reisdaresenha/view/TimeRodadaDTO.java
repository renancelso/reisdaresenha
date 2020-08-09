package br.com.reisdaresenha.view;

import java.util.List;

import br.com.reisdaresenha.model.Time;

public class TimeRodadaDTO {

	private Time time;
	
	private Long rodadaAtual;
	
	private Double patrimonio;
	
	private Double valorTime;
	
	private Double pontos;
	
	private Double pontosCampeonato;
	
	private List<Long> idAtletasEscalados;

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public Long getRodadaAtual() {
		return rodadaAtual;
	}

	public void setRodadaAtual(Long rodadaAtual) {
		this.rodadaAtual = rodadaAtual;
	}

	public Double getPatrimonio() {
		return patrimonio;
	}

	public void setPatrimonio(Double patrimonio) {
		this.patrimonio = patrimonio;
	}

	public Double getValorTime() {
		return valorTime;
	}

	public void setValorTime(Double valorTime) {
		this.valorTime = valorTime;
	}

	public Double getPontos() {
		return pontos;
	}

	public void setPontos(Double pontos) {
		this.pontos = pontos;
	}

	public Double getPontosCampeonato() {
		return pontosCampeonato;
	}

	public void setPontosCampeonato(Double pontosCampeonato) {
		this.pontosCampeonato = pontosCampeonato;
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
		TimeRodadaDTO other = (TimeRodadaDTO) obj;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	public List<Long> getIdAtletasEscalados() {
		return idAtletasEscalados;
	}

	public void setIdAtletasEscalados(List<Long> idAtletasEscalados) {
		this.idAtletasEscalados = idAtletasEscalados;
	}
	
	
}
