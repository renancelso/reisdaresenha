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

import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.service.InicioServiceLocal;

@ManagedBean(name = "restFulTestServicesControl")
@ViewScoped
public class RestFulTestServicesControl  extends BaseControl {
	
	private static final long serialVersionUID = -8812812855117667934L;

	@EJB
	private InicioServiceLocal inicioService; 

	private CartolaRestFulClient servicoCartola;
	
	private List<Time> listaTimesParticipantes;	
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {		
		listaTimesParticipantes = new ArrayList<>();
		listaTimesParticipantes = (List<Time>) inicioService.consultarTodos(Time.class, " order by o.nomeDonoTime, o.nomeTime ");	
	}	
	
	public String atualizarInfosTimesCartola() {
		try{ 
			
			servicoCartola = new CartolaRestFulClient(); 
			
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			Usuario usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");			
			
			for (Time time : listaTimesParticipantes) {			
				
				Time timeCartola = new Time();			
				timeCartola= servicoCartola.buscarTime(time.getNomeTime());
				
				time.setNomeDonoTime(timeCartola.getNomeDonoTime());
				time.setIdCartola(timeCartola.getIdCartola());
				time.setFotoPerfil(timeCartola.getFotoPerfil());
				time.setUrlEscudoPng(timeCartola.getUrlEscudoPng());
				time.setUrlEscudoSvg(timeCartola.getUrlEscudoSvg());
				time.setAssinante(timeCartola.getAssinante());
				time.setSlugTime(timeCartola.getSlugTime());
				time.setFacebookId(timeCartola.getFacebookId());	
							
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

	public List<Time> getListaTimesParticipantes() {
		return listaTimesParticipantes;
	}

	public void setListaTimesParticipantes(List<Time> listaTimesParticipantes) {
		this.listaTimesParticipantes = listaTimesParticipantes;
	}
}
