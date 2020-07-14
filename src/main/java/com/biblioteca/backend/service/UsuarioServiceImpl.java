package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Rol;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.model.Usuario.DTO.ChangePassword;
import com.biblioteca.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private ILocalService localService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsuario(String username) {
        return repository.findByUsuario(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    @Transactional
    public Usuario saveUser(Usuario usuario, String tipoOperacion) {
        switch (tipoOperacion){
            case "CUENTA INACTIVA":
                usuario.setActivo(false);
                usuario.setRol(roleService.findByAuthority("ROLE_USUARIO").orElseThrow());
                usuario.setLocal(localService.findById(1L).orElseThrow());
                break;
            case "USUARIO NUEVO DESDE SISTEMA":
                usuario.setActivo(true);
                break;
            case "CUENTA ACTIVADA":
            case "CUENTA RECUPERADA":
            case "CUENTA REGISTRADA DESDE SISTEMA":
            case "CUENTA ACTUALIZADA DESDE SISTEMA":
            case "CUENTA DESHABILITADA":
                break;
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return repository.save(usuario);
    }

    /*@Override
    @Transactional
    public Usuario save(Usuario usuario) {
        return repository.save(usuario);
    }*/

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Usuario cambiarPassword(ChangePassword dtoPassword) throws Exception {
        Usuario usuario = findById(dtoPassword.getId()).orElseThrow();

        if (!passwordEncoder.matches(dtoPassword.getPasswordActual(), usuario.getPassword())) {
            throw new Exception("La contraseña actual es incorrecta");
        }

        if (passwordEncoder.matches(dtoPassword.getNuevaPassword(), usuario.getPassword())) {
            throw new Exception("La nueva contraseña debe ser diferente a la actual");
        }

        if (!dtoPassword.getNuevaPassword().equals(dtoPassword.getConfirmarPassword())) {
            throw new Exception("Las contraseñas no coinciden");
        }

        String passwordHash = passwordEncoder.encode(dtoPassword.getNuevaPassword());
        usuario.setPassword(passwordHash);

        return repository.save(usuario);
    }

    @Override
    public Usuario recuperarPassword(ChangePassword dtoPassword) throws Exception {
        Usuario usuario = findById(dtoPassword.getId()).get();
        if (passwordEncoder.matches(dtoPassword.getNuevaPassword(), usuario.getPassword())) {
            throw new Exception("La nueva contraseña debe ser diferente a la actual");
        }
        if (!dtoPassword.getNuevaPassword().equals(dtoPassword.getConfirmarPassword())) {
            throw new Exception("Las contraseñas no coinciden");
        }
        String passwordHash = passwordEncoder.encode(dtoPassword.getNuevaPassword());
        usuario.setPassword(passwordHash);

        return repository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByDniAndEmail(String dni, String email) {
        return repository.findByDniAndEmail(dni, email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByLocal(Long id) {
        return repository.findByLocal(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByRol(String authority) {
        return repository.findByRol(authority);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByRoles() {
        return repository.findByRoles();
    }

}