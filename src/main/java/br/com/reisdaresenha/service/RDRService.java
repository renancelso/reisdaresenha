package br.com.reisdaresenha.service;

import java.util.List;

import javax.ejb.Stateless;

import br.com.reisdaresenha.model.RDRParticipante;
import br.com.reisdaresenha.padrao.GenericService;

/**
 * @author Renan Celso
 */
@Stateless
public class RDRService extends GenericService implements RDRServiceLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3306407480951745072L;

	@SuppressWarnings("unchecked")
	@Override
	public List<RDRParticipante> buscarRDRParticipantes(String fase, String serie) {		
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRParticipante.class.getSimpleName()).append(" o where 1=1 ");
			sql.append(" and o.faseLiga = '").append(fase).append("'");  
			sql.append(" and o.serieParticipante = '").append(serie).append("'"); 
			
			sql.append(" order by o.id"); 
			
			List<RDRParticipante> listaRDRParticipantes = (List<RDRParticipante>) consultarPorQuery(sql.toString(), 0, 0);
					
			if(listaRDRParticipantes != null && !listaRDRParticipantes.isEmpty()) {
				return listaRDRParticipantes; 	 
			}
			
			return null;
			
		} catch (Exception e) {			
			return null;
		}
	}



}
