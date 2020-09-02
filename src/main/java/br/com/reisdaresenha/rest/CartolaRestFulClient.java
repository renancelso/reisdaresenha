package br.com.reisdaresenha.rest;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.view.TimeRodadaDTO;

/**
 * 
 * @author Renan Celso, em 21/07/2020
 *
 */
public class CartolaRestFulClient {
	
	private transient Logger log = Logger.getLogger(CartolaRestFulClient.class.getName());
	
	public Time buscarTime(String nomeTimeNoCartola) {
		
		nomeTimeNoCartola = nomeTimeNoCartola.replace(" ", "%20");
		
		Time time = new Time();		
		String endPoint = "https://api.cartolafc.globo.com/times?q="+nomeTimeNoCartola;		
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(endPoint);
		
		try {	
							
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			
			//log.info("Inicializando chamada a: " + endPoint);
			
			int statusCode = client.executeMethod(method);

			//log.info("Status Text >>> " + HttpStatus.getStatusText(statusCode));			

			String jsonResponse = method.getResponseBodyAsString();

			JSONParser parser = new JSONParser();
			
			JSONArray jsonArray = (JSONArray) parser.parse(jsonResponse);		
			
			if(jsonArray != null && !jsonArray.isEmpty()) {		
				
				JSONObject jsonObject = (JSONObject) jsonArray.get(0);			
				String nomeDonoTimeCartola = (String) jsonObject.get("nome_cartola");				
				Long timeIdCartola = (Long) jsonObject.get("time_id");				
				String fotoPerfil = (String) jsonObject.get("foto_perfil");
				String urlEscudoPng = (String) jsonObject.get("url_escudo_png");				
				String urlEscudoSvg = (String) jsonObject.get("url_escudo_svg");				
				Boolean assinante = (Boolean) jsonObject.get("assinante");
				String slug = (String) jsonObject.get("slug");
				Long facebookId = (Long) jsonObject.get("facebook_id");
				
				time.setNomeDonoTime(nomeDonoTimeCartola);
				time.setIdCartola(timeIdCartola);
				time.setFotoPerfil(fotoPerfil);
				time.setUrlEscudoPng(urlEscudoPng);
				time.setUrlEscudoSvg(urlEscudoSvg);
				time.setAssinante(assinante ? "sim" : "nao");
				time.setSlugTime(slug);
				time.setFacebookId(facebookId);		
			}
						
			return time;		

		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}	
		
		return time;		
	}
	
