package br.com.reisdaresenha.service;

import java.util.List;

import javax.ejb.Stateless;

import br.com.reisdaresenha.model.RDRClassificacao;
import br.com.reisdaresenha.model.RDRCopaPontuacao;
import br.com.reisdaresenha.model.RDRParticipante;
import br.com.reisdaresenha.model.RDRPontuacao;
import br.com.reisdaresenha.model.RDRRodada;
import br.com.reisdaresenha.padrao.GenericService;

/**
 * @author Renan Celso
 */
@Stateless
public class RDRService extends GenericService implements RDRServiceLocal {
	
	private static final long serialVersionUID = 3306407480951745072L;

	@SuppressWarnings("unchecked")
	@Override
	public List<RDRParticipante> buscarRDRParticipantes(String fase, String serie) {		
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRParticipante.class.getSimpleName()).append(" o where 1=1 ");
			sql.append(" and o.faseLiga = '").append(fase).append("' ");  
			sql.append(" and o.serieParticipante = '").append(serie).append("' "); 
			
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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RDRClassificacao> buscarRDRClassificacao(String fase, String serie) {		
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRClassificacao.class.getSimpleName()).append(" o where 1=1 ");
			sql.append(" and o.faseLiga = '").append(fase).append("' ");  
			sql.append(" and o.serie = '").append(serie).append("' "); 
			
			sql.append(" order by o.vrPontos desc, o.vrPontuacaoAtualLigaPrincipalCartola desc"); 
			
			List<RDRClassificacao> listaRDRClassificacao = (List<RDRClassificacao>) consultarPorQuery(sql.toString(), 0, 0);
					
			if(listaRDRClassificacao != null && !listaRDRClassificacao.isEmpty()) {
				return listaRDRClassificacao; 	 
			}
			
			return null;
			
		} catch (Exception e) {			
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public RDRClassificacao buscarRDRClassificacaoPorRodadaParticipante(String fase, String serie, Long nrRodada, RDRParticipante rdrParticipante) {		
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRClassificacao.class.getSimpleName()).append(" o where 1=1 ");
			sql.append(" and o.faseLiga = '").append(fase).append("' ");  
			sql.append(" and o.serie = '").append(serie).append("' "); 
			sql.append(" and o.nrRodadaAtual = ").append(nrRodada); 
			sql.append(" and o.rdrParticipante.id = ").append(rdrParticipante.getId()); 			
			
			List<RDRClassificacao> listaRDRClassificacao = (List<RDRClassificacao>) consultarPorQuery(sql.toString(), 0, 0);
					
			if(listaRDRClassificacao != null && !listaRDRClassificacao.isEmpty()) {
				return listaRDRClassificacao.get(0); 	 				
			}
			
			return null;
			
		} catch (Exception e) {			
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public RDRClassificacao buscarRDRClassificacaoPorRodada(String fase, String serie, Long nrRodada) {		
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRClassificacao.class.getSimpleName()).append(" o where 1=1 ");
			sql.append(" and o.faseLiga = '").append(fase).append("' ");  
			sql.append(" and o.serie = '").append(serie).append("' "); 
			sql.append(" and o.nrRodadaAtual = ").append(nrRodada); 				
			
			List<RDRClassificacao> listaRDRClassificacao = (List<RDRClassificacao>) consultarPorQuery(sql.toString(), 0, 0);
					
			if(listaRDRClassificacao != null && !listaRDRClassificacao.isEmpty()) {
				return listaRDRClassificacao.get(0); 	 				
			}
			
			return null;
			
		} catch (Exception e) {			
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RDRRodada> buscarRDRRodadas(String fase, String serie) {		
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRRodada.class.getSimpleName()).append(" o where 1=1 ");
			sql.append(" and o.tipoRodada = '").append(fase).append("' ");  
			sql.append(" and o.serieRodada = '").append(serie).append("' "); 
			
			//sql.append(" order by o.nrRDRRodada desc ");
			
			sql.append(" order by o.nrRDRRodada asc ");		
			
			List<RDRRodada> listaRDRRodada = (List<RDRRodada>) consultarPorQuery(sql.toString(), 0, 0);
					
			if(listaRDRRodada != null && !listaRDRRodada.isEmpty()) {
				return listaRDRRodada; 	 
			}
			
			return null;
			
		} catch (Exception e) {			
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RDRPontuacao> buscarRDRPontuacaoPorRodada(RDRRodada rdrRodada) {
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRPontuacao.class.getSimpleName()).append(" o where 1=1 ");
			sql.append(" and o.rdrRodada.id = ").append(rdrRodada.getId());  	
			sql.append(" order by o.id desc ");			
			
			List<RDRPontuacao> listaRDRPontuacao = (List<RDRPontuacao>) consultarPorQuery(sql.toString(), 0, 0);
					
			if(listaRDRPontuacao != null && !listaRDRPontuacao.isEmpty()) {
				return listaRDRPontuacao; 	 
			}
			
			return null;
			
		} catch (Exception e) {			
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RDRCopaPontuacao> buscarRDRCopaPontuacao() {
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRCopaPontuacao.class.getSimpleName()).append(" o where 1=1 ");			
			sql.append(" order by o.nrJogoCopa ");			
			
			List<RDRCopaPontuacao> listaRDRCopaPontuacao = (List<RDRCopaPontuacao>) consultarPorQuery(sql.toString(), 0, 0);
					
			if(listaRDRCopaPontuacao != null && !listaRDRCopaPontuacao.isEmpty()) {
				return listaRDRCopaPontuacao; 	 
			}
			
			return null;
			
		} catch (Exception e) {			
			return null;
		}
	}



}
