package daw.examples.jpamysql.services;

import java.util.List;
import daw.examples.jpamysql.models.User;

public interface IUserService {

	public List<User> listarUsuarios();
	
	public void guardarUsuario(User user);
	
	public void eliminarUsuario(User user);
	
	public User buscarUsuario(User user);
	
}
