package br.com.reisdaresenha.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.OSBPontuacao;
import br.com.reisdaresenha.model.OSBRodada;
import br.com.reisdaresenha.model.Pontuacao;
import br.com.reisdaresenha.model.RDRClassificacao;
import br.com.reisdaresenha.model.RDRCopaPontuacao;
import br.com.reisdaresenha.model.RDRPontuacao;
import br.com.reisdaresenha.model.RDRRodada;
import br.com.reisdaresenha.model.Rodada;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.service.InicioServiceLocal;
import br.com.reisdaresenha.service.ParametroServiceLocal;
import br.com.reisdaresenha.service.RDRServiceLocal;
import br.com.reisdaresenha.service.RodadaServiceLocal;
import br.com.reisdaresenha.service.TimeServiceLocal;
import br.com.reisdaresenha.view.ClassificacaoLigaPrincipalDTO;
import br.com.reisdaresenha.view.TimeRodadaDTO;

/**
 * 
 * @author Renan Celso, em 03/08/2020
 *
 */
public class RDRJob implements Job {
	
	private transient Logger log = Logger.getLogger(RDRJob.class.getName());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {				
		
		try {	
			
			Calendar agora = Calendar.getInstance();
			
			RDRServiceLocal rdrService = lookupRdrService();
			RodadaServiceLocal rodadaService = lookupRodadaService();
			ParametroServiceLocal parametroService = lookupParametrosService();			
			InicioServiceLocal inicioService = lookupInicioService();		
			TimeServiceLocal timeService = lookupTimeService();
			
			CartolaRestFulClient servicoCartola = new CartolaRestFulClient(); 	
						
			String rodaJob = parametroService.buscarParametroPorChave("roda_job").getValor();
			
			if("SIM".equalsIgnoreCase(rodaJob.trim())) {	
				//if(true) {	
				
				log.info(" \n \n >>>>>>>>>>>> Iniciando JOB em '"+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())+"' <<<<<<<<<<<< \n ");		
				
				Liga ligaPrincipal = new Liga();
				ligaPrincipal = (Liga) rdrService.consultarPorChavePrimaria(ligaPrincipal, new Long(1));				
				Rodada rodadaEmAndamento = new Rodada();		
				rodadaEmAndamento = rodadaService.buscarRodadaEmAndamento(ligaPrincipal);					
				
				JSONObject jsonObject = servicoCartola.getStatusRodadaCartolaFC(rodadaEmAndamento != null && rodadaEmAndamento.getNrRodada() != null ? rodadaEmAndamento.getNrRodada() : null);				
				
				long statusMercado = jsonObject != null ? new Long(String.valueOf(jsonObject.get("status_mercado"))).longValue() : 0;
				
				if(statusMercado == 2) { // 2 = Mercado Fechado (Rodada em andamento)
								
					Calendar hoje = Calendar.getInstance();
					
					Calendar hora11 = Calendar.getInstance();
					hora11.setTime(new Date());
					hora11.set(hora11.get(Calendar.YEAR), hora11.get(Calendar.MONTH), hora11.get(Calendar.DATE), 11, 00, 00);
					
					Calendar hora23e59 = Calendar.getInstance();
					hora23e59.setTime(new Date());
					hora23e59.set(hora23e59.get(Calendar.YEAR), hora23e59.get(Calendar.MONTH), hora23e59.get(Calendar.DATE), 23, 59, 00);
									
					if(hoje.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || hoje.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
										
						if(agora.getTime().after(hora11.getTime()) && agora.getTime().before(hora23e59.getTime())) {	
							
							log.info("\n \n >>>>>>>>>>>> Iniciando ACESSOS AO APP DA GLOBO EM '"+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())+"' <<<<<<<<<<<< \n \n");
							
							try {
								log.info(">> INICIO atualizarPontuacaoRodadaEmAndamento <<");
								atualizarPontuacaoRodadaEmAndamento(rdrService, rodadaService, parametroService, servicoCartola);		
							} catch (Exception e) {
								log.error(">> ERRO EM atualizarPontuacaoRodadaEmAndamento <<");
								e.printStackTrace();
								return;
							}				
										
							atualizarPontuacaoOSobreviventeRodadaEmAndamento(timeService, inicioService,rdrService, rodadaService, parametroService, servicoCartola);
							
							atualizarPontuacaoLigaReisDaResenhaRodadaEmAndamento(timeService, inicioService,rdrService, rodadaService, parametroService, servicoCartola);
							
							try {
								log.info("VAI ATUALIZAR SUPER COPA - JOB");
								atualizarSuperCopaEmAndamento(timeService, inicioService, rdrService, rodadaService, parametroService, servicoCartola);
								
							} catch (Exception e) {
								log.error("ERRO AO ATUALIZAR SUPER COPA - JOB");
								e.printStackTrace();
								
							}
							
							log.info("\n \n >>>>>>>>>>>> FINALIZANDO ACESSOS AO APP DA GLOBO EM '"+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())+"' <<<<<<<<<<<< \n \n");
						}
						
					} else {
						
						Calendar hora16e30 = Calendar.getInstance();
						hora16e30.setTime(new Date());
						hora16e30.set(hora16e30.get(Calendar.YEAR), hora16e30.get(Calendar.MONTH), hora16e30.get(Calendar.DATE), 16, 30, 00);
									
						
						Calendar hora01e31daManha = Calendar.getInstance();
						hora01e31daManha.setTime(new Date());
						hora01e31daManha.set(hora01e31daManha.get(Calendar.YEAR), hora01e31daManha.get(Calendar.MONTH), hora01e31daManha.get(Calendar.DATE), 01, 31, 00);
						
						if( (agora.getTime().after(hora16e30.getTime()) && agora.getTime().before(hora23e59.getTime())) 
								|| (agora.getTime().before(hora01e31daManha.getTime())) ) {	
							
							log.info("\n \n >>>>>>>>>>>> Iniciando ACESSOS AO APP DA GLOBO EM '"+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())+"' <<<<<<<<<<<< \n \n");
														
							try {
								log.info(">> INICIO atualizarPontuacaoRodadaEmAndamento <<");
								atualizarPontuacaoRodadaEmAndamento(rdrService, rodadaService, parametroService, servicoCartola);		
							} catch (Exception e) {
								log.error(">> ERRO EM atualizarPontuacaoRodadaEmAndamento <<");
								e.printStackTrace();
								return;
							}				
										
							atualizarPontuacaoOSobreviventeRodadaEmAndamento(timeService, inicioService,rdrService, rodadaService, parametroService, servicoCartola);
							
							atualizarPontuacaoLigaReisDaResenhaRodadaEmAndamento(timeService, inicioService,rdrService, rodadaService, parametroService, servicoCartola);
							try {
								log.info("VAI ATUALIZAR SUPER COPA - JOB");
								atualizarSuperCopaEmAndamento(timeService, inicioService,rdrService, rodadaService, parametroService, servicoCartola);
							} catch (Exception e) {
								log.error("ERRO AO ATUALIZAR SUPER COPA - JOB");
								e.printStackTrace();
							}
							log.info(" \n \n >>>>>>>>>>>> FINALIZANDO ACESSOS AO APP DA GLOBO EM '"+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())+"' <<<<<<<<<<<< \n \n ");
						
						} 
						
					}
					
				} else {
					log.info("\n \n >>>>> STATUS DO MERCADO NO CARTOLA FC NAO ESTA FECHADO (Fechado = Codigo 2): Codigo Status ATUAL: '"+statusMercado+"' <<<<< \n ");
				}
				
				Calendar hora14 = Calendar.getInstance();
				hora14.setTime(new Date());
				hora14.set(hora14.get(Calendar.YEAR), hora14.get(Calendar.MONTH), hora14.get(Calendar.DATE), 14, 00, 00);
				
				Calendar hora15e55 = Calendar.getInstance();
				hora15e55.setTime(new Date());
				hora15e55.set(hora15e55.get(Calendar.YEAR), hora15e55.get(Calendar.MONTH), hora15e55.get(Calendar.DATE), 15, 55, 00);
				
				if(agora.getTime().after(hora14.getTime()) && agora.getTime().before(hora15e55.getTime())) {	
					try {
						log.info("\n \n >> INICIO sincronizarTimesComCartolaFC << \n \n");
						sincronizarTimesComCartolaFC(rdrService, servicoCartola);	
					} catch (Exception e) {
						log.error("\n \n >> ERRO EM sincronizarTimesComCartolaFC << \n \n ");
						e.printStackTrace();
					}	
				}
				
				log.info("\n \n >>>>>>>>>>>> Finalizando JOB em '"+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())+"' <<<<<<<<<<<< \n");				
				
			} else {
				log.info("\n \n parametro >>> roda_job: "+rodaJob+" \n \n ");				
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		
	}
	
	private void atualizarPontuacaoLigaReisDaResenhaRodadaEmAndamento(TimeServiceLocal timeService,
			InicioServiceLocal inicioService, RDRServiceLocal rdrService, RodadaServiceLocal rodadaService,
			ParametroServiceLocal parametroService, CartolaRestFulClient servicoCartola) {
		
		Liga ligaPrincipal = new Liga();
		ligaPrincipal = (Liga) rdrService.consultarPorChavePrimaria(ligaPrincipal, new Long(1));		
		
		Rodada rodadaEmAndamento = new Rodada();		
		rodadaEmAndamento = rodadaService.buscarRodadaEmAndamento(ligaPrincipal);	
		
		if(rodadaEmAndamento != null) {					
			
			List<RDRRodada> listaRDRRodadaAtualizarPontuacao = new ArrayList<RDRRodada>();
			
			listaRDRRodadaAtualizarPontuacao = rdrService.buscarRDRRodadaPorRodadaDaLigaPrincipal(rodadaEmAndamento.getNrRodada());
			
			if(listaRDRRodadaAtualizarPontuacao != null && !listaRDRRodadaAtualizarPontuacao.isEmpty()) {		
				
				for (RDRRodada rdrRodadaAtualizarPontuacao : listaRDRRodadaAtualizarPontuacao) {
					
					rdrRodadaAtualizarPontuacao.setListaRDRPontuacao(rdrService.buscarRDRPontuacaoPorRodada(rdrRodadaAtualizarPontuacao));		
					
					if(rdrRodadaAtualizarPontuacao.getListaRDRPontuacao() != null 
							&& !rdrRodadaAtualizarPontuacao.getListaRDRPontuacao().isEmpty()) {
						
						atualizarPontuacaoRDRRodada(timeService, inicioService,rdrService, rodadaService, rdrRodadaAtualizarPontuacao, rdrRodadaAtualizarPontuacao.getListaRDRPontuacao());
						
					}
				}
				
			}
			
		}
				
	}
	
	public void atualizarPontuacaoRDRRodada(TimeServiceLocal timeService, InicioServiceLocal inicioService, 
											RDRServiceLocal rdrService, RodadaServiceLocal rodadaService,											
											RDRRodada rdrRodadaAtualizarPontuacao, List<RDRPontuacao> listaRDRPontuacao) {
		
		List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<ClassificacaoLigaPrincipalDTO>();
		
		Integer anoAtual = 2021; //Calendar.getInstance().get(Calendar.YEAR);	
		
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
								
			Rodada rodadaPrincipal = new Rodada();
			rodadaPrincipal = rodadaService.buscarRodadaDaLigaPrincipalEspecifica(rdrRodadaAtualizarPontuacao.getNrRodadaCartola());			
						
			atualizarRDRClassificacao(rdrRodadaAtualizarPontuacao.getTipoRodada(), rdrRodadaAtualizarPontuacao.getSerieRodada(), rdrRodadaAtualizarPontuacao.getNrRDRRodada(), rdrService, inicioService);
			
			if(rodadaPrincipal != null) {
				rdrRodadaAtualizarPontuacao.setStatusRodada(rodadaPrincipal.getStatusRodada());				
				rdrRodadaAtualizarPontuacao = (RDRRodada) rdrService.atualizar(rdrRodadaAtualizarPontuacao);
			}			
		} 		
		
		
	
	}
	
	
	@SuppressWarnings("unchecked")
	public void atualizarRDRClassificacao(String fase, String serie, Long nrRodadaAtual, RDRServiceLocal rdrService, InicioServiceLocal inicioService) {		
		
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
			
			Integer anoAtual = 2021; // Calendar.getInstance().get(Calendar.YEAR);
			
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
					
		Double valorArredondarDentrodeCasa = valor;			
				
		Double restoDaDivisaoPor5 = (double) (valorArredondarDentrodeCasa.longValue() % 5);
			
		double valorSomar = 0;
				
		if(restoDaDivisaoPor5 > 0) {			
			for (int j = restoDaDivisaoPor5.intValue(); j < 5; j++) {
				valorSomar++;
			}
		}
		
		Double pontuacaoFinal = valorArredondarDentrodeCasa.longValue()+valorSomar;
		
				
		return pontuacaoFinal;
	}
	
	public Double arredondarValorVisitante(Double valor) {
						
		Double valorArredondarForadeCasa = valor;				
		
		Double restoDaDivisaoPor5 = (double) (valorArredondarForadeCasa.longValue()%5);
			
		double valorDiminuir = 0;
				
		if(restoDaDivisaoPor5 > 0) {			
			for (int j = 0; j < restoDaDivisaoPor5.intValue(); j++) {
				valorDiminuir++;
			}
		}
		
		Double pontuacaoFinal = valorArredondarForadeCasa.longValue()-valorDiminuir;
						
		return pontuacaoFinal;
	}

	private void atualizarPontuacaoOSobreviventeRodadaEmAndamento(TimeServiceLocal timeService, InicioServiceLocal inicioService, RDRServiceLocal rdrService, RodadaServiceLocal rodadaService, 
			 													  ParametroServiceLocal parametroService, CartolaRestFulClient servicoCartola) {
		
		Liga ligaOSobrevivente = new Liga();
		
		ligaOSobrevivente = (Liga) rdrService.consultarPorChavePrimaria(ligaOSobrevivente, new Long(3));		
		
		OSBRodada osbRodada = new OSBRodada();		
		
		osbRodada = rodadaService.buscarOsbRodadaEmAndamento(ligaOSobrevivente);		
		
		if(osbRodada != null && "EA".equalsIgnoreCase(osbRodada.getStatusRodada())) {
			
			osbRodada.setListaOsbPontuacao(inicioService.buscarHistoricoClassificacaoOsbRodadas(osbRodada));	
			
			Integer anoAtual = 2021; //Calendar.getInstance().get(Calendar.YEAR);
		
			List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = inicioService.buscarHistoricoClassificacaoRodadas(anoAtual, osbRodada.getNrRodada());
			
			for (ClassificacaoLigaPrincipalDTO classDTO : listaClassificacaoLigaPrincipalDTO) {					
				Time time = timeService.buscarTimePorIdCartola(classDTO.getIdTimeCartola());		
				for (OSBPontuacao pontuacao : osbRodada.getListaOsbPontuacao()) {	
					if(time.getId().equals(pontuacao.getOsbRodadaTimeParticipante().getTime().getId())) {						
						pontuacao.setVrPontuacao(classDTO.getPontuacao());		
						pontuacao = (OSBPontuacao) rodadaService.atualizar(pontuacao);
					}					
				}					
			}
			
			Collections.sort(osbRodada.getListaOsbPontuacao());
		}
		
	}

	@SuppressWarnings("unchecked")
	private void sincronizarTimesComCartolaFC(RDRServiceLocal rdrService, CartolaRestFulClient servicoCartola) {
		
		List<Time> listaTimesParticipantes = (List<Time>) rdrService.consultarTodos(Time.class, " order by o.nomeTime, o.nomeDonoTime");	
		
		for (Time time : listaTimesParticipantes) {		
			
			TimeRodadaDTO timeCartola = new TimeRodadaDTO();						
			
			timeCartola = servicoCartola.buscarTimeRodadaPorIDCartola(time, new Long(0));	
			
			time.setNomeDonoTime(timeCartola.getTime().getNomeDonoTime());
			time.setIdCartola(timeCartola.getTime().getIdCartola());
			time.setFotoPerfil(timeCartola.getTime().getFotoPerfil());
			time.setUrlEscudoPng(timeCartola.getTime().getUrlEscudoPng());
			time.setUrlEscudoSvg(timeCartola.getTime().getUrlEscudoSvg());
			time.setAssinante(timeCartola.getTime().getAssinante());
			time.setSlugTime(timeCartola.getTime().getSlugTime());
			time.setFacebookId(timeCartola.getTime().getFacebookId());							
			time.setIdUserAtu("JOB-RDR");
			time.setLoginUserAtu("JOB-RDR");
			time.setDhAtu(new Date());		
			
			rdrService.atualizar(time);
		}		
		
	}
	
	@SuppressWarnings("unchecked")
	private void atualizarPontuacaoRodadaEmAndamento(RDRServiceLocal rdrService, RodadaServiceLocal rodadaService, 
													 ParametroServiceLocal parametroService, CartolaRestFulClient servicoCartola) {		
		
		Liga ligaPrincipal = new Liga();
		ligaPrincipal = (Liga) rdrService.consultarPorChavePrimaria(ligaPrincipal, new Long(1));
		
		Rodada rodadaEmAndamento = new Rodada();		
		rodadaEmAndamento = rodadaService.buscarRodadaEmAndamento(ligaPrincipal);	
		
		if(rodadaEmAndamento != null) {
			rodadaEmAndamento.setListaPontuacao(new ArrayList<Pontuacao>());		
			List<Pontuacao> listaPontuacao = (List<Pontuacao>) 
					rodadaService.consultarPorQuery("select o from Pontuacao o where o.liga.id = "+ligaPrincipal.getId()+
													" and o.rodada.id = "+rodadaEmAndamento.getId()+
													" order by o.time.nomeTime, o.time.nomeDonoTime", 0, 0);	
									
			rodadaEmAndamento.setListaPontuacao(listaPontuacao);
			
			String str = rdrService.buscarTodasAsPontuacoesNoServicoCartolaFC(rodadaService, parametroService, servicoCartola, rodadaEmAndamento, rodadaEmAndamento.getListaPontuacao());
			
			if(str!= null && !str.toString().isEmpty()) {
				log.info("\n\n\n--------------------------------------------- JOB: '"+ str +"' NAO FORAM ATUALIZADOS ---------------------------------------------\n\n");	
			}
			
			//MOCK			
//			for (Pontuacao pontuacao : rodadaEmAndamento.getListaPontuacao()) {				
//				
//				TimeRodadaDTO timeRodadaDTO = new TimeRodadaDTO();	
//				Random gerador = new Random();
//				Double patrimonio = Double.parseDouble(String.valueOf(gerador.nextInt(300) + gerador.nextDouble()));			
//				Double pontos = Double.parseDouble(String.valueOf(gerador.nextInt(300) + gerador.nextDouble()));		
//				Double pontosCampeonato = Double.parseDouble(String.valueOf(gerador.nextInt(300) + gerador.nextDouble()));			
//				Double valorTime = Double.parseDouble(String.valueOf(gerador.nextInt(300) + gerador.nextDouble()));	
//				
//				timeRodadaDTO.setTime(pontuacao.getTime());
//				timeRodadaDTO.setRodadaAtual(pontuacao.getRodada().getNrRodada());
//				timeRodadaDTO.setPatrimonio(patrimonio);
//				timeRodadaDTO.setPontos(pontos);
//				timeRodadaDTO.setPontosCampeonato(pontosCampeonato);			
//				timeRodadaDTO.setValorTime(valorTime);					
//				
//				pontuacao.setVrPontuacao(timeRodadaDTO.getPontos() != null ? timeRodadaDTO.getPontos() : 0.0);			
//				pontuacao.setVrCartoletas(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);			
//				
//				pontuacao.getTime().setVrCartoletasAtuais(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);	
//				
//				rodadaService.atualizar(pontuacao.getTime());	
//			}	
//			
//			for (Pontuacao pontuacao : rodadaEmAndamento.getListaPontuacao()) {	
//				pontuacao.setNomeTime(pontuacao.getTime().getNomeTime());					
//				pontuacao.setIdCartola(pontuacao.getTime().getIdCartola());
//				
//				rodadaService.atualizar(pontuacao);				
//			}				
			//MOCK
			
			
		} else {
			log.info("JOB: NAO EXISTE RODADA EM ANDAMENTO");			
		}
	}
	

	/** LOOKUPS **/
	private RDRServiceLocal lookupRdrService() {
		try {
			Context c = new InitialContext();
			return (RDRServiceLocal) c.lookup("java:global/reisdaresenha/RDRService!br.com.reisdaresenha.service.RDRServiceLocal");
		} catch (Exception ne) {			
			log.error(ne);
			throw new RuntimeException(ne);			
		}
	}
	
	private RodadaServiceLocal lookupRodadaService() {
		try {
			Context c = new InitialContext();
			return (RodadaServiceLocal) c.lookup("java:global/reisdaresenha/RodadaService!br.com.reisdaresenha.service.RodadaServiceLocal");
		} catch (Exception ne) {			
			log.error(ne);
			throw new RuntimeException(ne);			
		}
	}
	
	
	private ParametroServiceLocal lookupParametrosService() {
		try {
			Context c = new InitialContext();
			return (ParametroServiceLocal) c.lookup("java:global/reisdaresenha/ParametroService!br.com.reisdaresenha.service.ParametroServiceLocal");
		} catch (Exception ne) {			
			log.error(ne);
			throw new RuntimeException(ne);			
		}
	}
	
	private InicioServiceLocal lookupInicioService() {
		try {
			Context c = new InitialContext();
			return (InicioServiceLocal) c.lookup("java:global/reisdaresenha/InicioService!br.com.reisdaresenha.service.InicioServiceLocal");
		} catch (Exception ne) {			
			log.error(ne);
			throw new RuntimeException(ne);			
		}
	}
	
	private TimeServiceLocal lookupTimeService() {
		try {
			Context c = new InitialContext();
			return (TimeServiceLocal) c.lookup("java:global/reisdaresenha/TimeService!br.com.reisdaresenha.service.TimeServiceLocal");
		} catch (Exception ne) {			
			log.error(ne);
			throw new RuntimeException(ne);			
		}
	}
	
	private void atualizarSuperCopaEmAndamento(TimeServiceLocal timeService, InicioServiceLocal inicioService,
			RDRServiceLocal rdrService, RodadaServiceLocal rodadaService, ParametroServiceLocal parametroService,
			CartolaRestFulClient servicoCartola) {		
		
		List<RDRCopaPontuacao> listaRdrCopaPontuacao = rdrService.buscarRDRCopaPontuacao();				
		
		for (RDRCopaPontuacao rdrCopa : listaRdrCopaPontuacao) {
			atualizarPontuacaoRodadaCopa(rdrCopa, timeService, inicioService, rdrService, rodadaService, parametroService, servicoCartola);
		}	
		
	}
	
	public String atualizarPontuacaoRodadaCopa(RDRCopaPontuacao rdrCopa,
			TimeServiceLocal timeService, InicioServiceLocal inicioService,
			RDRServiceLocal rdrService, RodadaServiceLocal rodadaService, ParametroServiceLocal parametroService,
			CartolaRestFulClient servicoCartola) { 
		
		List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO = new ArrayList<ClassificacaoLigaPrincipalDTO>();
		
		Integer anoAtual = 2021; //Calendar.getInstance().get(Calendar.YEAR);	
		
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
				atualizarClassificacaoCopa(rdrCopa, timeService, inicioService, rdrService, rodadaService, parametroService, servicoCartola);
											
			} else {
				log.error(rdrCopa.getNrRodadaCartola()+"ª Rodada Principal do Cartola FC ainda nao está em andamento");			
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
				atualizarClassificacaoCopa(rdrCopa, timeService, inicioService, rdrService, rodadaService, parametroService, servicoCartola);
					
			}			
			
		}
		
		return null;
	}
	
	private void atualizarClassificacaoCopa(RDRCopaPontuacao rdrCopaPontuacao,
			TimeServiceLocal timeService, InicioServiceLocal inicioService,
			RDRServiceLocal rdrService, RodadaServiceLocal rodadaService, ParametroServiceLocal parametroService,
			CartolaRestFulClient servicoCartola) {
		
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
						
			Integer anoAtual = 2021; // Calendar.getInstance().get(Calendar.YEAR);			
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
			
			Integer anoAtual = 2021; // Calendar.getInstance().get(Calendar.YEAR);	
			
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
	
	
}