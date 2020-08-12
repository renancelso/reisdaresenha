package br.com.reisdaresenha.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.json.simple.JSONObject;

import br.com.reisdaresenha.model.Pontuacao;
import br.com.reisdaresenha.model.RDRClassificacao;
import br.com.reisdaresenha.model.RDRCopaPontuacao;
import br.com.reisdaresenha.model.RDRParticipante;
import br.com.reisdaresenha.model.RDRPontuacao;
import br.com.reisdaresenha.model.RDRRodada;
import br.com.reisdaresenha.model.Rodada;
import br.com.reisdaresenha.padrao.GenericService;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.view.TimeRodadaDTO;

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
	public RDRParticipante buscarRDRParticipantesCopaPorClassificacaoFinal(String fase, String serie, String classFinal) {		
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRParticipante.class.getSimpleName()).append(" o where 1=1 ");
			sql.append(" and o.faseLiga = 'COPA' ");
			sql.append(" and o.serieParticipante = 'COPA'"); 
			sql.append(" and o.classificacaoFinalParaCopa = '").append(classFinal).append("'");
			
			sql.append(" order by o.id"); 
			
			List<RDRParticipante> listaRDRParticipantes = (List<RDRParticipante>) consultarPorQuery(sql.toString(), 0, 0);
					
			if(listaRDRParticipantes != null && !listaRDRParticipantes.isEmpty()) {
				return listaRDRParticipantes.get(0); 	 
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
			
			sql.append(" order by o.vrPontos desc, o.qtdVitorias desc, (o.vrGolsPro - o.vrGolsContra) desc, o.vrGolsPro desc, o.vrPontuacaoAtualLigaPrincipalCartola desc"); 
			
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
	public RDRClassificacao buscarRDRClassificacaoPorParticipante(String fase, String serie, RDRParticipante rdrParticipante) {		
		
		try {
						
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRClassificacao.class.getSimpleName()).append(" o where 1=1 ");
			sql.append(" and o.faseLiga = '").append(fase).append("' ");  
			sql.append(" and o.serie = '").append(serie).append("' "); 
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
	public RDRRodada buscarRDRRodadaPorRodadaDaLigaPrincipal(Long nrRodadaLigaPrincipal, String fase, String serie) {	
		
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRRodada.class.getSimpleName()).append(" o where 1=1 ");
			sql.append(" and o.nrRodadaCartola = ").append(nrRodadaLigaPrincipal);
			sql.append(" and o.tipoRodada = '").append(fase).append("'");
			sql.append(" and o.serieRodada = '").append(serie).append("'");
						
			List<RDRRodada> listaRDRRodada = (List<RDRRodada>) consultarPorQuery(sql.toString(), 0, 0);
					
			if(listaRDRRodada != null && !listaRDRRodada.isEmpty()) {
				return listaRDRRodada.get(0); 	 
			}
			
			return null;
			
		} catch (Exception e) {			
			return null;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RDRRodada> buscarRDRRodadaPorRodadaDaLigaPrincipal(Long nrRodadaLigaPrincipal) {			
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRRodada.class.getSimpleName()).append(" o where 1=1 ");
			sql.append(" and o.nrRodadaCartola = ").append(nrRodadaLigaPrincipal);
						
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	public RDRCopaPontuacao buscarRDRCopaPontuacaoPorNrJogoCopa(Long nrJogoCopa) {
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(RDRCopaPontuacao.class.getSimpleName()).append(" o where 1=1 ");			
			sql.append(" and o.nrJogoCopa =").append(nrJogoCopa);	
			sql.append(" order by o.nrJogoCopa ");			
			
			List<RDRCopaPontuacao> listaRDRCopaPontuacao = (List<RDRCopaPontuacao>) consultarPorQuery(sql.toString(), 0, 0);
					
			if(listaRDRCopaPontuacao != null && !listaRDRCopaPontuacao.isEmpty()) {
				return listaRDRCopaPontuacao.get(0); 	 
			}
			
			return null;
			
		} catch (Exception e) {			
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void atualizarSaldoDeGols(String fase, String serie, List<RDRParticipante> listaParticipantes) {
		
		// select
		// (SELECT sum(a.vr_pont_time_casa_arr) FROM rdrpontuacao a where a.serie = 'SA' and a.fase = 'A' and a.time_casa = 1) + (SELECT sum(o.vr_pont_time_fora_arr) FROM rdrpontuacao o where o.serie = 'SA' and o.fase = 'A' and o.time_fora = 1)
		// from dual;
		try {
			
			if(listaParticipantes != null && !listaParticipantes.isEmpty()) {
				for (RDRParticipante rdrParticipante : listaParticipantes) {				
					
					//GOLS PRO
					StringBuilder sql = new StringBuilder();	
					sql.append("select \n");
					sql.append(" (SELECT sum(if(c.vr_pont_time_casa_arr IS NULL, 0, c.vr_pont_time_casa_arr)) FROM rdrpontuacao c where c.serie = '"+serie+"' and c.fase = '"+fase+"' and c.time_casa = "+rdrParticipante.getId()+") \n");
					sql.append(" + \n");
					sql.append(" (SELECT sum(if(f.vr_pont_time_fora_arr IS NULL, 0, f.vr_pont_time_fora_arr)) FROM rdrpontuacao f where f.serie = '"+serie+"' and f.fase = '"+fase+"' and f.time_fora = "+rdrParticipante.getId()+") \n");
					sql.append(" from dual ");
					
					List<Object[]> listaGolsPro = new ArrayList<>();				
					listaGolsPro = (List<Object[]>) consultarPorQueryNativa(sql.toString(), 0, 0);    		
					
					Double golsPro = 0.0;
					for (Object obj : listaGolsPro) {
						golsPro = new Double(String.valueOf(obj));
					}	
									
					//GOLS CONTRA
					sql = new StringBuilder();	
					sql.append("select \n");
					sql.append(" (SELECT sum(if(c.vr_pont_time_fora_arr IS NULL, 0, c.vr_pont_time_fora_arr)) FROM rdrpontuacao c where c.serie = '"+serie+"' and c.fase = '"+fase+"' and c.time_casa = "+rdrParticipante.getId()+") \n");
					sql.append(" + \n");
					sql.append(" (SELECT sum(if(f.vr_pont_time_casa_arr IS NULL, 0, f.vr_pont_time_casa_arr)) FROM rdrpontuacao f where f.serie = '"+serie+"' and f.fase = '"+fase+"' and f.time_fora = "+rdrParticipante.getId()+") \n");
					sql.append(" from dual ");
					
					List<Object[]> listaGolsContra = new ArrayList<>();						
					listaGolsContra = (List<Object[]>) consultarPorQueryNativa(sql.toString(), 0, 0);    		
					
					Double golsContra = 0.0;
					for (Object obj : listaGolsContra) {
						golsContra = new Double(String.valueOf(obj));
					}	
									
					RDRClassificacao classificacao = buscarRDRClassificacaoPorParticipante(fase, serie, rdrParticipante);
					classificacao.setVrGolsPro(golsPro);
					classificacao.setVrGolsContra(golsContra);
					
					atualizar(classificacao);
					
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
	}

	@Override
	public String buscarTodasAsPontuacoesNoServicoCartolaFC(RodadaServiceLocal rodadaService, ParametroServiceLocal parametroService, CartolaRestFulClient servicoCartola, Rodada rodadaEmAndamento, List<Pontuacao> listaPontuacao) {
		
		try {	
			
			servicoCartola = new CartolaRestFulClient();	
			
			JSONObject jsonAtletas = servicoCartola.buscarPontuacaoRodadaAtual(rodadaEmAndamento.getNrRodada());
						
			StringBuilder str = new StringBuilder();
			
			for (Pontuacao pontuacao : listaPontuacao) {
				
				TimeRodadaDTO timeRodadaDTO = new TimeRodadaDTO();			
				timeRodadaDTO = servicoCartola.buscarTimeRodadaPorIDCartola(pontuacao.getTime(), pontuacao.getRodada().getNrRodada());	
				
				if(timeRodadaDTO == null || timeRodadaDTO.getTime() == null) { 					
					
					timeRodadaDTO = servicoCartola.buscarTimeRodadaPorIDCartola(pontuacao.getTime(), pontuacao.getRodada().getNrRodada());	
					
					if(timeRodadaDTO == null || timeRodadaDTO.getTime() == null) { 
						
						timeRodadaDTO = servicoCartola.buscarTimeRodadaPorIDCartola(pontuacao.getTime(), pontuacao.getRodada().getNrRodada());	
						
						if(timeRodadaDTO == null || timeRodadaDTO.getTime() == null) { 
							
							timeRodadaDTO = servicoCartola.buscarTimeRodadaPorIDCartola(pontuacao.getTime(), pontuacao.getRodada().getNrRodada());	
							
							if(timeRodadaDTO == null || timeRodadaDTO.getTime() == null) { 
								
								timeRodadaDTO = servicoCartola.buscarTimeRodadaPorIDCartola(pontuacao.getTime(), pontuacao.getRodada().getNrRodada());	
								
								if(timeRodadaDTO == null || timeRodadaDTO.getTime() == null) { 
									log.info(pontuacao.getRodada().getNrRodada()+"ª Rodada ainda não iniciou no Cartola FC e/ou nao foi possivel buscar o time: "+pontuacao.getTime().getNomeTime()+" (ID-Cartola: "+pontuacao.getTime().getIdCartola()+")");
									log.error(pontuacao.getRodada().getNrRodada()+"ª Rodada ainda não iniciou no Cartola FC e/ou nao foi possivel buscar o time: "+pontuacao.getTime().getNomeTime()+" (ID-Cartola: "+pontuacao.getTime().getIdCartola()+")");
									log.debug(pontuacao.getRodada().getNrRodada()+"ª Rodada ainda não iniciou no Cartola FC e/ou nao foi possivel buscar o time: "+pontuacao.getTime().getNomeTime()+" (ID-Cartola: "+pontuacao.getTime().getIdCartola()+")");
									log.warn(pontuacao.getRodada().getNrRodada()+"ª Rodada ainda não iniciou no Cartola FC e/ou nao foi possivel buscar o time: "+pontuacao.getTime().getNomeTime()+" (ID-Cartola: "+pontuacao.getTime().getIdCartola()+")");
									str.append(pontuacao.getTime().getNomeTime()).append(", ");
									
									continue;
								}
								
							}
							
						}
						
					}
					
				}
						
				timeRodadaDTO.setPontos(0.0);	
				
				if(jsonAtletas != null) {			
					
					if(timeRodadaDTO.getIdAtletasEscalados() != null && !timeRodadaDTO.getIdAtletasEscalados().isEmpty()) {	
						
						for (Long idEscalado : timeRodadaDTO.getIdAtletasEscalados()) {
							if(jsonAtletas.get(String.valueOf(idEscalado)) != null) {
								
								JSONObject pont = (JSONObject) jsonAtletas.get(String.valueOf(idEscalado));	
								
								Double pontuacaoSomar = Double.parseDouble(String.valueOf(pont.get("pontuacao")));	
								
								timeRodadaDTO.setPontos(timeRodadaDTO.getPontos()+pontuacaoSomar);				
							}
							
						}			
						
						pontuacao.setVrPontuacao(timeRodadaDTO.getPontos() != null ? timeRodadaDTO.getPontos() : 0.0);		
					}					
				}		
				
				pontuacao.setVrCartoletas(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);													
				pontuacao.getTime().setVrCartoletasAtuais(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);									
				rodadaService.atualizar(pontuacao.getTime());					
			}	
									
			for (Pontuacao pontuacao : listaPontuacao) {	
				pontuacao.setNomeTime(pontuacao.getTime().getNomeTime());					
				pontuacao.setIdCartola(pontuacao.getTime().getIdCartola());
				
				rodadaService.atualizar(pontuacao);				
			}	
			
			if(str!= null && str.toString() != null && !str.toString().isEmpty()) {
				return str.toString();
			}	
			
			return null;
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return null;
		
	}

}