	public TimeRodadaDTO buscarTimeRodadaPorIDCartola(Time time, Long nrRodada) {
		
		TimeRodadaDTO timeRodadaDTO = new TimeRodadaDTO();		
		String endPoint = "";
		
		if(nrRodada > 0) {
			endPoint = "https://api.cartolafc.globo.com/time/id/"+time.getIdCartola()+"/"+nrRodada;			
		} else {
			endPoint = "https://api.cartolafc.globo.com/time/id/"+time.getIdCartola();
		}
		
		HttpClient client = new HttpClient();		
		GetMethod method = new GetMethod(endPoint);
		
		try {			
				
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			
			//log.info("Inicializando chamada a: " + endPoint);

			int statusCode = client.executeMethod(method);

			//log.info("Status Text >>> " + HttpStatus.getStatusText(statusCode));					

			String jsonResponse = method.getResponseBodyAsString();

			JSONParser parser = new JSONParser();
			
			JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);		
						
			if(jsonObject.get("mensagem") != null) {
				if("Rodada Inválida.".equalsIgnoreCase(String.valueOf(jsonObject.get("mensagem")))
						|| "Mercado em manutenção.".equalsIgnoreCase(String.valueOf(jsonObject.get("mensagem")))){					
					timeRodadaDTO.setTime(null);
					return timeRodadaDTO;
				}
			}
							
			try {	
				Double patrimonio = (Double) jsonObject.get("patrimonio");	
				timeRodadaDTO.setPatrimonio(patrimonio);
			} catch (Exception e) {
				Long patrimonio = (Long) jsonObject.get("patrimonio");			
				timeRodadaDTO.setPatrimonio(patrimonio != null ? Double.parseDouble(String.valueOf(patrimonio)) : 0.0);
			}
			
			try {
				Double pontos = (Double) jsonObject.get("pontos");	
				timeRodadaDTO.setPontos(pontos);	
			} catch (Exception e) {
				Long pontos = (Long) jsonObject.get("pontos");	
				timeRodadaDTO.setPontos(new Double(String.valueOf(pontos)));	
			}
			
			try {
				Double pontosCampeonato = (Double) jsonObject.get("pontos_campeonato");		
				timeRodadaDTO.setPontosCampeonato(pontosCampeonato);		
			} catch (Exception e) {
				Long pontosCampeonato = (Long) jsonObject.get("pontos_campeonato");	
				timeRodadaDTO.setPontosCampeonato(new Double(String.valueOf(pontosCampeonato)));		
			}
			
			try {
				Long capitaoId = (Long) jsonObject.get("capitao_id");	
				timeRodadaDTO.setCapitaoId(capitaoId);
			} catch (Exception e) {
				try {
					Double capitaoId = (Double) jsonObject.get("capitao_id");	
					timeRodadaDTO.setCapitaoId(new Long(String.valueOf(capitaoId)));
				} catch (Exception ex) {
					log.warn("NAO FOI POSSIVEL BUSCAR O ID DO CAPITAO DO TIME: "+time.getNomeTime());					
				}
			}
			
			JSONObject jsonObjectTime = (JSONObject) jsonObject.get("time");
			
			String nomeDonoTimeCartola = (String) jsonObjectTime.get("nome_cartola");				
			Long timeIdCartola = (Long) jsonObjectTime.get("time_id");				
			String fotoPerfil = (String) jsonObjectTime.get("foto_perfil");
			String urlEscudoPng = (String) jsonObjectTime.get("url_escudo_png");				
			String urlEscudoSvg = (String) jsonObjectTime.get("url_escudo_svg");				
			Boolean assinante = (Boolean) jsonObjectTime.get("assinante");
			String slug = (String) jsonObjectTime.get("slug");
			Long facebookId = (Long) jsonObjectTime.get("facebook_id");
						
			timeRodadaDTO.setTime(time);
			timeRodadaDTO.getTime().setNomeDonoTime(nomeDonoTimeCartola);
			timeRodadaDTO.getTime().setIdCartola(timeIdCartola);
			timeRodadaDTO.getTime().setFotoPerfil(fotoPerfil);
			timeRodadaDTO.getTime().setUrlEscudoPng(urlEscudoPng);
			timeRodadaDTO.getTime().setUrlEscudoSvg(urlEscudoSvg);
			timeRodadaDTO.getTime().setAssinante(assinante ? "sim" : "nao");
			timeRodadaDTO.getTime().setSlugTime(slug);
			timeRodadaDTO.getTime().setFacebookId(facebookId);	
			
			timeRodadaDTO.setRodadaAtual(nrRodada);						
			
			if(new Long(String.valueOf(jsonObject.get("rodada_atual"))).longValue() == nrRodada.longValue()) {
							
				JSONArray jsonArray = (JSONArray) jsonObject.get("atletas");
					
				timeRodadaDTO.setIdAtletasEscalados(new ArrayList<Long>());
				
				if(jsonArray != null && !jsonArray.isEmpty()) {						
					for (int i = 0; i < jsonArray.size(); i++) {					
						JSONObject json = (JSONObject) jsonArray.get(i);						
						Long idAtleta = (Long) json.get("atleta_id");					
						timeRodadaDTO.getIdAtletasEscalados().add(idAtleta);
					}			
				}
			}
			
			return timeRodadaDTO;		

		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			method.releaseConnection();
		}			
	}
		
	public String buscarLogoDaLiga(String nomeLiga) {
		
		nomeLiga = nomeLiga.replace(" ", "%20");
					
		String endPoint = "https://api.cartolafc.globo.com/ligas?q="+nomeLiga;		
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(endPoint);
		
		try {				
						
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");				

			client.executeMethod(method);

			String jsonResponse = method.getResponseBodyAsString();

			JSONParser parser = new JSONParser();
			
			JSONArray jsonArray = (JSONArray) parser.parse(jsonResponse);		
			
			if(jsonArray != null && !jsonArray.isEmpty()) {				
				JSONObject jsonObject = (JSONObject) jsonArray.get(0);		
				
				String imagem = (String) jsonObject.get("imagem");
							
				return imagem;
			}

		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}		
		
		return null;		
	}
	
	
	public String buscarSlugDaLiga(String nomeLiga) {
		
		nomeLiga = nomeLiga.replace(" ", "%20");
					
		String endPoint = "https://api.cartolafc.globo.com/ligas?q="+nomeLiga;		
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(endPoint);
		
		try {				
						
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");				

			client.executeMethod(method);

			String jsonResponse = method.getResponseBodyAsString();

			JSONParser parser = new JSONParser();
			
			JSONArray jsonArray = (JSONArray) parser.parse(jsonResponse);		
			
			if(jsonArray != null && !jsonArray.isEmpty()) {				
				JSONObject jsonObject = (JSONObject) jsonArray.get(0);		
				
				String slug = (String) jsonObject.get("slug");
							
				return slug;
			}

		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}		
		
		return null;		
	}
	
	public String gerarTokenLoginCartola(String email, String senha) {
		
		try {						
			
			String endPoint = "https://login.globo.com/api/authentication";
			
			HttpClient client = new HttpClient();

			PostMethod method = new PostMethod(endPoint);
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
				
			String payload= "{\"payload\": {\"email\":\""+email+"\",\"password\":\""+senha+"\",\"serviceId\":4728}}";
			
			method.setRequestEntity(new StringRequestEntity(payload, "application/json", "UTF-8"));
					
			try {
			
				//log.info("Inicializando chamada a: " + endPoint);
				//log.info("-----------------------------------------------------");

				int statusCode = client.executeMethod(method);

				//log.info("Status Text >>> " + HttpStatus.getStatusText(statusCode));			

				String jsonResponse = method.getResponseBodyAsString();

				JSONParser parser = new JSONParser();
				
				JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);	
				
				String userMessage = (String) jsonObject.get("userMessage");
				String glbId = (String) jsonObject.get("glbId");
				String id = (String) jsonObject.get("id");		
				
				if(userMessage != null && id != null 
						&& userMessage.contains("autenticado com sucesso") 
						&& "Authenticated".equalsIgnoreCase(id)) {
					String token = glbId;
//					log.info("userMessage: "+userMessage);
//					log.info("token: \n"+token);		
//					log.info("id: "+id);
					return token;
				}
											
				method.releaseConnection();

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {			
			e.printStackTrace();			
		}
		
		return null;
	}
	
	public JSONObject buscarInformacoesLigaEspecifica(String slugLiga, String token) {
				
		try {
			String endPoint = "https://api.cartolafc.globo.com/auth/liga/"+slugLiga;			
					
			HttpClient client = new HttpClient();
	
			GetMethod method = new GetMethod(endPoint);
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");					
			method.setRequestHeader("X-GLB-Token", token);		
					
			try {
			
				//log.info("Inicializando chamada a: " + endPoint);
				//log.info("-----------------------------------------------------");
	
				int statusCode = client.executeMethod(method);
	
				//log.info("Status Text >>> " + HttpStatus.getStatusText(statusCode));						
	
				String jsonResponse = method.getResponseBodyAsString();
	
				JSONParser parser = new JSONParser();
				
				JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
				
				//log.info(jsonObject.toString());				
											
				method.releaseConnection();
				
				return jsonObject;
	
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();			
		}
		
		return null;
	}
		
	public JSONObject buscarPontuacaoRodadaAtual(Long nrRodada) {			
						
		String endPoint = "https://api.cartolafc.globo.com/atletas/pontuados";		
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(endPoint);
		
		try {				
						
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");				

			client.executeMethod(method);

			String jsonResponse = method.getResponseBodyAsString();
			
			if(jsonResponse == null) {
				return null;
			}

			JSONParser parser = new JSONParser();
			
			JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);	
			
			Long rodadaEmAndamentoCartolaFC = new Long(String.valueOf(jsonObject.get("rodada")));
			
			if(nrRodada.longValue() == rodadaEmAndamentoCartolaFC.longValue()) {					
				JSONObject jsonAtletas = (JSONObject) jsonObject.get("atletas");					
				return jsonAtletas;
			} else {
				return null;
			}

		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}		
		
		return null;		
	}
	
	public JSONObject getStatusRodadaCartolaFC(Long nrRodada) {			
		
		String endPoint = "https://api.cartolafc.globo.com/mercado/status";		
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(endPoint);
		
		try {				
						
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");				

			client.executeMethod(method);

			String jsonResponse = method.getResponseBodyAsString();

			JSONParser parser = new JSONParser();
			
			JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);	
			
			Long rodadaAtual = new Long(String.valueOf(jsonObject.get("rodada_atual")));
			
			if(rodadaAtual != null && nrRodada != null && rodadaAtual.longValue() == nrRodada.longValue()) {
				/**
				 * "status_mercado": 1 - Mercado Aberto
				 * "status_mercado": 2 - Mercado Fechado (Rodada em andamento)
				 * "status_mercado": 4 - Mercado em Manutenção (Pós Rodada)
				 */
				return jsonObject;
				
			} else {				
				if(jsonObject != null) {
					return jsonObject;
				}			
			}
			
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}		
		
		return null;		
	}
	
}
