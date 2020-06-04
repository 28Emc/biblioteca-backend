package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Usuario;
import com.biblioteca.backend.model.dto.Usuarios.ChangePassword;
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
    public Usuario save(Usuario usuario) {
        return repository.save(usuario);
    }

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
    public Optional<Usuario> findByNroDocumentoAndEmail(String nroDocumento, String email) {
        return repository.findByNroDocumentoAndEmail(nroDocumento, email);
    }

}