package br.com.reisdaresenha.service;

import javax.ejb.Local;

import br.com.reisdaresenha.model.Parametro;
import br.com.reisdaresenha.padrao.GenericServiceInterface;

/**
 * @author Renan Celso
 */
@Local
public interface ParametroServiceLocal extends GenericServiceInterface{

	public Parametro buscarParametroPorChave(String chave);

	
}
