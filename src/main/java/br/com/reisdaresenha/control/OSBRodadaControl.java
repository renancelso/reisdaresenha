package br.com.reisdaresenha.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.OSBPontuacao;
import br.com.reisdaresenha.model.OSBRodada;
import br.com.reisdaresenha.model.OSBRodadaTimeParticipante;
import br.com.reisdaresenha.model.Rodada;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.service.InicioServiceLocal;
import br.com.reisdaresenha.service.RodadaServiceLocal;
import br.com.reisdaresenha.service.TimeServiceLocal;
import br.com.reisdaresenha.view.ClassificacaoLigaPrincipalDTO;


/**
 * @author Renan Celso
 */
@ManagedBean(name = "osbRodadaControl")
@ViewScoped
public class OSBRodadaControl extends BaseControl {

	private static final long serialVersionUID = 1942738470369825926L;	
	private transient Logger log = Logger.getLogger(OSBRodadaControl.class.getName());
		
	@EJB
	private RodadaServiceLocal rodadaService; 
	
	@EJB
	private InicioServiceLocal inicioService; 
	
	@EJB 
	private TimeServiceLocal timeService;
	
	private Liga ligaOSobrevivente;
	
	private List<Rodada> listaRodadasLigaPrincipal;
	
	private List<OSBRodada> listaOsbRodadas;
	
	private OSBRodada novaOsbRodada;
	
	private List<OSBRodadaTimeParticipante> listaOSBRodadaTimeParticipante;
	
	private OSBRodada osbRodada33;	
	OSBRodadaTimeParticipante participante3Rodada34;	
	OSBRodadaTimeParticipante participante4Rodada34;
		
	@PostConstruct
	public void init() {			
		try {	
			
			listaOsbRodadas = new ArrayList<>();				
			
			ligaOSobrevivente = buscarLigaOSobrevivente();			
			
			listaOsbRodadas = rodadaService.listarOsbRodadasDesc(ligaOSobrevivente);	
			
			novaOsbRodada = new OSBRodada();
			
			novaOsbRodada = rodadaService.buscarOsbRodadaEmAndamento(ligaOSobrevivente);
			
			if(novaOsbRodada != null) {
				novaOsbRodada.setListaOsbPontuacao(listarClassificacaoHistoricoOsbRodadas(novaOsbRodada));
			}
			
			for (OSBRodada osbRodada : listaOsbRodadas) {
				osbRodada.setListaOsbPontuacao(new ArrayList<OSBPontuacao>());	
				osbRodada.setListaOsbPontuacao(listarClassificacaoHistoricoOsbRodadas(osbRodada));
				if(osbRodada.getNrRodada() == 34) {
					osbRodada.setListaOsbPontuacao(inicioService.buscarHistoricoClassificacaoOsbRodadasPorId(osbRodada));
				}
			}		
			
			if(novaOsbRodada.getNrRodada() == 34) {
				osbRodada33 = listaOsbRodadas.get(0);
				osbRodada33.setListaOsbPontuacao(listarClassificacaoHistoricoOsbRodadas(osbRodada33));		
				novaOsbRodada.setListaOsbPontuacao(inicioService.buscarHistoricoClassificacaoOsbRodadasPorId(novaOsbRodada));
			}				
			
		} catch (Exception e) {
			log.error("Erro no método init "+e.getMessage());			
		}
	}
		
