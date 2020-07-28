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
import br.com.reisdaresenha.model.RDRParticipante;
import br.com.reisdaresenha.model.RDRPontuacao;
import br.com.reisdaresenha.model.RDRRodada;
import br.com.reisdaresenha.model.Rodada;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
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
	
	private CartolaRestFulClient servicoCartola;
	
	private Usuario usuarioLogado;	
	
	/** VARIAVEIS APERTURA **/
	private List<RDRParticipante> listaParticipantesAperturaSerieA;
	private List<RDRClassificacao> listaClassificacaoAperturaSerieA;	
	private List<RDRRodada> listaRDRRodadasAperturaSerieA;
	
	private List<RDRParticipante> listaParticipantesAperturaSerieB;
	private List<RDRClassificacao> listaClassificacaoAperturaSerieB;
	private List<RDRRodada> listaRDRRodadasAperturaSerieB;	
	
	private List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalAteRodada4;	
	/** VARIAVEIS APERTURA **/
	
	
		
	@PostConstruct
	public void init() {
		
		try {	
			log.info("RDRControl --- INIT()");
			
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);	
			usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");		
			servicoCartola = new CartolaRestFulClient();	
			
			/** APERTURA **/
			listaParticipantesAperturaSerieA = new ArrayList<RDRParticipante>();
			listaParticipantesAperturaSerieA = rdrService.buscarRDRParticipantes("A", "SA");
			
			listaParticipantesAperturaSerieB = new ArrayList<RDRParticipante>();
			listaParticipantesAperturaSerieB = rdrService.buscarRDRParticipantes("A", "SB");
						
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
			
			listaClassificacaoLigaPrincipalAteRodada4 = new ArrayList<ClassificacaoLigaPrincipalDTO>();
			
			Integer anoAtual = 2020; //Calendar.getInstance().get(Calendar.YEAR);				
			listaClassificacaoLigaPrincipalAteRodada4 = inicioService.buscarClassificacaoLigaPrincipalAteRodada4(anoAtual);
			/** APERTURA **/	
						
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}
	
	public String btnGerarParticipantesApertura() {		
		try {			
			
			List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<ClassificacaoLigaPrincipalDTO>();			
			
			Integer anoAtual = 2020; //Calendar.getInstance().get(Calendar.YEAR);				
			
			listaClassificacaoLigaPrincipalDTO = inicioService.buscarClassificacaoLigaPrincipalAteRodada4(anoAtual);	
			
			listaParticipantesAperturaSerieA = new ArrayList<RDRParticipante>();
			
			listaParticipantesAperturaSerieB = new ArrayList<RDRParticipante>();
						
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
						listaParticipantesAperturaSerieA.add(participante);					
					
					} else if(i >= 16) {						
						participante.setSerieParticipante("SB");				
						participante = (RDRParticipante) timeService.atualizar(participante);
						listaParticipantesAperturaSerieB.add(participante);
					}
					
				}
			}		
			
			
		} catch (Exception e) {			
			addErrorMessage("ERRO AO GERAR APERTURA.");
			log.error(e);
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
			
			List<RDRParticipante> listaParticipantesGerarTabelaAUX = listaParticipantes;
			
			int t = listaParticipantesGerarTabelaAUX.size();
			int m = listaParticipantesGerarTabelaAUX.size() / 2;			
			
			for (int i = 0; i < t - 1; i++) {
				
				RDRRodada rdrRodada = new RDRRodada();
				rdrRodada.setNrRDRRodada(new Long(i+1));
				
				if("A".equalsIgnoreCase(tipo)) { // APERTURA	
					
					rdrRodada.setNrRodadaCartola(new Long(i+1) + 4);	
					
				} else if("C".equalsIgnoreCase(tipo)) {
					
					rdrRodada.setNrRodadaCartola(new Long(i+1) + 19);
					
				}
				
				rdrRodada.setStatusRodada("FT"); // FUTURA
				rdrRodada.setLiga(buscarLigaReisDaResenha());
				
				rdrRodada.setTipoRodada(tipo); // APERTURA OU CLAUSURA
				rdrRodada.setSerieRodada(serie); // SERIE A OU SERIE B
				
				rdrRodada = (RDRRodada) rdrService.atualizar(rdrRodada);
				
				if("A".equalsIgnoreCase(rdrRodada.getTipoRodada())) { // APERTURA	
					if("SA".equalsIgnoreCase(rdrRodada.getSerieRodada())) {
						listaRDRRodadasAperturaSerieA.add(rdrRodada);
					} else if("SB".equalsIgnoreCase(rdrRodada.getSerieRodada())) {
						listaRDRRodadasAperturaSerieB.add(rdrRodada);						
					} 					
				}
				
				/** Falta implementar a Clausura **/
				if("C".equalsIgnoreCase(rdrRodada.getTipoRodada())) { // CLAUSURA				
					if("SA".equalsIgnoreCase(rdrRodada.getSerieRodada())) {
						
					} else if("SB".equalsIgnoreCase(rdrRodada.getSerieRodada())) {
												
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
					rdrPontuacao.setNomeTimeVencedor("empate");
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
			
			List<ClassificacaoLigaPrincipalDTO> listaClassificacaoPrincipal = inicioService.buscarPontuacaoLigaPrincipalTime(anoAtual, rdrClassificacao.getRdrParticipante().getTime());
			 
			Double vrPontuacaoAtualLigaPrincipalCartola =  0.0;
			
			if(listaClassificacaoPrincipal!= null && !listaClassificacaoPrincipal.isEmpty()) { 
				vrPontuacaoAtualLigaPrincipalCartola = listaClassificacaoPrincipal.get(0).getPontuacao();
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
}
