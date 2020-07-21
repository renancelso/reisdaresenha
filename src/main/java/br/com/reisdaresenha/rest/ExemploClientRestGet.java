package br.com.reisdaresenha.rest;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//https://api.cartolafc.globo.com/mercado/status
//https://api.cartolafc.globo.com/atletas/mercado
//https://api.cartolafc.globo.com/patrocinadores
//https://api.cartolafc.globo.com/time/id/[id_time_cartola]/[rodada]
public class ExemploClientRestGet {

	public static void main(String[] args) {
		
		try {						
			
			String endPoint = "https://api.cartolafc.globo.com/ligas?q=RDR%202020";
			
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

				String jsonResponse = method.getResponseBodyAsString();

				JSONParser parser = new JSONParser();
				
				JSONArray jsonArray = (JSONArray) parser.parse(jsonResponse);		
				
				JSONObject jsonObject = (JSONObject) jsonArray.get(0);		
				
				String imagem = (String) jsonObject.get("imagem");
															
				method.releaseConnection();

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			
			e.printStackTrace();			
		}
	}

}
