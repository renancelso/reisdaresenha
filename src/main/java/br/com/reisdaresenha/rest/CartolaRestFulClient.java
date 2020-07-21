package br.com.reisdaresenha.rest;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import br.com.reisdaresenha.model.Time;

/**
 * 
 * @author Renan Celso, em 21/07/2020
 *
 */
public class CartolaRestFulClient {

	
	public Time buscarTime(String nomeTimeNoCartola) {
		
		Time time = new Time();
		
		try {	
			nomeTimeNoCartola = nomeTimeNoCartola.replace(" ", "%20");
			
			String endPoint = "https://api.cartolafc.globo.com/times?q="+nomeTimeNoCartola;		
						
			HttpClient client = new HttpClient();
	
			GetMethod method = new GetMethod(endPoint);
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			
			System.out.println("Inicializando chamada a: " + endPoint);
			System.out.println("-----------------------------------------------------");

			int statusCode = client.executeMethod(method);

			System.out.println("Status Code = " + statusCode);
			System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));			

			String jsonResponse = method.getResponseBodyAsString();

			JSONParser parser = new JSONParser();
			
			JSONArray jsonArray = (JSONArray) parser.parse(jsonResponse);		
			
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
			
			method.releaseConnection();
			
			return time;		

		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}		
		
		return time;		
	}
	
}
