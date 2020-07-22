package br.com.reisdaresenha.service;

import java.util.List;

import javax.ejb.Stateless;

import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.padrao.GenericService;

/**
 * @author Renan Celso
 */
@Stateless
public class TimeService extends GenericService implements TimeServiceLocal {

	private static final long serialVersionUID = -2979941075837305812L;
		
	@SuppressWarnings("unchecked")
	@Override
	public Time buscarTimePorNome(String nomeTime) {		
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(Time.class.getSimpleName()).append(" o ");
			sql.append(" where o.nomeTime = '").append(nomeTime).append("'");  
			
			List<Time> listaTimes = (List<Time>) consultarPorQuery(sql.toString(), 0, 0);
					
			if(listaTimes != null && !listaTimes.isEmpty()) {
				return listaTimes.get(0); 	 
			}
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


}
