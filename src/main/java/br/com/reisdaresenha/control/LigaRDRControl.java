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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.BaseControl;
import br.com.reisdaresenha.rest.CartolaRestFulClient;
import br.com.reisdaresenha.service.ParametroServiceLocal;
import br.com.reisdaresenha.view.TimeCartolaRestDTO;

/**
 * @author Renan Celso
 * Classe criada para Testes
 */
@ManagedBean(name = "ligaRDRControl")
@ViewScoped
public class LigaRDRControl extends BaseControl {

	private static final long serialVersionUID = 4471049778894375957L;	
	
	private transient Logger log = Logger.getLogger(LigaRDRControl.class.getName());
	
	@EJB
	private ParametroServiceLocal parametroService;
		
	private Usuario usuarioLogado;	
	
	private String nomeLiga;
	
	private CartolaRestFulClient servicoCartola;
	
	@SuppressWarnings("unused")
	@PostConstruct
	public void init() {
		
		try {	
			
			HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);	
			usuarioLogado = (Usuario) sessao.getAttribute("usuarioLogado");			
			nomeLiga = parametroService.buscarParametroPorChave("nome_liga").getValor();
			servicoCartola = new CartolaRestFulClient();	
			
//			String email = parametroService.buscarParametroPorChave("user_email").getValor();			
//			String senha = parametroService.buscarParametroPorChave("user_senha").getValor();
//			
//			String slugLiga = servicoCartola.buscarSlugDaLiga(nomeLiga);
//						
//			String token = servicoCartola.gerarTokenLoginCartola(email, senha);
//						
//			JSONObject jsonObject = new JSONObject();
//			
//			if(slugLiga != null && token != null) {
//				jsonObject = servicoCartola.buscarInformacoesLigaEspecifica(slugLiga, token);
//			}
//						
//			JSONObject jsonObjectLiga = (JSONObject) jsonObject.get("liga");
//						
//			JSONArray jsonArrayTimesParticipantes = (JSONArray) jsonObject.get("times");
//											
//			List<TimeCartolaRestDTO> listaTimeCartolaRestDTO = new ArrayList<TimeCartolaRestDTO>();
//			
//			for (int i = 0; i < jsonArrayTimesParticipantes.size(); i++) {				
//				JSONObject jsonObjectTime = (JSONObject) jsonArrayTimesParticipantes.get(i);		
//				
//				TimeCartolaRestDTO timeCartolaRestDTO = new TimeCartolaRestDTO();
//				
//				timeCartolaRestDTO.setIdCartola(new Long(String.valueOf(jsonObjectTime.get("time_id"))));
//				timeCartolaRestDTO.setNomeTime(String.valueOf(jsonObjectTime.get("nome")));
//				timeCartolaRestDTO.setSlug(String.valueOf(jsonObjectTime.get("slug")));								
//				JSONObject jsonObjectPontos = (JSONObject) jsonObjectTime.get("pontos");
//				timeCartolaRestDTO.setRodada(new Long(String.valueOf(jsonObjectPontos.get("rodada"))));				
//				timeCartolaRestDTO.setPontosCapitao(new Double(String.valueOf(jsonObjectPontos.get("capitao"))));	
//				
//				listaTimeCartolaRestDTO.add(timeCartolaRestDTO);
//			}			
			
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
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
}
