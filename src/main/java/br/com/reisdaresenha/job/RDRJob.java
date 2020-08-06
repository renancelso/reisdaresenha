package br.com.reisdaresenha.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.OSBPontuacao;
import br.com.reisdaresenha.model.OSBRodada;
import br.com.reisdaresenha.model.Pontuacao;
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
import br.com.reisdaresenha.view.TimeCartolaRestDTO;
import br.com.reisdaresenha.view.TimeRodadaDTO;

/**
 * 
 * @author Renan Celso, em 03/08/2020
 *
 */
public class RDRJob implements Job {
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {				
		
		try {	
			
			RDRServiceLocal rdrService = lookupRdrService();
			RodadaServiceLocal rodadaService = lookupRodadaService();
			ParametroServiceLocal parametroService = lookupParametrosService();			
			InicioServiceLocal inicioService = lookupInicioService();		
			TimeServiceLocal timeService = lookupTimeService();
			
			CartolaRestFulClient servicoCartola = new CartolaRestFulClient(); 	
						
			String rodaJob = parametroService.buscarParametroPorChave("roda_job").getValor();
			
			if("SIM".equalsIgnoreCase(rodaJob.trim())) {	
				System.out.println(">>>>>>>>>>>> Iniciando JOB em '"+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())+"' <<<<<<<<<<<<");			
				
				sincronizarTimesComCartolaFC(rdrService, servicoCartola);		
				
				atualizarPontuacaoRodadaEmAndamento(rdrService, rodadaService, parametroService, servicoCartola);		
				
				atualizarPontuacaoOSobreviventeRodadaEmAndamento(timeService, inicioService,rdrService, rodadaService, parametroService, servicoCartola);
				
				atualizarPontuacaoLigaReisDaResenhaRodadaEmAndamento(timeService, inicioService,rdrService, rodadaService, parametroService, servicoCartola);
				
				System.out.println(">>>>>>>>>>>> Finalizando JOB em '"+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())+"' <<<<<<<<<<<<");				
				
			} else {
				System.out.println("parametro >>> roda_job: "+rodaJob);				
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e.getMessage());
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
												
			Rodada rodadaPrincipal = new Rodada();
			rodadaPrincipal = rodadaService.buscarRodadaDaLigaPrincipalEspecifica(rdrRodadaAtualizarPontuacao.getNrRodadaCartola());			
			
			if(rodadaPrincipal != null) {
				rdrRodadaAtualizarPontuacao.setStatusRodada(rodadaPrincipal.getStatusRodada());				
				rdrRodadaAtualizarPontuacao = (RDRRodada) rdrService.atualizar(rdrRodadaAtualizarPontuacao);
			}			
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
			
			Integer anoAtual = 2020; //Calendar.getInstance().get(Calendar.YEAR);
		
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
			
			buscarTodasAsPontuacoesNoServicoCartolaFC(rdrService, rodadaService, parametroService, servicoCartola, rodadaEmAndamento.getListaPontuacao());
			
			//MOCK			
			for (Pontuacao pontuacao : rodadaEmAndamento.getListaPontuacao()) {				
				
				TimeRodadaDTO timeRodadaDTO = new TimeRodadaDTO();	
				Random gerador = new Random();
				Double patrimonio = Double.parseDouble(String.valueOf(gerador.nextInt(300) + gerador.nextDouble()));			
				Double pontos = Double.parseDouble(String.valueOf(gerador.nextInt(300) + gerador.nextDouble()));		
				Double pontosCampeonato = Double.parseDouble(String.valueOf(gerador.nextInt(300) + gerador.nextDouble()));			
				Double valorTime = Double.parseDouble(String.valueOf(gerador.nextInt(300) + gerador.nextDouble()));	
				
				timeRodadaDTO.setTime(pontuacao.getTime());
				timeRodadaDTO.setRodadaAtual(pontuacao.getRodada().getNrRodada());
				timeRodadaDTO.setPatrimonio(patrimonio);
				timeRodadaDTO.setPontos(pontos);
				timeRodadaDTO.setPontosCampeonato(pontosCampeonato);			
				timeRodadaDTO.setValorTime(valorTime);					
				
				pontuacao.setVrPontuacao(timeRodadaDTO.getPontos() != null ? timeRodadaDTO.getPontos() : 0.0);			
				pontuacao.setVrCartoletas(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);			
				
				pontuacao.getTime().setVrCartoletasAtuais(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);	
				
				rodadaService.atualizar(pontuacao.getTime());	
			}	
			
