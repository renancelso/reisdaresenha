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

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.RDRClassificacao;
import br.com.reisdaresenha.model.RDRCopaPontuacao;
import br.com.reisdaresenha.model.RDRParticipante;
import br.com.reisdaresenha.model.RDRPontuacao;
import br.com.reisdaresenha.model.RDRRodada;
import br.com.reisdaresenha.model.Rodada;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.service.InicioServiceLocal;
import br.com.reisdaresenha.service.ParametroServiceLocal;
import br.com.reisdaresenha.service.RDRServiceLocal;
import br.com.reisdaresenha.service.RodadaServiceLocal;
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
	
	@EJB
	private RodadaServiceLocal rodadaService;
	
	private Usuario usuarioLogado;	
	
	/** VARIAVEIS APERTURA **/
	private List<RDRParticipante> listaParticipantesAperturaSerieA;
	private List<RDRClassificacao> listaClassificacaoAperturaSerieA;	
	private List<RDRRodada> listaRDRRodadasAperturaSerieA;
	
	private List<RDRParticipante> listaParticipantesAperturaSerieB;
	private List<RDRClassificacao> listaClassificacaoAperturaSerieB;
	private List<RDRRodada> listaRDRRodadasAperturaSerieB;		
	/** VARIAVEIS APERTURA **/
	
	/** VARIAVEIS CLAUSURA **/
	private List<RDRParticipante> listaParticipantesClausuraSerieA;
	private List<RDRClassificacao> listaClassificacaoClausuraSerieA;	
	private List<RDRRodada> listaRDRRodadasClausuraSerieA;
	
	private List<RDRParticipante> listaParticipantesClausuraSerieB;
	private List<RDRClassificacao> listaClassificacaoClausuraSerieB;
	private List<RDRRodada> listaRDRRodadasClausuraSerieB;	
		
	private List<RDRParticipante> listaParticipantesClausuraSerieC;
	private List<RDRClassificacao> listaClassificacaoClausuraSerieC;
	private List<RDRRodada> listaRDRRodadasClausuraSerieC;		
	/** VARIAVEIS CLAUSURA **/
		
	private List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalAteRodada4;	
	
	private List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalAteRodada19;	
		
	private List<RDRCopaPontuacao> listaRDRCopa;
	
	private List<RDRParticipante> listaRDRParticipantesCopa;
	
	private RDRRodada rodadaEmAndamentoSerieAApertura;	
	private RDRRodada rodadaEmAndamentoSerieBApertura;	
	private RDRRodada rodadaEmAndamentoSerieAClausura;	
	private RDRRodada rodadaEmAndamentoSerieBClausura;	
	private RDRRodada rodadaEmAndamentoSerieCClausura;
			
	@PostConstruct
	public void init() {		
		try {				
			log.info("RDRControl --- INIT()");	
			
			Integer anoAtual = 2020; //Calendar.getInstance().get(Calendar.YEAR);			
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);	
			usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");						
			
			log.info("----- buscarInformacoesApertura");	
			buscarInformacoesApertura(anoAtual);
			
			log.info("----- buscarInformacoesClausura");
			buscarInformacoesClausura(anoAtual);		
			
			if(listaClassificacaoAperturaSerieA != null && listaClassificacaoClausuraSerieA != null
					&& !listaClassificacaoAperturaSerieA.isEmpty() && !listaClassificacaoClausuraSerieA.isEmpty()
					&& listaClassificacaoAperturaSerieB != null && listaClassificacaoClausuraSerieB != null
							&& !listaClassificacaoAperturaSerieB.isEmpty() && !listaClassificacaoClausuraSerieB.isEmpty()) {		
							
				log.info("----- buscarParticipantesRDRCopa");
				buscarParticipantesRDRCopa();
				
				log.info("----- buscarInformacaoRDRCopa");
				buscarInformacaoRDRCopa();
			}
						
		} catch (Exception e) {
			addErrorMessage("ERRO DE SISTEMA - RDRControl.init() ");
			log.error(e);
			e.printStackTrace();
		}
	}		

	private void buscarInformacaoRDRCopa() {	
		
		listaRDRCopa = new ArrayList<RDRCopaPontuacao>();
		listaRDRCopa = rdrService.buscarRDRCopaPontuacao();		
				
		if(listaRDRCopa == null || listaRDRCopa.isEmpty()) {		
			
			for (int i = 1; i < 10; i++) {			
				
				int rodadaCartola = 0; 	
				
				if(i <= 4) {
					rodadaCartola = 35;
				} else if(i > 4 && i < 7) {					
					rodadaCartola = 36;
				} else if(i >= 7 && i < 9) {					
					rodadaCartola = 37;
				} else if(i == 9) {
					rodadaCartola = 38;
				}
				
				RDRCopaPontuacao rdrCopaPontuacao = new RDRCopaPontuacao();				
				rdrCopaPontuacao.setNrJogoCopa(new Long(i));				
				rdrCopaPontuacao.setNrRodadaCartola(new Long(rodadaCartola));					
				
				switch(rodadaCartola) {				
					case 35:
						
						rdrCopaPontuacao.setFase("QF");	
						
						if(i == 1) {						
							rdrCopaPontuacao.setDescricaoTimeRodadaCasa("Campeão Série A Apertura");
							rdrCopaPontuacao.setDescricaoTimeRodadaFora("Vice-Campeão Série B Clausura");
						}
						
						if(i == 2) {						
							rdrCopaPontuacao.setDescricaoTimeRodadaCasa("Campeão Série B Clausura");
							rdrCopaPontuacao.setDescricaoTimeRodadaFora("Vice-Campeão Série A Apertura");
						}
						
						if(i == 3) {						
							rdrCopaPontuacao.setDescricaoTimeRodadaCasa("Campeão Série A Clausura");
							rdrCopaPontuacao.setDescricaoTimeRodadaFora("Vice-Campeão Série B Apertura");
						}
						
						if(i == 4) {						
							rdrCopaPontuacao.setDescricaoTimeRodadaCasa("Campeão Série B Apertura");
							rdrCopaPontuacao.setDescricaoTimeRodadaFora("Vice-Campeão Série A Clausura");
						}
						
						break;
						
					case 36:
						
						rdrCopaPontuacao.setFase("SFI");	
						
						if(i == 5) {						
							rdrCopaPontuacao.setDescricaoTimeRodadaCasa("Vencedor Jogo 1");
							rdrCopaPontuacao.setDescricaoTimeRodadaFora("Vencedor Jogo 2");
						}
						
						if(i == 6) {						
							rdrCopaPontuacao.setDescricaoTimeRodadaCasa("Vencedor Jogo 3");
							rdrCopaPontuacao.setDescricaoTimeRodadaFora("Vencedor Jogo 4");
						}
						
						break;
						
					case 37:
						
						rdrCopaPontuacao.setFase("SFV");
						
						if(i == 7) {						
							rdrCopaPontuacao.setDescricaoTimeRodadaCasa("Vencedor Jogo 2");
							rdrCopaPontuacao.setDescricaoTimeRodadaFora("Vencedor Jogo 1");
						}
						
						if(i == 8) {						
							rdrCopaPontuacao.setDescricaoTimeRodadaCasa("Vencedor Jogo 4");
							rdrCopaPontuacao.setDescricaoTimeRodadaFora("Vencedor Jogo 3");
						}
						
						break;
					case 38:
						
						rdrCopaPontuacao.setFase("F");	
						
						if(i == 9) {						
							rdrCopaPontuacao.setDescricaoTimeRodadaCasa("Vencedor (Jogo 5 + Jogo 7)");
							rdrCopaPontuacao.setDescricaoTimeRodadaFora("Vencedor (Jogo 6 + Jogo 8)");
						}
						
						break;	
					default:
						break;
				}
								
				rdrCopaPontuacao = (RDRCopaPontuacao) rdrService.atualizar(rdrCopaPontuacao);		
				
			}					
		
		} else {
			
			if(listaRDRParticipantesCopa.size() == 8) {		
				
				for (int i = 0; i < listaRDRCopa.size(); i++) {
					
					RDRCopaPontuacao rdrCopaPontuacao = listaRDRCopa.get(i);
										
					switch (i) {
					
						case 0:
							
							RDRParticipante campeaoSerieAApertura = rdrService.buscarRDRParticipantesCopaPorClassificacaoFinal("COPA", "COPA", "CA-SA");
							RDRParticipante viceCampeaoSerieBClausura = rdrService.buscarRDRParticipantesCopaPorClassificacaoFinal("COPA", "COPA", "VCC-SB");
							
							rdrCopaPontuacao.setRdrParticipanteTimeCasa(campeaoSerieAApertura);
							rdrCopaPontuacao.setNomeTimeCasa(rdrCopaPontuacao.getRdrParticipanteTimeCasa().getNomeTime());	
							
							rdrCopaPontuacao.setRdrParticipanteTimeFora(viceCampeaoSerieBClausura);
							rdrCopaPontuacao.setNomeTimeFora(rdrCopaPontuacao.getRdrParticipanteTimeFora().getNomeTime());	
							
							break;
						
						case 1:
							
							RDRParticipante campeaoSerieBClausura= rdrService.buscarRDRParticipantesCopaPorClassificacaoFinal("COPA", "COPA", "CC-SB");
							RDRParticipante viceCampeaoSerieAApertura = rdrService.buscarRDRParticipantesCopaPorClassificacaoFinal("COPA", "COPA", "VCA-SA");
							
							rdrCopaPontuacao.setRdrParticipanteTimeCasa(campeaoSerieBClausura);
							rdrCopaPontuacao.setNomeTimeCasa(rdrCopaPontuacao.getRdrParticipanteTimeCasa().getNomeTime());	
							
							rdrCopaPontuacao.setRdrParticipanteTimeFora(viceCampeaoSerieAApertura);
							rdrCopaPontuacao.setNomeTimeFora(rdrCopaPontuacao.getRdrParticipanteTimeFora().getNomeTime());	
							
							break;
							
						case 2:
							
							RDRParticipante campeaoSerieAClausura= rdrService.buscarRDRParticipantesCopaPorClassificacaoFinal("COPA", "COPA", "CC-SA");
							RDRParticipante viceCampeaoSerieBApertura = rdrService.buscarRDRParticipantesCopaPorClassificacaoFinal("COPA", "COPA", "VCA-SB");
							
							rdrCopaPontuacao.setRdrParticipanteTimeCasa(campeaoSerieAClausura);
							rdrCopaPontuacao.setNomeTimeCasa(rdrCopaPontuacao.getRdrParticipanteTimeCasa().getNomeTime());	
							
							rdrCopaPontuacao.setRdrParticipanteTimeFora(viceCampeaoSerieBApertura);
							rdrCopaPontuacao.setNomeTimeFora(rdrCopaPontuacao.getRdrParticipanteTimeFora().getNomeTime());								
												
							break;
							
						case 3:
							
							RDRParticipante campeaoSerieBApertura = rdrService.buscarRDRParticipantesCopaPorClassificacaoFinal("COPA", "COPA", "CA-SB");
							RDRParticipante viceCampeaoSerieAClausura= rdrService.buscarRDRParticipantesCopaPorClassificacaoFinal("COPA", "COPA", "VCC-SA");
							
							rdrCopaPontuacao.setRdrParticipanteTimeCasa(campeaoSerieBApertura);
							rdrCopaPontuacao.setNomeTimeCasa(rdrCopaPontuacao.getRdrParticipanteTimeCasa().getNomeTime());	
							
							rdrCopaPontuacao.setRdrParticipanteTimeFora(viceCampeaoSerieAClausura);
							rdrCopaPontuacao.setNomeTimeFora(rdrCopaPontuacao.getRdrParticipanteTimeFora().getNomeTime());		
							
							break;						
						
						default:
							
							break;
					}	
					
					rdrCopaPontuacao = (RDRCopaPontuacao) rdrService.atualizar(rdrCopaPontuacao);
					
				}
				
			}	
			
		}
		
		listaRDRCopa = new ArrayList<RDRCopaPontuacao>();
		listaRDRCopa = rdrService.buscarRDRCopaPontuacao();		
		
	}
	
	private void buscarParticipantesRDRCopa() {		
		
		listaRDRParticipantesCopa = new ArrayList<RDRParticipante>();
		
		listaRDRParticipantesCopa = rdrService.buscarRDRParticipantes("COPA", "COPA");
		
		if(listaRDRParticipantesCopa == null) {
			listaRDRParticipantesCopa = new ArrayList<RDRParticipante>();
		}
		
		Rodada rodada19 = rodadaService.buscarRodadaDaLigaPrincipalEspecifica(new Long(19));
	
		if(rodada19 != null && "PS".equalsIgnoreCase(rodada19.getStatusRodada()) && listaRDRParticipantesCopa.size() < 8) {	
			
			RDRRodada rdrRodada15AperturaSA = rdrService.buscarRDRRodadaPorRodadaDaLigaPrincipal(rodada19.getNrRodada(), "A", "SA");	
			RDRRodada rdrRodada15AperturaSB = rdrService.buscarRDRRodadaPorRodadaDaLigaPrincipal(rodada19.getNrRodada(), "A", "SB");		
			
			if(rdrRodada15AperturaSA != null && "PS".equalsIgnoreCase(rdrRodada15AperturaSA.getStatusRodada()) 
					&& rdrRodada15AperturaSB != null && "PS".equalsIgnoreCase(rdrRodada15AperturaSB.getStatusRodada())) {			
					
				RDRParticipante campeaoSerieAApertura = listaClassificacaoAperturaSerieA.get(0).getRdrParticipante();
				RDRParticipante viceCampeaoSerieAApertura = listaClassificacaoAperturaSerieA.get(1).getRdrParticipante();				
				RDRParticipante campeaoSerieBApertura = listaClassificacaoAperturaSerieB.get(0).getRdrParticipante();
				RDRParticipante viceCampeaoSerieBApertura = listaClassificacaoAperturaSerieB.get(1).getRdrParticipante();	
														
				if(listaRDRParticipantesCopa != null && !listaRDRParticipantesCopa.isEmpty()) {
					for (RDRParticipante rdrParticipante : listaRDRParticipantesCopa) {
						
						if(rdrParticipante.getTime().getId().equals(campeaoSerieAApertura.getTime().getId())) {
							campeaoSerieAApertura.setEstaNaCopa(true);
						}
						
						if(rdrParticipante.getTime().getId().equals(viceCampeaoSerieAApertura.getTime().getId())) {
							viceCampeaoSerieAApertura.setEstaNaCopa(true);
						}
						
						if(rdrParticipante.getTime().getId().equals(campeaoSerieBApertura.getTime().getId())) {
							campeaoSerieBApertura.setEstaNaCopa(true);
						}
						
						if(rdrParticipante.getTime().getId().equals(viceCampeaoSerieBApertura.getTime().getId())) {
							viceCampeaoSerieBApertura.setEstaNaCopa(true);
						}
						
					}
				}
				
				if(!campeaoSerieAApertura.isEstaNaCopa()) {
					RDRParticipante rdrParticipante = new RDRParticipante();
					rdrParticipante.setEstaNaCopa(true);
					rdrParticipante.setFaseLiga("COPA");
					rdrParticipante.setIdTimeCartola(campeaoSerieAApertura.getTime().getIdCartola());
					rdrParticipante.setNomeTime(campeaoSerieAApertura.getTime().getNomeTime());
					rdrParticipante.setSerieParticipante("COPA");
					rdrParticipante.setTime(campeaoSerieAApertura.getTime());
					
					rdrParticipante.setClassificacaoFinalParaCopa("CA-SA");
					
					rdrService.atualizar(rdrParticipante);
				}
				
				if(!viceCampeaoSerieAApertura.isEstaNaCopa()) {
					RDRParticipante rdrParticipante = new RDRParticipante();
					rdrParticipante.setEstaNaCopa(true);
					rdrParticipante.setFaseLiga("COPA");
					rdrParticipante.setIdTimeCartola(viceCampeaoSerieAApertura.getTime().getIdCartola());
					rdrParticipante.setNomeTime(viceCampeaoSerieAApertura.getTime().getNomeTime());
					rdrParticipante.setSerieParticipante("COPA");
					rdrParticipante.setTime(viceCampeaoSerieAApertura.getTime());
					
					rdrParticipante.setClassificacaoFinalParaCopa("VCA-SA");
					
					rdrService.atualizar(rdrParticipante);						
				}
				
				if(!campeaoSerieBApertura.isEstaNaCopa()) {
					
					RDRParticipante rdrParticipante = new RDRParticipante();
					rdrParticipante.setEstaNaCopa(true);
					rdrParticipante.setFaseLiga("COPA");
					rdrParticipante.setIdTimeCartola(campeaoSerieBApertura.getTime().getIdCartola());
					rdrParticipante.setNomeTime(campeaoSerieBApertura.getTime().getNomeTime());
					rdrParticipante.setSerieParticipante("COPA");
					rdrParticipante.setTime(campeaoSerieBApertura.getTime());
					
					rdrParticipante.setClassificacaoFinalParaCopa("CA-SB");
					
					rdrService.atualizar(rdrParticipante);							
				}
				
				if(!viceCampeaoSerieBApertura.isEstaNaCopa()) {
					
					RDRParticipante rdrParticipante = new RDRParticipante();
					rdrParticipante.setEstaNaCopa(true);
					rdrParticipante.setFaseLiga("COPA");
					rdrParticipante.setIdTimeCartola(viceCampeaoSerieBApertura.getTime().getIdCartola());
					rdrParticipante.setNomeTime(viceCampeaoSerieBApertura.getTime().getNomeTime());
					rdrParticipante.setSerieParticipante("COPA");
					rdrParticipante.setTime(viceCampeaoSerieBApertura.getTime());
					
					rdrParticipante.setClassificacaoFinalParaCopa("VCA-SB");
					
					rdrService.atualizar(rdrParticipante);	
				}			
					
			}	
		} 
		
		listaRDRParticipantesCopa = rdrService.buscarRDRParticipantes("COPA", "COPA");
		
		if(listaRDRParticipantesCopa == null) {
			listaRDRParticipantesCopa = new ArrayList<RDRParticipante>();
		}
		
		Rodada rodada34 = rodadaService.buscarRodadaDaLigaPrincipalEspecifica(new Long(34));		
		
		if(rodada34 != null && "PS".equalsIgnoreCase(rodada34.getStatusRodada()) && listaRDRParticipantesCopa.size() < 8) {		
								
			RDRRodada rdrRodada15ClausuraSA = rdrService.buscarRDRRodadaPorRodadaDaLigaPrincipal(rodada34.getNrRodada(), "C", "SA");	
			RDRRodada rdrRodada15ClausuraSB = rdrService.buscarRDRRodadaPorRodadaDaLigaPrincipal(rodada34.getNrRodada(), "C", "SB");		
			
			if(rdrRodada15ClausuraSA != null && "PS".equalsIgnoreCase(rdrRodada15ClausuraSA.getStatusRodada()) 
					&& rdrRodada15ClausuraSB != null && "PS".equalsIgnoreCase(rdrRodada15ClausuraSB.getStatusRodada())) {			
									
				RDRParticipante campeaoSerieAClausura = listaClassificacaoClausuraSerieA.get(0).getRdrParticipante();
				RDRParticipante viceCampeaoSerieAClausura = listaClassificacaoClausuraSerieA.get(1).getRdrParticipante();
				
				RDRParticipante terceiroLugarSerieAClausura = listaClassificacaoClausuraSerieA.get(2).getRdrParticipante();
				RDRParticipante quartoLugarSerieAClausura = listaClassificacaoClausuraSerieA.get(3).getRdrParticipante();
								
				RDRParticipante campeaoSerieBClausura = listaClassificacaoClausuraSerieB.get(0).getRdrParticipante();
				RDRParticipante viceCampeaoSerieBClausura = listaClassificacaoClausuraSerieB.get(1).getRdrParticipante();												
				
				if(listaRDRParticipantesCopa != null && !listaRDRParticipantesCopa.isEmpty()) {
					
					for (RDRParticipante rdrParticipante : listaRDRParticipantesCopa) {
							
						if(rdrParticipante.getTime().getId().equals(campeaoSerieAClausura.getTime().getId())) {							
							
							campeaoSerieAClausura.setEstaNaCopa(true);								
							
							for (RDRParticipante rdrPart : listaRDRParticipantesCopa) {								
								if(rdrPart.getTime().getId().equals(terceiroLugarSerieAClausura.getTime().getId())) {
									terceiroLugarSerieAClausura.setEstaNaCopa(true);
								}								
							}	
							
							if(!terceiroLugarSerieAClausura.isEstaNaCopa()) {
								
								terceiroLugarSerieAClausura.setIncluirNaCopa(true);
								terceiroLugarSerieAClausura.setIncluirNoLugarCampeao(true);
								
							} else {
								
								for (RDRParticipante rdrPart : listaRDRParticipantesCopa) {			
									if(rdrPart.getTime().getId().equals(quartoLugarSerieAClausura.getTime().getId())) {
										quartoLugarSerieAClausura.setEstaNaCopa(true);
									}	
								}
								
								if(!quartoLugarSerieAClausura.isEstaNaCopa()) {
									
									quartoLugarSerieAClausura.setIncluirNaCopa(true);
									quartoLugarSerieAClausura.setIncluirNoLugarCampeao(true);
									
								}								
							}	
							
							
						}
						
						if(rdrParticipante.getTime().getId().equals(viceCampeaoSerieAClausura.getTime().getId())) {
							
							viceCampeaoSerieAClausura.setEstaNaCopa(true);							
							
							for (RDRParticipante rdrPart : listaRDRParticipantesCopa) {								
								if(rdrPart.getTime().getId().equals(terceiroLugarSerieAClausura.getTime().getId())) {
									terceiroLugarSerieAClausura.setEstaNaCopa(true);
								}								
							}	
							
							if(!terceiroLugarSerieAClausura.isEstaNaCopa()) {
								
								terceiroLugarSerieAClausura.setIncluirNaCopa(true);
								terceiroLugarSerieAClausura.setIncluirNoLugarCampeao(false);
								
							} else {
								
								for (RDRParticipante rdrPart : listaRDRParticipantesCopa) {			
									if(rdrPart.getTime().getId().equals(quartoLugarSerieAClausura.getTime().getId())) {
										quartoLugarSerieAClausura.setEstaNaCopa(true);
									}	
								}
								
								if(!quartoLugarSerieAClausura.isEstaNaCopa()) {
									quartoLugarSerieAClausura.setIncluirNaCopa(true);
									quartoLugarSerieAClausura.setIncluirNoLugarCampeao(false);
								}								
							}			
							
						}
						
						if(rdrParticipante.getTime().getId().equals(campeaoSerieBClausura.getTime().getId())) {
							campeaoSerieBClausura.setEstaNaCopa(true);
						}
						
						if(rdrParticipante.getTime().getId().equals(viceCampeaoSerieBClausura.getTime().getId())) {
							viceCampeaoSerieBClausura.setEstaNaCopa(true);
						}
						
					}	
							
					if(!campeaoSerieAClausura.isEstaNaCopa()) {
						RDRParticipante rdrParticipante = new RDRParticipante();
						rdrParticipante.setEstaNaCopa(true);
						rdrParticipante.setFaseLiga("COPA");
						rdrParticipante.setIdTimeCartola(campeaoSerieAClausura.getTime().getIdCartola());
						rdrParticipante.setNomeTime(campeaoSerieAClausura.getTime().getNomeTime());
						rdrParticipante.setSerieParticipante("COPA");
						rdrParticipante.setTime(campeaoSerieAClausura.getTime());
						rdrParticipante.setClassificacaoFinalParaCopa("CC-SA");
						
						rdrService.atualizar(rdrParticipante);
					}
					
					if(!viceCampeaoSerieAClausura.isEstaNaCopa()) {
						RDRParticipante rdrParticipante = new RDRParticipante();
						rdrParticipante.setEstaNaCopa(true);
						rdrParticipante.setFaseLiga("COPA");
						rdrParticipante.setIdTimeCartola(viceCampeaoSerieAClausura.getTime().getIdCartola());
						rdrParticipante.setNomeTime(viceCampeaoSerieAClausura.getTime().getNomeTime());
						rdrParticipante.setSerieParticipante("COPA");
						rdrParticipante.setTime(viceCampeaoSerieAClausura.getTime());
						rdrParticipante.setClassificacaoFinalParaCopa("VCC-SA");
						
						rdrService.atualizar(rdrParticipante);						
					}
					
					if(!terceiroLugarSerieAClausura.isEstaNaCopa() && terceiroLugarSerieAClausura.isIncluirNaCopa()) {
						RDRParticipante rdrParticipante = new RDRParticipante();
						rdrParticipante.setEstaNaCopa(true);
						rdrParticipante.setFaseLiga("COPA");
						rdrParticipante.setIdTimeCartola(terceiroLugarSerieAClausura.getTime().getIdCartola());
						rdrParticipante.setNomeTime(terceiroLugarSerieAClausura.getTime().getNomeTime());
						rdrParticipante.setSerieParticipante("COPA");
						rdrParticipante.setTime(terceiroLugarSerieAClausura.getTime());
						
						if(terceiroLugarSerieAClausura.isIncluirNoLugarCampeao()) {
							rdrParticipante.setClassificacaoFinalParaCopa("CC-SA");
						} else {
							rdrParticipante.setClassificacaoFinalParaCopa("VCC-SA");
						}
						
						rdrService.atualizar(rdrParticipante);
					}
					
					if(!quartoLugarSerieAClausura.isEstaNaCopa() && quartoLugarSerieAClausura.isIncluirNaCopa()) {
						RDRParticipante rdrParticipante = new RDRParticipante();
						rdrParticipante.setEstaNaCopa(true);
						rdrParticipante.setFaseLiga("COPA");
						rdrParticipante.setIdTimeCartola(quartoLugarSerieAClausura.getTime().getIdCartola());
						rdrParticipante.setNomeTime(quartoLugarSerieAClausura.getTime().getNomeTime());
						rdrParticipante.setSerieParticipante("COPA");
						rdrParticipante.setTime(quartoLugarSerieAClausura.getTime());
						
						if(quartoLugarSerieAClausura.isIncluirNoLugarCampeao()) {
							rdrParticipante.setClassificacaoFinalParaCopa("CC-SA");
						} else {
							rdrParticipante.setClassificacaoFinalParaCopa("VCC-SA");
						}
						
						rdrService.atualizar(rdrParticipante);
					}
					
					if(!campeaoSerieBClausura.isEstaNaCopa()) {
						
						RDRParticipante rdrParticipante = new RDRParticipante();
						rdrParticipante.setEstaNaCopa(true);
						rdrParticipante.setFaseLiga("COPA");
						rdrParticipante.setIdTimeCartola(campeaoSerieBClausura.getTime().getIdCartola());
						rdrParticipante.setNomeTime(campeaoSerieBClausura.getTime().getNomeTime());
						rdrParticipante.setSerieParticipante("COPA");
						rdrParticipante.setTime(campeaoSerieBClausura.getTime());
						
						rdrParticipante.setClassificacaoFinalParaCopa("CC-SB");
						
						rdrService.atualizar(rdrParticipante);							
					}
					
					if(!viceCampeaoSerieBClausura.isEstaNaCopa()) {
						
						RDRParticipante rdrParticipante = new RDRParticipante();
						rdrParticipante.setEstaNaCopa(true);
						rdrParticipante.setFaseLiga("COPA");
						rdrParticipante.setIdTimeCartola(viceCampeaoSerieBClausura.getTime().getIdCartola());
						rdrParticipante.setNomeTime(viceCampeaoSerieBClausura.getTime().getNomeTime());
						rdrParticipante.setSerieParticipante("COPA");
						rdrParticipante.setTime(viceCampeaoSerieBClausura.getTime());
						
						rdrParticipante.setClassificacaoFinalParaCopa("VCC-SB");
						
						rdrService.atualizar(rdrParticipante);	
					}	
				}
			}
		}
		
		listaRDRParticipantesCopa = rdrService.buscarRDRParticipantes("COPA", "COPA");
	}

	private void buscarInformacoesApertura(Integer anoAtual) {
							
		/** APERTURA **/
		listaParticipantesAperturaSerieA = new ArrayList<RDRParticipante>();
		listaParticipantesAperturaSerieA = rdrService.buscarRDRParticipantes("A", "SA");
		
		listaParticipantesAperturaSerieB = new ArrayList<RDRParticipante>();
		listaParticipantesAperturaSerieB = rdrService.buscarRDRParticipantes("A", "SB");
				
		/**Atualizar Saldo de gols*/
		rdrService.atualizarSaldoDeGols("A", "SA",listaParticipantesAperturaSerieA);
		rdrService.atualizarSaldoDeGols("A", "SB",listaParticipantesAperturaSerieB);
		
		listaClassificacaoAperturaSerieA = new ArrayList<RDRClassificacao>();	
		listaClassificacaoAperturaSerieA = rdrService.buscarRDRClassificacao("A", "SA");
		
		listaClassificacaoAperturaSerieB = new ArrayList<RDRClassificacao>();
		listaClassificacaoAperturaSerieB = rdrService.buscarRDRClassificacao("A", "SB");
		
		listaRDRRodadasAperturaSerieA = new ArrayList<RDRRodada>();	
		listaRDRRodadasAperturaSerieA = rdrService.buscarRDRRodadas("A", "SA");
		
		listaRDRRodadasAperturaSerieB = new ArrayList<RDRRodada>();
		listaRDRRodadasAperturaSerieB = rdrService.buscarRDRRodadas("A", "SB");										
		
		if(listaRDRRodadasAperturaSerieA != null && !listaRDRRodadasAperturaSerieA.isEmpty()) {
			for (RDRRodada rdrRodadaSA : listaRDRRodadasAperturaSerieA) {
				rdrRodadaSA.setListaRDRPontuacao(rdrService.buscarRDRPontuacaoPorRodada(rdrRodadaSA));
			}
		}
		
		if(listaRDRRodadasAperturaSerieB != null && !listaRDRRodadasAperturaSerieB.isEmpty()) {				
			for (RDRRodada rdrRodadaSB : listaRDRRodadasAperturaSerieB) {
				rdrRodadaSB.setListaRDRPontuacao(rdrService.buscarRDRPontuacaoPorRodada(rdrRodadaSB));
			}
		}
		/** APERTURA **/	
		
		listaClassificacaoLigaPrincipalAteRodada4 = new ArrayList<ClassificacaoLigaPrincipalDTO>();					
		listaClassificacaoLigaPrincipalAteRodada4 = inicioService.buscarClassificacaoLigaPrincipalAteRodadaX(anoAtual, 4);
		
		listaClassificacaoLigaPrincipalAteRodada19 = new ArrayList<ClassificacaoLigaPrincipalDTO>();					
		listaClassificacaoLigaPrincipalAteRodada19 = inicioService.buscarClassificacaoLigaPrincipalAteRodadaX(anoAtual, 19);
						
		if(listaRDRRodadasAperturaSerieA != null && !listaRDRRodadasAperturaSerieA.isEmpty()) {
			
			for (RDRRodada rdrRodada : listaRDRRodadasAperturaSerieA) {				
				rodadaEmAndamentoSerieAApertura = null;				
				if("EA".equals(rdrRodada.getStatusRodada())) {					
					rodadaEmAndamentoSerieAApertura = rdrRodada;
					rodadaEmAndamentoSerieAApertura.setListaRDRPontuacao(rdrRodada.getListaRDRPontuacao());									
					break;					
				}
			}
			
			if(rodadaEmAndamentoSerieAApertura != null) {

				listaRDRRodadasAperturaSerieA = new ArrayList<RDRRodada>();	
				listaRDRRodadasAperturaSerieA.add(rodadaEmAndamentoSerieAApertura);
				listaRDRRodadasAperturaSerieA.addAll(rdrService.buscarRDRRodadas("A", "SA"));
				
				if(listaRDRRodadasAperturaSerieA != null && !listaRDRRodadasAperturaSerieA.isEmpty()) {
					for (RDRRodada rdrRodadaSA : listaRDRRodadasAperturaSerieA) {
						rdrRodadaSA.setListaRDRPontuacao(rdrService.buscarRDRPontuacaoPorRodada(rdrRodadaSA));
					}
				}
			}			
		}
		
		if(listaRDRRodadasAperturaSerieB != null && !listaRDRRodadasAperturaSerieB.isEmpty()) {
									
			for (RDRRodada rdrRodada : listaRDRRodadasAperturaSerieB) {				
				rodadaEmAndamentoSerieBApertura = null;				
				if("EA".equals(rdrRodada.getStatusRodada())) {					
					rodadaEmAndamentoSerieBApertura = rdrRodada;
					rodadaEmAndamentoSerieBApertura.setListaRDRPontuacao(rdrRodada.getListaRDRPontuacao());										
					break;
				}
			}	
			
			if(rodadaEmAndamentoSerieBApertura != null) {
			
				listaRDRRodadasAperturaSerieB = new ArrayList<RDRRodada>();
				listaRDRRodadasAperturaSerieB.add(rodadaEmAndamentoSerieBApertura);
				listaRDRRodadasAperturaSerieB.addAll(rdrService.buscarRDRRodadas("A", "SB"));			
				
				if(listaRDRRodadasAperturaSerieB != null && !listaRDRRodadasAperturaSerieB.isEmpty()) {				
					for (RDRRodada rdrRodadaSB : listaRDRRodadasAperturaSerieB) {
						rdrRodadaSB.setListaRDRPontuacao(rdrService.buscarRDRPontuacaoPorRodada(rdrRodadaSB));
					}
				}	
			}
		}
		
	}
	
	private void buscarInformacoesClausura(Integer anoAtual) {
		
		/** CLAUSURA **/
		listaParticipantesClausuraSerieA = new ArrayList<RDRParticipante>();
		listaParticipantesClausuraSerieA = rdrService.buscarRDRParticipantes("C", "SA");
		
		listaParticipantesClausuraSerieB = new ArrayList<RDRParticipante>();
		listaParticipantesClausuraSerieB = rdrService.buscarRDRParticipantes("C", "SB");
		
		listaParticipantesClausuraSerieC = new ArrayList<RDRParticipante>();
		listaParticipantesClausuraSerieC = rdrService.buscarRDRParticipantes("C", "SC");		
				
		/**Atualizar Saldo de gols*/
		rdrService.atualizarSaldoDeGols("C", "SA", listaParticipantesClausuraSerieA);
		rdrService.atualizarSaldoDeGols("C", "SB", listaParticipantesClausuraSerieB);
		rdrService.atualizarSaldoDeGols("C", "SC", listaParticipantesClausuraSerieC);
					
		listaClassificacaoClausuraSerieA = new ArrayList<RDRClassificacao>();	
		listaClassificacaoClausuraSerieA = rdrService.buscarRDRClassificacao("C", "SA");
		
		listaClassificacaoClausuraSerieB = new ArrayList<RDRClassificacao>();
		listaClassificacaoClausuraSerieB = rdrService.buscarRDRClassificacao("C", "SB");
		
		listaClassificacaoClausuraSerieC = new ArrayList<RDRClassificacao>();
		listaClassificacaoClausuraSerieC = rdrService.buscarRDRClassificacao("C", "SC");
		
		listaRDRRodadasClausuraSerieA = new ArrayList<RDRRodada>();	
		listaRDRRodadasClausuraSerieA = rdrService.buscarRDRRodadas("C", "SA");
		
		listaRDRRodadasClausuraSerieB = new ArrayList<RDRRodada>();
		listaRDRRodadasClausuraSerieB = rdrService.buscarRDRRodadas("C", "SB");		
		
		listaRDRRodadasClausuraSerieC = new ArrayList<RDRRodada>();
		listaRDRRodadasClausuraSerieC = rdrService.buscarRDRRodadas("C", "SC");		
		
		if(listaRDRRodadasClausuraSerieA != null && !listaRDRRodadasClausuraSerieA.isEmpty()) {
			for (RDRRodada rdrRodadaSA : listaRDRRodadasClausuraSerieA) {
				rdrRodadaSA.setListaRDRPontuacao(rdrService.buscarRDRPontuacaoPorRodada(rdrRodadaSA));
			}
		}
		
		if(listaRDRRodadasClausuraSerieB != null && !listaRDRRodadasClausuraSerieB.isEmpty()) {				
			for (RDRRodada rdrRodadaSB : listaRDRRodadasClausuraSerieB) {
				rdrRodadaSB.setListaRDRPontuacao(rdrService.buscarRDRPontuacaoPorRodada(rdrRodadaSB));
			}
		}
		
		if(listaRDRRodadasClausuraSerieC != null && !listaRDRRodadasClausuraSerieC.isEmpty()) {				
			for (RDRRodada rdrRodadaSC : listaRDRRodadasClausuraSerieC) {
				rdrRodadaSC.setListaRDRPontuacao(rdrService.buscarRDRPontuacaoPorRodada(rdrRodadaSC));
			}
		}
		
		/** CLAUSURA **/
		
		if(listaRDRRodadasClausuraSerieA != null && !listaRDRRodadasClausuraSerieA.isEmpty()) {
			
			for (RDRRodada rdrRodada : listaRDRRodadasClausuraSerieA) {
				rodadaEmAndamentoSerieAClausura = null;
				if("EA".equals(rdrRodada.getStatusRodada())) {
					rodadaEmAndamentoSerieAClausura = rdrRodada;
					rodadaEmAndamentoSerieAClausura.setListaRDRPontuacao(rdrRodada.getListaRDRPontuacao());
					break;
				}
			}
			
			if(rodadaEmAndamentoSerieAClausura != null) {
				
				listaRDRRodadasClausuraSerieA = new ArrayList<RDRRodada>();	
				listaRDRRodadasClausuraSerieA.add(rodadaEmAndamentoSerieAClausura);
				listaRDRRodadasClausuraSerieA.addAll(rdrService.buscarRDRRodadas("C", "SA"));
				
				if(listaRDRRodadasClausuraSerieA != null && !listaRDRRodadasClausuraSerieA.isEmpty()) {
					for (RDRRodada rdrRodadaSA : listaRDRRodadasClausuraSerieA) {
						rdrRodadaSA.setListaRDRPontuacao(rdrService.buscarRDRPontuacaoPorRodada(rdrRodadaSA));
					}
				}			

			}			
		}
		
		if(listaRDRRodadasClausuraSerieB != null && !listaRDRRodadasClausuraSerieB.isEmpty()) {
			
			for (RDRRodada rdrRodada : listaRDRRodadasClausuraSerieB) {
				rodadaEmAndamentoSerieBClausura = null;
				if("EA".equals(rdrRodada.getStatusRodada())) {
					rodadaEmAndamentoSerieBClausura = rdrRodada;
					rodadaEmAndamentoSerieBClausura.setListaRDRPontuacao(rdrRodada.getListaRDRPontuacao());
					break;
				}
			}
			
			if(rodadaEmAndamentoSerieBClausura != null){
								
				listaRDRRodadasClausuraSerieB = new ArrayList<RDRRodada>();
				listaRDRRodadasClausuraSerieB.add(rodadaEmAndamentoSerieBClausura);				
				listaRDRRodadasClausuraSerieB.addAll(rdrService.buscarRDRRodadas("C", "SB"));		
				
				if(listaRDRRodadasClausuraSerieB != null && !listaRDRRodadasClausuraSerieB.isEmpty()) {				
					for (RDRRodada rdrRodadaSB : listaRDRRodadasClausuraSerieB) {
						rdrRodadaSB.setListaRDRPontuacao(rdrService.buscarRDRPontuacaoPorRodada(rdrRodadaSB));
					}
				}
			}
		}
				
		if(listaRDRRodadasClausuraSerieC != null && !listaRDRRodadasClausuraSerieC.isEmpty()) {
			
			for (RDRRodada rdrRodada : listaRDRRodadasClausuraSerieC) {
				rodadaEmAndamentoSerieCClausura = null;
				if("EA".equals(rdrRodada.getStatusRodada())) {
					rodadaEmAndamentoSerieCClausura = rdrRodada;
					rodadaEmAndamentoSerieCClausura.setListaRDRPontuacao(rdrRodada.getListaRDRPontuacao());
					break;
				}
			}
			
			if(rodadaEmAndamentoSerieCClausura != null){
								
				listaRDRRodadasClausuraSerieC = new ArrayList<RDRRodada>();
				listaRDRRodadasClausuraSerieC.add(rodadaEmAndamentoSerieCClausura);				
				listaRDRRodadasClausuraSerieC.addAll(rdrService.buscarRDRRodadas("C", "SC"));		
				
				if(listaRDRRodadasClausuraSerieC != null && !listaRDRRodadasClausuraSerieC.isEmpty()) {				
					for (RDRRodada rdrRodadaSC : listaRDRRodadasClausuraSerieC) {
						rdrRodadaSC.setListaRDRPontuacao(rdrService.buscarRDRPontuacaoPorRodada(rdrRodadaSC));
					}
				}
			}
		}
		
	}
	
	public String btnGerarParticipantesClausura() {	
		
		try {	
			
			Rodada decimaNonaRodadaFinalizada = null;			
			
			try {					
				decimaNonaRodadaFinalizada = new Rodada();				
				
				StringBuilder sql = new StringBuilder();
				sql.append("select o from ").append(Rodada.class.getSimpleName()).append(" o where o.nrRodada = 19 and o.statusRodada = 'PS'");				
				
				decimaNonaRodadaFinalizada = (Rodada) rdrService.consultarPorQuery(sql.toString(), 1, 0).get(0);			
				
			} catch (Exception e) {
				addFatalMessage("Não é possivel gerar a CLAUSURA antes da 19ª rodada do Cartola FC ser finalizada.");				
				return null;
			}
			
			if(decimaNonaRodadaFinalizada == null) {
				addFatalMessage("Não é possivel gerar a CLAUSURA antes da 19ª rodada do Cartola FC ser finalizada.");				
				return null;
			}
			
			/**Série A Clausura - INICIO**/		
			listaParticipantesClausuraSerieA = new ArrayList<RDRParticipante>();				
			//Pegar os 8 primeiros da série A da APERTURA			
			int i = 1;			
			for (RDRClassificacao rdrClassificacaoAperturaA : listaClassificacaoAperturaSerieA) {				
				if(i <= 8) {
					RDRParticipante participante = new RDRParticipante();					
					Time time = timeService.buscarTimePorIdCartola(rdrClassificacaoAperturaA.getIdCartolaTime());					
					participante.setTime(time);
					participante.setNomeTime(time.getNomeTime());				
					participante.setIdTimeCartola(time.getIdCartola());
					participante.setFaseLiga("C");								
					participante.setSerieParticipante("SA");				
					participante = (RDRParticipante) timeService.atualizar(participante);
					listaParticipantesClausuraSerieA.add(participante);		
				} else {
					break;
				}				
				i++;
			}	
			////Pegar os 8 primeiros da série B da APERTURA
			i = 1;
			for (RDRClassificacao rdrClassificacaoAperturaB : listaClassificacaoAperturaSerieB) {				
				if(i <= 8) {					
					RDRParticipante participante = new RDRParticipante();					
					Time time = timeService.buscarTimePorIdCartola(rdrClassificacaoAperturaB.getIdCartolaTime());					
					participante.setTime(time);
					participante.setNomeTime(time.getNomeTime());				
					participante.setIdTimeCartola(time.getIdCartola());
					participante.setFaseLiga("C");								
					participante.setSerieParticipante("SA");				
					participante = (RDRParticipante) timeService.atualizar(participante);
					listaParticipantesClausuraSerieA.add(participante);	
				} 			
				i++;
			}
			/**Série A Clausura - FIM**/
			
			/********************************************************************************/
			
			/**Série B Clausura - INICIO**/
			listaParticipantesClausuraSerieB = new ArrayList<RDRParticipante>();
			// Pegar os 8 ultimos da serie A
			i = 1;			
			for (RDRClassificacao rdrClassificacaoAperturaA : listaClassificacaoAperturaSerieA) {				
				if(i > 8) {					
					RDRParticipante participante = new RDRParticipante();					
					Time time = timeService.buscarTimePorIdCartola(rdrClassificacaoAperturaA.getIdCartolaTime());					
					participante.setTime(time);
					participante.setNomeTime(time.getNomeTime());				
					participante.setIdTimeCartola(time.getIdCartola());
					participante.setFaseLiga("C");								
					participante.setSerieParticipante("SB");				
					participante = (RDRParticipante) timeService.atualizar(participante);	
					listaParticipantesClausuraSerieB.add(participante);	
				} 			
				i++;
			}	
			
			i = 8 - (listaClassificacaoLigaPrincipalAteRodada4.size()-32);		
			for (int j = 0; j < listaClassificacaoAperturaSerieB.size(); j++) {
				if(j > 7 && i > 0) {
					RDRParticipante participante = new RDRParticipante();					
					Time time = timeService.buscarTimePorIdCartola(listaClassificacaoAperturaSerieB.get(j).getIdCartolaTime());		
					participante.setTime(time);
					participante.setNomeTime(time.getNomeTime());				
					participante.setIdTimeCartola(time.getIdCartola());
					participante.setFaseLiga("C");								
					participante.setSerieParticipante("SB");				
					participante = (RDRParticipante) timeService.atualizar(participante);						
					listaParticipantesClausuraSerieB.add(participante);	
					i--;
				}
			}		
			
			//listaClassificacaoLigaPrincipalAteRodada4 // Pegar os ultimos "(total de participantes - 32 até a 4ª rodada)" e adicionar na clausura
			i = 1;	
			for (ClassificacaoLigaPrincipalDTO classificacaoteRodada4 : listaClassificacaoLigaPrincipalAteRodada4) {					
				if(i > 32) {	
					RDRParticipante participante = new RDRParticipante();					
					Time time = timeService.buscarTimePorIdCartola(classificacaoteRodada4.getIdTimeCartola());					
					participante.setTime(time);
					participante.setNomeTime(time.getNomeTime());				
					participante.setIdTimeCartola(time.getIdCartola());
					participante.setFaseLiga("C");								
					participante.setSerieParticipante("SB");				
					participante = (RDRParticipante) timeService.atualizar(participante);						
					listaParticipantesClausuraSerieB.add(participante);	
				} 			
				i++;
			}		
			
			/**Série B Clausura - FIM**/
			
			init();
			
		} catch (Exception e) {			
			addErrorMessage("ERRO AO GERAR CLAUSURA.");
			log.error(e);
		}	
			
		return null;
	}
	
	public String btnGerarParticipantesSerieCClausura() {
		try {
			
			listaParticipantesClausuraSerieC = new ArrayList<>();
			
			List<Time> listaTimesParticipantes = new ArrayList<>();
			listaTimesParticipantes = (List<Time>) inicioService.consultarTodos(Time.class, " order by o.nomeTime, o.nomeDonoTime ");	
			
			for (Time time : listaTimesParticipantes) {
				
				boolean timeSerieC = true;			
				
				for (RDRParticipante timeSerieAClausura : listaParticipantesClausuraSerieA) {
					if(timeSerieAClausura.getTime().getId().equals(time.getId())) {
						timeSerieC = false;
						break;
					}
				}
				
				for (RDRParticipante timeSerieBClausura : listaParticipantesClausuraSerieB) {
					if(timeSerieBClausura.getTime().getId().equals(time.getId())) {
						timeSerieC = false;
						break;
					}
				}				
				
				if(timeSerieC) {
					RDRParticipante participante = new RDRParticipante();	
					participante.setTime(time);
					participante.setNomeTime(time.getNomeTime());				
					participante.setIdTimeCartola(time.getIdCartola());
					participante.setFaseLiga("C");								
					participante.setSerieParticipante("SC");				
					participante = (RDRParticipante) timeService.atualizar(participante);							
					listaParticipantesClausuraSerieC.add(participante);
				}
			}
			
			
		} catch (Exception e) {			
			addErrorMessage("ERRO AO GERAR SERIE C CLAUSURA.");
			log.error(e);
		}	
		
		return null;
	}

	public String btnGerarParticipantesApertura() {		
		try {							
			Rodada quartaRodada = null;			
			
			try {	
				
				quartaRodada = new Rodada();				
				
				StringBuilder sql = new StringBuilder();
				sql.append("select o from ").append(Rodada.class.getSimpleName()).append(" o where o.nrRodada = 4 and o.statusRodada = 'PS'");				
				
				quartaRodada = (Rodada) rdrService.consultarPorQuery(sql.toString(), 1, 0).get(0);			
				
			} catch (Exception e) {
				addFatalMessage("Não é possivel gerar a Apertura antes da 4ª rodada do Cartola FC ser finalizada.");				
				return null;
			}
			
			if(quartaRodada == null) {
				addFatalMessage("Não é possivel gerar a Apertura antes da 4ª rodada do Cartola FC ser finalizada.");				
				return null;
			}			
			
			List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<ClassificacaoLigaPrincipalDTO>();			
			
			Integer anoAtual = 2020; //Calendar.getInstance().get(Calendar.YEAR);				
			
			listaClassificacaoLigaPrincipalDTO = inicioService.buscarClassificacaoLigaPrincipalAteRodadaX(anoAtual,4);	
			
			listaParticipantesAperturaSerieA = new ArrayList<RDRParticipante>();
			
			listaParticipantesAperturaSerieB = new ArrayList<RDRParticipante>();
						
			for (int i = 0; i < listaClassificacaoLigaPrincipalDTO.size(); i++) {					
				if(i < 32) {					
					RDRParticipante participante = new RDRParticipante();					
					Time time = timeService.buscarTimePorIdCartola(listaClassificacaoLigaPrincipalDTO.get(i).getIdTimeCartola());
					
					participante.setTime(time);
					participante.setNomeTime(time.getNomeTime());				
					participante.setIdTimeCartola(time.getIdCartola());
					participante.setFaseLiga("A");
					
					if(i < 16) {
						
						participante.setSerieParticipante("SA");						
						participante = (RDRParticipante) timeService.atualizar(participante);						
						listaParticipantesAperturaSerieA.add(participante);					
					
					} else if(i >= 16) {						
						participante.setSerieParticipante("SB");				
						participante = (RDRParticipante) timeService.atualizar(participante);
						listaParticipantesAperturaSerieB.add(participante);
					}
					
				}
			}		
			
			init();
		} catch (Exception e) {			
			addErrorMessage("ERRO AO GERAR APERTURA.");
			log.error(e);
		}		
		return null;
	}
	
	public String btnGerarClassificacaoETabelaSerieCClausura() {
		try {
			
			if(listaParticipantesClausuraSerieC != null && !listaParticipantesClausuraSerieC.isEmpty()) {				
					
					if(listaClassificacaoClausuraSerieC == null) {
						listaClassificacaoClausuraSerieC = new ArrayList<RDRClassificacao>();
					}
				
					// Gera Classificacao Inicial				
					for (RDRParticipante rdrParticipanteC : listaParticipantesClausuraSerieC) {	
						
						RDRClassificacao classificacaoClausuraSerieC = new RDRClassificacao();					
						classificacaoClausuraSerieC.setSerie(rdrParticipanteC.getSerieParticipante());
						classificacaoClausuraSerieC.setFaseLiga(rdrParticipanteC.getFaseLiga());
						classificacaoClausuraSerieC.setQtdJogosDisputados(new Long(0));					
						classificacaoClausuraSerieC.setQtdDerrotas(new Long(0));
						classificacaoClausuraSerieC.setQtdVitorias(new Long(0));
						classificacaoClausuraSerieC.setQtdEmpates(new Long(0));
						classificacaoClausuraSerieC.setVrPontos(0.0);					
						classificacaoClausuraSerieC.setRdrParticipante(rdrParticipanteC);	
						
						classificacaoClausuraSerieC.setNomeTime(rdrParticipanteC.getTime().getNomeTime());
						classificacaoClausuraSerieC.setNomeDonoTime(rdrParticipanteC.getTime().getNomeDonoTime());
						classificacaoClausuraSerieC.setIdCartolaTime(rdrParticipanteC.getIdTimeCartola());
						
						classificacaoClausuraSerieC.setNrRodadaAtual(new Long(1));
						
						classificacaoClausuraSerieC = (RDRClassificacao) rdrService.atualizar(classificacaoClausuraSerieC);
											
						listaClassificacaoClausuraSerieC.add(classificacaoClausuraSerieC);		
						
						atualizarRDRClassificacao(classificacaoClausuraSerieC.getFaseLiga(), classificacaoClausuraSerieC.getSerie(), new Long(1));
					}	
					
					//GERAR TABELA Clausura SERIE C
					if(!gerarTabela(listaParticipantesClausuraSerieC, "C", "SC")) { // String tipoRodada ("A" ou "B" ou "C"), String serie("SA" ou "SB" ou "SC")
						addErrorMessage("ERRO AO GERAR TABELA DE JOGOS");
					}					
					
					addInfoMessage("Classificação e Tabela da Série C da Clausura geradas com sucesso");				
				}
				
				init();
			
			
		} catch (Exception e) {			
			addErrorMessage("ERRO AO GERAR SERIE C CLAUSURA.");			
			log.error(e);
			init();
		}			
		return null;
	}
	
	public String btnGerarClassificacaoETabelaClausura() {
		try {		
			
			if(listaParticipantesClausuraSerieA != null && !listaParticipantesClausuraSerieA.isEmpty() 
			   && listaParticipantesClausuraSerieB != null && !listaParticipantesClausuraSerieB.isEmpty()) {				
				
				if(listaClassificacaoClausuraSerieA == null) {
					listaClassificacaoClausuraSerieA = new ArrayList<RDRClassificacao>();
				}
			
				// Gera Classificacao Inicial				
				for (RDRParticipante rdrParticipanteA : listaParticipantesClausuraSerieA) {	
					
					RDRClassificacao classificacaoClausuraSerieA = new RDRClassificacao();					
					classificacaoClausuraSerieA.setSerie(rdrParticipanteA.getSerieParticipante());
					classificacaoClausuraSerieA.setFaseLiga(rdrParticipanteA.getFaseLiga());
					classificacaoClausuraSerieA.setQtdJogosDisputados(new Long(0));					
					classificacaoClausuraSerieA.setQtdDerrotas(new Long(0));
					classificacaoClausuraSerieA.setQtdVitorias(new Long(0));
					classificacaoClausuraSerieA.setQtdEmpates(new Long(0));
					classificacaoClausuraSerieA.setVrPontos(0.0);					
					classificacaoClausuraSerieA.setRdrParticipante(rdrParticipanteA);	
					
					classificacaoClausuraSerieA.setNomeTime(rdrParticipanteA.getTime().getNomeTime());
					classificacaoClausuraSerieA.setNomeDonoTime(rdrParticipanteA.getTime().getNomeDonoTime());
					classificacaoClausuraSerieA.setIdCartolaTime(rdrParticipanteA.getIdTimeCartola());
					
					classificacaoClausuraSerieA.setNrRodadaAtual(new Long(1));
					
					classificacaoClausuraSerieA = (RDRClassificacao) rdrService.atualizar(classificacaoClausuraSerieA);
										
					listaClassificacaoClausuraSerieA.add(classificacaoClausuraSerieA);		
					
					atualizarRDRClassificacao(classificacaoClausuraSerieA.getFaseLiga(), classificacaoClausuraSerieA.getSerie(), new Long(1));
				}	
				
				if(listaClassificacaoClausuraSerieB == null) {
					listaClassificacaoClausuraSerieB = new ArrayList<RDRClassificacao>();
				}
				
				for (RDRParticipante rdrParticipanteB : listaParticipantesClausuraSerieB) {		
					
					RDRClassificacao classificacaoClausuraSerieB = new RDRClassificacao();					
					classificacaoClausuraSerieB.setSerie(rdrParticipanteB.getSerieParticipante());
					classificacaoClausuraSerieB.setFaseLiga(rdrParticipanteB.getFaseLiga());
					classificacaoClausuraSerieB.setQtdJogosDisputados(new Long(0));					
					classificacaoClausuraSerieB.setQtdDerrotas(new Long(0));
					classificacaoClausuraSerieB.setQtdVitorias(new Long(0));
					classificacaoClausuraSerieB.setQtdEmpates(new Long(0));	
					classificacaoClausuraSerieB.setVrPontos(0.0);					
					classificacaoClausuraSerieB.setRdrParticipante(rdrParticipanteB);	
					
					classificacaoClausuraSerieB.setNomeTime(rdrParticipanteB.getTime().getNomeTime());
					classificacaoClausuraSerieB.setNomeDonoTime(rdrParticipanteB.getTime().getNomeDonoTime());
					classificacaoClausuraSerieB.setIdCartolaTime(rdrParticipanteB.getIdTimeCartola());
					
					classificacaoClausuraSerieB.setNrRodadaAtual(new Long(1));
					
					classificacaoClausuraSerieB = (RDRClassificacao) rdrService.atualizar(classificacaoClausuraSerieB);
					
					listaClassificacaoClausuraSerieB.add(classificacaoClausuraSerieB);		
					
					atualizarRDRClassificacao(classificacaoClausuraSerieB.getFaseLiga(), classificacaoClausuraSerieB.getSerie(), new Long(1));
				}	
				
				//GERAR TABELA Clausura SERIE A
				if(!gerarTabela(listaParticipantesClausuraSerieA, "C", "SA")) { // String tipoRodada ("A" ou "B"), String serie("SA" ou "SB")
					addErrorMessage("ERRO AO GERAR TABELA DE JOGOS");
				}	
				
				//GERAR TABELA Clausura SERIE B
				if(!gerarTabela(listaParticipantesClausuraSerieB, "C", "SB")) { // String tipoRodada ("A" ou "B"), String serie("SA" ou "SB")
					addErrorMessage("ERRO AO GERAR TABELA DE JOGOS");
				}	
				
				addInfoMessage("Classificação e Tabela da fase Clausura geradas com sucesso");				
			}
			
			init();
			
		} catch (Exception e) {			
			addErrorMessage("ERRO AO GERAR CLAUSURA.");			
			log.error(e);
			init();
		}			
		return null;
	}
	
	public String btnGerarClassificacaoETabelaApertura() {	
		try {		
			
			if(listaParticipantesAperturaSerieA != null && !listaParticipantesAperturaSerieA.isEmpty() 
			   && listaParticipantesAperturaSerieB != null && !listaParticipantesAperturaSerieB.isEmpty()) {				
				
				if(listaClassificacaoAperturaSerieA == null) {
					listaClassificacaoAperturaSerieA = new ArrayList<RDRClassificacao>();
				}
			
				// Gera Classificacao Inicial				
				for (RDRParticipante rdrParticipanteA : listaParticipantesAperturaSerieA) {	
					
					RDRClassificacao classificacaoAperturaSerieA = new RDRClassificacao();					
					classificacaoAperturaSerieA.setSerie(rdrParticipanteA.getSerieParticipante());
					classificacaoAperturaSerieA.setFaseLiga(rdrParticipanteA.getFaseLiga());
					classificacaoAperturaSerieA.setQtdJogosDisputados(new Long(0));					
					classificacaoAperturaSerieA.setQtdDerrotas(new Long(0));
					classificacaoAperturaSerieA.setQtdVitorias(new Long(0));
					classificacaoAperturaSerieA.setQtdEmpates(new Long(0));
					classificacaoAperturaSerieA.setVrPontos(0.0);					
					classificacaoAperturaSerieA.setRdrParticipante(rdrParticipanteA);	
					
					classificacaoAperturaSerieA.setNomeTime(rdrParticipanteA.getTime().getNomeTime());
					classificacaoAperturaSerieA.setNomeDonoTime(rdrParticipanteA.getTime().getNomeDonoTime());
					classificacaoAperturaSerieA.setIdCartolaTime(rdrParticipanteA.getIdTimeCartola());
					
					classificacaoAperturaSerieA.setNrRodadaAtual(new Long(1));
					
					classificacaoAperturaSerieA = (RDRClassificacao) rdrService.atualizar(classificacaoAperturaSerieA);
										
					listaClassificacaoAperturaSerieA.add(classificacaoAperturaSerieA);		
					
					atualizarRDRClassificacao(classificacaoAperturaSerieA.getFaseLiga(), classificacaoAperturaSerieA.getSerie(), new Long(1));
				}	
				
				if(listaClassificacaoAperturaSerieB == null) {
					listaClassificacaoAperturaSerieB = new ArrayList<RDRClassificacao>();
				}
				
				for (RDRParticipante rdrParticipanteB : listaParticipantesAperturaSerieB) {		
					
					RDRClassificacao classificacaoAperturaSerieB = new RDRClassificacao();					
					classificacaoAperturaSerieB.setSerie(rdrParticipanteB.getSerieParticipante());
					classificacaoAperturaSerieB.setFaseLiga(rdrParticipanteB.getFaseLiga());
					classificacaoAperturaSerieB.setQtdJogosDisputados(new Long(0));					
					classificacaoAperturaSerieB.setQtdDerrotas(new Long(0));
					classificacaoAperturaSerieB.setQtdVitorias(new Long(0));
					classificacaoAperturaSerieB.setQtdEmpates(new Long(0));	
					classificacaoAperturaSerieB.setVrPontos(0.0);					
					classificacaoAperturaSerieB.setRdrParticipante(rdrParticipanteB);	
					
					classificacaoAperturaSerieB.setNomeTime(rdrParticipanteB.getTime().getNomeTime());
					classificacaoAperturaSerieB.setNomeDonoTime(rdrParticipanteB.getTime().getNomeDonoTime());
					classificacaoAperturaSerieB.setIdCartolaTime(rdrParticipanteB.getIdTimeCartola());
					
					classificacaoAperturaSerieB.setNrRodadaAtual(new Long(1));
					
					classificacaoAperturaSerieB = (RDRClassificacao) rdrService.atualizar(classificacaoAperturaSerieB);
					
					listaClassificacaoAperturaSerieB.add(classificacaoAperturaSerieB);		
					
					atualizarRDRClassificacao(classificacaoAperturaSerieB.getFaseLiga(), classificacaoAperturaSerieB.getSerie(), new Long(1));
				}	
				
				
				
				//GERAR TABELA APERTURA SERIE A
				if(!gerarTabela(listaParticipantesAperturaSerieA, "A", "SA")) { // String tipoRodada ("A" ou "B"), String serie("SA" ou "SB")
					addErrorMessage("ERRO AO GERAR TABELA DE JOGOS");
				}	
				
				//GERAR TABELA APERTURA SERIE B
				if(!gerarTabela(listaParticipantesAperturaSerieB, "A", "SB")) { // String tipoRodada ("A" ou "B"), String serie("SA" ou "SB")
					addErrorMessage("ERRO AO GERAR TABELA DE JOGOS");
				}	
				
				addInfoMessage("Classificação e Tabela da fase apertura geradas com sucesso");				
			}
			
			init();
			
		} catch (Exception e) {			
			addErrorMessage("ERRO AO GERAR APERTURA.");			
			log.error(e);
			init();
		}			
		return null;
	}
	
	private boolean gerarTabela(List<RDRParticipante> listaParticipantes, String tipo, String serie) {		
		
		try {	
			
			if(listaRDRRodadasAperturaSerieA == null) {
				listaRDRRodadasAperturaSerieA = new ArrayList<RDRRodada>();
			}
			
			if(listaRDRRodadasAperturaSerieB == null) {
				listaRDRRodadasAperturaSerieB = new ArrayList<RDRRodada>();
			}
			
			if(listaRDRRodadasClausuraSerieA == null) {
				listaRDRRodadasClausuraSerieA = new ArrayList<RDRRodada>();
			}
			
			if(listaRDRRodadasClausuraSerieB == null) {
				listaRDRRodadasClausuraSerieB = new ArrayList<RDRRodada>();
			}
			
			if(listaRDRRodadasClausuraSerieC == null) {
				listaRDRRodadasClausuraSerieC = new ArrayList<RDRRodada>();
			}
			
			List<RDRParticipante> listaParticipantesGerarTabelaAUX = listaParticipantes;
			
			int t = listaParticipantesGerarTabelaAUX.size();
			int m = listaParticipantesGerarTabelaAUX.size() / 2;			
			
			for (int i = 0; i < t - 1; i++) {
				
				RDRRodada rdrRodada = new RDRRodada();
				rdrRodada.setNrRDRRodada(new Long(i+1));
				
				if("A".equalsIgnoreCase(tipo)) { // APERTURA	
					
					rdrRodada.setNrRodadaCartola(new Long(i+1) + 4);	
					
				} else if("C".equalsIgnoreCase(tipo)) {
					
					if("SA".equalsIgnoreCase(serie) || "SB".equalsIgnoreCase(serie)) {					
						
						rdrRodada.setNrRodadaCartola(new Long(i+1) + 19);
					
					} else if("SC".equalsIgnoreCase(serie)) {			
						
						rdrRodada.setNrRodadaCartola(new Long(i+1) + 27);
						
					}
					
				}
				
				rdrRodada.setStatusRodada("FT"); // FUTURA
				rdrRodada.setLiga(buscarLigaReisDaResenha());
				
				rdrRodada.setTipoRodada(tipo); // APERTURA OU CLAUSURA
				rdrRodada.setSerieRodada(serie); // SERIE A OU SERIE B OU SERIE C
				
				rdrRodada = (RDRRodada) rdrService.atualizar(rdrRodada);
				
				if("A".equalsIgnoreCase(rdrRodada.getTipoRodada())) { // APERTURA	
					if("SA".equalsIgnoreCase(rdrRodada.getSerieRodada())) {
						listaRDRRodadasAperturaSerieA.add(rdrRodada);
					} else if("SB".equalsIgnoreCase(rdrRodada.getSerieRodada())) {
						listaRDRRodadasAperturaSerieB.add(rdrRodada);						
					} 					
				}
				
				if("C".equalsIgnoreCase(rdrRodada.getTipoRodada())) { // CLAUSURA				
					if("SA".equalsIgnoreCase(rdrRodada.getSerieRodada())) {
						
						listaRDRRodadasClausuraSerieA.add(rdrRodada);
					
					} else if("SB".equalsIgnoreCase(rdrRodada.getSerieRodada())) {
						
						listaRDRRodadasClausuraSerieB.add(rdrRodada);			
						
					} else if("SC".equalsIgnoreCase(rdrRodada.getSerieRodada())) {
						
						listaRDRRodadasClausuraSerieC.add(rdrRodada);			
						
					} 					
				}				
								
				log.info(rdrRodada.getNrRDRRodada() + "ª rodada RDR || "+rdrRodada.getNrRodadaCartola()+"ª RODADA DO CARTOLA FC");
				
				if(rdrRodada.getNrRDRRodada() > 1) {
					
					for (int j = 0; j < m; j++) {	
						
						RDRPontuacao rdrPontuacao = new RDRPontuacao();
						rdrPontuacao.setRdrRodada(rdrRodada);	
						rdrPontuacao.setFase(rdrRodada.getTipoRodada());
						rdrPontuacao.setSerie(rdrRodada.getSerieRodada());
						
						if (j % 2 == 1 || i % 2 == 1 && j == 0) {						
							//log.info(listaParticipantesGerarTabelaAUX.get(t - j - 1).getNomeTime() + " x " + listaParticipantesGerarTabelaAUX.get(j).getNomeTime() + "  ");	
							rdrPontuacao.setRdrParticipanteTimeCasa(listaParticipantesGerarTabelaAUX.get(t - j - 1));
							rdrPontuacao.setNomeTimeCasa(listaParticipantesGerarTabelaAUX.get(t - j - 1).getNomeTime());							
							rdrPontuacao.setRdrParticipanteTimeFora(listaParticipantesGerarTabelaAUX.get(j));
							rdrPontuacao.setNomeTimeFora(listaParticipantesGerarTabelaAUX.get(j).getNomeTime());
							
						} else {
							//log.info(listaParticipantesGerarTabelaAUX.get(j).getNomeTime() + " x " + listaParticipantesGerarTabelaAUX.get(t - j - 1).getNomeTime() + "  ");		
							
							rdrPontuacao.setRdrParticipanteTimeCasa(listaParticipantesGerarTabelaAUX.get(j));
							rdrPontuacao.setNomeTimeCasa(listaParticipantesGerarTabelaAUX.get(j).getNomeTime());							
							rdrPontuacao.setRdrParticipanteTimeFora(listaParticipantesGerarTabelaAUX.get(t - j - 1));
							rdrPontuacao.setNomeTimeFora(listaParticipantesGerarTabelaAUX.get(t - j - 1).getNomeTime());									
						}		
						
						log.info(rdrPontuacao.getNomeTimeCasa() + " x " + rdrPontuacao.getNomeTimeFora() + "  ");
						
						rdrPontuacao = (RDRPontuacao) rdrService.atualizar(rdrPontuacao);
					}	
					
				} else {
					for (int j = 0; j < t && j < m; j++) {
												
						RDRPontuacao rdrPontuacao = new RDRPontuacao();
						rdrPontuacao.setRdrRodada(rdrRodada);	
						rdrPontuacao.setFase(rdrRodada.getTipoRodada());
						rdrPontuacao.setSerie(rdrRodada.getSerieRodada());		
					
						rdrPontuacao.setRdrParticipanteTimeCasa(listaParticipantesGerarTabelaAUX.get(j));
						rdrPontuacao.setNomeTimeCasa(listaParticipantesGerarTabelaAUX.get(j).getNomeTime());	
						
						rdrPontuacao.setRdrParticipanteTimeFora(listaParticipantesGerarTabelaAUX.get(t-j-1));
						rdrPontuacao.setNomeTimeFora(listaParticipantesGerarTabelaAUX.get(t-j-1).getNomeTime());
						
						log.info(rdrPontuacao.getNomeTimeCasa() + " x " + rdrPontuacao.getNomeTimeFora() + "  ");
						
						rdrPontuacao = (RDRPontuacao) rdrService.atualizar(rdrPontuacao);
						
					}
				}
				
				log.info("\n");	
				
				listaParticipantesGerarTabelaAUX.add(1, listaParticipantesGerarTabelaAUX.remove(listaParticipantesGerarTabelaAUX.size() - 1));						
			}	
			
			return true;
			
		} catch (Exception e) {
			log.error(e);
			addErrorMessage("ERRO AO GERAR TABELA DE JOGOS");			
			return false;
		}
		
	}
	
	public String atualizarPontuacaoRodada(RDRRodada rdrRodadaAtualizarPontuacao, List<RDRPontuacao> listaRDRPontuacao) {
				
		List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<ClassificacaoLigaPrincipalDTO>();
		
		Integer anoAtual = 2020; //Calendar.getInstance().get(Calendar.YEAR);	
		
		listaClassificacaoLigaPrincipalDTO = inicioService.buscarHistoricoClassificacaoRodadas(anoAtual, rdrRodadaAtualizarPontuacao.getNrRodadaCartola());	
					
		if(listaClassificacaoLigaPrincipalDTO != null && !listaClassificacaoLigaPrincipalDTO.isEmpty()) {
		
			rdrRodadaAtualizarPontuacao.setStatusRodada("EA");		
			
			rdrRodadaAtualizarPontuacao = (RDRRodada) rdrService.atualizar(rdrRodadaAtualizarPontuacao);
			
			if(rdrRodadaAtualizarPontuacao.getListaRDRPontuacao() == null || rdrRodadaAtualizarPontuacao.getListaRDRPontuacao().isEmpty()){
				rdrRodadaAtualizarPontuacao.setListaRDRPontuacao(new ArrayList<RDRPontuacao>());
				rdrRodadaAtualizarPontuacao.setListaRDRPontuacao(listaRDRPontuacao);
			}
			
			for (ClassificacaoLigaPrincipalDTO classificacaoPrincipalRodadaDTO : listaClassificacaoLigaPrincipalDTO) {	
				
				Time time = timeService.buscarTimePorIdCartola(classificacaoPrincipalRodadaDTO.getIdTimeCartola());			
				
				for (RDRPontuacao rdrPontuacao : rdrRodadaAtualizarPontuacao.getListaRDRPontuacao()) {				
					
					if(rdrPontuacao.getRdrParticipanteTimeCasa().getTime().getId().equals(time.getId())) {
						
						rdrPontuacao.setVrPontuacaoTimeCasa(classificacaoPrincipalRodadaDTO.getPontuacao());					
						
						rdrPontuacao.setVrPontuacaoTimeCasaArredondada(
								arredondarValorDonoDaCasa(classificacaoPrincipalRodadaDTO.getPontuacao()) //ARREDONDAMENTO DENTRO DE CASA
								);					
					}
					
					if(rdrPontuacao.getRdrParticipanteTimeFora().getTime().getId().equals(time.getId())) {
						
						rdrPontuacao.setVrPontuacaoTimeFora(classificacaoPrincipalRodadaDTO.getPontuacao());				
						
						rdrPontuacao.setVrPontuacaoTimeForaArredondada(
								arredondarValorVisitante(classificacaoPrincipalRodadaDTO.getPontuacao()) //ARREDONDAMENTO FORA DE CASA
								);					
					}					
									
					rdrPontuacao = (RDRPontuacao) rdrService.atualizar(rdrPontuacao);		
										
				}	
			}	
			
			for (RDRPontuacao rdrPontuacao : rdrRodadaAtualizarPontuacao.getListaRDRPontuacao()) {					
				
				if(rdrPontuacao.getVrPontuacaoTimeCasaArredondada() > rdrPontuacao.getVrPontuacaoTimeForaArredondada()) {					
					rdrPontuacao.setEmpate("nao");					
					rdrPontuacao.setRdrParticipanteTimeVencedor(rdrPontuacao.getRdrParticipanteTimeCasa());
					rdrPontuacao.setNomeTimeVencedor((rdrPontuacao.getRdrParticipanteTimeCasa().getNomeTime()));	
					rdrPontuacao.setRdrParticipanteTimePerdedor(rdrPontuacao.getRdrParticipanteTimeFora());
					rdrPontuacao.setNomeTimePerdedor((rdrPontuacao.getRdrParticipanteTimeFora().getNomeTime()));
				}					
				
				if(rdrPontuacao.getVrPontuacaoTimeCasaArredondada() < rdrPontuacao.getVrPontuacaoTimeForaArredondada()) {					
					rdrPontuacao.setEmpate("nao");
					rdrPontuacao.setRdrParticipanteTimeVencedor(rdrPontuacao.getRdrParticipanteTimeFora());
					rdrPontuacao.setNomeTimeVencedor((rdrPontuacao.getRdrParticipanteTimeFora().getNomeTime()));
					rdrPontuacao.setRdrParticipanteTimePerdedor(rdrPontuacao.getRdrParticipanteTimeCasa());
					rdrPontuacao.setNomeTimePerdedor((rdrPontuacao.getRdrParticipanteTimeCasa().getNomeTime()));
					
				}
				
				if(rdrPontuacao.getVrPontuacaoTimeCasaArredondada().equals(rdrPontuacao.getVrPontuacaoTimeForaArredondada())) {
					rdrPontuacao.setEmpate("sim");				
					rdrPontuacao.setRdrParticipanteTimeVencedor(null);
					rdrPontuacao.setNomeTimeVencedor("empate");
					rdrPontuacao.setRdrParticipanteTimePerdedor(null);
					rdrPontuacao.setNomeTimePerdedor("empate");				
					rdrPontuacao.setRdrParticipanteTimeEmpateEmCasa(rdrPontuacao.getRdrParticipanteTimeCasa());
					rdrPontuacao.setRdrParticipanteTimeEmpateFora(rdrPontuacao.getRdrParticipanteTimeFora());
				}
				
				rdrPontuacao = (RDRPontuacao) rdrService.atualizar(rdrPontuacao);
			}
			
			atualizarRDRClassificacao(rdrRodadaAtualizarPontuacao.getTipoRodada(), rdrRodadaAtualizarPontuacao.getSerieRodada(), rdrRodadaAtualizarPontuacao.getNrRDRRodada());
						
			Rodada rodadaPrincipal = new Rodada();
			rodadaPrincipal = rodadaService.buscarRodadaDaLigaPrincipalEspecifica(rdrRodadaAtualizarPontuacao.getNrRodadaCartola());			
			
			if(rodadaPrincipal != null) {
				rdrRodadaAtualizarPontuacao.setStatusRodada(rodadaPrincipal.getStatusRodada());				
				rdrRodadaAtualizarPontuacao = (RDRRodada) rdrService.atualizar(rdrRodadaAtualizarPontuacao);
			}
			
			addInfoMessage(rdrRodadaAtualizarPontuacao.getNrRDRRodada()+"ª Rodada Atualizada com Sucesso");
			
			init();
			
		} else {
			addErrorMessage(rdrRodadaAtualizarPontuacao.getNrRodadaCartola()+"ª Rodada Principal do Cartola FC ainda nao está em andamento");			
		}
		
		return null;
	}
	
	public String atualizarPontuacaoRodadaCopa(RDRCopaPontuacao rdrCopa) { //(RDRRodada rdrRodadaAtualizarPontuacao, List<RDRPontuacao> listaRDRPontuacao) {
		
		List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<ClassificacaoLigaPrincipalDTO>();
		
		Integer anoAtual = 2020; //Calendar.getInstance().get(Calendar.YEAR);	
		
		listaClassificacaoLigaPrincipalDTO = inicioService.buscarHistoricoClassificacaoRodadas(anoAtual, rdrCopa.getNrRodadaCartola());	
				
		if(rdrCopa.getNrJogoCopa() < 9) {
		
			if(listaClassificacaoLigaPrincipalDTO != null && !listaClassificacaoLigaPrincipalDTO.isEmpty()) {			
				
				for (ClassificacaoLigaPrincipalDTO classificacaoPrincipalRodadaDTO : listaClassificacaoLigaPrincipalDTO) {	
					
					Time time = timeService.buscarTimePorIdCartola(classificacaoPrincipalRodadaDTO.getIdTimeCartola());		
					
					if(rdrCopa.getRdrParticipanteTimeCasa().getTime().getId().equals(time.getId())) {
						
						rdrCopa.setVrPontuacaoTimeCasa(classificacaoPrincipalRodadaDTO.getPontuacao());					
						
						rdrCopa.setVrPontuacaoTimeCasaArredondada(
								arredondarValorDonoDaCasa(classificacaoPrincipalRodadaDTO.getPontuacao()) //ARREDONDAMENTO DENTRO DE CASA
								);					
					}
					
					if(rdrCopa.getRdrParticipanteTimeFora().getTime().getId().equals(time.getId())) {
						
						rdrCopa.setVrPontuacaoTimeFora(classificacaoPrincipalRodadaDTO.getPontuacao());				
						
						rdrCopa.setVrPontuacaoTimeForaArredondada(
								arredondarValorVisitante(classificacaoPrincipalRodadaDTO.getPontuacao()) //ARREDONDAMENTO FORA DE CASA
								);					
					}					
									
					rdrCopa = (RDRCopaPontuacao) rdrService.atualizar(rdrCopa);		
					
				}
				
				//Atualizar classificacao Copa
				atualizarClassificacaoCopa(rdrCopa);
									
				init();		
				
			} else {
				addErrorMessage(rdrCopa.getNrRodadaCartola()+"ª Rodada Principal do Cartola FC ainda nao está em andamento");			
			}
		
		} else if(rdrCopa.getNrJogoCopa().intValue() == 9 && rdrCopa.getNrRodadaCartola().intValue() == 38) {
				
			if(listaClassificacaoLigaPrincipalDTO != null && !listaClassificacaoLigaPrincipalDTO.isEmpty()) {			
				
				for (ClassificacaoLigaPrincipalDTO classificacaoPrincipalRodadaDTO : listaClassificacaoLigaPrincipalDTO) {	
					
					Time time = timeService.buscarTimePorIdCartola(classificacaoPrincipalRodadaDTO.getIdTimeCartola());		
					
					if(rdrCopa.getRdrParticipanteTimeCasa().getTime().getId().equals(time.getId())) { //FINAL NAO TEM ARREDONDAMENTO
						
						rdrCopa.setVrPontuacaoTimeCasa(classificacaoPrincipalRodadaDTO.getPontuacao());					
						
						rdrCopa.setVrPontuacaoTimeCasaArredondada(classificacaoPrincipalRodadaDTO.getPontuacao());					
					}
					
					if(rdrCopa.getRdrParticipanteTimeFora().getTime().getId().equals(time.getId())) { //FINAL NAO TEM ARREDONDAMENTO
						
						rdrCopa.setVrPontuacaoTimeFora(classificacaoPrincipalRodadaDTO.getPontuacao());				
						
						rdrCopa.setVrPontuacaoTimeForaArredondada(classificacaoPrincipalRodadaDTO.getPontuacao() );					
					}					
									
					rdrCopa = (RDRCopaPontuacao) rdrService.atualizar(rdrCopa);		
					
				}
				
				//Atualizar classificacao Copa
				atualizarClassificacaoCopa(rdrCopa);
									
				init();		
			}			
			
		}
		
		return null;
	}
	
	private void atualizarClassificacaoCopa(RDRCopaPontuacao rdrCopaPontuacao) {
		
		if(rdrCopaPontuacao.getVrPontuacaoTimeCasaArredondada() > rdrCopaPontuacao.getVrPontuacaoTimeForaArredondada()) {					
			rdrCopaPontuacao.setEmpate("nao");					
			rdrCopaPontuacao.setRdrParticipanteTimeVencedor(rdrCopaPontuacao.getRdrParticipanteTimeCasa());
			rdrCopaPontuacao.setNomeTimeVencedor((rdrCopaPontuacao.getRdrParticipanteTimeCasa().getNomeTime()));	
			rdrCopaPontuacao.setRdrParticipanteTimePerdedor(rdrCopaPontuacao.getRdrParticipanteTimeFora());
			rdrCopaPontuacao.setNomeTimePerdedor((rdrCopaPontuacao.getRdrParticipanteTimeFora().getNomeTime()));
		}					
		
		if(rdrCopaPontuacao.getVrPontuacaoTimeCasaArredondada() < rdrCopaPontuacao.getVrPontuacaoTimeForaArredondada()) {					
			rdrCopaPontuacao.setEmpate("nao");
			rdrCopaPontuacao.setRdrParticipanteTimeVencedor(rdrCopaPontuacao.getRdrParticipanteTimeFora());
			rdrCopaPontuacao.setNomeTimeVencedor((rdrCopaPontuacao.getRdrParticipanteTimeFora().getNomeTime()));
			rdrCopaPontuacao.setRdrParticipanteTimePerdedor(rdrCopaPontuacao.getRdrParticipanteTimeCasa());
			rdrCopaPontuacao.setNomeTimePerdedor((rdrCopaPontuacao.getRdrParticipanteTimeCasa().getNomeTime()));
			
		}
		
		if(rdrCopaPontuacao.getVrPontuacaoTimeCasaArredondada().equals(rdrCopaPontuacao.getVrPontuacaoTimeForaArredondada())) {
			rdrCopaPontuacao.setEmpate("sim");						
			rdrCopaPontuacao.setNomeTimeVencedor("empate");
			rdrCopaPontuacao.setRdrParticipanteTimeEmpateEmCasa(rdrCopaPontuacao.getRdrParticipanteTimeCasa());
			rdrCopaPontuacao.setRdrParticipanteTimeEmpateFora(rdrCopaPontuacao.getRdrParticipanteTimeFora());		
						
			Integer anoAtual = 2020; // Calendar.getInstance().get(Calendar.YEAR);			
			ClassificacaoLigaPrincipalDTO classificacaoPrincipalTimeCasa = new ClassificacaoLigaPrincipalDTO();						
			classificacaoPrincipalTimeCasa = inicioService.buscarPontuacaoLigaPrincipalTime(anoAtual, rdrCopaPontuacao.getRdrParticipanteTimeCasa().getTime()).get(0);
			
			ClassificacaoLigaPrincipalDTO classificacaoPrincipalTimeFora = new ClassificacaoLigaPrincipalDTO();						
			classificacaoPrincipalTimeFora = inicioService.buscarPontuacaoLigaPrincipalTime(anoAtual, rdrCopaPontuacao.getRdrParticipanteTimeFora().getTime()).get(0);
			
			if(classificacaoPrincipalTimeCasa.getPontuacao().doubleValue() >  classificacaoPrincipalTimeFora.getPontuacao().doubleValue()) {				
				rdrCopaPontuacao.setRdrParticipanteTimeVencedor(rdrCopaPontuacao.getRdrParticipanteTimeCasa());
				rdrCopaPontuacao.setNomeTimeVencedor((rdrCopaPontuacao.getRdrParticipanteTimeCasa().getNomeTime()));				
				rdrCopaPontuacao.setRdrParticipanteTimePerdedor(rdrCopaPontuacao.getRdrParticipanteTimeFora());
				rdrCopaPontuacao.setNomeTimePerdedor((rdrCopaPontuacao.getRdrParticipanteTimeFora().getNomeTime()));	
			} else if(classificacaoPrincipalTimeCasa.getPontuacao().doubleValue() < classificacaoPrincipalTimeFora.getPontuacao().doubleValue()) {				
				rdrCopaPontuacao.setRdrParticipanteTimeVencedor(rdrCopaPontuacao.getRdrParticipanteTimeFora());
				rdrCopaPontuacao.setNomeTimeVencedor((rdrCopaPontuacao.getRdrParticipanteTimeFora().getNomeTime()));				
				rdrCopaPontuacao.setRdrParticipanteTimePerdedor(rdrCopaPontuacao.getRdrParticipanteTimeCasa());
				rdrCopaPontuacao.setNomeTimePerdedor((rdrCopaPontuacao.getRdrParticipanteTimeCasa().getNomeTime()));									
			}
			
		}
		
		rdrCopaPontuacao = (RDRCopaPontuacao) rdrService.atualizar(rdrCopaPontuacao);
		
		if(rdrCopaPontuacao.getNrRodadaCartola() >= 35) {
			
			RDRCopaPontuacao jogo1 = rdrService.buscarRDRCopaPontuacaoPorNrJogoCopa(new Long(1));
			RDRCopaPontuacao jogo2 = rdrService.buscarRDRCopaPontuacaoPorNrJogoCopa(new Long(2));
			RDRCopaPontuacao jogo3 = rdrService.buscarRDRCopaPontuacaoPorNrJogoCopa(new Long(3));
			RDRCopaPontuacao jogo4 = rdrService.buscarRDRCopaPontuacaoPorNrJogoCopa(new Long(4));	
						
			RDRCopaPontuacao jogo5 = rdrService.buscarRDRCopaPontuacaoPorNrJogoCopa(new Long(5));
			RDRCopaPontuacao jogo6 = rdrService.buscarRDRCopaPontuacaoPorNrJogoCopa(new Long(6));
			RDRCopaPontuacao jogo7 = rdrService.buscarRDRCopaPontuacaoPorNrJogoCopa(new Long(7));
			RDRCopaPontuacao jogo8 = rdrService.buscarRDRCopaPontuacaoPorNrJogoCopa(new Long(8));
			
			RDRCopaPontuacao jogo9 = rdrService.buscarRDRCopaPontuacaoPorNrJogoCopa(new Long(9));
			
			jogo5.setRdrParticipanteTimeCasa(jogo1.getRdrParticipanteTimeVencedor());	
			jogo5.setRdrParticipanteTimeFora(jogo2.getRdrParticipanteTimeVencedor());				
			jogo6.setRdrParticipanteTimeCasa(jogo3.getRdrParticipanteTimeVencedor());	
			jogo6.setRdrParticipanteTimeFora(jogo4.getRdrParticipanteTimeVencedor());
			
			jogo7.setRdrParticipanteTimeCasa(jogo2.getRdrParticipanteTimeVencedor());	
			jogo7.setRdrParticipanteTimeFora(jogo1.getRdrParticipanteTimeVencedor());			
			jogo8.setRdrParticipanteTimeCasa(jogo4.getRdrParticipanteTimeVencedor());	
			jogo8.setRdrParticipanteTimeFora(jogo3.getRdrParticipanteTimeVencedor());
						
			jogo5.setNomeTimeCasa(jogo5.getRdrParticipanteTimeCasa() != null ? jogo5.getRdrParticipanteTimeCasa().getNomeTime() : null);
			jogo5.setNomeTimeFora(jogo5.getRdrParticipanteTimeFora() != null ? jogo5.getRdrParticipanteTimeFora().getNomeTime() : null);
			
			jogo6.setNomeTimeCasa(jogo6.getRdrParticipanteTimeCasa() != null ? jogo6.getRdrParticipanteTimeCasa().getNomeTime() : null);
			jogo6.setNomeTimeFora(jogo6.getRdrParticipanteTimeFora() != null ? jogo6.getRdrParticipanteTimeFora().getNomeTime() : null);
			
			jogo7.setNomeTimeCasa(jogo7.getRdrParticipanteTimeCasa() != null ? jogo7.getRdrParticipanteTimeCasa().getNomeTime() : null);
			jogo7.setNomeTimeFora(jogo7.getRdrParticipanteTimeFora() != null ? jogo7.getRdrParticipanteTimeFora().getNomeTime() : null);
			
			jogo8.setNomeTimeCasa(jogo8.getRdrParticipanteTimeCasa() != null ? jogo8.getRdrParticipanteTimeCasa().getNomeTime() : null);		
			jogo8.setNomeTimeFora(jogo8.getRdrParticipanteTimeFora() != null ? jogo8.getRdrParticipanteTimeFora().getNomeTime() : null);	
			
			
			List<RDRCopaPontuacao> jogos5678 = new ArrayList<RDRCopaPontuacao>();
			jogos5678.add(jogo5);
			jogos5678.add(jogo6);
			jogos5678.add(jogo7);
			jogos5678.add(jogo8);
			
			Double somaVencedorJogo1 = 0.0;
			Double somaVencedorJogo2 = 0.0;
			Double somaVencedorJogo3 = 0.0;
			Double somaVencedorJogo4 = 0.0;
			
			for (RDRCopaPontuacao rdrCopa : jogos5678) {
				
				if("Vencedor Jogo 1".equalsIgnoreCase(rdrCopa.getDescricaoTimeRodadaCasa())) {
					somaVencedorJogo1 += rdrCopa.getVrPontuacaoTimeCasaArredondada();
				}			
				if("Vencedor Jogo 1".equalsIgnoreCase(rdrCopa.getDescricaoTimeRodadaFora())) {
					somaVencedorJogo1 += rdrCopa.getVrPontuacaoTimeForaArredondada();
				}
				
				if("Vencedor Jogo 2".equalsIgnoreCase(rdrCopa.getDescricaoTimeRodadaCasa())) {
					somaVencedorJogo2 += rdrCopa.getVrPontuacaoTimeCasaArredondada();
				}			
				if("Vencedor Jogo 2".equalsIgnoreCase(rdrCopa.getDescricaoTimeRodadaFora())) {
					somaVencedorJogo2 += rdrCopa.getVrPontuacaoTimeForaArredondada();
				}
				
				if("Vencedor Jogo 3".equalsIgnoreCase(rdrCopa.getDescricaoTimeRodadaCasa())) {
					somaVencedorJogo3 += rdrCopa.getVrPontuacaoTimeCasaArredondada();
				}			
				if("Vencedor Jogo 3".equalsIgnoreCase(rdrCopa.getDescricaoTimeRodadaFora())) {
					somaVencedorJogo3 += rdrCopa.getVrPontuacaoTimeForaArredondada();
				}
				
				if("Vencedor Jogo 4".equalsIgnoreCase(rdrCopa.getDescricaoTimeRodadaCasa())) {
					somaVencedorJogo4 += rdrCopa.getVrPontuacaoTimeCasaArredondada();
				}			
				if("Vencedor Jogo 4".equalsIgnoreCase(rdrCopa.getDescricaoTimeRodadaFora())) {
					somaVencedorJogo4 += rdrCopa.getVrPontuacaoTimeForaArredondada();
				}
			}
			
			if(somaVencedorJogo1 > somaVencedorJogo2) {				
				jogo9.setRdrParticipanteTimeCasa(jogo5.getRdrParticipanteTimeCasa());				
			
			} else if(somaVencedorJogo1 < somaVencedorJogo2) {				
				jogo9.setRdrParticipanteTimeCasa(jogo5.getRdrParticipanteTimeFora());				
			}
			
			if(somaVencedorJogo3 > somaVencedorJogo4) {				
				jogo9.setRdrParticipanteTimeFora(jogo6.getRdrParticipanteTimeCasa());			
			
			} else if(somaVencedorJogo3 < somaVencedorJogo4) {				
				jogo9.setRdrParticipanteTimeFora(jogo6.getRdrParticipanteTimeFora());				
			}
			
			Integer anoAtual = 2020; // Calendar.getInstance().get(Calendar.YEAR);	
			
			if(somaVencedorJogo1 == somaVencedorJogo2) {
									
				ClassificacaoLigaPrincipalDTO classificacaoPrincipalTimeCasa = new ClassificacaoLigaPrincipalDTO();						
				classificacaoPrincipalTimeCasa = inicioService.buscarPontuacaoLigaPrincipalTime(anoAtual, jogo5.getRdrParticipanteTimeCasa().getTime()).get(0);
				
				ClassificacaoLigaPrincipalDTO classificacaoPrincipalTimeFora = new ClassificacaoLigaPrincipalDTO();						
				classificacaoPrincipalTimeFora = inicioService.buscarPontuacaoLigaPrincipalTime(anoAtual, jogo5.getRdrParticipanteTimeFora().getTime()).get(0);
				
				if(classificacaoPrincipalTimeCasa.getPontuacao().doubleValue() > classificacaoPrincipalTimeFora.getPontuacao().doubleValue()) {				
					
					jogo9.setRdrParticipanteTimeCasa(jogo5.getRdrParticipanteTimeCasa());		
				
				} else if(classificacaoPrincipalTimeCasa.getPontuacao().doubleValue() < classificacaoPrincipalTimeFora.getPontuacao().doubleValue()) {			
					
					jogo9.setRdrParticipanteTimeCasa(jogo5.getRdrParticipanteTimeFora());		
					
				}
			}
			
			if(somaVencedorJogo3 == somaVencedorJogo4) {			
				
				ClassificacaoLigaPrincipalDTO classificacaoPrincipalTimeCasa = new ClassificacaoLigaPrincipalDTO();						
				classificacaoPrincipalTimeCasa = inicioService.buscarPontuacaoLigaPrincipalTime(anoAtual, jogo6.getRdrParticipanteTimeCasa().getTime()).get(0);
				
				ClassificacaoLigaPrincipalDTO classificacaoPrincipalTimeFora = new ClassificacaoLigaPrincipalDTO();						
				classificacaoPrincipalTimeFora = inicioService.buscarPontuacaoLigaPrincipalTime(anoAtual, jogo6.getRdrParticipanteTimeFora().getTime()).get(0);
			
				if(classificacaoPrincipalTimeCasa.getPontuacao().doubleValue() > classificacaoPrincipalTimeFora.getPontuacao().doubleValue()) {				
					
					jogo9.setRdrParticipanteTimeCasa(jogo6.getRdrParticipanteTimeCasa());		
					
				} else if(classificacaoPrincipalTimeCasa.getPontuacao().doubleValue() < classificacaoPrincipalTimeFora.getPontuacao().doubleValue()) {		
					
					jogo9.setRdrParticipanteTimeCasa(jogo6.getRdrParticipanteTimeFora());							
				}
			
			}
			
			jogo9.setNomeTimeCasa(jogo9.getRdrParticipanteTimeCasa() != null ? jogo9.getRdrParticipanteTimeCasa().getNomeTime() : null);		
			jogo9.setNomeTimeFora(jogo9.getRdrParticipanteTimeFora() != null ? jogo9.getRdrParticipanteTimeFora().getNomeTime() : null);	
									
			jogo5 = (RDRCopaPontuacao) rdrService.atualizar(jogo5);
			jogo6 = (RDRCopaPontuacao) rdrService.atualizar(jogo6);
			jogo7 = (RDRCopaPontuacao) rdrService.atualizar(jogo7);
			jogo8 = (RDRCopaPontuacao) rdrService.atualizar(jogo8);
			jogo9 = (RDRCopaPontuacao) rdrService.atualizar(jogo9);
		}
				
	}

	@SuppressWarnings("unchecked")
	public void atualizarRDRClassificacao(String fase, String serie, Long nrRodadaAtual) {		
		
		List<RDRClassificacao> listaRDRClassificacao = new ArrayList<RDRClassificacao>();	
		listaRDRClassificacao = rdrService.buscarRDRClassificacao(fase, serie);
				
		for (RDRClassificacao rdrClassificacao : listaRDRClassificacao) {	
			
			List <RDRPontuacao> listaRdrPontuacao = new ArrayList<RDRPontuacao>();					
			StringBuilder sql;
			
			/** QTD VITORIAS **/
			sql = new StringBuilder();
			sql.append("select o from ").append(RDRPontuacao.class.getSimpleName()).append(" o where 1=1 ");			
			sql.append(" and o.rdrParticipanteTimeVencedor.id = ").append(rdrClassificacao.getRdrParticipante().getId());						
			listaRdrPontuacao = (List<RDRPontuacao>) rdrService.consultarPorQuery(sql.toString(), 0, 0);	
			
			if(listaRdrPontuacao != null && !listaRdrPontuacao.isEmpty()) {
				rdrClassificacao.setQtdVitorias(new Long(listaRdrPontuacao.size()));
			} else {
				rdrClassificacao.setQtdVitorias(new Long(0));
			}
			
			/** QTD DERROTAS **/
			sql = new StringBuilder();
			sql.append("select o from ").append(RDRPontuacao.class.getSimpleName()).append(" o where 1=1 ");			
			sql.append(" and o.rdrParticipanteTimePerdedor.id = ").append(rdrClassificacao.getRdrParticipante().getId());						
			listaRdrPontuacao = (List<RDRPontuacao>) rdrService.consultarPorQuery(sql.toString(), 0, 0);	
			
			if(listaRdrPontuacao != null && !listaRdrPontuacao.isEmpty()) {
				rdrClassificacao.setQtdDerrotas(new Long(listaRdrPontuacao.size()));
			} else {
				rdrClassificacao.setQtdDerrotas(new Long(0));
			}
			
			/** QTD Empates **/
			sql = new StringBuilder();
			sql.append("select o from ").append(RDRPontuacao.class.getSimpleName()).append(" o where 1=1 ");			
			sql.append(" and o.empate = 'sim' ");
			sql.append(" and (o.rdrParticipanteTimeEmpateEmCasa.id = ").append(rdrClassificacao.getRdrParticipante().getId());
			sql.append("      or o.rdrParticipanteTimeEmpateFora.id = " ).append(rdrClassificacao.getRdrParticipante().getId()).append(") ");
			
			listaRdrPontuacao = (List<RDRPontuacao>) rdrService.consultarPorQuery(sql.toString(), 0, 0);	
			
			if(listaRdrPontuacao != null && !listaRdrPontuacao.isEmpty()) {
				rdrClassificacao.setQtdEmpates(new Long(listaRdrPontuacao.size()));
			} else {
				rdrClassificacao.setQtdEmpates(new Long(0));
			}			
			
			rdrClassificacao.setQtdJogosDisputados(rdrClassificacao.getQtdVitorias()+rdrClassificacao.getQtdDerrotas()+rdrClassificacao.getQtdEmpates());
			
			Double vrPontos = new Double((rdrClassificacao.getQtdVitorias() * 3) + (rdrClassificacao.getQtdEmpates() * 1) + (rdrClassificacao.getQtdDerrotas() * 0));
			
			rdrClassificacao.setVrPontos(vrPontos);
			
			rdrClassificacao.setNrRodadaAtual(nrRodadaAtual);
			
			Integer anoAtual = 2020; // Calendar.getInstance().get(Calendar.YEAR);
			
			List<ClassificacaoLigaPrincipalDTO> listaClassificacaoPrincipalAteRodadaX = new ArrayList<ClassificacaoLigaPrincipalDTO>();
			
			if("A".equalsIgnoreCase(fase)) {
				listaClassificacaoPrincipalAteRodadaX = inicioService.buscarPontuacaoLigaPrincipalTimeAteRodadaX(anoAtual, rdrClassificacao.getRdrParticipante().getTime(), 19);
			} else if("C".equalsIgnoreCase(fase)) {
				listaClassificacaoPrincipalAteRodadaX = inicioService.buscarPontuacaoLigaPrincipalTime(anoAtual, rdrClassificacao.getRdrParticipante().getTime());
			}
			 
			Double vrPontuacaoAtualLigaPrincipalCartola =  0.0;
			
			if(listaClassificacaoPrincipalAteRodadaX!= null && !listaClassificacaoPrincipalAteRodadaX.isEmpty()) { 
				vrPontuacaoAtualLigaPrincipalCartola = listaClassificacaoPrincipalAteRodadaX.get(0).getPontuacao();
			}
			
			rdrClassificacao.setVrPontuacaoAtualLigaPrincipalCartola(vrPontuacaoAtualLigaPrincipalCartola);
						
			rdrClassificacao = (RDRClassificacao) rdrService.atualizar(rdrClassificacao);				
		}
						
	}

	public Double arredondarValorDonoDaCasa(Double valor) {
		
		log.info("Vai Arredondar '"+valor+"' PARA TIME JOGANDO DENTRO DE CASA");
		
		Double valorArredondarDentrodeCasa = valor;			
				
		Double restoDaDivisaoPor5 = (double) (valorArredondarDentrodeCasa.longValue() % 5);
			
		double valorSomar = 0;
				
		if(restoDaDivisaoPor5 > 0) {			
			for (int j = restoDaDivisaoPor5.intValue(); j < 5; j++) {
				valorSomar++;
			}
		}
		
		Double pontuacaoFinal = valorArredondarDentrodeCasa.longValue()+valorSomar;
		
		log.info("Pontuacao Final Dentro de Casa: "+pontuacaoFinal+"\n");
		
		return pontuacaoFinal;
	}
	
	public Double arredondarValorVisitante(Double valor) {
		
		log.info("Vai Arredondar '"+valor+"' PARA TIME JOGANDO FORA DE CASA");
		
		Double valorArredondarForadeCasa = valor;				
		
		Double restoDaDivisaoPor5 = (double) (valorArredondarForadeCasa.longValue()%5);
			
		double valorDiminuir = 0;
				
		if(restoDaDivisaoPor5 > 0) {			
			for (int j = 0; j < restoDaDivisaoPor5.intValue(); j++) {
				valorDiminuir++;
			}
		}
		
		Double pontuacaoFinal = valorArredondarForadeCasa.longValue()-valorDiminuir;
		
		log.info("Pontuacao Final Fora de Casa: "+pontuacaoFinal+"\n");
		
		return pontuacaoFinal;
	}
	

	public Liga buscarLigaReisDaResenha() {		
		Integer anoAtual = 2020; //Calendar.getInstance().get(Calendar.YEAR);	
		List<Liga> listaLigas = inicioService.buscarLigas(anoAtual);		
		if(listaLigas != null && !listaLigas.isEmpty()) {			
			for (Liga liga : listaLigas) {	
				if(liga.getNomeLiga().toUpperCase().contains("RESENHA")) {
					return liga;
				}
			}
		}			
		return null;			
	}

	public Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(Usuario usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public List<RDRParticipante> getListaParticipantesAperturaSerieA() {
		return listaParticipantesAperturaSerieA;
	}

	public void setListaParticipantesAperturaSerieA(List<RDRParticipante> listaParticipantesAperturaSerieA) {
		this.listaParticipantesAperturaSerieA = listaParticipantesAperturaSerieA;
	}

	public List<RDRParticipante> getListaParticipantesAperturaSerieB() {
		return listaParticipantesAperturaSerieB;
	}

	public void setListaParticipantesAperturaSerieB(List<RDRParticipante> listaParticipantesAperturaSerieB) {
		this.listaParticipantesAperturaSerieB = listaParticipantesAperturaSerieB;
	}

	public List<RDRClassificacao> getListaClassificacaoAperturaSerieA() {
		return listaClassificacaoAperturaSerieA;
	}

	public void setListaClassificacaoAperturaSerieA(List<RDRClassificacao> listaClassificacaoAperturaSerieA) {
		this.listaClassificacaoAperturaSerieA = listaClassificacaoAperturaSerieA;
	}

	public List<RDRClassificacao> getListaClassificacaoAperturaSerieB() {
		return listaClassificacaoAperturaSerieB;
	}

	public void setListaClassificacaoAperturaSerieB(List<RDRClassificacao> listaClassificacaoAperturaSerieB) {
		this.listaClassificacaoAperturaSerieB = listaClassificacaoAperturaSerieB;
	}
	
	public List<RDRRodada> getListaRDRRodadasAperturaSerieA() {
		return listaRDRRodadasAperturaSerieA;
	}

	public void setListaRDRRodadasAperturaSerieA(List<RDRRodada> listaRDRRodadasAperturaSerieA) {
		this.listaRDRRodadasAperturaSerieA = listaRDRRodadasAperturaSerieA;
	}

	public List<RDRRodada> getListaRDRRodadasAperturaSerieB() {
		return listaRDRRodadasAperturaSerieB;
	}

	public void setListaRDRRodadasAperturaSerieB(List<RDRRodada> listaRDRRodadasAperturaSerieB) {
		this.listaRDRRodadasAperturaSerieB = listaRDRRodadasAperturaSerieB;
	}

	public List<ClassificacaoLigaPrincipalDTO> getListaClassificacaoLigaPrincipalAteRodada4() {
		return listaClassificacaoLigaPrincipalAteRodada4;
	}

	public void setListaClassificacaoLigaPrincipalAteRodada4(List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalAteRodada4) {
		this.listaClassificacaoLigaPrincipalAteRodada4 = listaClassificacaoLigaPrincipalAteRodada4;
	}

	public List<RDRParticipante> getListaParticipantesClausuraSerieA() {
		return listaParticipantesClausuraSerieA;
	}

	public void setListaParticipantesClausuraSerieA(List<RDRParticipante> listaParticipantesClausuraSerieA) {
		this.listaParticipantesClausuraSerieA = listaParticipantesClausuraSerieA;
	}

	public List<RDRClassificacao> getListaClassificacaoClausuraSerieA() {
		return listaClassificacaoClausuraSerieA;
	}

	public void setListaClassificacaoClausuraSerieA(List<RDRClassificacao> listaClassificacaoClausuraSerieA) {
		this.listaClassificacaoClausuraSerieA = listaClassificacaoClausuraSerieA;
	}

	public List<RDRRodada> getListaRDRRodadasClausuraSerieA() {
		return listaRDRRodadasClausuraSerieA;
	}

	public void setListaRDRRodadasClausuraSerieA(List<RDRRodada> listaRDRRodadasClausuraSerieA) {
		this.listaRDRRodadasClausuraSerieA = listaRDRRodadasClausuraSerieA;
	}

	public List<RDRParticipante> getListaParticipantesClausuraSerieB() {
		return listaParticipantesClausuraSerieB;
	}

	public void setListaParticipantesClausuraSerieB(List<RDRParticipante> listaParticipantesClausuraSerieB) {
		this.listaParticipantesClausuraSerieB = listaParticipantesClausuraSerieB;
	}

	public List<RDRClassificacao> getListaClassificacaoClausuraSerieB() {
		return listaClassificacaoClausuraSerieB;
	}

	public void setListaClassificacaoClausuraSerieB(List<RDRClassificacao> listaClassificacaoClausuraSerieB) {
		this.listaClassificacaoClausuraSerieB = listaClassificacaoClausuraSerieB;
	}

	public List<RDRRodada> getListaRDRRodadasClausuraSerieB() {
		return listaRDRRodadasClausuraSerieB;
	}

	public void setListaRDRRodadasClausuraSerieB(List<RDRRodada> listaRDRRodadasClausuraSerieB) {
		this.listaRDRRodadasClausuraSerieB = listaRDRRodadasClausuraSerieB;
	}

	public List<ClassificacaoLigaPrincipalDTO> getListaClassificacaoLigaPrincipalAteRodada19() {
		return listaClassificacaoLigaPrincipalAteRodada19;
	}

	public void setListaClassificacaoLigaPrincipalAteRodada19(List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalAteRodada19) {
		this.listaClassificacaoLigaPrincipalAteRodada19 = listaClassificacaoLigaPrincipalAteRodada19;
	}

	public List<RDRCopaPontuacao> getListaRDRCopa() {
		return listaRDRCopa;
	}

	public void setListaRDRCopa(List<RDRCopaPontuacao> listaRDRCopa) {
		this.listaRDRCopa = listaRDRCopa;
	}

	public List<RDRParticipante> getListaRDRParticipantesCopa() {
		return listaRDRParticipantesCopa;
	}

	public void setListaRDRParticipantesCopa(List<RDRParticipante> listaRDRParticipantesCopa) {
		this.listaRDRParticipantesCopa = listaRDRParticipantesCopa;
	}

	public RDRRodada getRodadaEmAndamentoSerieAApertura() {
		return rodadaEmAndamentoSerieAApertura;
	}

	public void setRodadaEmAndamentoSerieAApertura(RDRRodada rodadaEmAndamentoSerieAApertura) {
		this.rodadaEmAndamentoSerieAApertura = rodadaEmAndamentoSerieAApertura;
	}

	public RDRRodada getRodadaEmAndamentoSerieBApertura() {
		return rodadaEmAndamentoSerieBApertura;
	}

	public void setRodadaEmAndamentoSerieBApertura(RDRRodada rodadaEmAndamentoSerieBApertura) {
		this.rodadaEmAndamentoSerieBApertura = rodadaEmAndamentoSerieBApertura;
	}

	public RDRRodada getRodadaEmAndamentoSerieAClausura() {
		return rodadaEmAndamentoSerieAClausura;
	}

	public void setRodadaEmAndamentoSerieAClausura(RDRRodada rodadaEmAndamentoSerieAClausura) {
		this.rodadaEmAndamentoSerieAClausura = rodadaEmAndamentoSerieAClausura;
	}

	public RDRRodada getRodadaEmAndamentoSerieBClausura() {
		return rodadaEmAndamentoSerieBClausura;
	}

	public void setRodadaEmAndamentoSerieBClausura(RDRRodada rodadaEmAndamentoSerieBClausura) {
		this.rodadaEmAndamentoSerieBClausura = rodadaEmAndamentoSerieBClausura;
	}

	public List<RDRParticipante> getListaParticipantesClausuraSerieC() {
		return listaParticipantesClausuraSerieC;
	}

	public void setListaParticipantesClausuraSerieC(List<RDRParticipante> listaParticipantesClausuraSerieC) {
		this.listaParticipantesClausuraSerieC = listaParticipantesClausuraSerieC;
	}

	public List<RDRClassificacao> getListaClassificacaoClausuraSerieC() {
		return listaClassificacaoClausuraSerieC;
	}

	public void setListaClassificacaoClausuraSerieC(List<RDRClassificacao> listaClassificacaoClausuraSerieC) {
		this.listaClassificacaoClausuraSerieC = listaClassificacaoClausuraSerieC;
	}

	public List<RDRRodada> getListaRDRRodadasClausuraSerieC() {
		return listaRDRRodadasClausuraSerieC;
	}

	public void setListaRDRRodadasClausuraSerieC(List<RDRRodada> listaRDRRodadasClausuraSerieC) {
		this.listaRDRRodadasClausuraSerieC = listaRDRRodadasClausuraSerieC;
	}

	public RDRRodada getRodadaEmAndamentoSerieCClausura() {
		return rodadaEmAndamentoSerieCClausura;
	}

	public void setRodadaEmAndamentoSerieCClausura(RDRRodada rodadaEmAndamentoSerieCClausura) {
		this.rodadaEmAndamentoSerieCClausura = rodadaEmAndamentoSerieCClausura;
	}	
	
	
	
}
