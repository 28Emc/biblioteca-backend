package com.biblioteca.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.biblioteca.backend.model.Empleado.Empleado;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Persona.DTO.PersonaDTO;
import com.biblioteca.backend.model.Persona.Persona;
import com.biblioteca.backend.model.Rol.Rol;
import com.biblioteca.backend.model.Token.Token;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.model.Usuario.DTO.ChangePassword;
import com.biblioteca.backend.repository.core.EmpleadoRepository;
import com.biblioteca.backend.repository.security.PersonaRepository;
import com.biblioteca.backend.repository.security.UsuarioRepository;
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
    public List<Usuario> findAllUsers() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonaDTO> findAll() throws Exception {
        List<PersonaDTO> personasDTO = new ArrayList<>();
        findAllUsers().forEach(usuario -> {
            try {
                Usuario usuarioFound = findById(usuario.getId())
                        .orElseThrow(() -> new Exception("El usuario no existe"));
                Rol rolFound = roleService
                        .findById(usuarioFound.getRol().getId())
                        .orElseThrow(() -> new Exception("El rol no existe"));

                if (rolFound.getAuthority().equals("ROLE_USUARIO")) {
                    Persona personaFound = personaRepository
                            .findById(usuarioFound.getPersona().getId())
                            .orElseThrow(() -> new Exception("La persona no existe"));
                    personasDTO.add(new PersonaDTO(usuarioFound.getId(), personaFound.getNombre(),
                            personaFound.getApellidoPaterno(), personaFound.getApellidoMaterno(),
                            personaFound.getTipoDocumento(), personaFound.getNroDocumento(), personaFound.getSexo(),
                            personaFound.getDireccion(), personaFound.getCelular(), usuarioFound.getFechaRegistro(),
                            usuarioFound.getFechaActualizacion(), usuarioFound.getFechaBaja(), rolFound.getId(),
                            null, usuarioFound.getUsuario(), usuarioFound.getPassword(),
                            usuarioFound.getFotoUsuario(), usuarioFound.isActivo()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return personasDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDTO findPersonaByIdUsuario(Long id) throws Exception {
        Usuario usuarioFound = findById(id)
                .orElseThrow(() -> new Exception("El usuario no existe"));
        Rol rolFound = roleService
                .findById(usuarioFound.getRol().getId())
                .orElseThrow(() -> new Exception("El rol no existe"));

        if (!rolFound.getAuthority().equals("ROLE_USUARIO")) {
            throw new Exception("El usuario encontrado no tiene el rol correcto. Rol del usuario: "
                    .concat(rolFound.getAuthority()));
        }

        Persona personaFound = personaRepository
                .findById(usuarioFound.getPersona().getId())
                .orElseThrow(() -> new Exception("La persona no existe"));

        return new PersonaDTO(usuarioFound.getId(), personaFound.getNombre(), personaFound.getApellidoPaterno(),
                personaFound.getApellidoMaterno(), personaFound.getTipoDocumento(), personaFound.getNroDocumento(),
                personaFound.getSexo(), personaFound.getDireccion(), personaFound.getCelular(),
                usuarioFound.getFechaRegistro(), usuarioFound.getFechaActualizacion(), usuarioFound.getFechaBaja(),
                rolFound.getId(), null, usuarioFound.getUsuario(), usuarioFound.getPassword(),
                usuarioFound.getFotoUsuario(), usuarioFound.isActivo());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findByPersona(Persona persona) throws Exception {
        Persona personaFound = personaRepository
                .findById(persona.getId())
                .orElseThrow(() -> new Exception("La persona no existe"));

        return usuarioRepository.findByPersona(personaFound);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Usuario findByNroDocumentoAndUsuario(String nroDocumento, String usuario) throws Exception {
        Persona personaFound = personaRepository
                .findByNroDocumento(nroDocumento)
                .orElseThrow(() -> new Exception("La persona con ese documento no existe"));

        return findByPersona(personaFound)
                .stream()
                .filter(usuarioItem -> usuarioItem.getUsuario().equals(usuario))
                .findFirst()
                .orElseThrow(() -> new Exception("El usuario no existe"));
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

    /*@Override
    @Transactional(readOnly = true)
    public Optional<Usuario> existsAdminInLocal(Long local) {
        return usuarioRepository.existsAdminInLocal(local);
    }*/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Usuario save(PersonaDTO personaDTO) throws Exception {
        Optional<Usuario> usuarioFound = findByUsuario(personaDTO.getUsuario());

        if (usuarioFound.isPresent()) throw new Exception("El correo ya està siendo utilizado");

        Rol rolFound = roleService
                .findById(personaDTO.getIdRol())
                .orElseThrow(() -> new Exception("El rol no existe"));

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
        // TODO: AGREGAR LA RUTA DE LA FOTO POR DEFECTO
        usuarioNew.setFotoUsuario("");
        usuarioNew.setActivo(true);

        return usuarioRepository.save(usuarioNew);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Usuario activateUser(String token) throws Exception {
        Token tokenConfirma = tokenService
                .findByToken(token)
                .orElseThrow(() -> new Exception("El enlace es inválido o el token ya caducó"));
        Usuario usuarioFound = findByUsuario(tokenConfirma.getUsuario().getUsuario())
                .orElseThrow(() -> new Exception("El usuario no existe"));
        usuarioFound.setActivo(true);
        tokenService.delete(tokenConfirma.getId());

        return usuarioRepository.save(usuarioFound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Usuario update(Long id, PersonaDTO personaDTO, Usuario usuarioLogueado) throws Exception {
        Rol rolFound = roleService
                .findById(personaDTO.getIdRol())
                .orElseThrow(() -> new Exception("El rol no existe"));
        Usuario usuarioUpdate = findById(id)
                .orElseThrow(() -> new Exception("El usuario no existe"));

        if (usuarioUpdate.getId().equals(usuarioLogueado.getId())
                && usuarioLogueado.getRol().getAuthority().equals("ROLE_ADMIN")) {
            usuarioUpdate.setRol(usuarioLogueado.getRol());
            Empleado empleadoFound = empleadoRepository.findByIdUsuario(usuarioLogueado.getId());
            Local localFound = localService
                    .findById(empleadoFound.getLocal().getId())
                    .orElseThrow(() -> new Exception("El local no existe"));
            empleadoFound.setLocal(localFound);
            empleadoRepository.save(empleadoFound);
        }

        Persona personaUpdate = personaRepository
                .findById(usuarioUpdate.getPersona().getId())
                .orElseThrow(() -> new Exception(("La persona no existe")));

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
        Usuario usuarioUpdate = findById(idUsuario)
                .orElseThrow(() -> new Exception("El usuario no existe"));
        usuarioUpdate.setActivo(tipoOperacion);
        usuarioUpdate.setFechaActualizacion(LocalDateTime.now());

        if (!tipoOperacion) usuarioUpdate.setFechaBaja(LocalDateTime.now());

        Persona personaUpdate = personaRepository
                .findById(usuarioUpdate.getPersona().getId())
                .orElseThrow(() -> new Exception(("La persona no existe")));
        personaUpdate.setFechaActualizacion(LocalDateTime.now());

        if (!tipoOperacion) personaUpdate.setFechaBaja(LocalDateTime.now());

        personaRepository.save(personaUpdate);

        usuarioUpdate.setPersona(personaUpdate);
        return usuarioRepository.save(usuarioUpdate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Usuario cambiarPassword(ChangePassword dtoPassword) throws Exception {
        Usuario usuario = findById(dtoPassword.getId())
                .orElseThrow(() -> new Exception("El usuario no existe"));

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
        Usuario usuario = findById(dtoPassword.getId())
                .orElseThrow(() -> new Exception("El usuario no existe"));

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