			for (Pontuacao pontuacao : rodadaEmAndamento.getListaPontuacao()) {	
				pontuacao.setNomeTime(pontuacao.getTime().getNomeTime());					
				pontuacao.setIdCartola(pontuacao.getTime().getIdCartola());
				
				rodadaService.atualizar(pontuacao);				
			}				
			//MOCK
			
			
		} else {
			System.out.println("JOB: NAO EXISTE RODADA EM ANDAMENTO");			
		}
	}
	
	private String buscarTodasAsPontuacoesNoServicoCartolaFC(RDRServiceLocal rdrService, RodadaServiceLocal rodadaService, ParametroServiceLocal parametroService, CartolaRestFulClient servicoCartola, List<Pontuacao> listaPontuacao) {
				
		try {	
			
			servicoCartola = new CartolaRestFulClient();		
			
			String email = parametroService.buscarParametroPorChave("user_email").getValor();			
			String senha = parametroService.buscarParametroPorChave("user_senha").getValor();				
			String slugLiga = servicoCartola.buscarSlugDaLiga(parametroService.buscarParametroPorChave("nome_liga").getValor());							
			String token = servicoCartola.gerarTokenLoginCartola(email, senha);	
			
			JSONObject jsonObject = new JSONObject();				
			
			if(slugLiga != null && token != null) {
				jsonObject = servicoCartola.buscarInformacoesLigaEspecifica(slugLiga, token);
			}	
			
			JSONObject jsonObjectLiga = new JSONObject();
			boolean isLigaSemCapitao = false;
			
			if(jsonObject != null) {
				jsonObjectLiga = (JSONObject) jsonObject.get("liga");										
				isLigaSemCapitao = (boolean) jsonObjectLiga.get("sem_capitao");
			}
			
			for (Pontuacao pontuacao : listaPontuacao) {
				
				TimeRodadaDTO timeRodadaDTO = new TimeRodadaDTO();			
				timeRodadaDTO = servicoCartola.buscarTimeRodadaPorIDCartola(pontuacao.getTime(), pontuacao.getRodada().getNrRodada());	
				
				if(timeRodadaDTO.getTime() == null) {					
					return null;
				}
				
				if(jsonObject != null) {
					
					if(isLigaSemCapitao) {	
						
						//buscar pontuacao do capitacao	 de cada time	
						JSONArray jsonArrayTimesParticipantes = (JSONArray) jsonObject.get("times");
						
						System.out.println("JSON: "+jsonArrayTimesParticipantes.toString());		
											
						List<TimeCartolaRestDTO> listaTimeCartolaRestDTO = new ArrayList<TimeCartolaRestDTO>();
						
						for (int i = 0; i < jsonArrayTimesParticipantes.size(); i++) {				
							JSONObject jsonObjectTime = (JSONObject) jsonArrayTimesParticipantes.get(i);						
							
							TimeCartolaRestDTO timeCartolaRestDTO = new TimeCartolaRestDTO();
							timeCartolaRestDTO.setIdCartola(new Long(String.valueOf(jsonObjectTime.get("time_id"))));
							timeCartolaRestDTO.setNomeTime(String.valueOf(jsonObjectTime.get("nome")));
							timeCartolaRestDTO.setSlug(String.valueOf(jsonObjectTime.get("slug")));	
							timeCartolaRestDTO.setNomeDonoTime(String.valueOf(jsonObjectTime.get("nome_cartola")));				
							timeCartolaRestDTO.setUrlEscudoSvg(String.valueOf(jsonObjectTime.get("url_escudo_svg")));
							timeCartolaRestDTO.setUrlEscudoPng(String.valueOf(jsonObjectTime.get("url_escudo_png")));
							JSONObject jsonObjectPontos = (JSONObject) jsonObjectTime.get("pontos");
							timeCartolaRestDTO.setRodada(new Long(String.valueOf(jsonObjectPontos.get("rodada"))));		
							
							timeCartolaRestDTO.setPontosCapitao(new Double(String.valueOf(jsonObjectPontos.get("capitao"))));
							
							listaTimeCartolaRestDTO.add(timeCartolaRestDTO);
						}	
						
						for (TimeCartolaRestDTO timeCartolaRestDTO : listaTimeCartolaRestDTO) {							
							if(timeRodadaDTO.getTime().getIdCartola().equals(timeCartolaRestDTO.getIdCartola()) 
									&& timeRodadaDTO.getRodadaAtual().equals(timeCartolaRestDTO.getRodada())) {										
								timeRodadaDTO.setPontos(timeRodadaDTO.getPontos() - (timeCartolaRestDTO.getPontosCapitao() / 2));								
							}							
						}						
					} 						
				
				
					pontuacao.setVrPontuacao(timeRodadaDTO.getPontos() != null ? timeRodadaDTO.getPontos() : 0.0);					
					pontuacao.setVrCartoletas(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);
					
					pontuacao.getTime().setVrCartoletasAtuais(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);	
									
					rodadaService.atualizar(pontuacao.getTime());	
				}
			}	
									
			for (Pontuacao pontuacao : listaPontuacao) {	
				pontuacao.setNomeTime(pontuacao.getTime().getNomeTime());					
				pontuacao.setIdCartola(pontuacao.getTime().getIdCartola());
				
				rodadaService.atualizar(pontuacao);				
			}	
			
			return null;
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return null;
		
	}

	/** LOOKUPS **/
	private RDRServiceLocal lookupRdrService() {
		try {
			Context c = new InitialContext();
			return (RDRServiceLocal) c.lookup("java:global/reisdaresenha/RDRService!br.com.reisdaresenha.service.RDRServiceLocal");
		} catch (Exception ne) {			
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
			throw new RuntimeException(ne);			
		}
	}
	
	private RodadaServiceLocal lookupRodadaService() {
		try {
			Context c = new InitialContext();
			return (RodadaServiceLocal) c.lookup("java:global/reisdaresenha/RodadaService!br.com.reisdaresenha.service.RodadaServiceLocal");
		} catch (Exception ne) {			
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
			throw new RuntimeException(ne);			
		}
	}
	
	
	private ParametroServiceLocal lookupParametrosService() {
		try {
			Context c = new InitialContext();
			return (ParametroServiceLocal) c.lookup("java:global/reisdaresenha/ParametroService!br.com.reisdaresenha.service.ParametroServiceLocal");
		} catch (Exception ne) {			
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
			throw new RuntimeException(ne);			
		}
	}
	
	private InicioServiceLocal lookupInicioService() {
		try {
			Context c = new InitialContext();
			return (InicioServiceLocal) c.lookup("java:global/reisdaresenha/InicioService!br.com.reisdaresenha.service.InicioServiceLocal");
		} catch (Exception ne) {			
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
			throw new RuntimeException(ne);			
		}
	}
	
	private TimeServiceLocal lookupTimeService() {
		try {
			Context c = new InitialContext();
			return (TimeServiceLocal) c.lookup("java:global/reisdaresenha/TimeService!br.com.reisdaresenha.service.TimeServiceLocal");
		} catch (Exception ne) {			
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
			throw new RuntimeException(ne);			
		}
	}
	
}