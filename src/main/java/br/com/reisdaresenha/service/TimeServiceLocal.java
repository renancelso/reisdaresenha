package br.com.reisdaresenha.service;

import javax.ejb.Local;

import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.padrao.GenericServiceInterface;

/**
 * @author Renan Celso
 */
@Local
public interface TimeServiceLocal extends GenericServiceInterface {
	
	public Time buscarTimePorNome(String nomeTime);
	
	public Time buscarTimePorIdCartola(Long idCartola);
	
}
