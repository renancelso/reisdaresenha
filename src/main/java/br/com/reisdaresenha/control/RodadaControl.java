package br.com.reisdaresenha.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.Pontuacao;
import br.com.reisdaresenha.model.Rodada;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.service.InicioServiceLocal;
import br.com.reisdaresenha.service.RodadaServiceLocal;
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
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		try {
			
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
					
					//buscarPontuacaoECartoletasRodadasEmAndamento(); ---IMPLEMENTAR
					
					
					//pontuacao.setVrCartoletas();
					//pontuacao.setVrPontuacao();
										
					novaRodada.getListaPontuacao().add(pontuacao);
				}	
			}
			
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
			servicoCartola = new CartolaRestFulClient();
			
			TimeRodadaDTO timeRodadaDTO = new TimeRodadaDTO();
			
			timeRodadaDTO = servicoCartola.buscarTimeRodadaPorIDCartola(pontuacao.getTime(), pontuacao.getRodada().getNrRodada());		
			
			pontuacao.setVrPontuacao(timeRodadaDTO.getPontos() != null ? timeRodadaDTO.getPontos() : 0.0);			
			pontuacao.setVrCartoletas(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);
			
		} catch (Exception e) {
			addErrorMessage("Erro ao atualizar pontuacao do time");			
			log.error("Erro ao atualizar pontuacao do time \n"+e);
		}
		
		btnSalvarRodada();
		
		return null;
	}
	
	
	public String atualizarTodasPontuacoesRodadaEmAndamento() {		
		try {				
			servicoCartola = new CartolaRestFulClient();			
			
//			for (Pontuacao pontuacao : novaRodada.getListaPontuacao()) {
//				TimeRodadaDTO timeRodadaDTO = new TimeRodadaDTO();			
//				timeRodadaDTO = servicoCartola.buscarTimeRodadaPorIDCartola(pontuacao.getTime(), pontuacao.getRodada().getNrRodada());	
//				pontuacao.setVrPontuacao(timeRodadaDTO.getPontos() != null ? timeRodadaDTO.getPontos() : 0.0);			
//				pontuacao.setVrCartoletas(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);
//			}	
			
			//MOCK
			for (Pontuacao pontuacao : novaRodada.getListaPontuacao()) {
				
				TimeRodadaDTO timeRodadaDTO = new TimeRodadaDTO();					
				
				Random gerador = new Random();
				
				Double patrimonio = Double.parseDouble(String.valueOf(gerador.nextInt(300)));			
				Double pontos = Double.parseDouble(String.valueOf(gerador.nextInt(300)));		
				Double pontosCampeonato = Double.parseDouble(String.valueOf(gerador.nextInt(300)));			
				Double valorTime = Double.parseDouble(String.valueOf(gerador.nextInt(300)));	
				
				timeRodadaDTO.setTime(pontuacao.getTime());
				timeRodadaDTO.setRodadaAtual(pontuacao.getRodada().getNrRodada());
				timeRodadaDTO.setPatrimonio(patrimonio);
				timeRodadaDTO.setPontos(pontos);
				timeRodadaDTO.setPontosCampeonato(pontosCampeonato);			
				timeRodadaDTO.setValorTime(valorTime);					
				pontuacao.setVrPontuacao(timeRodadaDTO.getPontos() != null ? timeRodadaDTO.getPontos() : 0.0);			
				pontuacao.setVrCartoletas(timeRodadaDTO.getPatrimonio() != null ? timeRodadaDTO.getPatrimonio()  : 0.0);				
			}	
			
			
		} catch (Exception e) {
			addErrorMessage("Erro ao atualizar pontuacao do time");			
			log.error("Erro ao atualizar pontuacao do time \n"+e);
		}
		
		btnSalvarRodada();
		
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
					//pontuacao.setVrCartoletas();
					//pontuacao.setVrPontuacao();
					novaRodada.getListaPontuacao().add(pontuacao);
				}	
			} else {
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
			
			addInfoMessage(novaRodada.getNrRodada()+"ª Rodada atualizada com sucesso!");
			
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
		
}
