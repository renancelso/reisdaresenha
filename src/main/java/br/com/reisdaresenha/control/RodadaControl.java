package br.com.reisdaresenha.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.Pontuacao;
import br.com.reisdaresenha.model.Rodada;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.service.InicioServiceLocal;
import br.com.reisdaresenha.service.ParametroServiceLocal;
import br.com.reisdaresenha.service.RodadaServiceLocal;
import br.com.reisdaresenha.view.ClassificacaoLigaPrincipalDTO;
import br.com.reisdaresenha.view.TimeCartolaRestDTO;
import br.com.reisdaresenha.view.TimeRodadaDTO;

/**
 * @author Renan Celso
 */

@ManagedBean(name = "rodadaControl")
@ViewScoped
public class RodadaControl extends BaseControl {

	private static final long serialVersionUID = -7487285227083712357L;

	private transient Logger log = Logger.getLogger(RodadaControl.class.getName());
	
	private CartolaRestFulClient servicoCartola;
	
	@EJB
	private RodadaServiceLocal rodadaService; 
	
	@EJB
	private InicioServiceLocal inicioService; 
	
	private List<Rodada> listaRodadas;
	
	private List<Liga> listaLigas;
	
	private Rodada novaRodada;
	
	private Liga ligaPrincipal;
	
	private List<Time> listaTimes;
	
	@EJB
	private ParametroServiceLocal parametroService;
		
