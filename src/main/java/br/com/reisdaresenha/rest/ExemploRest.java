package br.com.reisdaresenha.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/exemplo")
public class ExemploRest {
	private List<String> list = new ArrayList<String>();

	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)	
	public List<String> helloGet() {
		list.add("Hello");
		list.add("Word");
		list.add("Get");
		return list;
	}
	
//	@POST
//	@Path("/post")
//	@Consumes(MediaType.APPLICATION_JSON)	
//	public Response helloPost() {
//		
//		list.add("Hello");
//		list.add("Word");
//		list.add("Post");		
//		return Response.status(201).entity(list).build();		
//	}
}