	public String btnExcluirRodadaEmAndamento() {		
		
		if(novaOsbRodada != null) {
			
			for (OSBPontuacao pontuacao : novaOsbRodada.getListaOsbPontuacao()) {				
				OSBRodadaTimeParticipante part = pontuacao.getOsbRodadaTimeParticipante();
				rodadaService.remover(pontuacao);
				rodadaService.remover(part);
			}			
			rodadaService.remover(novaOsbRodada);
			
			addInfoMessage("Rodada Excluida com sucesso.");			
			
			init();
			
		} else {
			addErrorMessage("Nao existe rodada em andamento");			
		}
		
		return null;
	}
	
	
	public String btnFinalizarRodadaEmAndamento() {		
		
		List<Rodada> listaRodadasLigaPrincipal = rodadaService.listarTodasRodadasDesc(buscarLigaPrincipal());	
		
		if(listaRodadasLigaPrincipal != null 
				&& !listaRodadasLigaPrincipal.isEmpty()
				&& "EA".equalsIgnoreCase(listaRodadasLigaPrincipal.get(0).getStatusRodada())
				&& listaRodadasLigaPrincipal.get(0).getNrRodada().equals(novaOsbRodada.getNrRodada())) {				
			addErrorMessage("É necessário finalizar a Rodada da 'Liga Principal', para depois finalizar a rodada da liga 'O Sobrevivente'.");			
			init();	
			return null;
		}
		
		if(novaOsbRodada != null) {
			
			if("N".equalsIgnoreCase(novaOsbRodada.getTipoRodada())) {
									
				for (OSBPontuacao osbPontuacao : novaOsbRodada.getListaOsbPontuacao()) {
					osbPontuacao.setSituacaoFinalRodada("CLASSIFICADO");	
					osbPontuacao = (OSBPontuacao) rodadaService.atualizar(osbPontuacao);	
				}	
				
				if(novaOsbRodada.getNrRodada().intValue() > 29 && novaOsbRodada.getNrRodada().intValue() < 38) { //RODADAS 31 a 37: os DOIS times com a piores pontuações de cada rodada, considerando apenas os que ainda não foram eliminados, são eliminados.					
					
					OSBPontuacao osbPontuacaoEliminadoUltimo = novaOsbRodada.getListaOsbPontuacao().get(novaOsbRodada.getListaOsbPontuacao().size()-1);			
					osbPontuacaoEliminadoUltimo.setSituacaoFinalRodada("ELIMINADO");				
					osbPontuacaoEliminadoUltimo = (OSBPontuacao) rodadaService.atualizar(osbPontuacaoEliminadoUltimo);	
					
					OSBPontuacao osbPontuacaoEliminadoPenultimo = novaOsbRodada.getListaOsbPontuacao().get(novaOsbRodada.getListaOsbPontuacao().size()-2);			
					osbPontuacaoEliminadoPenultimo.setSituacaoFinalRodada("ELIMINADO");				
					osbPontuacaoEliminadoPenultimo = (OSBPontuacao) rodadaService.atualizar(osbPontuacaoEliminadoPenultimo);	
					
				} else { // APENAS 1 ELIMINADO
					
					OSBPontuacao osbPontuacaoEliminadoUltimo = novaOsbRodada.getListaOsbPontuacao().get(novaOsbRodada.getListaOsbPontuacao().size()-1);			
					osbPontuacaoEliminadoUltimo.setSituacaoFinalRodada("ELIMINADO");				
					osbPontuacaoEliminadoUltimo = (OSBPontuacao) rodadaService.atualizar(osbPontuacaoEliminadoUltimo);			
					
				}
			
				novaOsbRodada.setStatusRodada("PS");				
				novaOsbRodada = (OSBRodada) rodadaService.atualizar(novaOsbRodada);								
			
			} else if("R".equalsIgnoreCase(novaOsbRodada.getTipoRodada())) {
				
				for (OSBPontuacao osbPontuacao : novaOsbRodada.getListaOsbPontuacao()) {
					osbPontuacao.setSituacaoFinalRodada("ELIMINADO-REPESCAGEM");	
					osbPontuacao = (OSBPontuacao) rodadaService.atualizar(osbPontuacao);	
				}	
				
				OSBPontuacao osbPontuacaoClassificadoRepescagem = novaOsbRodada.getListaOsbPontuacao().get(0);		
				
				List<OSBPontuacao> pontuacaoEliminadosRodadasAnteriores = rodadaService.buscarEliminadosRodadasAnteriores(novaOsbRodada.getNrRodada());	
				
				for (OSBPontuacao osbPontuacao : pontuacaoEliminadosRodadasAnteriores) {
					
					if(osbPontuacao.getOsbRodadaTimeParticipante().getTime().getId().equals(osbPontuacaoClassificadoRepescagem.getOsbRodadaTimeParticipante().getTime().getId())) {
						
						osbPontuacao.setSituacaoFinalRodada("CLASSIFICADO-RESPESCAGEM - Rodada "+novaOsbRodada.getNrRodada());
						osbPontuacao = (OSBPontuacao) rodadaService.atualizar(osbPontuacao);	
						break;
						
					}
				}
				
				osbPontuacaoClassificadoRepescagem.setSituacaoFinalRodada("CLASSIFICADO-RESPESCAGEM");				
				osbPontuacaoClassificadoRepescagem = (OSBPontuacao) rodadaService.atualizar(osbPontuacaoClassificadoRepescagem);	
							
				novaOsbRodada.setStatusRodada("PS");				
				novaOsbRodada = (OSBRodada) rodadaService.atualizar(novaOsbRodada);	
			}
			
			addInfoMessage(+novaOsbRodada.getNrRodada()+"ª rodada finalizada com sucesso");
			
			init();	
			
		} else {
			addErrorMessage("Nao existe rodada em andamento");			
		}
		
		return null;
	}
	
