package br.com.reisdaresenha.control;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import br.com.reisdaresenha.model.Ano;
import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.service.InicioServiceLocal;


/**
 * @author Renan Celso
 */
@SuppressWarnings("deprecation")
@ManagedBean(name = "ligaControl")
@ViewScoped
public class LigaControl extends BaseControl {

	private static final long serialVersionUID = 5863488645598931213L;

	private transient Logger log = Logger.getLogger(LigaControl.class.getName());
	
	@EJB
	private InicioServiceLocal inicioService; 
	
	private Liga ligaCadastrar;
	
	private List<Liga> listaLigas;
		
	@PostConstruct
	public void init() {
		try {			
			buscarLigasCadastradas();
			ligaCadastrar = new Liga();	
		} catch (Exception e) {
			log.error("Erro no método init "+e.getMessage());			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void buscarLigasCadastradas() {
		listaLigas = new ArrayList<>();
		listaLigas = (List<Liga>) inicioService.consultarTodos(Liga.class, " order by o.id asc");		
	}

	public String cadastrarLiga() {		
		try {		
			
			if(ligaCadastrar.getNomeLiga() == null || "".equalsIgnoreCase(ligaCadastrar.getNomeLiga())) {				
				addErrorMessage("O Nome da liga é um campo obrigatório");
				return null;
			}	
			
			Ano anoAtual = new Ano();			
			anoAtual =  (Ano) inicioService.consultarPorChavePrimaria(anoAtual, new Long(Calendar.getInstance().get(Calendar.YEAR)));	
			
			if(anoAtual == null) {
				addErrorMessage("É necessário efetuar o cadastro do ano atual ("+Calendar.getInstance().get(Calendar.YEAR)+") para cadastrar uma liga.");
				return null;
			}
			
			ligaCadastrar.setAno(anoAtual);			
						
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			Usuario usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");						
						
			ligaCadastrar.setIdUserAtu(usuarioLogado.getId().toString());
			ligaCadastrar.setLoginUserAtu(usuarioLogado.getLogin());
			ligaCadastrar.setDhAtu(new Date());		
			
			ligaCadastrar = (Liga) inicioService.atualizar(ligaCadastrar);
				
			buscarLigasCadastradas();
			
			addInfoMessage("Liga "+ligaCadastrar.getNomeLiga()+" cadastrada com Sucesso");
			
			ligaCadastrar = new Liga();
			
			return null;
		} catch (Exception e) {
			log.error("Erro ao cadastrar ano "+e.getMessage());
			addErrorMessage("Erro ao cadastrar ano "+e.getMessage());
			return null;
		}
	}

	public Liga getLigaCadastrar() {
		return ligaCadastrar;
	}

	public void setLigaCadastrar(Liga ligaCadastrar) {
		this.ligaCadastrar = ligaCadastrar;
	}

	public List<Liga> getListaLigas() {
		return listaLigas;
	}

	public void setListaLigas(List<Liga> listaLigas) {
		this.listaLigas = listaLigas;
	}
	
	
}
