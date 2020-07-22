package br.com.reisdaresenha.service;

import java.util.List;

import javax.ejb.Local;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.Rodada;
import br.com.reisdaresenha.padrao.GenericServiceInterface;

/**
 * @author Renan Celso
 */
@Local
public interface RodadaServiceLocal extends GenericServiceInterface{


	public List<Rodada> listarRodadasDesc(Liga liga);

	public Rodada buscarRodadaEmAndamento(Liga liga);		
		
}