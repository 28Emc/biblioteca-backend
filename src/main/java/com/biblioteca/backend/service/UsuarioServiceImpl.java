package com.biblioteca.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Empleado;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Persona.DTO.PersonaDTO;
import com.biblioteca.backend.model.Persona.Persona;
import com.biblioteca.backend.model.Rol;
import com.biblioteca.backend.model.Token;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.model.Usuario.DTO.ChangePassword;
import com.biblioteca.backend.repository.EmpleadoRepository;
import com.biblioteca.backend.repository.PersonaRepository;
import com.biblioteca.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ILocalService localService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ITokenService tokenService;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) throws Exception {
        if (!id.toString().matches("^\\d+$")) throw new Exception("El id es inválido");

        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByIdPersona(Long idPersona) throws Exception {
        return usuarioRepository.findByIdPersona(idPersona);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsuario(String usuario) {
        return usuarioRepository.findByEmail(usuario);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Usuario findByNroDocumentoAndUsuario(String nroDocumento, String usuario) throws Exception {
        Persona personaFound = personaRepository.findByNroDocumento(nroDocumento).orElseThrow(() ->
                new Exception("La persona con ese documento no existe"));
        return findByIdPersona(personaFound.getId())
                .stream()
                .filter(usuarioItem -> usuarioItem.getUsuario().equals(usuario))
                .findFirst()
                .orElseThrow(() -> new Exception("El usuario no existe o está deshabilitado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByRoles() {
        return usuarioRepository.findByRoles();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByRol(String authority) {
        return usuarioRepository.findByRol(authority);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> existsAdminInLocal(Long local) {
        return usuarioRepository.existsAdminInLocal(local);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Usuario save(PersonaDTO personaDTO) throws Exception {
        Optional<Usuario> usuarioFound = findByUsuario(personaDTO.getUsuario());

        if (usuarioFound.isPresent()) throw new Exception("El correo ya està registrado");

        Rol rolFound = roleService.findById(personaDTO.getIdRol()).orElseThrow(() ->
                new Exception("El rol no existe"));

        Persona personaNew = new Persona();
        personaNew.setNombre(personaDTO.getNombre());
        personaNew.setApellidoPaterno(personaDTO.getApellidoPaterno());
        personaNew.setApellidoMaterno(personaDTO.getApellidoMaterno());
        personaNew.setTipoDocumento(personaDTO.getTipoDocumento());
        personaNew.setNroDocumento(personaDTO.getNroDocumento());
        personaNew.setSexo(personaDTO.getSexo());
        personaNew.setDireccion(personaDTO.getDireccion());
        personaNew.setCelular(personaDTO.getCelular());
        personaRepository.save(personaNew);

        Usuario usuarioNew = new Usuario();
        usuarioNew.setPersona(personaNew);
        usuarioNew.setRol(rolFound);
        usuarioNew.setUsuario(personaDTO.getUsuario());
        usuarioNew.setPassword(passwordEncoder.encode(personaDTO.getPassword()));
        usuarioNew.setFotoUsuario(""); // TODO: AGREGAR LA RUTA DE LA FOTO POR DEFECTO
        usuarioNew.setActivo(true);

        return usuarioRepository.save(usuarioNew);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Usuario activateUser(String token) throws Exception {
        Token tokenConfirma = tokenService.findByToken(token).orElseThrow(() ->
                new Exception("El enlace es inválido o el token ya caducó"));
        Usuario usuarioFound = findByUsuario(tokenConfirma.getUsuario().getUsuario()).orElseThrow(() ->
                new Exception("El usuario no existe"));
        usuarioFound.setActivo(true);

        tokenService.delete(tokenConfirma.getId());

        return usuarioRepository.save(usuarioFound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Usuario update(Long id, PersonaDTO personaDTO, Usuario usuarioLogueado) throws Exception {
        if (!id.toString().matches("^\\d+$")) throw new Exception("El id es inválido");

        Rol rolFound = roleService.findById(personaDTO.getIdRol()).orElseThrow(() ->
                new Exception("El rol no existe"));

        Usuario usuarioUpdate = findById(id).orElseThrow(() -> new Exception("El usuario no existe"));

        if (usuarioUpdate.getId().equals(usuarioLogueado.getId()) && usuarioLogueado.getRol().getAuthority().equals("ROLE_ADMIN")) {
            usuarioUpdate.setRol(usuarioLogueado.getRol());
            Empleado empleadoFound = empleadoRepository.findByIdUsuario(usuarioLogueado.getId());
            Local localFound = localService.findById(empleadoFound.getLocal().getId()).orElseThrow(() ->
                    new Exception("El local no existe"));
            empleadoFound.setLocal(localFound);
            empleadoRepository.save(empleadoFound);
        }

        Persona personaUpdate = personaRepository.findById(usuarioUpdate.getPersona().getId()).orElseThrow(() ->
                new Exception(("La persona no existe")));

        personaUpdate.setNombre(personaDTO.getNombre());
        personaUpdate.setApellidoPaterno(personaDTO.getApellidoPaterno());
        personaUpdate.setApellidoMaterno(personaDTO.getApellidoMaterno());
        personaUpdate.setTipoDocumento(personaDTO.getTipoDocumento());
        personaUpdate.setNroDocumento(personaDTO.getNroDocumento());
        personaUpdate.setSexo(personaDTO.getSexo());
        personaUpdate.setDireccion(personaDTO.getDireccion());
        personaUpdate.setCelular(personaDTO.getCelular());
        personaRepository.save(personaUpdate);

        usuarioUpdate.setPersona(personaUpdate);
        usuarioUpdate.setRol(rolFound);
        usuarioUpdate.setUsuario(personaDTO.getUsuario());
        usuarioUpdate.setPassword(passwordEncoder.encode(personaDTO.getPassword()));
        usuarioUpdate.setFotoUsuario(""); // TODO: AGREGAR LA RUTA DE LA FOTO POR DEFECTO
        usuarioUpdate.setActivo(true);
        return usuarioRepository.save(usuarioUpdate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Usuario changeUsuarioState(Long idUsuario, boolean tipoOperacion) throws Exception {
        if (!idUsuario.toString().matches("^\\d+$")) throw new Exception("El id es inválido");

        Usuario usuarioUpdate = usuarioRepository.findById(idUsuario).orElseThrow(() ->
                new Exception("El usuario no existe"));

        usuarioUpdate.setActivo(tipoOperacion);
        usuarioUpdate.setFechaActualizacion(LocalDateTime.now());
        if (!tipoOperacion) usuarioUpdate.setFechaBaja(LocalDateTime.now());

        Persona personaUpdate = personaRepository.findById(usuarioUpdate.getPersona().getId()).orElseThrow(() ->
                new Exception(("La persona no existe")));

        personaUpdate.setFechaActualizacion(LocalDateTime.now());
        if (!tipoOperacion) personaUpdate.setFechaBaja(LocalDateTime.now());
        personaRepository.save(personaUpdate);

        usuarioUpdate.setPersona(personaUpdate);
        return usuarioRepository.save(usuarioUpdate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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

        return usuarioRepository.save(usuario);
    }

}