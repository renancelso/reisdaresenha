package br.com.reisdaresenha.service;

import java.util.List;

import javax.ejb.Local;

import br.com.reisdaresenha.model.RDRClassificacao;
import br.com.reisdaresenha.model.RDRCopaPontuacao;
import br.com.reisdaresenha.model.RDRParticipante;
import br.com.reisdaresenha.model.RDRPontuacao;
import br.com.reisdaresenha.model.RDRRodada;
import br.com.reisdaresenha.padrao.GenericServiceInterface;

/**
 * @author Renan Celso
 */
@Local
public interface RDRServiceLocal extends GenericServiceInterface {

	public List<RDRParticipante> buscarRDRParticipantes(String fase, String serie);

	public List<RDRClassificacao> buscarRDRClassificacao(String fase, String serie);

	public List<RDRRodada> buscarRDRRodadas(String fase, String serie);

	public List<RDRPontuacao> buscarRDRPontuacaoPorRodada(RDRRodada rdrRodada);

	public RDRClassificacao buscarRDRClassificacaoPorRodadaParticipante(String fase, String serie, Long nrRodada, RDRParticipante rdrParticipante);

	public RDRClassificacao buscarRDRClassificacaoPorRodada(String fase, String serie, Long nrRodada);

	public List<RDRCopaPontuacao> buscarRDRCopaPontuacao();

	public RDRRodada buscarRDRRodadaPorRodadaDaLigaPrincipal(Long nrRodadaLigaPrincipal, String fase, String serie);

	public RDRCopaPontuacao buscarRDRCopaPontuacaoPorNrJogoCopa(Long nrJogoCopa);

	public void atualizarSaldoDeGols(String fase, String serie, List<RDRParticipante> listaParticipantes);

	public RDRClassificacao buscarRDRClassificacaoPorParticipante(String fase, String serie, RDRParticipante rdrParticipante);

	public RDRParticipante buscarRDRParticipantesCopaPorClassificacaoFinal(String fase, String serie, String classFinal);		
	
		
}
