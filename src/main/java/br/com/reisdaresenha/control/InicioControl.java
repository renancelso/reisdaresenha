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
import br.com.reisdaresenha.model.OSBPontuacao;
import br.com.reisdaresenha.model.OSBRodada;
import br.com.reisdaresenha.model.Premiacao;
import br.com.reisdaresenha.model.RDRParticipante;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.service.InicioServiceLocal;
import br.com.reisdaresenha.service.RDRServiceLocal;
import br.com.reisdaresenha.service.RodadaServiceLocal;
import br.com.reisdaresenha.view.ClassificacaoLigaPrincipalDTO;


/**
 * @author Renan Celso
 */
@ManagedBean(name = "inicioControl")
@ViewScoped
public class InicioControl extends BaseControl {

	private static final long serialVersionUID = 6586582974617047711L;

	private transient Logger log = Logger.getLogger(InicioControl.class.getName());
	
	@EJB
	private InicioServiceLocal inicioService; 
	
	@EJB
	private RodadaServiceLocal rodadaService; 
	
	private List<Liga> listaLigas;
	private List<Premiacao> listaPremiacaoLigaPrincipal;	
	private List<Premiacao> listaPremiacaoLigaReisResenha;	
	private List<Premiacao> listaPremiacaoLigaOsobrevivente;		
	private List<Time> listaTimesParticipantes;	
	
	private List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO;
		
	// O Sobrevivente	
	private Liga ligaOSobrevivente;
	private List<OSBRodada> listaOsbRodadas;
	
	@EJB
	private RDRServiceLocal rdrService;		
	
