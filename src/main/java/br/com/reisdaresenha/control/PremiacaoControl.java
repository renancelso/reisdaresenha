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
import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.service.InicioServiceLocal;


/**
 * @author Renan Celso
 */
@SuppressWarnings("deprecation")
@ManagedBean(name = "premiacaoControl")
@ViewScoped
public class PremiacaoControl extends BaseControl {

	private static final long serialVersionUID = 2232607681331474877L;

	private transient Logger log = Logger.getLogger(PremiacaoControl.class.getName());
	
	@EJB
	private InicioServiceLocal inicioService; 
	
	private Premiacao premiacaoCadastrar;
	
	private Premiacao premiacaoSelecionada;
	
	private List<Premiacao> listaPremiacoes;
	
	private List<Liga> listaLigas;
		
	@PostConstruct
	public void init() {
		try {			
			buscarLigasCadastradas();
			buscarPremiacoesCadastradas();
			premiacaoCadastrar = new Premiacao();			
			premiacaoCadastrar.setLiga(new Liga());
			premiacaoSelecionada = null;
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
	
	public String cadastrarPremiacao() {		
		try {		
			
			if(premiacaoCadastrar.getLiga() == null) {
				addErrorMessage("É necessário vincular uma Liga ao prêmio '"+premiacaoCadastrar.getDescricaoPremio()+"' para cadastrá-lo");
				return null;
			}
			
			if(premiacaoCadastrar.getDescricaoPremio() == null || "".equalsIgnoreCase(premiacaoCadastrar.getDescricaoPremio())) {				
				addErrorMessage("A descrição do premio é um campo obrigatório");
				return null;
			}	
			
			premiacaoCadastrar.setAno(premiacaoCadastrar.getLiga().getAno());		
						
			if(premiacaoCadastrar.getVrPremio() == null) {
				addErrorMessage("É necessário vincular definir um valor para a prêmio '"+premiacaoCadastrar.getDescricaoPremio()+"' da liga "+premiacaoCadastrar.getLiga().getNomeLiga());
				return null;
			}
												
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			Usuario usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");						
						
			premiacaoCadastrar.setIdUserAtu(usuarioLogado.getId().toString());
			premiacaoCadastrar.setLoginUserAtu(usuarioLogado.getLogin());
			premiacaoCadastrar.setDhAtu(new Date());	
			
			if(listaPremiacoes != null && !listaPremiacoes.isEmpty()) {
				premiacaoCadastrar.setOrdem(listaPremiacoes.get(listaPremiacoes.size()-1).getId().intValue()+1);	
			} else {
				premiacaoCadastrar.setOrdem(1);				
			}
			
			premiacaoCadastrar = (Premiacao) inicioService.atualizar(premiacaoCadastrar);			
							
			buscarLigasCadastradas();
			buscarPremiacoesCadastradas();
			
			addInfoMessage("Premiação '"+premiacaoCadastrar.getDescricaoPremio()+"' cadastrada com sucesso para a liga '"+premiacaoCadastrar.getLiga().getNomeLiga()+"'");
			
			premiacaoCadastrar = new Premiacao();	
			premiacaoCadastrar.setLiga(new Liga());
			premiacaoSelecionada = null;
			
			return null;
			
		} catch (Exception e) {
			log.error("Erro ao cadastrar Premiação "+e.getMessage());
			addErrorMessage("Erro ao cadastrar Premiação "+e.getMessage());
			return null;
		}
	}
	
	
	public String alterarPremiacao() {		
		try {		
			
			if(premiacaoSelecionada.getLiga() == null) {
				addErrorMessage("É necessário vincular uma Liga ao prêmio '"+premiacaoSelecionada.getDescricaoPremio()+"' para alterá-lo");
				return null;
			}
			
			if(premiacaoSelecionada.getDescricaoPremio() == null || "".equalsIgnoreCase(premiacaoSelecionada.getDescricaoPremio())) {				
				addErrorMessage("A descrição do premio é um campo obrigatório");
				return null;
			}	
			
			premiacaoSelecionada.setAno(premiacaoSelecionada.getLiga().getAno());		
						
			if(premiacaoSelecionada.getVrPremio() == null) {
				addErrorMessage("É necessário vincular definir um valor para a prêmio '"+premiacaoSelecionada.getDescricaoPremio()+"' da liga "+premiacaoSelecionada.getLiga().getNomeLiga());
				return null;
			}
												
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			Usuario usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");						
						
			premiacaoSelecionada.setIdUserAtu(usuarioLogado.getId().toString());
			premiacaoSelecionada.setLoginUserAtu(usuarioLogado.getLogin());
			premiacaoSelecionada.setDhAtu(new Date());	
			
			if(premiacaoSelecionada.getOrdem() == null) {
				if(listaPremiacoes != null && !listaPremiacoes.isEmpty()) {
					premiacaoSelecionada.setOrdem(listaPremiacoes.get(listaPremiacoes.size()-1).getId().intValue()+1);	
				} else {
					premiacaoSelecionada.setOrdem(1);				
				}
			}
			
			premiacaoSelecionada = (Premiacao) inicioService.atualizar(premiacaoSelecionada);			
							
			buscarLigasCadastradas();
			buscarPremiacoesCadastradas();
			
			addInfoMessage("Premiação '"+premiacaoSelecionada.getDescricaoPremio()+"' alterada com sucesso para a liga '"+premiacaoSelecionada.getLiga().getNomeLiga()+"'");
			
			premiacaoCadastrar = new Premiacao();	
			premiacaoCadastrar.setLiga(new Liga());
			premiacaoSelecionada = null;
			
			return null;
			
		} catch (Exception e) {
			log.error("Erro ao cadastrar Premiação "+e.getMessage());
			addErrorMessage("Erro ao cadastrar Premiação "+e.getMessage());
			return null;
		}
	}
	
	public void onRowSelect(SelectEvent event) {
		premiacaoCadastrar = null;
		premiacaoSelecionada = new Premiacao();
		premiacaoSelecionada = (Premiacao) event.getObject();  	
    }
	
	@SuppressWarnings("unchecked")
	private void buscarLigasCadastradas() {
		listaLigas = new ArrayList<>();
		listaLigas = (List<Liga>) inicioService.consultarTodos(Liga.class, " order by o.id asc");		
	}
	
	@SuppressWarnings("unchecked")
	private void buscarPremiacoesCadastradas() {
		listaPremiacoes = new ArrayList<>();
		listaPremiacoes = (List<Premiacao>) inicioService.consultarTodos(Premiacao.class, " order by o.liga.id asc, o.ordem asc, o.vrPremio desc, o.id asc");		
	}

	
	public Premiacao getPremiacaoCadastrar() {
		return premiacaoCadastrar;
	}

	public void setPremiacaoCadastrar(Premiacao premiacaoCadastrar) {
		this.premiacaoCadastrar = premiacaoCadastrar;
	}

	public List<Premiacao> getListaPremiacoes() {
		return listaPremiacoes;
	}

	public void setListaPremiacoes(List<Premiacao> listaPremiacoes) {
		this.listaPremiacoes = listaPremiacoes;
	}

	public List<Liga> getListaLigas() {
		return listaLigas;
	}

	public void setListaLigas(List<Liga> listaLigas) {
		this.listaLigas = listaLigas;
	}

	public Premiacao getPremiacaoSelecionada() {
		return premiacaoSelecionada;
	}

	public void setPremiacaoSelecionada(Premiacao premiacaoSelecionada) {
		this.premiacaoSelecionada = premiacaoSelecionada;
	}		
}
