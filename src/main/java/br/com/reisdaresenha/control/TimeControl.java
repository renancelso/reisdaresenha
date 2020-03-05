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
import br.com.reisdaresenha.model.Premiacao;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.service.InicioServiceLocal;


/**
 * @author Renan Celso
 */
@SuppressWarnings("deprecation")
@ManagedBean(name = "timeControl")
@ViewScoped
public class TimeControl extends BaseControl {

	private static final long serialVersionUID = 2232607681331474877L;

	private transient Logger log = Logger.getLogger(TimeControl.class.getName());
	
	@EJB
	private InicioServiceLocal inicioService; 
	
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
	
	public String cadastrarTime() {		
		try {	
			
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
			
			if(timeCadastrar.getNomeDonoTime() == null) {
				addErrorMessage("Dono do time é um campo obrigatório");
				return null;
			}
			
			if(timeCadastrar.getNomeTime() == null) {
				addErrorMessage("Nome do time é um campo orbigatório");
				return null;
			}			
												
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			Usuario usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");						
						
			timeCadastrar.setIdUserAtu(usuarioLogado.getId().toString());
			timeCadastrar.setLoginUserAtu(usuarioLogado.getLogin());
			timeCadastrar.setDhAtu(new Date());		
			
			
			timeCadastrar = (Time) inicioService.atualizar(timeCadastrar);			
						
			buscarTimesCadastrados();
			
			addInfoMessage("Time '"+timeCadastrar.getNomeTime()+"("+timeCadastrar.getNomeDonoTime()+")' cadastrado com sucesso.");
			
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
			
			if(timeSelecionado.getNomeDonoTime() == null) {
				addErrorMessage("Dono do time é um campo obrigatório");
				return null;
			}
			
			if(timeSelecionado.getNomeTime() == null) {
				addErrorMessage("Nome do time é um campo orbigatório");
				return null;
			}			
												
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			Usuario usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");						
						
			timeSelecionado.setIdUserAtu(usuarioLogado.getId().toString());
			timeSelecionado.setLoginUserAtu(usuarioLogado.getLogin());
			timeSelecionado.setDhAtu(new Date());		
			
			
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
		listaTimes= new ArrayList<>();
		listaTimes = (List<Time>) inicioService.consultarTodos(Time.class, " order by o.nomeDonoTime, o.nomeTime ");		
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
