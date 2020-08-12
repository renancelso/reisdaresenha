package br.com.reisdaresenha.control;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.com.reisdaresenha.model.Rodada;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.service.InicioServiceLocal;
import br.com.reisdaresenha.view.ClassificacaoLigaPrincipalDTO;
import br.com.reisdaresenha.view.TimeRodadaDTO;

@ManagedBean(name = "restFulTestServicesControl")
@ViewScoped
public class RestFulTestServicesControl  extends BaseControl {
	
	private static final long serialVersionUID = -8812812855117667934L;

	@EJB
	private InicioServiceLocal inicioService; 

	private CartolaRestFulClient servicoCartola;
	
	private List<Time> listaTimesParticipantes;	
	
	private List<Rodada> listaHistoricoRodadas;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {		
		
		listaTimesParticipantes = new ArrayList<>();
		listaTimesParticipantes = (List<Time>) inicioService.consultarTodos(Time.class, " order by o.nomeTime, o.nomeDonoTime ");			
		
		listaHistoricoRodadas = new ArrayList<Rodada>();
		listaHistoricoRodadas = (List<Rodada>) inicioService.consultarTodos(Rodada.class, " order by o.nrRodada desc");
		
	}	
	
	public String atualizarInfosTimesCartola() {
		try{ 
			
			servicoCartola = new CartolaRestFulClient(); 
			
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			Usuario usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");			
			
			for (Time time : listaTimesParticipantes) {			
				
				TimeRodadaDTO timeCartola = new TimeRodadaDTO();			
				timeCartola = servicoCartola.buscarTimeRodadaPorIDCartola(time, new Long(0));
				
				time.setNomeDonoTime(timeCartola.getTime().getNomeDonoTime());
				time.setIdCartola(timeCartola.getTime().getIdCartola());
				time.setFotoPerfil(timeCartola.getTime().getFotoPerfil());
				time.setUrlEscudoPng(timeCartola.getTime().getUrlEscudoPng());
				time.setUrlEscudoSvg(timeCartola.getTime().getUrlEscudoSvg());
				time.setAssinante(timeCartola.getTime().getAssinante());
				time.setSlugTime(timeCartola.getTime().getSlugTime());
				time.setFacebookId(timeCartola.getTime().getFacebookId());	
							
				time.setIdUserAtu(usuarioLogado.getId().toString());
				time.setLoginUserAtu(usuarioLogado.getLogin());
				time.setDhAtu(new Date());	
				
				inicioService.atualizar(time);
			}
			
			addInfoMessage("Times sincronizados com sucesso.");
			
		} catch (Exception e) {
			
			addErrorMessage("Erro ao atualizar times. "+e.getMessage());			
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<ClassificacaoLigaPrincipalDTO> listarClassificacaoHistoricoRodadas(Long nrRodada){
		
		try {		
			List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<ClassificacaoLigaPrincipalDTO>();
			
			Integer anoAtual = 2020;//Calendar.getInstance().get(Calendar.YEAR);	
			
			listaClassificacaoLigaPrincipalDTO = inicioService.buscarHistoricoClassificacaoRodadas(anoAtual, nrRodada);		
			
			return listaClassificacaoLigaPrincipalDTO;	
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ClassificacaoLigaPrincipalDTO>();
		}
	}
	
	public String mostrarLog() {			
		StringBuilder logRetorno = new StringBuilder();		
		try {
	    	BufferedReader br = new BufferedReader(new FileReader("/home/reisdaresenha/appservers/wildfly-17.0.1.Final/standalone/log/server.log"));
	    	while(br.ready()){
	    		String linha = br.readLine();
	    		logRetorno.append(linha).append("\n");	    		
	    	}
	    	br.close();
	    } catch (IOException e) {
	        System.err.printf("Erro na abertura do arquivo: %s.\n",
	        e.getMessage());
	    }		
		return logRetorno.toString();
	}

	public List<Time> getListaTimesParticipantes() {
		return listaTimesParticipantes;
	}

	public void setListaTimesParticipantes(List<Time> listaTimesParticipantes) {
		this.listaTimesParticipantes = listaTimesParticipantes;
	}

	public List<Rodada> getListaHistoricoRodadas() {
		return listaHistoricoRodadas;
	}

	public void setListaHistoricoRodadas(List<Rodada> listaHistoricoRodadas) {
		this.listaHistoricoRodadas = listaHistoricoRodadas;
	}
}
