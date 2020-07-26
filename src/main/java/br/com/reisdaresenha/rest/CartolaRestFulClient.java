package br.com.reisdaresenha.rest;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
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
			
			//System.out.println("Inicializando chamada a: " + endPoint);
			
			int statusCode = client.executeMethod(method);

			//System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));
			//System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));			

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
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}	
		
		return time;		
	}
	
	public TimeRodadaDTO buscarTimeRodadaPorIDCartola(Time time, Long nrRodada) {
		
		TimeRodadaDTO timeRodadaDTO = new TimeRodadaDTO();				
		String endPoint = "https://api.cartolafc.globo.com/time/id/"+time.getIdCartola()+"/"+nrRodada;			
		HttpClient client = new HttpClient();		
		GetMethod method = new GetMethod(endPoint);
		
		try {			
				
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			
			//System.out.println("Inicializando chamada a: " + endPoint);

			int statusCode = client.executeMethod(method);

			//System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));					

			String jsonResponse = method.getResponseBodyAsString();

			JSONParser parser = new JSONParser();
			
			JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);		
				
			try {
				
				Double patrimonio = (Double) jsonObject.get("patrimonio");				
				Double pontos = (Double) jsonObject.get("pontos");				
				Double pontosCampeonato = (Double) jsonObject.get("pontos_campeonato");				
				Double valorTime = (Double) jsonObject.get("valor_time");
				
				timeRodadaDTO.setTime(time);
				timeRodadaDTO.setRodadaAtual(nrRodada);
				timeRodadaDTO.setPatrimonio(patrimonio);				
				timeRodadaDTO.setPontos(pontos);	
				
				timeRodadaDTO.setPontosCampeonato(pontosCampeonato);			
				timeRodadaDTO.setValorTime(valorTime);		
				
			} catch (Exception e) {				
				
				Long patrimonio = (Long) jsonObject.get("patrimonio");				
				Double pontos = (Double) jsonObject.get("pontos");				
				Double pontosCampeonato = (Double) jsonObject.get("pontos_campeonato");				
				Long valorTime = (Long) jsonObject.get("valor_time");
				
				timeRodadaDTO.setTime(time);
				timeRodadaDTO.setRodadaAtual(nrRodada);
				timeRodadaDTO.setPatrimonio(patrimonio != null ? Double.parseDouble(String.valueOf(patrimonio)) : 0.0);
				timeRodadaDTO.setPontos(pontos);
				timeRodadaDTO.setPontosCampeonato(pontosCampeonato);			
				timeRodadaDTO.setValorTime(valorTime != null ? Double.parseDouble(String.valueOf(valorTime)) : 0.0);
				
			}	
			
			return timeRodadaDTO;		

		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}	
		
		return timeRodadaDTO;		
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
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
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
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
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
			
				//System.out.println("Inicializando chamada a: " + endPoint);
				//System.out.println("-----------------------------------------------------");

				int statusCode = client.executeMethod(method);

				//System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));			

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
//					System.out.println("userMessage: "+userMessage);
//					System.out.println("token: \n"+token);		
//					System.out.println("id: "+id);
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
			
				//System.out.println("Inicializando chamada a: " + endPoint);
				//System.out.println("-----------------------------------------------------");
	
				int statusCode = client.executeMethod(method);
	
				//System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));						
	
				String jsonResponse = method.getResponseBodyAsString();
	
				JSONParser parser = new JSONParser();
				
				JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
				
				//System.out.println(jsonObject.toString());				
											
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
	
}
