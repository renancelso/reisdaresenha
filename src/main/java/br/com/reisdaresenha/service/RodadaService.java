package br.com.reisdaresenha.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import br.com.reisdaresenha.model.Liga;
import br.com.reisdaresenha.model.OSBPontuacao;
import br.com.reisdaresenha.model.OSBRodada;
import br.com.reisdaresenha.model.Rodada;
import br.com.reisdaresenha.padrao.GenericService;

/**
 * @author Renan Celso
 */
@Stateless
public class RodadaService extends GenericService implements RodadaServiceLocal {

	private static final long serialVersionUID = 8475703620320469907L;

	@SuppressWarnings("unchecked")
	@Override
	public List<Rodada> listarRodadasDesc(Liga liga) {

		List<Rodada> lista = new ArrayList<>();

		try {

			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(Rodada.class.getSimpleName()).append(" o where 1=1 ");

			if (liga != null) {
				sql.append(" and o.liga.id = ").append(liga.getId());
			}

			sql.append(" and statusRodada = 'PS' "); // PS = PASSADA
			sql.append(" order by o.nrRodada desc");
			
			lista = (List<Rodada>) consultarPorQuery(sql.toString(), 0, 0);

			return lista;

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<OSBRodada> listarOsbRodadasDesc(Liga liga) {

		List<OSBRodada> lista = new ArrayList<>();

		try {

			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(OSBRodada.class.getSimpleName()).append(" o where 1=1 ");

			if (liga != null) {
				sql.append(" and o.liga.id = ").append(liga.getId());
			}

			sql.append(" and statusRodada = 'PS' "); // PS = PASSADA
			sql.append(" order by o.nrRodada desc");
			
			lista = (List<OSBRodada>) consultarPorQuery(sql.toString(), 0, 0);

			return lista;

		} catch (Exception e) {
			log.error(e);
			return null;
		}

	}
	

	@Override
	public Rodada buscarRodadaEmAndamento(Liga liga) {
		Rodada rodada = new Rodada();

		try {

			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(Rodada.class.getSimpleName()).append(" o where 1=1 ");

			if (liga != null) {
				sql.append(" and o.liga.id = ").append(liga.getId());
			}

			sql.append(" and statusRodada = 'EA' "); // EA = EM ANDAMENTO
			sql.append(" order by o.nrRodada desc");
			
			rodada = (Rodada) consultarPorQuery(sql.toString(), 1, 0).get(0);

			return rodada;

		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}	
	
	@Override
	public OSBRodada buscarOsbRodadaEmAndamento(Liga liga) {
		
		OSBRodada rodada = new OSBRodada();

		try {

			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(OSBRodada.class.getSimpleName()).append(" o where 1=1 ");

			if (liga != null) {
				sql.append(" and o.liga.id = ").append(liga.getId());
			}

			sql.append(" and statusRodada = 'EA' "); // EA = EM ANDAMENTO
			sql.append(" order by o.nrRodada desc");
			
			rodada = (OSBRodada) consultarPorQuery(sql.toString(), 1, 0).get(0);

			return rodada;

		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<OSBPontuacao> buscarEliminadosRodadasAnteriores(Long nrRodada) {
		
		nrRodada = nrRodada-1; // Rodada Anterior		
		List<OSBPontuacao> listaPontuacao = new ArrayList<OSBPontuacao>();
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("select o from ").append(OSBPontuacao.class.getSimpleName()).append(" o where 1=1 ");
			sql.append(" and o.situacaoFinalRodada = 'ELIMINADO' ");						
			
			listaPontuacao = (List<OSBPontuacao>) consultarPorQuery(sql.toString(), 0, 0);

			return listaPontuacao;

		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}


}
