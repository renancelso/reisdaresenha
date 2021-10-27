package br.com.reisdaresenha.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.service.ParametroServiceLocal;
import br.com.reisdaresenha.service.TimeServiceLocal;
import br.com.reisdaresenha.view.TimeCartolaRestDTO;
import br.com.reisdaresenha.view.TimeRodadaDTO;

/**
 * @author Renan Celso
 * Classe criada para Testes
 */
@ManagedBean(name = "sincronizacaoControl")
@ViewScoped
public class SincronizacaoControl extends BaseControl {

	private static final long serialVersionUID = 4471049778894375957L;	
	
	private transient Logger log = Logger.getLogger(SincronizacaoControl.class.getName());
	
	@EJB
	private ParametroServiceLocal parametroService;
	
	@EJB
	private TimeServiceLocal timeService;
		
	private Usuario usuarioLogado;	
	
	private String nomeLiga;
	
	private CartolaRestFulClient servicoCartola;
	
	private List<TimeCartolaRestDTO> listaTimeCartolaRestDTO;
	
	private List<Time> listaTimes;	
	
	private List<TimeCartolaRestDTO> listaTimesCartolaRestDTOQueNaoEstaoNoSistema;
	
	private List<Time> listaTimesQueEstaoNoSistemaENaoEstaoNaLigaNoCartola;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		
		try {	
			log.info("SincronizacaoControl: INIT BEGIN...");
			
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);	
			usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");			
			nomeLiga = parametroService.buscarParametroPorChave("nome_liga").getValor();
			servicoCartola = new CartolaRestFulClient();	
			
			String email = parametroService.buscarParametroPorChave("user_email").getValor();			
			String senha = parametroService.buscarParametroPorChave("user_senha").getValor();
			
			String slugLiga = servicoCartola.buscarSlugDaLiga(nomeLiga);
						
			String token = servicoCartola.gerarTokenLoginCartola(email, senha);
						
			JSONObject jsonObject = new JSONObject();
			
			if(slugLiga != null && token != null) {
				jsonObject = servicoCartola.buscarInformacoesLigaEspecifica(slugLiga, token);
			}
			
			//System.out.println(jsonObject.toString());
						
			JSONObject jsonObjectLiga = (JSONObject) jsonObject.get("liga");
						
			JSONArray jsonArrayTimesParticipantes = (JSONArray) jsonObject.get("times");
											
			listaTimeCartolaRestDTO = new ArrayList<TimeCartolaRestDTO>();
			
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
				//timeCartolaRestDTO.setRodada(new Long(String.valueOf(jsonObjectPontos.get("rodada"))));				
				timeCartolaRestDTO.setPontosCapitao(new Double(String.valueOf(jsonObjectPontos.get("capitao"))));
				
