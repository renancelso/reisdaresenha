package br.com.reisdaresenha.rest;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//https://api.cartolafc.globo.com/mercado/status
//http://localhost:8080/rest/exemplo/get
//https://api.cartolafc.globo.com/atletas/mercado
//https://api.cartolafc.globo.com/patrocinadores
//https://reisdaresenha.com.br/rest/exemplo/get
public class ExemploClientRestGet {

	public static void main(String[] args) {
		
		try {
			
			String time = "GagoShow%20FR";			
				
			String endPoint = "https://api.cartolafc.globo.com/times?q="+time;
			
			HttpClient client = new HttpClient();

			GetMethod method = new GetMethod(endPoint);
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
				
			try {
			
				System.out.println("Inicializando chamada a: " + endPoint);
				System.out.println("-----------------------------------------------------");

				int statusCode = client.executeMethod(method);

				System.out.println("Status Code = " + statusCode);
				System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));

				// System.out.println("Retorno: "+method.getResponseBodyAsString());

				String jsonResponse = method.getResponseBodyAsString();

				JSONParser parser = new JSONParser();
				
				JSONArray jsonArray = (JSONArray) parser.parse(jsonResponse);		
				
				JSONObject jsonObject = (JSONObject) jsonArray.get(0);
				
				String nomeCartola = (String) jsonObject.get("nome_cartola");				
				Long timeIdCartola = (Long) jsonObject.get("time_id");				
				String fotoPerfil = (String) jsonObject.get("foto_perfil");
				String urlEscudoPng = (String) jsonObject.get("url_escudo_png");				
				String urlEscudoSvg = (String) jsonObject.get("url_escudo_svg");				
				Boolean assinante = (Boolean) jsonObject.get("assinante");
				String slug = (String) jsonObject.get("slug");
				Long facebookId = (Long) jsonObject.get("facebook_id");				
				
								
				method.releaseConnection();

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			
			e.printStackTrace();			
		}
	}

}
