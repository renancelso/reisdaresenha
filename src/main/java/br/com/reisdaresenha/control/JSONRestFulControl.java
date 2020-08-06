package br.com.reisdaresenha.control;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.rest.JSONRestFulClient;
import br.com.reisdaresenha.service.ParametroServiceLocal;
import br.com.reisdaresenha.service.TimeServiceLocal;

/**
 * @author Renan Celso
 */
@ManagedBean(name = "jsonRestFulControl")
@ViewScoped
public class JSONRestFulControl {

	@EJB
	private ParametroServiceLocal parametroService;
	
	@EJB
	private TimeServiceLocal timeService;
	
	private JSONRestFulClient jsonRestFulClient;
	
	private CartolaRestFulClient servicoCartola;
	
	private transient Logger log = Logger.getLogger(JSONRestFulControl.class.getName());
	
	@PostConstruct
	public void init() {
		try {			
			jsonRestFulClient = new JSONRestFulClient();
			servicoCartola = new CartolaRestFulClient();
		} catch (Exception e) {
			log.error("Erro no método init "+e.getMessage());			
		}
	}		
	
	public String buscarTime(String nomeTimeNoCartola) {		
		
		jsonRestFulClient = new JSONRestFulClient();
		servicoCartola = new CartolaRestFulClient();
		
		return jsonRestFulClient.buscarTime(nomeTimeNoCartola);	
	}
	
	public String buscarTimeRodadaPorIDCartola(Long idTimeCartola, Long nrRodada) {
		
		jsonRestFulClient = new JSONRestFulClient();
		servicoCartola = new CartolaRestFulClient();
		
		Time time = new Time();		
		time = timeService.buscarTimePorIdCartola(idTimeCartola);		
		return jsonRestFulClient.buscarTimeRodadaPorIDCartola(time, nrRodada);
	}
	
	public String buscarInformacoesLigaEspecifica() {
		jsonRestFulClient = new JSONRestFulClient();
		servicoCartola = new CartolaRestFulClient();
		
		String email = parametroService.buscarParametroPorChave("user_email").getValor();			
		String senha = parametroService.buscarParametroPorChave("user_senha").getValor();			
		
		String slugLiga = servicoCartola.buscarSlugDaLiga(parametroService.buscarParametroPorChave("nome_liga").getValor());		
		
		String token = servicoCartola.gerarTokenLoginCartola(email, senha);	
		
		return jsonRestFulClient.buscarInformacoesLigaEspecifica(slugLiga, token);				
		
	}
	
	
}
