package br.com.reisdaresenha.service;

import java.util.List;

import javax.ejb.Local;

import br.com.reisdaresenha.model.RDRParticipante;
import br.com.reisdaresenha.padrao.GenericServiceInterface;

/**
 * @author Renan Celso
 */
@Local
public interface RDRServiceLocal extends GenericServiceInterface {

	public List<RDRParticipante> buscarRDRParticipantes(String fase, String serie);
	
		
}
