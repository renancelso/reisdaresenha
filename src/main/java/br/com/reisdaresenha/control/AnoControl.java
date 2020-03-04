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

import br.com.reisdaresenha.model.Ano;
import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.service.InicioServiceLocal;


/**
 * @author Renan Celso
 */
@SuppressWarnings("deprecation")
@ManagedBean(name = "anoControl")
@ViewScoped
public class AnoControl extends BaseControl {

	private static final long serialVersionUID = 4312269786019341771L;

	private transient Logger log = Logger.getLogger(AnoControl.class.getName());
	
	@EJB
	private InicioServiceLocal inicioService; 
	
	private Ano anoCadastrar;
	
	private List<Ano> listaAno;
	
	
	@PostConstruct
	public void init() {
		try {			
			buscarAnosCadastrados();
			anoCadastrar = new Ano();	
		} catch (Exception e) {
			log.error("Erro no método init "+e.getMessage());			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void buscarAnosCadastrados() {
		listaAno = new ArrayList<>();
		listaAno = (List<Ano>) inicioService.consultarTodos(Ano.class, " order by o.id desc");		
	}

	public String cadastrarAno() {		
		try {					
			if(anoCadastrar.getId() == null || (anoCadastrar.getId() != null && anoCadastrar.getId() == 0)) {
				addErrorMessage("Ano inválido");
				return null;
			}		
			
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			Usuario usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");
			
			anoCadastrar.setIdUserAtu(usuarioLogado.getId().toString());
			anoCadastrar.setLoginUserAtu(usuarioLogado.getLogin());
			anoCadastrar.setDhAtu(new Date());		
			
			anoCadastrar = (Ano) inicioService.atualizar(anoCadastrar);				
			
			buscarAnosCadastrados();
			
			addInfoMessage("Ano "+anoCadastrar.getId()+" cadastrado com Sucesso");
			
			anoCadastrar = new Ano();
			
			return null;
		} catch (Exception e) {
			log.error("Erro ao cadastrar ano "+e.getMessage());
			addErrorMessage("Erro ao cadastrar ano "+e.getMessage());
			return null;
		}
	}
		
	public Ano getAnoCadastrar() {
		return anoCadastrar;
	}

	public void setAnoCadastrar(Ano anoCadastrar) {
		this.anoCadastrar = anoCadastrar;
	}

	public List<Ano> getListaAno() {
		return listaAno;
	}

	public void setListaAno(List<Ano> listaAno) {
		this.listaAno = listaAno;
	}
	
	
	
	
}
