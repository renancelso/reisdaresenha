package br.com.reisdaresenha.rest;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

//https://api.cartolafc.globo.com/mercado/status
//http://localhost:8080/rest/exemplo/get
//https://api.cartolafc.globo.com/atletas/mercado
//https://api.cartolafc.globo.com/patrocinadores
//https://reisdaresenha.com.br/rest/exemplo/get
public class ExemploClientRestGet {

	public static void main(String[] args) {
				
		try {

			TrustManager[] trustManager = new X509TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {

                }
            }};

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManager, null);
            
            Client client = ClientBuilder.newBuilder().sslContext(sslContext).build();
            WebTarget target = client.target("https://reisdaresenha.com.br/rest/exemplo/get");
           
            Response response = target.request().get();
            
            String value = response.readEntity(String.class);
            
            System.out.println("RETORNO: "+value);
            
            response.close();  

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

}
