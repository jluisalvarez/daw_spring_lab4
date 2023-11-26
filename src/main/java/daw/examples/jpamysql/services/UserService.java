package daw.examples.jpamysql.services;

import java.util.List;
import daw.examples.jpamysql.dao.IUserDAO;
import  daw.examples.jpamysql.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
    
@Service
public class UserService implements IUserService {
    
    @Autowired
    private IUserDAO usuarioDao;
    
    @Override
    @Transactional(readOnly = true)
    public List<User> listarUsuarios() {
        List<User> listUsers = (List<User>) usuarioDao.findAll();
        return listUsers;
    }

    @Override
    @Transactional
    public void guardarUsuario(User usuario) {
        usuarioDao.save(usuario);
    }

    @Override
    @Transactional
    public void eliminarUsuario(User usuario) {
        usuarioDao.delete(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public User buscarUsuario(User usuario) {
        return usuarioDao.findById(usuario.getId()).orElse(null);
    }
}
