package br.com.reisdaresenha.control;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.service.ParametroServiceLocal;

/**
 * @author Renan Celso
 */
@ManagedBean(name = "ligaRDRControl")
@ViewScoped
public class LigaRDRControl extends BaseControl {

	private static final long serialVersionUID = 4471049778894375957L;	
	
	private transient Logger log = Logger.getLogger(LigaRDRControl.class.getName());
	
	@EJB
	private ParametroServiceLocal parametroService;
		
	private Usuario usuarioLogado;	
	
	private String nomeLiga;
	
	@PostConstruct
	public void init() {		
		try {			
			
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);	
			usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");			
			nomeLiga = parametroService.buscarParametroPorChave("nome_liga").getValor();
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	public Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(Usuario usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public String getNomeLiga() {
		return nomeLiga;
	}

	public void setNomeLiga(String nomeLiga) {
		this.nomeLiga = nomeLiga;
	}
}
