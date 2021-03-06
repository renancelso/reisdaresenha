package br.com.reisdaresenha.padrao;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

import br.com.reisdaresenha.model.Parametro;
import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.service.ParametroServiceLocal;

/**
 *
 * @author Renan Celso
 *
 */

@ManagedBean(name = "sessionControl")
@SessionScoped
public class SessionControl extends BaseControl {

	private static final long serialVersionUID = 7314625690695764468L;

	private transient Logger log = Logger.getLogger(SessionControl.class.getName());
	
	@EJB
	private ParametroServiceLocal parametroService;

	/**
	 *
	 * @return Retorna o tempo em milisegundos necessario para realizar o logout da
	 *         aplicacao.
	 */
	public int getSessionTimeout() {
		return (FacesContext.getCurrentInstance().getExternalContext().getSessionMaxInactiveInterval()) * 1000;
	}

	public Object getSessionAttribute(String name) {
		Object result = null;
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		if (session != null) {
			result = session.getAttribute(name);
		}
		return result;
	}

	public Usuario usuarioLogado() {
		Object o = getSessionAttribute("usuarioLogado");
		return o != null ? (Usuario) o : null;
	}

	/**
	 * Invalida a sessao e redireciona para pagina de login.
	 *
	 * @return
	 */
	public String invalidateSession() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		log.info("Invalidando Sessăo e fazendo logout!");
		// redirect("/cadastro-perito");
		session.invalidate();
		PrimeFaces.current().executeScript("window.location.reload();");
		return null;
	}

	public String deslogar() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		// redirect("/cadastro-perito");
		session.invalidate();
		PrimeFaces.current().executeScript("window.location.reload();");
		return null;
	}

	public String refresh() {
		PrimeFaces.current().executeScript("window.location.reload();");
		return null;
	}

	/**
	 * Renova a sessao.
	 *
	 */
	public String renovarSessao() {
		log.info("Renovando Sessăo!");
		return null;
	}

	public String getContexto() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		return request.getSession().getServletContext().getContextPath();
	}

	public String getUrlAtual() {
		StringBuilder url = new StringBuilder();
		url.append(FacesContext.getCurrentInstance().getExternalContext().getRequestServerName()).append(":");
		url.append(FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort()).append("/");
		url.append(FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath());
		return url.toString();
	}
	
	public String getLogoLiga() {
		try {
			
			//CartolaRestFulClient servicoCartola = new CartolaRestFulClient();			
			Parametro param = parametroService.buscarParametroPorChave("logo_liga");			
			
			String logo = param.getValor().trim();
			
			return logo != null ? logo : "publico/estilo/images/cartola00.png";		
			
		} catch (Exception e) {
			return "publico/estilo/images/cartola00.png";
		}
	}
	
	public String getNomeLiga() {
		try {				
			Parametro param = parametroService.buscarParametroPorChave("nome_liga");	
			return param.getValor().trim();
		} catch (Exception e) {
			return "publico/estilo/images/cartola00.png";
		}
	}

	public Logger getLogger() {
		return log;
	}

	public void init() {
		log.info("Init SessionControl");
	}
}
