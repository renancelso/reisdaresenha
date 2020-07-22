package br.com.reisdaresenha.rest;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;

//https://api.cartolafc.globo.com/mercado/status
//https://api.cartolafc.globo.com/atletas/mercado
//https://api.cartolafc.globo.com/patrocinadores
//https://api.cartolafc.globo.com/time/id/[id_time_cartola]/[rodada]
public class ExemploClientRestGet {

	public static void main(String[] args) {
		
		try {						
			
			String endPoint = "https://login.globo.com/api/authentication";
			
			HttpClient client = new HttpClient();

			PostMethod method = new PostMethod(endPoint);
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept", "*/*");
			method.setRequestHeader("Content-type", "application/x-www-form-urlencoded");							  
			
			NameValuePair nvp1 = new NameValuePair("payload", "{\"payload\": {\"email\":\"renancelso@globo.com\",\"password\":\"04162003\",\"serviceId\":4728}}");						
			
			method.setRequestBody(new NameValuePair[]{nvp1});
			
			//{\"payload\": {\"email\":\"renancelso@globo.com\",\"password\":\"04162003\",\"serviceId\":4728}}
				
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
