package br.com.reisdaresenha.service;

import java.util.List;

import javax.ejb.Stateless;

import br.com.reisdaresenha.model.Parametro;
import br.com.reisdaresenha.padrao.GenericService;

/**
 * @author Renan Celso
 */
@Stateless
public class ParametroService extends GenericService implements ParametroServiceLocal {

	private static final long serialVersionUID = -2979941075837305812L;
		
	@SuppressWarnings("unchecked")
	@Override
	public Parametro buscarParametroPorChave(String chave) {		
		
		StringBuilder sql = new StringBuilder();
		sql.append("select o from ").append(Parametro.class.getSimpleName()).append(" o ");
		sql.append(" where o.chave = '").append(chave).append("'");  
		
		List<Parametro> listaParametros = (List<Parametro>) consultarPorQuery(sql.toString(), 0, 0);
				
		if(listaParametros != null && !listaParametros.isEmpty()) {
			return listaParametros.get(0); 	 
		}
		
		return null;	
	}


}