				listaTimeCartolaRestDTO.add(timeCartolaRestDTO);
			}	
			
			Collections.sort(listaTimeCartolaRestDTO);			
			
			listaTimes = (List<Time>) parametroService.consultarTodos(Time.class, " order by o.nomeTime");
								
			listaTimesCartolaRestDTOQueNaoEstaoNoSistema = new ArrayList<TimeCartolaRestDTO>();	
			
			for (TimeCartolaRestDTO timeCartolaRestDTO : listaTimeCartolaRestDTO) {
				Time time = timeService.buscarTimePorIdCartola(timeCartolaRestDTO.getIdCartola());				
				if(time == null) {
					listaTimesCartolaRestDTOQueNaoEstaoNoSistema.add(timeCartolaRestDTO);
				}
			}
			
			listaTimesQueEstaoNoSistemaENaoEstaoNaLigaNoCartola = new ArrayList<Time>();
			
			for (Time time : listaTimes) {
				
				boolean timeEstaNoSistemaENoCartolaTambem = false;
				
				for (TimeCartolaRestDTO timeCartolaRestDTO : listaTimeCartolaRestDTO) {	
					
					if(time.getIdCartola().equals(timeCartolaRestDTO.getIdCartola())) {
						timeEstaNoSistemaENoCartolaTambem = true;
						break;
					}
					
				}
				
				if(!timeEstaNoSistemaENoCartolaTambem) {
					listaTimesQueEstaoNoSistemaENaoEstaoNaLigaNoCartola.add(time);
				}
				
			}
									
			log.info("SincronizacaoControl: INIT FINISH...");
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public String btnCadastrarTimesCartolaRestDTOQueNaoEstaoNoSistema() {
		
		try {
		
			for (TimeCartolaRestDTO timeCartolaRestDTO : listaTimesCartolaRestDTOQueNaoEstaoNoSistema) {
				
				Time timeCadastrar = new Time();
				
				timeCadastrar.setNomeTime(removerAcentos(timeCartolaRestDTO.getNomeTime()));
				timeCadastrar.setStatusPagamento("P");
				
				if(timeService.buscarTimePorIdCartola(timeCadastrar.getIdCartola()) != null) {
					addErrorMessage("Time "+timeCadastrar.getNomeTime()+" já existe na base de dados.");				
					timeCadastrar = new Time();
					return null;
				}
				
				if(timeCadastrar.getLiga() == null || (timeCadastrar.getLiga() != null && timeCadastrar.getLiga().getId() == null)) {				
					
					List<Liga>listaLigas = new ArrayList<>();
					
					listaLigas = (List<Liga>) timeService.consultarTodos(Liga.class, " order by o.id asc");	
					
					timeCadastrar.setLiga(new Liga());	
					if(listaLigas != null && !listaLigas.isEmpty()) {			
						for (Liga liga : listaLigas) {		
							if(liga.getNomeLiga().toUpperCase().contains("PRINCIPAL")) {
								timeCadastrar.setLiga(liga);
								break;
							} 					
						}
					}
				}				
													
				HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				Usuario usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");						
							
				timeCadastrar.setIdUserAtu(usuarioLogado.getId().toString());
				timeCadastrar.setLoginUserAtu(usuarioLogado.getLogin());
				timeCadastrar.setDhAtu(new Date());		
					
				/** Buscar no restful do Cartola **/
				timeCadastrar.setIdCartola(timeCartolaRestDTO.getIdCartola());			
				timeCadastrar = buscarTimeNoCartola(timeCadastrar, timeCadastrar.getNomeTime());
				
				if(timeCadastrar == null) {
					timeCadastrar = new Time();
					return null;
				}
				
				if(timeCadastrar.getNomeTime() == null) {
					addErrorMessage("Nome do time é um campo obrigatório");
					return null;
				}	
				
				if(timeCadastrar.getNomeDonoTime() == null) {
					addErrorMessage("Dono do time é um campo obrigatório");
					return null;
				}				
				
				if("P".equalsIgnoreCase(timeCadastrar.getStatusPagamento()) 
						&& (timeCadastrar.getValorPago() == null || timeCadastrar.getValorPago() <= 0.0)) {
					timeCadastrar.setValorPago(250.0);
				}
				
				timeCadastrar = (Time) timeService.atualizar(timeCadastrar);			
			}
			
			addInfoMessage("Times cadastrados com sucesso.");
						
		} catch (Exception e) {
			addInfoMessage("Ocorreu erro ao cadastrar times no sistema. "+e.getMessage());
			log.error(e);			
		}
		
		init();
		
		return null;
		
	}
	
	public String retornaEstiloDiferenteSeTimeAdicionarNoCartola(Time time) {
		
		for (Time timeAux : listaTimesQueEstaoNoSistemaENaoEstaoNaLigaNoCartola) {
			if(timeAux.getIdCartola().equals(time.getIdCartola())) {					
				return "background-color: #FFC0CB; color: black; font-size: 12px; font-weight: bold; "
					  +"border-left: 2px solid #FFC0CB; border-top: 2px solid #FFC0CB;"
					  +"border-right: 2px solid #FFC0CB; border-bottom: 2px solid #FFC0CB;";					
			}			
		}
		return "";	
	}
	
	public String retornaEstiloDiferenteSeTimeAdicionarNoSistema(TimeCartolaRestDTO timeDTO) {
		
		for (Time time: listaTimes) {
			if(timeDTO.getIdCartola().equals(time.getIdCartola())) {					
				return "";				
			}			
		}
		
		return "background-color: #FFC0CB; color: black; font-size: 12px; font-weight: bold; "
		  +"border-left: 2px solid #FFC0CB; border-top: 2px solid #FFC0CB;"
		  +"border-right: 2px solid #FFC0CB; border-bottom: 2px solid #FFC0CB;";	
		
	}
	
	
	public Time buscarTimeNoCartola(Time time, String nomeTime) {		
		try {			
			servicoCartola = new CartolaRestFulClient();			
			
			TimeRodadaDTO timeRodadaDTO = new TimeRodadaDTO();			
			timeRodadaDTO = servicoCartola.buscarTimeRodadaPorIDCartola(time, new Long(0));		
						
			time.setNomeDonoTime(timeRodadaDTO.getTime().getNomeDonoTime());
			time.setIdCartola(timeRodadaDTO.getTime().getIdCartola());
			time.setFotoPerfil(timeRodadaDTO.getTime().getFotoPerfil());
			time.setUrlEscudoPng(timeRodadaDTO.getTime().getUrlEscudoPng());
			time.setUrlEscudoSvg(timeRodadaDTO.getTime().getUrlEscudoSvg());
			time.setAssinante(timeRodadaDTO.getTime().getAssinante());
			time.setSlugTime(timeRodadaDTO.getTime().getSlugTime());
			time.setFacebookId(timeRodadaDTO.getTime().getFacebookId());	
			
			return time;
			
		} catch (Exception e) {
			log.error(e);
			addErrorMessage("Nao foi possivel encontrar o time "+nomeTime+" no Cartola");			
			return null;
		}		
	}
	
	public List<TimeCartolaRestDTO> getListaTimeCartolaRestDTO() {
		return listaTimeCartolaRestDTO;
	}

	public void setListaTimeCartolaRestDTO(List<TimeCartolaRestDTO> listaTimeCartolaRestDTO) {
		this.listaTimeCartolaRestDTO = listaTimeCartolaRestDTO;
	}

	public Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(Usuario usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public String getNomeLiga() {
		return nomeLiga;
	}

	public void setNomeLiga(String nomeLiga) {
		this.nomeLiga = nomeLiga;
	}

	public List<Time> getListaTimes() {
		return listaTimes;
	}

	public void setListaTimes(List<Time> listaTimes) {
		this.listaTimes = listaTimes;
	}

	public List<TimeCartolaRestDTO> getListaTimesCartolaRestDTOQueNaoEstaoNoSistema() {
		return listaTimesCartolaRestDTOQueNaoEstaoNoSistema;
	}

	public void setListaTimesCartolaRestDTOQueNaoEstaoNoSistema(
			List<TimeCartolaRestDTO> listaTimesCartolaRestDTOQueNaoEstaoNoSistema) {
		this.listaTimesCartolaRestDTOQueNaoEstaoNoSistema = listaTimesCartolaRestDTOQueNaoEstaoNoSistema;
	}

	public List<Time> getListaTimesQueEstaoNoSistemaENaoEstaoNaLigaNoCartola() {
		return listaTimesQueEstaoNoSistemaENaoEstaoNaLigaNoCartola;
	}

	public void setListaTimesQueEstaoNoSistemaENaoEstaoNaLigaNoCartola(
			List<Time> listaTimesQueEstaoNoSistemaENaoEstaoNaLigaNoCartola) {
		this.listaTimesQueEstaoNoSistemaENaoEstaoNaLigaNoCartola = listaTimesQueEstaoNoSistemaENaoEstaoNaLigaNoCartola;
	}
}
