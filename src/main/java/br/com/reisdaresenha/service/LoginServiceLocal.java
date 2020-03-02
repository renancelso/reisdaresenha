package br.com.reisdaresenha.service;

import java.util.List;

import javax.ejb.Local;

import br.com.reisdaresenha.model.Usuario;
import br.com.reisdaresenha.padrao.GenericServiceInterface;

/**
 * @author Renan Celso
 */
@Local
public interface LoginServiceLocal extends GenericServiceInterface{

	public List<Usuario> buscarUsuarioPorLogin(String email);

}