	private List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO;
		
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		try {
			
			Integer anoAtual = 2020;//Calendar.getInstance().get(Calendar.YEAR);	
			
			servicoCartola = new CartolaRestFulClient();		
			
			listaRodadas = new ArrayList<>();		
			
			ligaPrincipal = buscarLigaPrincipal();		
			
			listaRodadas = rodadaService.listarRodadasDesc(ligaPrincipal);	
			
			for (Rodada rodada : listaRodadas) {
				rodada.setListaPontuacao(new ArrayList<Pontuacao>());		
				List<Pontuacao> listaPontuacao = (List<Pontuacao>) 
						rodadaService.consultarPorQuery("select o from Pontuacao o where o.liga.id = "+ligaPrincipal.getId()+
														" and o.rodada.id = "+rodada.getId()+
														" order by o.time.nomeTime, o.time.nomeDonoTime", 0, 0);				
				rodada.setListaPontuacao(listaPontuacao);
			}
			
			novaRodada =  rodadaService.buscarRodadaEmAndamento(ligaPrincipal);	
			
			if(novaRodada != null) {
				novaRodada.setListaPontuacao(new ArrayList<Pontuacao>());		
				List<Pontuacao> listaPontuacao = (List<Pontuacao>) 
						rodadaService.consultarPorQuery("select o from Pontuacao o where o.liga.id = "+ligaPrincipal.getId()+
														" and o.rodada.id = "+novaRodada.getId()+
														" order by o.time.nomeTime, o.time.nomeDonoTime", 0, 0);				
				novaRodada.setListaPontuacao(listaPontuacao);
			}
						
			listaTimes = new ArrayList<>();
			listaTimes = (List<Time>) rodadaService.consultarTodos(Time.class, " order by o.nomeTime, o.nomeDonoTime ");	
			
			if(novaRodada != null && (novaRodada.getListaPontuacao() == null || novaRodada.getListaPontuacao().isEmpty())) {
				
				novaRodada.setListaPontuacao(new ArrayList<>());
				
				for (Time time : listaTimes) {
					
					Pontuacao pontuacao = new Pontuacao();
					pontuacao.setAno(novaRodada.getLiga().getAno());
					pontuacao.setLiga(novaRodada.getLiga());
					pontuacao.setRodada(novaRodada);
					pontuacao.setTime(time);					
					
					//pontuacao.setVrCartoletas();
					//pontuacao.setVrPontuacao();
										
					novaRodada.getListaPontuacao().add(pontuacao);
				}	
			}
			
			setListaClassificacaoLigaPrincipalDTO(new ArrayList<>());					
			setListaClassificacaoLigaPrincipalDTO(inicioService.buscarClassificacaoLigaPrincipal(anoAtual));		
			
		} catch (Exception e) {
			log.error("Erro no método init "+e.getMessage());			
		}
	}
	
	public Liga buscarLigaPrincipal() {		
		Integer anoAtual = 2020;//Calendar.getInstance().get(Calendar.YEAR);	
		listaLigas = inicioService.buscarLigas(anoAtual);		
		if(listaLigas != null && !listaLigas.isEmpty()) {			
			for (Liga liga : listaLigas) {	
				if(liga.getNomeLiga().toUpperCase().contains("PRINCIPAL")) {
					return liga;
				}
			}
		}			
		return null;			
	}
	
	public String atualizarPontuacaoTimeRodada(Pontuacao pontuacao) {		
		try {	
			
			List<Pontuacao> listaPontuacao = new ArrayList<Pontuacao>();	
			if(pontuacao != null) {
				listaPontuacao.add(pontuacao);
			} 
			
			buscarTodasAsPontuacoesNoServicoCartolaFC(listaPontuacao);	
			
			init();
			
		} catch (Exception e) {
			addErrorMessage("Erro ao atualizar pontuacao do time");			
			log.error("Erro ao atualizar pontuacao do time \n"+e);
		}		
		return null;
	}
	
	
	public String atualizarTodasPontuacoesRodadaEmAndamento() {		
		try {			
			
			buscarTodasAsPontuacoesNoServicoCartolaFC(novaRodada.getListaPontuacao());
			
			//MOCK			
//			for (Pontuacao pontuacao : novaRodada.getListaPontuacao()) {				
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
//			btnSalvarRodada();
//			btnFinalizarRodada();			
			//MOCK
						
			init();
			
		} catch (Exception e) {
			addErrorMessage("Erro ao atualizar pontuacao do time");			
			log.error("Erro ao atualizar pontuacao do time \n"+e);
		}
		
		return null;
	}

	private String buscarTodasAsPontuacoesNoServicoCartolaFC(List<Pontuacao> listaPontuacao) {
		
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
					addErrorMessage(pontuacao.getRodada().getNrRodada()+"ª Rodada ainda não iniciou no Cartola FC.");
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
				}
				
				pontuacao.setVrPontuacao(timeRodadaDTO.getPontos() != null ? timeRodadaDTO.getPontos() : 0.0);					
				pontuacao.setVrCartoletas(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);
				
				pontuacao.getTime().setVrCartoletasAtuais(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);	
								
				rodadaService.atualizar(pontuacao.getTime());				
			}		
			
			btnSalvarRodada();
			
			return null;
		
		} catch (Exception e) {
			addErrorMessage("Erro ao atualizar pontuacao do time");			
			log.error("Erro ao atualizar pontuacao do time \n"+e);
		}
		
		return null;
		
	}

	public String btnNovaRodada() {	
		
		try {	
			
			listaRodadas = new ArrayList<>();		
			
			ligaPrincipal = buscarLigaPrincipal();		
			
			listaRodadas = rodadaService.listarRodadasDesc(ligaPrincipal);	
			
			if(novaRodada == null) {					
				novaRodada = new Rodada();
				novaRodada.setLiga(ligaPrincipal);
				novaRodada.setNrRodada(new Long(listaRodadas.size()+1));		
				novaRodada.setStatusRodada("EA"); //Em Andamento	
				
				novaRodada = (Rodada) rodadaService.atualizar(novaRodada);
				
				novaRodada.setListaPontuacao(new ArrayList<>());	
				
				for (Time time : listaTimes) {
					Pontuacao pontuacao = new Pontuacao();
					pontuacao.setAno(novaRodada.getLiga().getAno());
					pontuacao.setLiga(novaRodada.getLiga());
					pontuacao.setRodada(novaRodada);
					pontuacao.setTime(time);
					
					pontuacao.setNomeTime(pontuacao.getTime().getNomeTime());					
					pontuacao.setIdCartola(pontuacao.getTime().getIdCartola());
					
					pontuacao = (Pontuacao) rodadaService.atualizar(pontuacao);
					
					//pontuacao.setVrCartoletas();
					//pontuacao.setVrPontuacao();
					novaRodada.getListaPontuacao().add(pontuacao);
				}	
				
				atualizarTodasPontuacoesRodadaEmAndamento();
				
			} else {
				addFatalMessage("Já existe uma rodada em Andamento.");
				init();			
			}		
			
			listaRodadas = rodadaService.listarRodadasDesc(ligaPrincipal);	
			
		} catch (Exception e) {
			log.error(e.getMessage());			
		}		
		return null;
	}
	

	@SuppressWarnings("unchecked")
	public String btnSalvarRodada() {
		try {
			
			List<Pontuacao> lista = novaRodada.getListaPontuacao();
								
			novaRodada = rodadaService.buscarRodadaEmAndamento(ligaPrincipal);
									
			for (Pontuacao pontuacao : lista) {
				pontuacao.setRodada(novaRodada);
				
				pontuacao.setNomeTime(pontuacao.getTime().getNomeTime());					
				pontuacao.setIdCartola(pontuacao.getTime().getIdCartola());
				
				rodadaService.atualizar(pontuacao);				
			}	
			
			if(novaRodada != null) {
				novaRodada.setListaPontuacao(new ArrayList<Pontuacao>());		
				List<Pontuacao> listaPontuacao = (List<Pontuacao>) 
						rodadaService.consultarPorQuery("select o from Pontuacao o where o.liga.id = "+ligaPrincipal.getId()+
														" and o.rodada.id = "+novaRodada.getId()+
														" order by o.time.nomeDonoTime, o.time.nomeTime", 0, 0);				
				novaRodada.setListaPontuacao(listaPontuacao);
			}		
			
			addInfoMessage(novaRodada.getNrRodada()+"ª Rodada atualizada com sucesso.");
			
		} catch (Exception e) {
			log.error(e);
		}
		
		return null;
	}

	public String btnFinalizarRodada() {	
		
		Long nrRodada = new Long(0);
		
		try {	
					
			/** Finalizar **/
			
			List<Pontuacao> lista = novaRodada.getListaPontuacao();
			
			novaRodada = rodadaService.buscarRodadaEmAndamento(ligaPrincipal);
			
			novaRodada.setStatusRodada("PS");
			
			novaRodada = (Rodada) rodadaService.atualizar(novaRodada);		
			
			nrRodada = novaRodada.getNrRodada();
						
			for (Pontuacao pontuacao : lista) {
				pontuacao.setRodada(novaRodada);
				
				pontuacao.setNomeTime(pontuacao.getTime().getNomeTime());					
				pontuacao.setIdCartola(pontuacao.getTime().getIdCartola());
				
				rodadaService.atualizar(pontuacao);			
				
				pontuacao.getTime().setVrCartoletasAtuais(pontuacao.getVrCartoletas());				
				rodadaService.atualizar(pontuacao.getTime());				
			}				
			
			init();
			
		} catch (Exception e) {
			log.error(e);
		}	
		
		novaRodada = null;
		
		addInfoMessage(nrRodada+"ª Rodada finalizada com sucesso");		
		
		return null;
	}

	public String btLimpar() {
		try {			
			init();
		} catch (Exception e) {
			log.error(e.getMessage());			
		}		
		return null;
	}

	public List<Rodada> getListaRodadas() {
		return listaRodadas;
	}

	public void setListaRodadas(List<Rodada> listaRodadas) {
		this.listaRodadas = listaRodadas;
	}

	public Rodada getNovaRodada() {
		return novaRodada;
	}

	public void setNovaRodada(Rodada novaRodada) {
		this.novaRodada = novaRodada;
	}

	public List<Liga> getListaLigas() {
		return listaLigas;
	}

	public void setListaLigas(List<Liga> listaLigas) {
		this.listaLigas = listaLigas;
	}

	public Liga getLigaPrincipal() {
		return ligaPrincipal;
	}

	public void setLigaPrincipal(Liga ligaPrincipal) {
		this.ligaPrincipal = ligaPrincipal;
	}

	public List<Time> getListaTimes() {
		return listaTimes;
	}

	public void setListaTimes(List<Time> listaTimes) {
		this.listaTimes = listaTimes;
	}

	public List<ClassificacaoLigaPrincipalDTO> getListaClassificacaoLigaPrincipalDTO() {
		return listaClassificacaoLigaPrincipalDTO;
	}

	public void setListaClassificacaoLigaPrincipalDTO(List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO) {
		this.listaClassificacaoLigaPrincipalDTO = listaClassificacaoLigaPrincipalDTO;
	}			
		
}