	private String strTimesCopaDoBrasilCartolaBelem;	
		
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		try {
			
			listarPremiacoes();
			listarClassificacaoLigaPrincipal();
			
			listaTimesParticipantes = new ArrayList<>();
			listaTimesParticipantes = (List<Time>) inicioService.consultarTodos(Time.class, " order by o.nomeTime, o.nomeDonoTime ");		
			
			ligaOSobrevivente = buscarLigaOSobrevivente();	
			listaOsbRodadas = rodadaService.listarTODASOsbRodadasDesc(ligaOSobrevivente);	
			
			for (OSBRodada osbRodada : listaOsbRodadas) {
				osbRodada.setListaOsbPontuacao(new ArrayList<OSBPontuacao>());	
				osbRodada.setListaOsbPontuacao(inicioService.buscarHistoricoClassificacaoOsbRodadas(osbRodada));
			}	
			
			strTimesCopaDoBrasilCartolaBelem = "";
						
		} catch (Exception e) {
			log.error("Erro no método init "+e.getMessage());			
		}
	}
	
	public void listarClassificacaoLigaPrincipal() {		
		
		listaClassificacaoLigaPrincipalDTO = new ArrayList<>();
		Integer anoAtual = 2020;//Calendar.getInstance().get(Calendar.YEAR);			
		listaClassificacaoLigaPrincipalDTO = inicioService.buscarClassificacaoLigaPrincipal(anoAtual);	
		
		try {
		
			//Apertura
			List<RDRParticipante> listaParticipantesAperturaSerieA = new ArrayList<RDRParticipante>();
			listaParticipantesAperturaSerieA = rdrService.buscarRDRParticipantes("A", "SA");		
			List<RDRParticipante> listaParticipantesAperturaSerieB = new ArrayList<RDRParticipante>();
			listaParticipantesAperturaSerieB = rdrService.buscarRDRParticipantes("A", "SB");
					
			//Clausura
			List<RDRParticipante> listaParticipantesClausuraSerieA = new ArrayList<RDRParticipante>();
			listaParticipantesClausuraSerieA = rdrService.buscarRDRParticipantes("C", "SA");		
			List<RDRParticipante> listaParticipantesClausuraSerieB = new ArrayList<RDRParticipante>();
			listaParticipantesClausuraSerieB = rdrService.buscarRDRParticipantes("C", "SB");
							
			for (ClassificacaoLigaPrincipalDTO classificacao : listaClassificacaoLigaPrincipalDTO) {
				
				if(classificacao.getJogos() < 20) {				
					for (RDRParticipante rdrParticipante : listaParticipantesAperturaSerieA) {
						if(classificacao.getIdTimeCartola().equals(rdrParticipante.getIdTimeCartola())) {
							classificacao.setSerieA(true);	
							classificacao.setSerieB(false);	
							break;
						}
					}
					
					for (RDRParticipante rdrParticipante : listaParticipantesAperturaSerieB) {
						if(classificacao.getIdTimeCartola().equals(rdrParticipante.getIdTimeCartola())) {
							classificacao.setSerieA(false);	
							classificacao.setSerieB(true);	
							break;
						}					
					}
					
				} else {
					
					for (RDRParticipante rdrParticipante : listaParticipantesClausuraSerieA) {
						if(classificacao.getIdTimeCartola().equals(rdrParticipante.getIdTimeCartola())) {
							classificacao.setSerieA(true);	
							classificacao.setSerieB(false);	
							break;
						}
					}
					
					for (RDRParticipante rdrParticipante : listaParticipantesClausuraSerieB) {
						if(classificacao.getIdTimeCartola().equals(rdrParticipante.getIdTimeCartola())) {
							classificacao.setSerieA(false);	
							classificacao.setSerieB(true);	
							break;
						}					
					}
					
				}
				
			}
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT distinct(nome_time) FROM osbpontuacao o where o.situacao_final_rodada = 'CLASSIFICADO' and o.nome_time not in(select nome_time from OSBPontuacao o where o.situacao_Final_Rodada = 'ELIMINADO')");
			
			List<String> listaOSB = (List<String>) inicioService.consultarPorQueryNativa(sql.toString(), 0, 0);
			
			for (ClassificacaoLigaPrincipalDTO classificacao : listaClassificacaoLigaPrincipalDTO) {
				for (String str : listaOSB) {				
					if(classificacao.getTime().equalsIgnoreCase(str)) {
						classificacao.setSobrevivente(true);
					}				
				}
			}	
		
		} catch (Exception e) {
			log.error(e);
		}		
	}

	private void listarPremiacoes() {		
		
		Integer anoAtual = 2020;//Calendar.getInstance().get(Calendar.YEAR);	
		
		listaLigas =  new ArrayList<>();
		listaPremiacaoLigaPrincipal = new ArrayList<>();
		listaPremiacaoLigaReisResenha = new ArrayList<>();
		listaPremiacaoLigaOsobrevivente = new ArrayList<>();		
		
		listaLigas = inicioService.buscarLigas(anoAtual);
		
		if(listaLigas != null && !listaLigas.isEmpty()) {			
			for (Liga liga : listaLigas) {				
				
				if(liga.getNomeLiga().toUpperCase().contains("PRINCIPAL")) {
					
					listaPremiacaoLigaPrincipal = inicioService.buscarPremiacoes(anoAtual, liga);
				
				} else if(liga.getNomeLiga().toUpperCase().contains("RESENHA")) {
					
					listaPremiacaoLigaReisResenha = inicioService.buscarPremiacoes(anoAtual, liga);
				
				} else if(liga.getNomeLiga().toUpperCase().contains("SOBREVIVENTE")) {
					
					listaPremiacaoLigaOsobrevivente = inicioService.buscarPremiacoes(anoAtual, liga);
				
				}
				
			}
		}
		
	}
	
	public Liga buscarLigaOSobrevivente() {		
		Integer anoAtual = 2020;//Calendar.getInstance().get(Calendar.YEAR);	
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
	
	public String btnLimparCopaDoBrasil() {
		
		strTimesCopaDoBrasilCartolaBelem = "";
		
		return null;
	}
	
	public String sortearTimesCopaDoBrasilLigaCartolaBelem() {
	
		strTimesCopaDoBrasilCartolaBelem = " \n ";
		
		Random gerador = new Random();	
		
		List<String> timesLigasCartolaBelem = new ArrayList<String>();
		
		while(timesLigasCartolaBelem.size() < 16) {
			Long numero = Long.parseLong(String.valueOf(gerador.nextInt(72)));
			if(numero > 0 && !timesLigasCartolaBelem.contains("Time "+numero)) {
				timesLigasCartolaBelem.add("Time "+numero);
			}
		}
		
		if (timesLigasCartolaBelem.size() % 2 == 1) {
			timesLigasCartolaBelem.add(0, "");
		}

		int t = timesLigasCartolaBelem.size();
		int m = timesLigasCartolaBelem.size() / 2;
				
		for (int i = 0; i < 1; i++) {			
			
			for (int j = 0; j < m; j++) {	
				if (j % 2 == 1 || i % 2 == 1 && j == 0) {
					strTimesCopaDoBrasilCartolaBelem += (timesLigasCartolaBelem.get(t - j - 1) + " x " + timesLigasCartolaBelem.get(j) + "\n \n");
				} else {
					strTimesCopaDoBrasilCartolaBelem += (timesLigasCartolaBelem.get(j) + " x " + timesLigasCartolaBelem.get(t - j - 1) + "\n \n");
				}
			}
						
			timesLigasCartolaBelem.add(1, timesLigasCartolaBelem.remove(timesLigasCartolaBelem.size() - 1));
			
		}
		
		return null;
	}	
	
	public List<Premiacao> getListaPremiacaoLigaPrincipal() {
		return listaPremiacaoLigaPrincipal;
	}

	public void setListaPremiacaoLigaPrincipal(List<Premiacao> listaPremiacaoLigaPrincipal) {
		this.listaPremiacaoLigaPrincipal = listaPremiacaoLigaPrincipal;
	}

	public List<Premiacao> getListaPremiacaoLigaReisResenha() {
		return listaPremiacaoLigaReisResenha;
	}

	public void setListaPremiacaoLigaReisResenha(List<Premiacao> listaPremiacaoLigaReisResenha) {
		this.listaPremiacaoLigaReisResenha = listaPremiacaoLigaReisResenha;
	}

	public List<Premiacao> getListaPremiacaoLigaOsobrevivente() {
		return listaPremiacaoLigaOsobrevivente;
	}

	public void setListaPremiacaoLigaOsobrevivente(List<Premiacao> listaPremiacaoLigaOsobrevivente) {
		this.listaPremiacaoLigaOsobrevivente = listaPremiacaoLigaOsobrevivente;
	}

	public List<Liga> getListaLigas() {
		return listaLigas;
	}

	public void setListaLigas(List<Liga> listaLigas) {
		this.listaLigas = listaLigas;
	}

	public List<ClassificacaoLigaPrincipalDTO> getListaClassificacaoLigaPrincipalDTO() {
		return listaClassificacaoLigaPrincipalDTO;
	}

	public void setListaClassificacaoLigaPrincipalDTO(List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO) {
		this.listaClassificacaoLigaPrincipalDTO = listaClassificacaoLigaPrincipalDTO;
	}

	public List<Time> getListaTimesParticipantes() {
		return listaTimesParticipantes;
	}

	public void setListaTimesParticipantes(List<Time> listaTimesParticipantes) {
		this.listaTimesParticipantes = listaTimesParticipantes;
	}

	public List<OSBRodada> getListaOsbRodadas() {
		return listaOsbRodadas;
	}

	public void setListaOsbRodadas(List<OSBRodada> listaOsbRodadas) {
		this.listaOsbRodadas = listaOsbRodadas;
	}

	public Liga getLigaOSobrevivente() {
		return ligaOSobrevivente;
	}

	public void setLigaOSobrevivente(Liga ligaOSobrevivente) {
		this.ligaOSobrevivente = ligaOSobrevivente;
	}

	public String getStrTimesCopaDoBrasilCartolaBelem() {
		return strTimesCopaDoBrasilCartolaBelem;
	}

	public void setStrTimesCopaDoBrasilCartolaBelem(String strTimesCopaDoBrasilCartolaBelem) {
		this.strTimesCopaDoBrasilCartolaBelem = strTimesCopaDoBrasilCartolaBelem;
	}
	
}
