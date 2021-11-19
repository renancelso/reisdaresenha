package br.com.reisdaresenha.service;

import java.util.List;

import javax.ejb.Local;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.OSBPontuacao;
import br.com.reisdaresenha.model.OSBRodada;
import br.com.reisdaresenha.model.Premiacao;
import br.com.reisdaresenha.model.Time;
import br.com.reisdaresenha.padrao.GenericServiceInterface;
import br.com.reisdaresenha.view.ClassificacaoLigaPrincipalDTO;

/**
 * @author Renan Celso
 */
@Local
public interface InicioServiceLocal extends GenericServiceInterface{

	public List<Premiacao> buscarPremiacoes(Integer ano, Liga liga);

	public List<Liga> buscarLigas(Integer ano);

	public List<ClassificacaoLigaPrincipalDTO> buscarClassificacaoLigaPrincipal(Integer ano);

	public List<ClassificacaoLigaPrincipalDTO> buscarHistoricoClassificacaoRodadas(Integer ano, Long nrRodada);

	public List<OSBPontuacao> buscarHistoricoClassificacaoOsbRodadas(OSBRodada osbRodada);

	public List<ClassificacaoLigaPrincipalDTO> buscarPontuacaoLigaPrincipalTime(Integer ano, Time time);

	public List<ClassificacaoLigaPrincipalDTO> buscarClassificacaoLigaPrincipalAteRodadaX(Integer ano, Integer nrRodada);

	public List<ClassificacaoLigaPrincipalDTO> buscarPontuacaoLigaPrincipalTimeAteRodadaX(Integer ano, Time time, Integer nrRodada);

	public List<OSBPontuacao> buscarHistoricoClassificacaoOsbRodadasPorId(OSBRodada osbRodada);

	public List<ClassificacaoLigaPrincipalDTO> buscarClassificacaoLigaPrincipalSemOsClassificadosDaCopa(Integer ano);

	public List<ClassificacaoLigaPrincipalDTO> buscarClassificacaoLigaPrincipalSoCopa(Integer ano);
	
}
