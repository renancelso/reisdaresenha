package br.com.reisdaresenha.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.Premiacao;
import br.com.reisdaresenha.padrao.GenericService;

/**
 * @author Renan Celso
 */
@Stateless
public class InicioService extends GenericService implements InicioServiceLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8475703620320469907L;
	
	
	@SuppressWarnings("unchecked")
	@Override
    public List<Liga> buscarLigas(Integer ano) {		
    	try {
    		
    		List<Liga> listaLigas = new ArrayList<>();    		
    		
    		StringBuilder sql = new StringBuilder();
    		sql.append("select o from ").append(Liga.class.getSimpleName()).append(" o where 1 = 1");
    		if(ano != null) {
    			sql.append(" and o.ano.id = ").append(ano);
    		}
    		listaLigas = (List<Liga>) consultarPorQuery(sql.toString(), 0, 0);
    		
	    	return listaLigas; 	
	    	
    	} catch(Exception e) {
    		log.error(e);
    		return null;
    	}
    }	
	

	@SuppressWarnings("unchecked")
	@Override
    public List<Premiacao> buscarPremiacoes(Integer ano, Liga liga) {		
    	try {
    		
    		List<Premiacao> listaPremiacao = new ArrayList<>();    		
    		
    		StringBuilder sql = new StringBuilder();
    		sql.append("select o from ").append(Premiacao.class.getSimpleName()).append(" o where 1 = 1 ");
    		
    		if(ano != null && ano > 0) {
    			sql.append(" and o.ano.id = ").append(ano);
    		}
    		
    		if(liga!= null && liga.getId() != null) {
    			sql.append(" and o.liga.id = ").append(liga.getId());   
    		}
    		
    		sql.append(" order by o.liga.id asc, o.ordem asc, o.vrPremio desc, o.id asc");
    		
    		listaPremiacao = (List<Premiacao>) consultarPorQuery(sql.toString(), 0, 0);
    		
	    	return listaPremiacao; 	
	    	
    	} catch(Exception e) {
    		log.error(e);
    		return null;
    	}
    }	

}
