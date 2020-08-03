package br.com.reisdaresenha.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.Pontuacao;
import br.com.reisdaresenha.model.Rodada;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.service.ParametroServiceLocal;
import br.com.reisdaresenha.service.RDRServiceLocal;
import br.com.reisdaresenha.service.RodadaServiceLocal;
import br.com.reisdaresenha.view.TimeCartolaRestDTO;
import br.com.reisdaresenha.view.TimeRodadaDTO;

/**
 * 
 * @author Renan Celso, em 03/08/2020
 *
 */
public class RDRJob implements Job {
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {				
		
		try {	
			
			RDRServiceLocal rdrService = lookupRdrService();
			RodadaServiceLocal rodadaService = lookupRodadaService();
			ParametroServiceLocal parametroService = lookupParametrosService();
			
			CartolaRestFulClient servicoCartola = new CartolaRestFulClient(); 	
			
			sincronizarTimesComCartolaFC(rdrService, servicoCartola);
			
			atualizarPontuacaoRodadaEmAndamento(rdrService, rodadaService, parametroService, servicoCartola);			
		
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e.getMessage());
		}
		
	}

	@SuppressWarnings("unchecked")
	private void sincronizarTimesComCartolaFC(RDRServiceLocal rdrService, CartolaRestFulClient servicoCartola) {
		
		List<Time> listaTimesParticipantes = (List<Time>) rdrService.consultarTodos(Time.class, " order by o.nomeTime, o.nomeDonoTime");	
		
		for (Time time : listaTimesParticipantes) {		
			
			TimeRodadaDTO timeCartola = new TimeRodadaDTO();						
			
			timeCartola = servicoCartola.buscarTimeRodadaPorIDCartola(time, new Long(0));	
			
			time.setNomeDonoTime(timeCartola.getTime().getNomeDonoTime());
			time.setIdCartola(timeCartola.getTime().getIdCartola());
			time.setFotoPerfil(timeCartola.getTime().getFotoPerfil());
			time.setUrlEscudoPng(timeCartola.getTime().getUrlEscudoPng());
			time.setUrlEscudoSvg(timeCartola.getTime().getUrlEscudoSvg());
			time.setAssinante(timeCartola.getTime().getAssinante());
			time.setSlugTime(timeCartola.getTime().getSlugTime());
			time.setFacebookId(timeCartola.getTime().getFacebookId());							
			time.setIdUserAtu("JOB-RDR");
			time.setLoginUserAtu("JOB-RDR");
			time.setDhAtu(new Date());		
			
			rdrService.atualizar(time);
		}		
		
	}
	
	@SuppressWarnings("unchecked")
	private void atualizarPontuacaoRodadaEmAndamento(RDRServiceLocal rdrService, RodadaServiceLocal rodadaService, 
													 ParametroServiceLocal parametroService, CartolaRestFulClient servicoCartola) {		
		
		Liga ligaPrincipal = new Liga();
		ligaPrincipal = (Liga) rdrService.consultarPorChavePrimaria(ligaPrincipal, new Long(1));
		
		Rodada rodadaEmAndamento = new Rodada();		
		rodadaEmAndamento = rodadaService.buscarRodadaEmAndamento(ligaPrincipal);	
		
		if(rodadaEmAndamento != null) {
			rodadaEmAndamento.setListaPontuacao(new ArrayList<Pontuacao>());		
			List<Pontuacao> listaPontuacao = (List<Pontuacao>) 
					rodadaService.consultarPorQuery("select o from Pontuacao o where o.liga.id = "+ligaPrincipal.getId()+
													" and o.rodada.id = "+rodadaEmAndamento.getId()+
													" order by o.time.nomeTime, o.time.nomeDonoTime", 0, 0);				
			rodadaEmAndamento.setListaPontuacao(listaPontuacao);
			
			buscarTodasAsPontuacoesNoServicoCartolaFC(rdrService, rodadaService, parametroService, servicoCartola, rodadaEmAndamento.getListaPontuacao());
		}		
	}
	
	private String buscarTodasAsPontuacoesNoServicoCartolaFC(RDRServiceLocal rdrService, RodadaServiceLocal rodadaService, ParametroServiceLocal parametroService, CartolaRestFulClient servicoCartola, List<Pontuacao> listaPontuacao) {
				
		try {	
			
			servicoCartola = new CartolaRestFulClient();		
			
			String email = parametroService.buscarParametroPorChave("user_email").getValor();			
			String senha = parametroService.buscarParametroPorChave("user_senha").getValor();				
			String slugLiga = servicoCartola.buscarSlugDaLiga(parametroService.buscarParametroPorChave("nome_liga").getValor());							
			String token = servicoCartola.gerarTokenLoginCartola(email, senha);	
			
			JSONObject jsonObject = new JSONObject();				
			
			if(slugLiga != null && token != null) {
				jsonObject = servicoCartola.buscarInformacoesLigaEspecifica(slugLiga, token);
			}	
			
			JSONObject jsonObjectLiga = new JSONObject();
			boolean isLigaSemCapitao = false;
			
			if(jsonObject != null) {
				jsonObjectLiga = (JSONObject) jsonObject.get("liga");										
				isLigaSemCapitao = (boolean) jsonObjectLiga.get("sem_capitao");
			}
			
			for (Pontuacao pontuacao : listaPontuacao) {
				
				TimeRodadaDTO timeRodadaDTO = new TimeRodadaDTO();			
				timeRodadaDTO = servicoCartola.buscarTimeRodadaPorIDCartola(pontuacao.getTime(), pontuacao.getRodada().getNrRodada());	
				
				if(timeRodadaDTO.getTime() == null) {					
					return null;
				}
				
				if(jsonObject != null) {
					
					if(isLigaSemCapitao) {	
						
						//buscar pontuacao do capitacao	 de cada time	
						JSONArray jsonArrayTimesParticipantes = (JSONArray) jsonObject.get("times");
						
						System.out.println("JSON: "+jsonArrayTimesParticipantes.toString());		
											
						List<TimeCartolaRestDTO> listaTimeCartolaRestDTO = new ArrayList<TimeCartolaRestDTO>();
						
						for (int i = 0; i < jsonArrayTimesParticipantes.size(); i++) {				
							JSONObject jsonObjectTime = (JSONObject) jsonArrayTimesParticipantes.get(i);						
							
							TimeCartolaRestDTO timeCartolaRestDTO = new TimeCartolaRestDTO();
							timeCartolaRestDTO.setIdCartola(new Long(String.valueOf(jsonObjectTime.get("time_id"))));
							timeCartolaRestDTO.setNomeTime(String.valueOf(jsonObjectTime.get("nome")));
							timeCartolaRestDTO.setSlug(String.valueOf(jsonObjectTime.get("slug")));	
							timeCartolaRestDTO.setNomeDonoTime(String.valueOf(jsonObjectTime.get("nome_cartola")));				
							timeCartolaRestDTO.setUrlEscudoSvg(String.valueOf(jsonObjectTime.get("url_escudo_svg")));
							timeCartolaRestDTO.setUrlEscudoPng(String.valueOf(jsonObjectTime.get("url_escudo_png")));
							JSONObject jsonObjectPontos = (JSONObject) jsonObjectTime.get("pontos");
							timeCartolaRestDTO.setRodada(new Long(String.valueOf(jsonObjectPontos.get("rodada"))));		
							
							timeCartolaRestDTO.setPontosCapitao(new Double(String.valueOf(jsonObjectPontos.get("capitao"))));
							
							listaTimeCartolaRestDTO.add(timeCartolaRestDTO);
						}	
						
						for (TimeCartolaRestDTO timeCartolaRestDTO : listaTimeCartolaRestDTO) {							
							if(timeRodadaDTO.getTime().getIdCartola().equals(timeCartolaRestDTO.getIdCartola()) 
									&& timeRodadaDTO.getRodadaAtual().equals(timeCartolaRestDTO.getRodada())) {										
								timeRodadaDTO.setPontos(timeRodadaDTO.getPontos() - (timeCartolaRestDTO.getPontosCapitao() / 2));								
							}							
						}						
					} 						
				}
				
				pontuacao.setVrPontuacao(timeRodadaDTO.getPontos() != null ? timeRodadaDTO.getPontos() : 0.0);					
				pontuacao.setVrCartoletas(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);
				
				pontuacao.getTime().setVrCartoletasAtuais(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);	
								
				rodadaService.atualizar(pontuacao.getTime());				
			}	
									
			for (Pontuacao pontuacao : listaPontuacao) {	
				pontuacao.setNomeTime(pontuacao.getTime().getNomeTime());					
				pontuacao.setIdCartola(pontuacao.getTime().getIdCartola());
				
				rodadaService.atualizar(pontuacao);				
			}	
			
			return null;
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return null;
		
	}

	/** LOOKUPS **/
	private RDRServiceLocal lookupRdrService() {
		try {
			Context c = new InitialContext();
			return (RDRServiceLocal) c.lookup("java:global/reisdaresenha/RDRService!br.com.reisdaresenha.service.RDRServiceLocal");
		} catch (Exception ne) {			
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
			throw new RuntimeException(ne);			
		}
	}
	
	private RodadaServiceLocal lookupRodadaService() {
		try {
			Context c = new InitialContext();
			return (RodadaServiceLocal) c.lookup("java:global/reisdaresenha/RodadaService!br.com.reisdaresenha.service.RodadaServiceLocal");
		} catch (Exception ne) {			
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
			throw new RuntimeException(ne);			
		}
	}
	
	
	private ParametroServiceLocal lookupParametrosService() {
		try {
			Context c = new InitialContext();
			return (ParametroServiceLocal) c.lookup("java:global/reisdaresenha/ParametroService!br.com.reisdaresenha.service.ParametroServiceLocal");
		} catch (Exception ne) {			
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
			throw new RuntimeException(ne);			
		}
	}
	
}