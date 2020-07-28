package br.com.reisdaresenha.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.OSBPontuacao;
import br.com.reisdaresenha.model.OSBRodada;
import br.com.reisdaresenha.model.Premiacao;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.padrao.GenericService;
import br.com.reisdaresenha.view.ClassificacaoLigaPrincipalDTO;

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
	
	@SuppressWarnings("unchecked")
	@Override
    public List<ClassificacaoLigaPrincipalDTO> buscarPontuacaoLigaPrincipalTime(Integer ano, Time time) {		
    	try {
    		
    		List<Object[]> listaClassificacaoLigaPrincipalObject = new ArrayList<>();
    		
    		StringBuilder sql = new StringBuilder();
    		
    		sql.append(" SELECT t.nome_time time, ");   		  // 0 		    	
    		sql.append(" count(r.nr_rodada) jogos, "); 			  // 1
    		sql.append(" t.vr_cartoletasAtuais cartoletas, ");    // 2
    		sql.append(" round(sum(vr_pontuacao),2) pontuacao, "); // 3  
    		sql.append(" t.url_escudo_png escudo_time "); //4
    		sql.append(" FROM pontuacao p inner join time t on p.time = t.id ");
    		sql.append(" inner join liga l on p.liga = l.id ");
    		sql.append(" inner join rodada r on p.rodada = r.id ");
    		sql.append(" where l.ano = ").append(ano);
    		sql.append(" and t.id = ").append(time.getId());
    		sql.append(" group by t.nome_time ");
    		sql.append(" order by sum(vr_pontuacao) desc ");
    		    		
    		listaClassificacaoLigaPrincipalObject = (List<Object[]>) consultarPorQueryNativa(sql.toString(), 0, 0);    		
    		    		
    		List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<>();
    	    
    		Integer colocacao = 1;
    		for (Object[] obj : listaClassificacaoLigaPrincipalObject) {
    			
    			ClassificacaoLigaPrincipalDTO classificacao = new ClassificacaoLigaPrincipalDTO();
    			classificacao.setTime(String.valueOf(obj[0]));
    			classificacao.setJogos(Integer.parseInt(String.valueOf(obj[1])));
    			classificacao.setCartoletas(Double.parseDouble(String.valueOf(obj[2])));
    			classificacao.setPontuacao(Double.parseDouble(String.valueOf(obj[3])));      			
    			classificacao.setColocacao(colocacao++); 

    			classificacao.setEscudoTime(String.valueOf(obj[4]));
    			
    			listaClassificacaoLigaPrincipalDTO.add(classificacao);    			
			}
    		    	
	    	return listaClassificacaoLigaPrincipalDTO;
	    	
    	} catch(Exception e) {
    		log.error(e);
    		return null;
    	}
    }	
		
	@SuppressWarnings("unchecked")
	@Override
    public List<ClassificacaoLigaPrincipalDTO> buscarClassificacaoLigaPrincipal(Integer ano) {		
    	try {
    		
    		List<Object[]> listaClassificacaoLigaPrincipalObject = new ArrayList<>();
    		
    		StringBuilder sql = new StringBuilder();
    		
    		sql.append(" SELECT t.nome_time time, ");   		  // 0 		    	
    		sql.append(" count(r.nr_rodada) jogos, "); 			  // 1
    		sql.append(" t.vr_cartoletasAtuais cartoletas, ");    // 2
    		sql.append(" round(sum(vr_pontuacao),2) pontuacao, "); // 3  
    		sql.append(" t.url_escudo_png escudo_time "); //4
    		sql.append(" FROM pontuacao p inner join time t on p.time = t.id ");
    		sql.append(" inner join liga l on p.liga = l.id ");
    		sql.append(" inner join rodada r on p.rodada = r.id ");
    		sql.append(" where l.ano = ").append(ano);
    		sql.append(" group by t.nome_time ");
    		sql.append(" order by sum(vr_pontuacao) desc ");
    		    		
    		listaClassificacaoLigaPrincipalObject = (List<Object[]>) consultarPorQueryNativa(sql.toString(), 0, 0);    		
    		    		
    		List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<>();
    	    
    		Integer colocacao = 1;
    		for (Object[] obj : listaClassificacaoLigaPrincipalObject) {
    			
    			ClassificacaoLigaPrincipalDTO classificacao = new ClassificacaoLigaPrincipalDTO();
    			classificacao.setTime(String.valueOf(obj[0]));
    			classificacao.setJogos(Integer.parseInt(String.valueOf(obj[1])));
    			classificacao.setCartoletas(Double.parseDouble(String.valueOf(obj[2])));
    			classificacao.setPontuacao(Double.parseDouble(String.valueOf(obj[3])));      			
    			classificacao.setColocacao(colocacao++); 

    			classificacao.setEscudoTime(String.valueOf(obj[4]));
    			
    			listaClassificacaoLigaPrincipalDTO.add(classificacao);    			
			}
    		    	
	    	return listaClassificacaoLigaPrincipalDTO;
	    	
    	} catch(Exception e) {
    		log.error(e);
    		return null;
    	}
    }	
	
	@SuppressWarnings("unchecked")
	@Override
    public List<ClassificacaoLigaPrincipalDTO> buscarClassificacaoLigaPrincipalAteRodada4(Integer ano) {		
    	try {
    		
    		List<Object[]> listaClassificacaoLigaPrincipalObject = new ArrayList<>();
    		
    		StringBuilder sql = new StringBuilder();
    		
    		sql.append(" SELECT t.nome_time time, ");   		   // 0 		    	
    		sql.append(" count(r.nr_rodada) jogos, "); 			   // 1
    		sql.append(" t.vr_cartoletasAtuais cartoletas, ");     // 2
    		sql.append(" round(sum(vr_pontuacao),2) pontuacao, "); // 3  
    		sql.append(" t.url_escudo_png escudo_time "); 		   // 4   
    		sql.append(" FROM pontuacao p inner join time t on p.time = t.id ");
    		sql.append(" inner join liga l on p.liga = l.id ");
    		sql.append(" inner join rodada r on p.rodada = r.id ");
    		sql.append(" where l.ano = ").append(ano);    	
    		
    		sql.append(" and r.nr_rodada in (1,2,3,4) ");   
    		
    		sql.append(" group by t.nome_time ");
    		sql.append(" order by sum(vr_pontuacao) desc ");
    		    		
    		listaClassificacaoLigaPrincipalObject = (List<Object[]>) consultarPorQueryNativa(sql.toString(), 0, 0);    		
    		    		
    		List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<>();
    	    
    		Integer colocacao = 1;
    		for (Object[] obj : listaClassificacaoLigaPrincipalObject) {
    			
    			ClassificacaoLigaPrincipalDTO classificacao = new ClassificacaoLigaPrincipalDTO();
    			classificacao.setTime(String.valueOf(obj[0]));
    			classificacao.setJogos(Integer.parseInt(String.valueOf(obj[1])));
    			classificacao.setCartoletas(Double.parseDouble(String.valueOf(obj[2])));
    			classificacao.setPontuacao(Double.parseDouble(String.valueOf(obj[3])));      			
    			classificacao.setColocacao(colocacao++); 

    			classificacao.setEscudoTime(String.valueOf(obj[4]));
    			
    			listaClassificacaoLigaPrincipalDTO.add(classificacao);    			
			}
    		    	
	    	return listaClassificacaoLigaPrincipalDTO;
	    	
    	} catch(Exception e) {
    		log.error(e);
    		return null;
    	}
    }	 
	
	
	@SuppressWarnings("unchecked")
	@Override
    public List<ClassificacaoLigaPrincipalDTO> buscarHistoricoClassificacaoRodadas(Integer ano, Long nrRodada) {		
    	try {
    		
    		List<Object[]> listaClassificacaoLigaPrincipalObject = new ArrayList<>();
    		
    		StringBuilder sql = new StringBuilder();
    		sql.append(" \n");   	
    		sql.append(" SELECT t.nome_time time, \n");   		  // 0 		    	
    		sql.append(" 		count(r.nr_rodada) jogos, \n"); 			  // 1
    		sql.append(" 		t.vr_cartoletasAtuais cartoletas, \n");    // 2
    		sql.append(" 		round(sum(vr_pontuacao),2) pontuacao, \n"); // 3  
    		sql.append(" 		t.url_escudo_png escudo_time, \n"); // 4    		
    		sql.append(" 		p.rodada, \n");   // 5  		
    		sql.append(" 		t.id_cartola \n"); //6
    		sql.append(" FROM pontuacao p inner join time t on p.time = t.id \n");
    		sql.append(" 	inner join liga l on p.liga = l.id \n");
    		sql.append(" 	inner join rodada r on p.rodada = r.id \n");
    		sql.append(" where l.ano = ").append(ano).append("\n");    		
    		sql.append(" and r.nr_rodada  = ").append(nrRodada).append("\n");   		
    		sql.append(" group by t.nome_time ").append("\n");
    		sql.append(" order by sum(vr_pontuacao) desc ");
    		    		
    		listaClassificacaoLigaPrincipalObject = (List<Object[]>) consultarPorQueryNativa(sql.toString(), 0, 0);    		
    		    		
    		List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<>();
    	    
    		Integer colocacao = 1;
    		for (Object[] obj : listaClassificacaoLigaPrincipalObject) {
    			
    			ClassificacaoLigaPrincipalDTO classificacao = new ClassificacaoLigaPrincipalDTO();
    			classificacao.setTime(String.valueOf(obj[0]));
    			classificacao.setJogos(Integer.parseInt(String.valueOf(obj[1])));
    			classificacao.setCartoletas(obj[2] != null ? Double.parseDouble(String.valueOf(obj[2])) : 0.0);
    			classificacao.setPontuacao(Double.parseDouble(String.valueOf(obj[3])));      			
    			classificacao.setColocacao(colocacao++);     			
    			classificacao.setIdTimeCartola(Long.parseLong(String.valueOf(obj[6])));    
    			classificacao.setEscudoTime(String.valueOf(obj[4]));    			
    			listaClassificacaoLigaPrincipalDTO.add(classificacao);    			
			}
    		    	
	    	return listaClassificacaoLigaPrincipalDTO;
	    	
    	} catch(Exception e) {
    		log.error(e);
    		return null;
    	}
    }	 
	
	@SuppressWarnings("unchecked")
	@Override
    public List<OSBPontuacao> buscarHistoricoClassificacaoOsbRodadas(OSBRodada osbRodada) {		
		try {
			StringBuilder sql = new StringBuilder();			
			sql.append("select o from ").append(OSBPontuacao.class.getSimpleName()).append(" o where 1 = 1 ");
			sql.append(" and o.osbRodada.id =" ).append(osbRodada.getId());		
			sql.append(" order by o.vrPontuacao desc ");		
			
			List<OSBPontuacao> listaOSBPontuacao = new ArrayList<>();		
			listaOSBPontuacao = (List<OSBPontuacao>) consultarPorQuery(sql.toString(), 0, 0);
			
			return listaOSBPontuacao;
		} catch(Exception e) {
    		log.error(e);
    		return null;
    	}
	}	

}
