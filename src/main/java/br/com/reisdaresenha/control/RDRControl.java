package br.com.reisdaresenha.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import br.com.reisdaresenha.model.RDRParticipante;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.service.InicioServiceLocal;
import br.com.reisdaresenha.service.ParametroServiceLocal;
import br.com.reisdaresenha.service.RDRServiceLocal;
import br.com.reisdaresenha.service.TimeServiceLocal;
import br.com.reisdaresenha.view.ClassificacaoLigaPrincipalDTO;

/**
 * @author Renan Celso
 * 
 */
@ManagedBean(name = "rdrControl")
@ViewScoped
public class RDRControl extends BaseControl {
	
	private static final long serialVersionUID = 963123137399826380L;
	
	private transient Logger log = Logger.getLogger(RDRControl.class.getName());

	@EJB
	private ParametroServiceLocal parametroService;
	
	@EJB
	private TimeServiceLocal timeService;
	
	@EJB
	private InicioServiceLocal inicioService;
	
	@EJB
	private RDRServiceLocal rdrService;
	
	private CartolaRestFulClient servicoCartola;
	
	private Usuario usuarioLogado;	
	
	private List<RDRParticipante> listaParticipantesSerieA;
	
	private List<RDRParticipante> listaParticipantesSerieB;
		
	@PostConstruct
	public void init() {
		
		try {				
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);	
			usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");		
			servicoCartola = new CartolaRestFulClient();	
			
			listaParticipantesSerieA = new ArrayList<RDRParticipante>();
			listaParticipantesSerieA = rdrService.buscarRDRParticipantes("A", "SA");
			
			listaParticipantesSerieB = new ArrayList<RDRParticipante>();
			listaParticipantesSerieB = rdrService.buscarRDRParticipantes("A", "SB");
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}
	
	public String btnGerarParticipantesApertura() {		
		try {			
			
			List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<ClassificacaoLigaPrincipalDTO>();			
			
			Integer anoAtual = 2020; //Calendar.getInstance().get(Calendar.YEAR);				
			
			listaClassificacaoLigaPrincipalDTO = inicioService.buscarClassificacaoLigaPrincipal(anoAtual);	
			
			listaParticipantesSerieA = new ArrayList<RDRParticipante>();
			
			listaParticipantesSerieB = new ArrayList<RDRParticipante>();
						
			for (int i = 0; i < listaClassificacaoLigaPrincipalDTO.size(); i++) {					
				if(i < 32) {					
					RDRParticipante participante = new RDRParticipante();					
					Time time = timeService.buscarTimePorNome(listaClassificacaoLigaPrincipalDTO.get(i).getTime());
					
					participante.setTime(time);
					participante.setNomeTime(time.getNomeTime());				
					participante.setIdTimeCartola(time.getIdCartola());
					participante.setFaseLiga("A");
					
					if(i < 16) {
						
						participante.setSerieParticipante("SA");						
						participante = (RDRParticipante) timeService.atualizar(participante);						
						listaParticipantesSerieA.add(participante);					
					
					} else if(i >= 16) {						
						participante.setSerieParticipante("SB");				
						participante = (RDRParticipante) timeService.atualizar(participante);
						listaParticipantesSerieB.add(participante);
					}
					
				}
			}		
			
			
		} catch (Exception e) {			
			addErrorMessage("ERRO AO GERAR APERTURA.");
			log.error(e);
		}		
		return null;
	}

	public Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(Usuario usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public List<RDRParticipante> getListaParticipantesSerieA() {
		return listaParticipantesSerieA;
	}

	public void setListaParticipantesSerieA(List<RDRParticipante> listaParticipantesSerieA) {
		this.listaParticipantesSerieA = listaParticipantesSerieA;
	}

	public List<RDRParticipante> getListaParticipantesSerieB() {
		return listaParticipantesSerieB;
	}

	public void setListaParticipantesSerieB(List<RDRParticipante> listaParticipantesSerieB) {
		this.listaParticipantesSerieB = listaParticipantesSerieB;
	}
	

}