	public String btnAtualizarRodadaEmAndamento() {
		try {
			
			Integer anoAtual = 2021; //Calendar.getInstance().get(Calendar.YEAR);
			
			if(novaOsbRodada.getListaOsbPontuacao() == null || novaOsbRodada.getListaOsbPontuacao().isEmpty()) {				
				criarListaParticipantesEGerarPontuacaoOSBRodada(anoAtual, novaOsbRodada.getNrRodada());
			}			
			
			List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = inicioService.buscarHistoricoClassificacaoRodadas(anoAtual, novaOsbRodada.getNrRodada());
			
			for (ClassificacaoLigaPrincipalDTO classDTO : listaClassificacaoLigaPrincipalDTO) {					
				Time time = timeService.buscarTimePorIdCartola(classDTO.getIdTimeCartola());		
				for (OSBPontuacao pontuacao : novaOsbRodada.getListaOsbPontuacao()) {	
					if(time.getId().equals(pontuacao.getOsbRodadaTimeParticipante().getTime().getId())) {						
						pontuacao.setVrPontuacao(classDTO.getPontuacao());		
						pontuacao = (OSBPontuacao) rodadaService.atualizar(pontuacao);
					}					
				}					
			}
			
			Collections.sort(novaOsbRodada.getListaOsbPontuacao());
			
			addInfoMessage("Pontuacao da "+novaOsbRodada.getNrRodada()+"ª rodada atualizada com sucesso");
			
			init();
			
			return null;
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			addInfoMessage("Erro ao atualizar rodada");
		}
		
		init();
		
		return null;
	}
	
