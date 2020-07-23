package br.com.reisdaresenha.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.service.InicioServiceLocal;
import br.com.reisdaresenha.service.TimeServiceLocal;


/**
 * @author Renan Celso
 */

@ManagedBean(name = "timeControl")
@ViewScoped
public class TimeControl extends BaseControl {

	private static final long serialVersionUID = 2232607681331474877L;

	private transient Logger log = Logger.getLogger(TimeControl.class.getName());
	
	private CartolaRestFulClient servicoCartola;
	
	@EJB
	private InicioServiceLocal inicioService; 
	
	@EJB
	private TimeServiceLocal timeService; 
	
	private Time timeCadastrar;
	
	private Time timeSelecionado;
	
	private List<Time> listaTimes;	
		
	@PostConstruct
	public void init() {
		try {
			buscarTimesCadastrados();
			timeCadastrar = new Time();			
			timeCadastrar.setLiga(new Liga());
			timeSelecionado = null;
		} catch (Exception e) {
			log.error("Erro no método init "+e.getMessage());			
		}
	}
	
	public String btLimpar() {
		try {			
			init();
		} catch (Exception e) {
			log.error(e.getMessage());			
		}		
		return null;
	}
	
	public Time buscarTimeNoCartola(Time time, String nomeTime) {		
		try {			
			servicoCartola = new CartolaRestFulClient();
			
			Time timeCartola = new Time();
			timeCartola = servicoCartola.buscarTime(nomeTime);		
						
			time.setNomeDonoTime(timeCartola.getNomeDonoTime());
			time.setIdCartola(timeCartola.getIdCartola());
			time.setFotoPerfil(timeCartola.getFotoPerfil());
			time.setUrlEscudoPng(timeCartola.getUrlEscudoPng());
			time.setUrlEscudoSvg(timeCartola.getUrlEscudoSvg());
			time.setAssinante(timeCartola.getAssinante());
			time.setSlugTime(timeCartola.getSlugTime());
			time.setFacebookId(timeCartola.getFacebookId());	
			
			return time;
			
		} catch (Exception e) {
			log.error(e);
			addErrorMessage("Nao foi possivel encontrar o time "+nomeTime+" no Cartola");			
			return null;
		}		
	}
	
	public String btnConsultarTimeCartola(String nomeTime) {
		try {
				
			if(nomeTime == null || "".equalsIgnoreCase(nomeTime)) {
				addErrorMessage("Informe o nome do time para consultar no cartola.");
				return null;
			}
			
			servicoCartola = new CartolaRestFulClient();
			
			Time timeAux = new Time();
			timeAux = servicoCartola.buscarTime(nomeTime);	
			
			if(timeAux != null && timeAux.getIdCartola() != null) {						
				addInfoMessage("Time EXISTE: "+nomeTime+" ("+timeAux.getNomeDonoTime()+"). ID Cartola: "+timeAux.getIdCartola()+". Slug: "+timeAux.getSlugTime());				
				//return timeAux.getUrlEscudoSvg();						
			} else {
				addErrorMessage("Time "+nomeTime+" NAO EXISTE no cartola");
			}
			
		} catch (Exception e) {
			addErrorMessage("Time "+nomeTime+" NAO EXISTE no cartola");
		}
		
		return null;
	}
	
