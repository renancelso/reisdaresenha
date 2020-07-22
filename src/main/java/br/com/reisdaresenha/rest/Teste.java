package br.com.reisdaresenha.rest;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//https://api.cartolafc.globo.com/mercado/status
//https://api.cartolafc.globo.com/atletas/mercado
//https://api.cartolafc.globo.com/patrocinadores
//https://api.cartolafc.globo.com/time/id/[id_time_cartola]/[rodada]
public class Teste {

	public static void main(String[] args) {
		
		try {						
			
			String endPoint = "https://api.cartolafc.globo.com/auth/time/info";
			String token = "17095db237570f32a97e638e73f880b3f546252674a424e3847626266392d3467704378537a554d766a444268724254354171593651514b59324c534a33494d6a764e4b3668722d3064642d3567466c7a776d57776e6a76324d716d68633963396546532d6f413d3d3a303a72656e616e63656c736f";
			
			HttpClient client = new HttpClient();

			PostMethod method = new PostMethod(endPoint);
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");		
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");			
			method.setRequestHeader("X-GLB-Token", token);		
				
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
											
				method.releaseConnection();

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			
			e.printStackTrace();			
		}
	}

}
