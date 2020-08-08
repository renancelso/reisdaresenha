package br.com.reisdaresenha.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.reisdaresenha.model.Rodada;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.rest.JSONRestFulClient;
import br.com.reisdaresenha.service.ParametroServiceLocal;
import br.com.reisdaresenha.service.TimeServiceLocal;

/**
 * @author Renan Celso
 */
@ManagedBean(name = "jsonRestFulControl")
@ViewScoped
public class JSONRestFulControl extends BaseControl{
	
	private static final long serialVersionUID = -2378287215058061868L;

	private transient Logger log = Logger.getLogger(JSONRestFulControl.class.getName());

	@EJB
	private ParametroServiceLocal parametroService;
	
	@EJB
	private TimeServiceLocal timeService;
	
	private JSONRestFulClient jsonRestFulClient;
	
	private CartolaRestFulClient servicoCartola;
	
	private List<Time> listaTimes;
	
	private Time timeSelecionado;
	
	private Long rodadaSelecionada;
	
	private String jsonTimeSelecionado;
	
	private List<Rodada> listaRodadas;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		
		try {
			
			jsonRestFulClient = new JSONRestFulClient();
			servicoCartola = new CartolaRestFulClient();
			
			timeSelecionado = new Time();
			
			listaTimes = new ArrayList<Time>();
			listaTimes = (List<Time>) timeService.consultarTodos(Time.class, " order by o.nomeTime, o.nomeDonoTime ");
			
			listaRodadas = new ArrayList<Rodada>();
			listaRodadas = (List<Rodada>) timeService.consultarTodos(Rodada.class," order by o.nrRodada desc ");
			
			if(listaRodadas != null && !listaRodadas.isEmpty()) {
				
				if("EA".equalsIgnoreCase(listaRodadas.get(0).getStatusRodada())){
					
					rodadaSelecionada = listaRodadas.get(0).getNrRodada();
					
				} else if("PS".equalsIgnoreCase(listaRodadas.get(0).getStatusRodada())){
					
					rodadaSelecionada = listaRodadas.get(0).getNrRodada()+1;
					
				} else {
					
					rodadaSelecionada = listaRodadas.get(0).getNrRodada();
					
				}
				
			} else {
				rodadaSelecionada = new Long(1);
			}
			
		} catch (Exception e) {
			log.error("Erro no método init "+e.getMessage());			
		}
		
	}		
	
	public String buscarTime(String nomeTimeNoCartola) {		
		
		jsonRestFulClient = new JSONRestFulClient();
		servicoCartola = new CartolaRestFulClient();
		
		return jsonRestFulClient.buscarTime(nomeTimeNoCartola);	
	}
	
	public String buscarTimeRodadaPorIDCartola() {
		
		jsonRestFulClient = new JSONRestFulClient();
		servicoCartola = new CartolaRestFulClient();
		
		if(rodadaSelecionada == null) {
			rodadaSelecionada = new Long(0);
		}
		
		if(timeSelecionado != null) {					
			
			jsonTimeSelecionado = jsonRestFulClient.buscarTimeRodadaPorIDCartola(timeSelecionado, rodadaSelecionada);	
		
		} else {
			
			StringBuilder jsonTimeSelecionadoBuilder = new StringBuilder();
			
			for (Time time : listaTimes) {
				jsonTimeSelecionado = jsonRestFulClient.buscarTimeRodadaPorIDCartola(time, rodadaSelecionada);	
				jsonTimeSelecionadoBuilder.append("-- "+time.getNomeTime()+"\n").append(jsonTimeSelecionado).append("\n\n");
			}	
			
			jsonTimeSelecionado = jsonTimeSelecionadoBuilder.toString();
			
		}
		
		return null;
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
	
	public String onChangeTime() {
		
		return null;
	}

	public List<Time> getListaTimes() {
		return listaTimes;
	}

	public void setListaTimes(List<Time> listaTimes) {
		this.listaTimes = listaTimes;
	}

	public Time getTimeSelecionado() {
		return timeSelecionado;
	}

	public void setTimeSelecionado(Time timeSelecionado) {
		this.timeSelecionado = timeSelecionado;
	}

	public Long getRodadaSelecionada() {
		return rodadaSelecionada;
	}

	public void setRodadaSelecionada(Long rodadaSelecionada) {
		this.rodadaSelecionada = rodadaSelecionada;
	}

	public String getJsonTimeSelecionado() {
		return jsonTimeSelecionado;
	}

	public void setJsonTimeSelecionado(String jsonTimeSelecionado) {
		this.jsonTimeSelecionado = jsonTimeSelecionado;
	}

	public List<Rodada> getListaRodadas() {
		return listaRodadas;
	}

	public void setListaRodadas(List<Rodada> listaRodadas) {
		this.listaRodadas = listaRodadas;
	}
	
	
}
