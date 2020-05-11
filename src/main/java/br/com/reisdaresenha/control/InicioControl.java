package br.com.reisdaresenha.control;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.Premiacao;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.service.InicioServiceLocal;
import br.com.reisdaresenha.view.ClassificacaoLigaPrincipalDTO;


/**
 * @author Renan Celso
 */
@SuppressWarnings("deprecation")
@ManagedBean(name = "inicioControl")
@ViewScoped
public class InicioControl extends BaseControl {

	private static final long serialVersionUID = 6586582974617047711L;

	private transient Logger log = Logger.getLogger(InicioControl.class.getName());
	
	@EJB
	private InicioServiceLocal inicioService; 
	
	private List<Liga> listaLigas;
	private List<Premiacao> listaPremiacaoLigaPrincipal;	
	private List<Premiacao> listaPremiacaoLigaReisResenha;	
	private List<Premiacao> listaPremiacaoLigaOsobrevivente;
	
	private List<ClassificacaoLigaPrincipalDTO> listaClassificacaoLigaPrincipalDTO;
		
	@PostConstruct
	public void init() {
		try {
			listarPremiacoes();
			listarClassificacaoLigaPrincipal();
		} catch (Exception e) {
			log.error("Erro no método init "+e.getMessage());			
		}
	}
	
	public void listarClassificacaoLigaPrincipal() {		
		listaClassificacaoLigaPrincipalDTO = new ArrayList<>();
		Integer anoAtual = Calendar.getInstance().get(Calendar.YEAR);			
		listaClassificacaoLigaPrincipalDTO = inicioService.buscarClassificacaoLigaPrincipal(anoAtual);		
	}

	private void listarPremiacoes() {		
		
		Integer anoAtual = Calendar.getInstance().get(Calendar.YEAR);	
		
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
	
}
