package br.com.reisdaresenha.rest;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//https://api.cartolafc.globo.com/mercado/status
//https://api.cartolafc.globo.com/atletas/mercado
//https://api.cartolafc.globo.com/patrocinadores
//https://api.cartolafc.globo.com/time/id/[id_time_cartola]/[rodada]
public class _ExemploClientRestGet {

	public static void main(String[] args) {
		
		try {						
			
			String endPoint = "https://login.globo.com/api/authentication";
			
			HttpClient client = new HttpClient();

			PostMethod method = new PostMethod(endPoint);
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
				
			String payload= "{\"payload\": {\"email\":\"email\",\"password\":\"senha\",\"serviceId\":4728}}";
			
			method.setRequestEntity(new StringRequestEntity(payload, "application/json", "UTF-8"));
					
			try {
			
				System.out.println("Inicializando chamada a: " + endPoint);
				System.out.println("-----------------------------------------------------");

				int statusCode = client.executeMethod(method);

				System.out.println("Status Code = " + statusCode);
				System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));				

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
					System.out.println("token: \n"+token);					
				}
											
				method.releaseConnection();

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {			
			e.printStackTrace();			
		}
	}

}
