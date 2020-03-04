package br.com.reisdaresenha.service;

import java.util.List;

import javax.ejb.Local;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.Premiacao;
import br.com.reisdaresenha.padrao.GenericServiceInterface;

/**
 * @author Renan Celso
 */
@Local
public interface InicioServiceLocal extends GenericServiceInterface{

	public List<Premiacao> buscarPremiacoes(Integer ano, Liga liga);

	public List<Liga> buscarLigas(Integer ano);
	
}