	public String cadastrarTime() {		
		try {	
			
			if(timeService.buscarTimePorNome(timeCadastrar.getNomeTime()) != null) {
				addErrorMessage("Time "+timeCadastrar.getNomeTime()+" já existe na base de dados.");				
				timeCadastrar = new Time();
				return null;
			}
			
			if(timeCadastrar.getLiga() == null || (timeCadastrar.getLiga() != null && timeCadastrar.getLiga().getId() == null)) {
				List<Liga>listaLigas = new ArrayList<>();
				listaLigas = buscarLigasCadastradas();			
				timeCadastrar.setLiga(new Liga());	
				if(listaLigas != null && !listaLigas.isEmpty()) {			
					for (Liga liga : listaLigas) {		
						if(liga.getNomeLiga().toUpperCase().contains("PRINCIPAL")) {
							timeCadastrar.setLiga(liga);
							break;
						} 					
					}
				}
			}				
												
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			Usuario usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");						
						
			timeCadastrar.setIdUserAtu(usuarioLogado.getId().toString());
			timeCadastrar.setLoginUserAtu(usuarioLogado.getLogin());
			timeCadastrar.setDhAtu(new Date());		
						
			/** Buscar no restful do Cartola **/
			timeCadastrar = buscarTimeNoCartola(timeCadastrar, timeCadastrar.getNomeTime());
			
			if(timeCadastrar == null) {
				timeCadastrar = new Time();
				return null;
			}
			
			if(timeCadastrar.getNomeTime() == null) {
				addErrorMessage("Nome do time é um campo obrigatório");
				return null;
			}	
			
			if(timeCadastrar.getNomeDonoTime() == null) {
				addErrorMessage("Dono do time é um campo obrigatório");
				return null;
			}				
			
			if("P".equalsIgnoreCase(timeCadastrar.getStatusPagamento()) 
					&& (timeCadastrar.getValorPago() == null || timeCadastrar.getValorPago() <= 0.0)) {
				timeCadastrar.setValorPago(250.0);
			}
			
			timeCadastrar = (Time) inicioService.atualizar(timeCadastrar);			
						
			buscarTimesCadastrados();
			
			addInfoMessage("Time '"+timeCadastrar.getNomeTime()+" ("+timeCadastrar.getNomeDonoTime()+")' cadastrado com sucesso.");
			
			timeCadastrar = new Time();	
			timeCadastrar.setLiga(new Liga());
			timeSelecionado = null;
			
			return null;
				
			
		} catch (Exception e) {
			log.error("Erro ao cadastrar Time "+e.getMessage());
			addErrorMessage("Erro ao cadastrar Time "+e.getMessage());
			return null;
		}
	}
	
	
	public String excluirTime() {
	
		if(inicioService.remover(timeSelecionado)) {
			
			buscarTimesCadastrados();		
			addInfoMessage("Time excluido com sucesso.");			
			timeCadastrar = new Time();	
			timeCadastrar.setLiga(new Liga());
			timeSelecionado = null;
			
		} else {
			addErrorMessage("Erro ao excluir Time");
		}
		
		return null;
	}
		
	public String alterarTime() {		
		try {	
			
			if(timeSelecionado.getLiga() == null) {
				List<Liga>listaLigas = new ArrayList<>();
				listaLigas = buscarLigasCadastradas();			
				timeSelecionado.setLiga(new Liga());	
				if(listaLigas != null && !listaLigas.isEmpty()) {			
					for (Liga liga : listaLigas) {		
						if(liga.getNomeLiga().toUpperCase().contains("PRINCIPAL")) {
							timeSelecionado.setLiga(liga);	
							break;
						} 					
					}
				}
			}				
												
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			Usuario usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");						
						
			timeSelecionado.setIdUserAtu(usuarioLogado.getId().toString());
			timeSelecionado.setLoginUserAtu(usuarioLogado.getLogin());
			timeSelecionado.setDhAtu(new Date());	
			
			timeSelecionado = buscarTimeNoCartola(timeSelecionado, timeSelecionado.getNomeTime());
			
			if(timeSelecionado == null) {
				return null;
			}
			
			if(timeSelecionado.getNomeDonoTime() == null) {
				addErrorMessage("Dono do time é um campo obrigatório");
				return null;
			}
			
			if(timeSelecionado.getNomeTime() == null) {
				addErrorMessage("Nome do time é um campo obrigatório");
				return null;
			}		
			
			timeSelecionado = (Time) inicioService.atualizar(timeSelecionado);			
						
			buscarTimesCadastrados();
			
			addInfoMessage("Time '"+timeSelecionado.getNomeTime()+"("+timeSelecionado.getNomeDonoTime()+")' alterado com sucesso.");
			
			timeCadastrar = new Time();	
			timeCadastrar.setLiga(new Liga());
			timeSelecionado = null;
			
			return null;			
		} catch (Exception e) {
			log.error("Erro ao alterar Time "+e.getMessage());
			addErrorMessage("Erro ao alterar Time "+e.getMessage());
			return null;
		}
	}
	
	public void onRowSelect(SelectEvent event) {
		timeCadastrar = null;
		timeSelecionado = new Time();
		timeSelecionado = (Time) event.getObject();  	
    }
	
	@SuppressWarnings("unchecked")
	private List<Liga> buscarLigasCadastradas() {		
		return (List<Liga>) inicioService.consultarTodos(Liga.class, " order by o.id asc");		
	}
	
	@SuppressWarnings("unchecked")
	private void buscarTimesCadastrados() {
		listaTimes = new ArrayList<>();
		listaTimes = (List<Time>) inicioService.consultarTodos(Time.class, " order by o.nomeTime, o.nomeDonoTime ");		
	}

	public Time getTimeCadastrar() {
		return timeCadastrar;
	}

	public void setTimeCadastrar(Time timeCadastrar) {
		this.timeCadastrar = timeCadastrar;
	}

	public Time getTimeSelecionado() {
		return timeSelecionado;
	}

	public void setTimeSelecionado(Time timeSelecionado) {
		this.timeSelecionado = timeSelecionado;
	}

	public List<Time> getListaTimes() {
		return listaTimes;
	}

	public void setListaTimes(List<Time> listaTimes) {
		this.listaTimes = listaTimes;
	}
	
}