	public String btnOsbGerarDisputa3Lugar() {	
		
		OSBPontuacao osbPontuacao3 =  osbRodada33.getListaOsbPontuacao().get(2);
		OSBPontuacao osbPontuacao4 =  osbRodada33.getListaOsbPontuacao().get(3);
				
		participante3Rodada34 = new OSBRodadaTimeParticipante();		
		participante3Rodada34.setTime(osbPontuacao3.getOsbRodadaTimeParticipante().getTime());
		participante3Rodada34.setIdTimeCartola(osbPontuacao3.getOsbRodadaTimeParticipante().getIdTimeCartola());
		participante3Rodada34.setNomeTime(osbPontuacao3.getOsbRodadaTimeParticipante().getNomeTime());
		participante3Rodada34.setOsbRodada(novaOsbRodada);	
		participante3Rodada34 = (OSBRodadaTimeParticipante) timeService.atualizar(participante3Rodada34);
				
		OSBPontuacao pontuacao = new OSBPontuacao();	
		pontuacao.setOsbRodadaTimeParticipante(participante3Rodada34);
		pontuacao.setNomeTime(participante3Rodada34.getNomeTime());
		pontuacao.setOsbRodada(novaOsbRodada);							
		pontuacao.setVrPontuacao(0d);		
		pontuacao = (OSBPontuacao) timeService.atualizar(pontuacao);

		participante4Rodada34 = new OSBRodadaTimeParticipante();		
		participante4Rodada34.setTime(osbPontuacao4.getOsbRodadaTimeParticipante().getTime());
		participante4Rodada34.setIdTimeCartola(osbPontuacao4.getOsbRodadaTimeParticipante().getIdTimeCartola());
		participante4Rodada34.setNomeTime(osbPontuacao4.getOsbRodadaTimeParticipante().getNomeTime());
		participante4Rodada34.setOsbRodada(novaOsbRodada);	
		participante4Rodada34 = (OSBRodadaTimeParticipante) timeService.atualizar(participante4Rodada34);
		
		pontuacao = new OSBPontuacao();	
		pontuacao.setOsbRodadaTimeParticipante(participante4Rodada34);
		pontuacao.setNomeTime(participante4Rodada34.getNomeTime());
		pontuacao.setOsbRodada(novaOsbRodada);							
		pontuacao.setVrPontuacao(0d);		
		pontuacao = (OSBPontuacao) timeService.atualizar(pontuacao);
		
		init();
						
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String btnOsbNovaRodada() {	
		
		listaOsbRodadas = new ArrayList<>();		
		
		ligaOSobrevivente = buscarLigaOSobrevivente();		
		
		listaOsbRodadas = rodadaService.listarOsbRodadasDesc(ligaOSobrevivente);	
						
		listaRodadasLigaPrincipal = rodadaService.listarTodasRodadasDesc(buscarLigaPrincipal());	
		
		List<Time> listaTimes = new ArrayList<Time>();		
		listaTimes = (List<Time>) timeService.consultarTodos(new Time().getClass());
				
		Long rodadaInicio = new Long(1);
		
		switch (listaTimes.size()) {		
			case 40:
				rodadaInicio = new Long(1);
				break;
			case 39:
				rodadaInicio = new Long(2);
				break;
			case 38:
				rodadaInicio = new Long(3);
				break;
			case 37:
				rodadaInicio = new Long(4);
				break;
			case 36:
				rodadaInicio = new Long(5);
				break;
			case 35:
				rodadaInicio = new Long(6);
				break;
			case 34:
				rodadaInicio = new Long(7);
				break;
			case 33:
				rodadaInicio = new Long(8);
				break;
				
			case 32: // No ano de 2021, vai começar na rodada 2 e terminar na rodada 34
				rodadaInicio = new Long(2);
				break;
				
			default:
				rodadaInicio = new Long(1);
				break;
		}
		
		if(listaRodadasLigaPrincipal != null 
				&& !listaRodadasLigaPrincipal.isEmpty()
				&& listaRodadasLigaPrincipal.get(0).getNrRodada() > (!listaOsbRodadas.isEmpty() ? listaOsbRodadas.get(0).getNrRodada() : listaOsbRodadas.size())) {	
			
			if(rodadaInicio > listaRodadasLigaPrincipal.get(0).getNrRodada()) {
				addFatalMessage("O Liga Principal tem apenas "+listaTimes.size()+" times. Portanto a liga 'O Sobrevivente' só iniciará na rodada "+rodadaInicio);
				return null;
			}
		
			if(novaOsbRodada == null) {	
				
				novaOsbRodada = new OSBRodada();
				novaOsbRodada.setLiga(ligaOSobrevivente);				
				novaOsbRodada.setNrRodada(new Long( !listaOsbRodadas.isEmpty() ? (listaOsbRodadas.get(0).getNrRodada()+1) : rodadaInicio) );		
				
				if(novaOsbRodada.getNrRodada() > 34) {
					addErrorMessage("A "+(novaOsbRodada.getNrRodada()-1)+"ª RODADA É A ÚLTIMA");
					init();
					return null;
				}
				
				novaOsbRodada.setStatusRodada("EA"); //Em Andamento			
				
				if(novaOsbRodada.getNrRodada() == 8 || novaOsbRodada.getNrRodada() == 16 || novaOsbRodada.getNrRodada() == 24) { //Repescagem
										
					novaOsbRodada.setTipoRodada("R");
					
					//Cadastra a rodada
					novaOsbRodada = (OSBRodada) rodadaService.atualizar(novaOsbRodada);
																								
					novaOsbRodada.setListaOsbPontuacao(new ArrayList<>());		
					
					Integer anoAtual = 2021;//Calendar.getInstance().get(Calendar.YEAR);
					
					if(novaOsbRodada.getId() != null) {
						
						// Criar Lista de participantes e gerar pontuacao baseada na ultima classificacao					
						if(criarListaParticipantesEGerarPontuacaoOSBRodada(anoAtual, novaOsbRodada.getNrRodada())) {						
							addInfoMessage(novaOsbRodada.getNrRodada()+"ª Rodada gerada com sucesso");
							init();
							return null;
						} else {
							addErrorMessage("Erro ao Criar Participantes e Gerar Pontuacao.");
							return null;
						}	
						
					}
				
				
				} else {					
					
					novaOsbRodada.setTipoRodada("N"); //Normal
					
					//Cadastra a rodada
					novaOsbRodada = (OSBRodada) rodadaService.atualizar(novaOsbRodada);
																								
					novaOsbRodada.setListaOsbPontuacao(new ArrayList<>());		
					
					Integer anoAtual = 2021;//Calendar.getInstance().get(Calendar.YEAR);
					
					if(novaOsbRodada.getId() != null) {
						
						// Criar Lista de participantes e gerar pontuacao baseada na ultima classificacao					
						if(criarListaParticipantesEGerarPontuacaoOSBRodada(anoAtual, novaOsbRodada.getNrRodada())) {						
							addInfoMessage(novaOsbRodada.getNrRodada()+"ª Rodada gerada com sucesso");
							init();
							return null;
						} else {
							addErrorMessage("Erro ao Criar Participantes e Gerar Pontuacao.");
							return null;
						}	
						
					}
					
				}						
				
			} else {
				init();			
			}				
			
			listaOsbRodadas = rodadaService.listarOsbRodadasDesc(ligaOSobrevivente);
			
		} else {
			addErrorMessage("Nao existem rodadas a serem geradas");
			init();
			return null;
		}
		
		return null;
	}	
	
	private boolean criarListaParticipantesEGerarPontuacaoOSBRodada(Integer anoAtual, Long nrRodada) {
		try {
		
			List<OSBRodadaTimeParticipante> listaParticipantes = new ArrayList<OSBRodadaTimeParticipante>();
			
			if(novaOsbRodada.getNrRodada() == 1) {
				
				List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<ClassificacaoLigaPrincipalDTO>();				
						
				listaClassificacaoLigaPrincipalDTO = inicioService.buscarHistoricoClassificacaoRodadas(anoAtual, novaOsbRodada.getNrRodada());
				
				for (ClassificacaoLigaPrincipalDTO classificacaoLigaPrincipalDTO : listaClassificacaoLigaPrincipalDTO) {				
					
					OSBRodadaTimeParticipante participante = new OSBRodadaTimeParticipante();		
					
					Time time = timeService.buscarTimePorIdCartola(classificacaoLigaPrincipalDTO.getIdTimeCartola());
					participante.setTime(time);
					participante.setIdTimeCartola(time.getIdCartola());
					participante.setNomeTime(classificacaoLigaPrincipalDTO.getTime());
					participante.setOsbRodada(novaOsbRodada);	
					
					//Cadastra os participantes
					participante = (OSBRodadaTimeParticipante) timeService.atualizar(participante);
					
					listaParticipantes.add(participante);					
				}				
							
				List<OSBPontuacao> listaOSBPontuacoes = new ArrayList<OSBPontuacao>();
				
				for (ClassificacaoLigaPrincipalDTO classDTO : listaClassificacaoLigaPrincipalDTO) {				
										
					boolean timeParticipa = false;					
					
					Time timeClassDTO = timeService.buscarTimePorIdCartola(classDTO.getIdTimeCartola());					
							
					OSBPontuacao pontuacao = new OSBPontuacao();
					
					for (OSBRodadaTimeParticipante part : listaParticipantes) {							
						if(part.getTime().getId().equals(timeClassDTO.getId())) {							
							
							pontuacao.setOsbRodadaTimeParticipante(part);
							pontuacao.setNomeTime(part.getNomeTime());
							pontuacao.setOsbRodada(novaOsbRodada);							
							pontuacao.setVrPontuacao(classDTO.getPontuacao());							
							timeParticipa = true;
							
							break;							
						}							
					}
					
					//Cadastra as pontuacoes
					if(timeParticipa) {		
						pontuacao = (OSBPontuacao) timeService.atualizar(pontuacao);
						listaOSBPontuacoes.add(pontuacao);
					}					
				}				
				
				return true;
			
			} if(novaOsbRodada.getNrRodada() > 1) {	
				
				if("N".equalsIgnoreCase(novaOsbRodada.getTipoRodada())) { // Normal
//															
//					if(novaOsbRodada.getNrRodada() == 9 
//							|| novaOsbRodada.getNrRodada() == 17
//							|| novaOsbRodada.getNrRodada() == 25) {// Pós repescagem
//						
//					} else {
					
						List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<ClassificacaoLigaPrincipalDTO>();				
						
						listaClassificacaoLigaPrincipalDTO = inicioService.buscarHistoricoClassificacaoRodadas(anoAtual, novaOsbRodada.getNrRodada());
						
						List<OSBPontuacao> pontuacaoEliminadosRodadasAnteriores = rodadaService.buscarEliminadosRodadasAnteriores(novaOsbRodada.getNrRodada());												
						
						List<Long> idsTimesEliminados = new ArrayList<Long>();
						
						for (OSBPontuacao pont : pontuacaoEliminadosRodadasAnteriores) {
							idsTimesEliminados.add(pont.getOsbRodadaTimeParticipante().getTime().getId());
						}		
																		
						for (ClassificacaoLigaPrincipalDTO classificacaoLigaPrincipalDTO : listaClassificacaoLigaPrincipalDTO) {				
							
							OSBRodadaTimeParticipante participante = new OSBRodadaTimeParticipante();		
							
							Time time = timeService.buscarTimePorIdCartola(classificacaoLigaPrincipalDTO.getIdTimeCartola());
							
							if(!idsTimesEliminados.contains(time.getId())) { // Elimina o ultimos colocados das rodadas anteriores
								
								participante.setTime(time);
								participante.setIdTimeCartola(time.getIdCartola());
								participante.setNomeTime(classificacaoLigaPrincipalDTO.getTime());
								participante.setOsbRodada(novaOsbRodada);	
								
								//Cadastra os participantes
								participante = (OSBRodadaTimeParticipante) timeService.atualizar(participante);
								
								listaParticipantes.add(participante);									
							}
							
						}	
						
						List<OSBPontuacao> listaOSBPontuacoes = new ArrayList<OSBPontuacao>();
						
						for (ClassificacaoLigaPrincipalDTO classDTO : listaClassificacaoLigaPrincipalDTO) {				
												
							boolean timeParticipa = false;					
							
							Time timeClassDTO = timeService.buscarTimePorIdCartola(classDTO.getIdTimeCartola());					
									
							OSBPontuacao pontuacao = new OSBPontuacao();
							
							for (OSBRodadaTimeParticipante part : listaParticipantes) {							
								if(part.getTime().getId().equals(timeClassDTO.getId())) {							
									
									pontuacao.setOsbRodadaTimeParticipante(part);
									pontuacao.setNomeTime(part.getNomeTime());
									pontuacao.setOsbRodada(novaOsbRodada);							
									pontuacao.setVrPontuacao(classDTO.getPontuacao());							
									timeParticipa = true;
									
									break;							
								}							
							}
							
							//Cadastra as pontuacoes
							if(timeParticipa) {		
								pontuacao = (OSBPontuacao) timeService.atualizar(pontuacao);
								listaOSBPontuacoes.add(pontuacao);
							}					
						}			
						
						return true;
//					}
					
				} else if("R".equalsIgnoreCase(novaOsbRodada.getTipoRodada())) { // Rodada de Repescagem
										
					List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<ClassificacaoLigaPrincipalDTO>();				
					
					listaClassificacaoLigaPrincipalDTO = inicioService.buscarHistoricoClassificacaoRodadas(anoAtual, novaOsbRodada.getNrRodada());
					
					List<OSBPontuacao> pontuacaoEliminadosRodadasAnteriores = rodadaService.buscarEliminadosRodadasAnteriores(novaOsbRodada.getNrRodada());
					
					List<Long> idsTimesEliminados = new ArrayList<Long>();
					
					for (OSBPontuacao pont : pontuacaoEliminadosRodadasAnteriores) {
						idsTimesEliminados.add(pont.getOsbRodadaTimeParticipante().getTime().getId());
					}
					
					for (ClassificacaoLigaPrincipalDTO classificacaoLigaPrincipalDTO : listaClassificacaoLigaPrincipalDTO) {				
						
						OSBRodadaTimeParticipante participante = new OSBRodadaTimeParticipante();		
						
						Time time = timeService.buscarTimePorIdCartola(classificacaoLigaPrincipalDTO.getIdTimeCartola());
						
						if(idsTimesEliminados.contains(time.getId())) { // Elimina o ultimos colocados das rodadas anteriores
							
							participante.setTime(time);
							participante.setIdTimeCartola(time.getIdCartola());
							participante.setNomeTime(classificacaoLigaPrincipalDTO.getTime());
							participante.setOsbRodada(novaOsbRodada);	
							
							//Cadastra os participantes
							participante = (OSBRodadaTimeParticipante) timeService.atualizar(participante);
							
							listaParticipantes.add(participante);									
						}
						
					}	
					
					List<OSBPontuacao> listaOSBPontuacoes = new ArrayList<OSBPontuacao>();
					
					for (ClassificacaoLigaPrincipalDTO classDTO : listaClassificacaoLigaPrincipalDTO) {				
											
						boolean timeParticipa = false;					
						
						Time timeClassDTO = timeService.buscarTimePorIdCartola(classDTO.getIdTimeCartola());					
								
						OSBPontuacao pontuacao = new OSBPontuacao();
						
						for (OSBRodadaTimeParticipante part : listaParticipantes) {							
							if(part.getTime().getId().equals(timeClassDTO.getId())) {							
								
								pontuacao.setOsbRodadaTimeParticipante(part);
								pontuacao.setNomeTime(part.getNomeTime());
								pontuacao.setOsbRodada(novaOsbRodada);							
								pontuacao.setVrPontuacao(classDTO.getPontuacao());							
								timeParticipa = true;
								
								break;							
							}							
						}
						
						//Cadastra as pontuacoes
						if(timeParticipa) {		
							pontuacao = (OSBPontuacao) timeService.atualizar(pontuacao);
							listaOSBPontuacoes.add(pontuacao);
						}					
					}			
					
					return true;
					
				}			
							
				
			}
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			return false;
		}
		
		return false;
	}

	public Liga buscarLigaOSobrevivente() {		
		Integer anoAtual = 2021;//Calendar.getInstance().get(Calendar.YEAR);	
		List<Liga> listaLigas = inicioService.buscarLigas(anoAtual);		
		if(listaLigas != null && !listaLigas.isEmpty()) {			
			for (Liga liga : listaLigas) {	
				if(liga.getNomeLiga().toUpperCase().contains("SOBREVIVENTE")) {
					return liga;
				}
			}
		}			
		return null;			
	}

	public Liga buscarLigaPrincipal() {		
		Integer anoAtual = 2021;//Calendar.getInstance().get(Calendar.YEAR);	
		List<Liga> listaLigas = inicioService.buscarLigas(anoAtual);		
		if(listaLigas != null && !listaLigas.isEmpty()) {			
			for (Liga liga : listaLigas) {	
				if(liga.getNomeLiga().toUpperCase().contains("PRINCIPAL")) {
					return liga;
				}
			}
		}			
		return null;			
	}
	
	public List<OSBPontuacao> listarClassificacaoHistoricoOsbRodadas(OSBRodada osbRodada){
		
		try {	
			
			List<OSBPontuacao> listaClassificacaoLigaPrincipalDTO = new ArrayList<OSBPontuacao>();			
			
			listaClassificacaoLigaPrincipalDTO = inicioService.buscarHistoricoClassificacaoOsbRodadas(osbRodada);		
			
			return listaClassificacaoLigaPrincipalDTO;	
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			return new ArrayList<OSBPontuacao>();
		}
	}
	
		
	public List<OSBRodada> getListaOsbRodadas() {
		return listaOsbRodadas;
	}

	public void setListaOsbRodadas(List<OSBRodada> listaOsbRodadas) {
		this.listaOsbRodadas = listaOsbRodadas;
	}

	public OSBRodada getNovaOsbRodada() {
		return novaOsbRodada;
	}

	public void setNovaOsbRodada(OSBRodada novaOsbRodada) {
		this.novaOsbRodada = novaOsbRodada;
	}

	public List<OSBRodadaTimeParticipante> getListaOSBRodadaTimeParticipante() {
		return listaOSBRodadaTimeParticipante;
	}

	public void setListaOSBRodadaTimeParticipante(List<OSBRodadaTimeParticipante> listaOSBRodadaTimeParticipante) {
		this.listaOSBRodadaTimeParticipante = listaOSBRodadaTimeParticipante;
	}

	public Liga getLigaOSobrevivente() {
		return ligaOSobrevivente;
	}

	public void setLigaOSobrevivente(Liga ligaOSobrevivente) {
		this.ligaOSobrevivente = ligaOSobrevivente;
	}

	public List<Rodada> getListaRodadasLigaPrincipal() {
		return listaRodadasLigaPrincipal;
	}

	public void setListaRodadasLigaPrincipal(List<Rodada> listaRodadasLigaPrincipal) {
		this.listaRodadasLigaPrincipal = listaRodadasLigaPrincipal;
	}

	public OSBRodada getOsbRodada33() {
		return osbRodada33;
	}

	public void setOsbRodada33(OSBRodada osbRodada33) {
		this.osbRodada33 = osbRodada33;
	}

	public OSBRodadaTimeParticipante getParticipante3Rodada34() {
		return participante3Rodada34;
	}

	public void setParticipante3Rodada34(OSBRodadaTimeParticipante participante3Rodada34) {
		this.participante3Rodada34 = participante3Rodada34;
	}

	public OSBRodadaTimeParticipante getParticipante4Rodada34() {
		return participante4Rodada34;
	}

	public void setParticipante4Rodada34(OSBRodadaTimeParticipante participante4Rodada34) {
		this.participante4Rodada34 = participante4Rodada34;
	}

}
