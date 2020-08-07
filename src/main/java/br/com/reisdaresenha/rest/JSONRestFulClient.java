package br.com.reisdaresenha.rest;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import br.com.reisdaresenha.model.Time;

public class JSONRestFulClient {

	public String buscarTime(String nomeTimeNoCartola) {
		
		nomeTimeNoCartola = nomeTimeNoCartola.replace(" ", "%20");
	
		String endPoint = "https://api.cartolafc.globo.com/times?q="+nomeTimeNoCartola;		
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(endPoint);
		
		try {	
							
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");						
			
			int statusCode = client.executeMethod(method);
			
			//System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));
				
			String jsonResponse = method.getResponseBodyAsString();
					
			JsonParser parser = new JsonParser();
			Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
			JsonElement el = parser.parse(jsonResponse);
			String jsonString = gson.toJson(el);			
			
			return jsonString;

		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}  finally {
			method.releaseConnection();
		}	
		
		return null;
	}
	
	public String buscarTimeRodadaPorIDCartola(Time time, Long nrRodada) {
				
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
			
			int statusCode = client.executeMethod(method);			
			//System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));				
			String jsonResponse = method.getResponseBodyAsString();					
			JsonParser parser = new JsonParser();
			Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create(); //new GsonBuilder().setPrettyPrinting().create();			
			JsonElement el = parser.parse(jsonResponse);
			String jsonString = gson.toJson(el);			
			
			return jsonString;

		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}  finally {
			method.releaseConnection();
		}		
		
		return null;
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
			
			int statusCode = client.executeMethod(method);
			
			//System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));
				
			String jsonResponse = method.getResponseBodyAsString();
					
			JsonParser parser = new JsonParser();
			Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
			JsonElement el = parser.parse(jsonResponse);
			String jsonString = gson.toJson(el);			
			
			return jsonString;

		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}  finally {
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
			
			int statusCode = client.executeMethod(method);
			
			//System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));
				
			String jsonResponse = method.getResponseBodyAsString();
					
			JsonParser parser = new JsonParser();
			Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
			JsonElement el = parser.parse(jsonResponse);
			String jsonString = gson.toJson(el);			
			
			return jsonString;

		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}  finally {
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
	
	public String buscarInformacoesLigaEspecifica(String slugLiga, String token) {
				
		String endPoint = "https://api.cartolafc.globo.com/auth/liga/"+slugLiga;			
				
		HttpClient client = new HttpClient();

		GetMethod method = new GetMethod(endPoint);
		method.setRequestHeader("Connection", "keep-alive");
		method.setRequestHeader("Accept", "*/*");					
		method.setRequestHeader("X-GLB-Token", token);		
				
		try {	
			
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");						
			
			int statusCode = client.executeMethod(method);
			
			//System.out.println("Status Text >>> " + HttpStatus.getStatusText(statusCode));
				
			String jsonResponse = method.getResponseBodyAsString();
					
			JsonParser parser = new JsonParser();
			Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
			JsonElement el = parser.parse(jsonResponse);
			String jsonString = gson.toJson(el);			
			
			return jsonString;

		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}  finally {
			method.releaseConnection();
		}
		
		return null;
	}
}
