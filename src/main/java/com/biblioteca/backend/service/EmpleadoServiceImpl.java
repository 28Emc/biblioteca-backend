package com.biblioteca.backend.service;

import com.biblioteca.backend.model.Empleado;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Persona.DTO.PersonaDTO;
import com.biblioteca.backend.model.Persona.Persona;
import com.biblioteca.backend.model.Rol;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.repository.EmpleadoRepository;
import com.biblioteca.backend.repository.LocalRepository;
import com.biblioteca.backend.repository.PersonaRepository;
import com.biblioteca.backend.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpleadoServiceImpl implements IEmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PersonaDTO> findAll() {
        List<PersonaDTO> personasDTO = new ArrayList<>();
        empleadoRepository.findAll().forEach(empleado -> {
            Usuario usuarioFound = null;
            try {
                usuarioFound = usuarioService.findById(empleado.getIdUsuario()).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Rol rolFound = rolRepository.findById(usuarioFound.getRol().getId()).get();
            Persona personaFound = personaRepository.findById(usuarioFound.getPersona().getId()).get();
            Local localFound = localRepository.findById(empleado.getLocal().getId()).get();
            personasDTO.add(new PersonaDTO(personaFound.getNombre(), personaFound.getApellidoPaterno(),
                    personaFound.getApellidoMaterno(), personaFound.getTipoDocumento(), personaFound.getNroDocumento(),
                    personaFound.getSexo(), personaFound.getDireccion(), personaFound.getCelular(),
                    usuarioFound.getFechaRegistro(), usuarioFound.getFechaActualizacion(), usuarioFound.getFechaBaja(),
                    rolFound.getId(), localFound.getId(), usuarioFound.getUsuario(),
                    usuarioFound.getPassword(), usuarioFound.getFotoUsuario(), usuarioFound.isActivo()));
        });
        return personasDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonaDTO> findActivos() {
        return findAll()
                .stream()
                .filter(PersonaDTO::isActivo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDTO findById(Long id) throws Exception {
        if (!id.toString().matches("^\\d+$")) throw new Exception("El id es inválido");

        Empleado empleadoFound = empleadoRepository.findById(id).orElseThrow(() ->
                new Exception("El empleado no existe"));
        Usuario usuarioFound = usuarioService.findById(empleadoFound.getIdUsuario()).get();
        Rol rolFound = rolRepository.findById(usuarioFound.getRol().getId()).get();
        Persona personaFound = personaRepository.findById(usuarioFound.getPersona().getId()).get();
        Local localFound = localRepository.findById(empleadoFound.getLocal().getId()).get();
        return new PersonaDTO(personaFound.getNombre(), personaFound.getApellidoPaterno(),
                personaFound.getApellidoMaterno(), personaFound.getTipoDocumento(), personaFound.getNroDocumento(),
                personaFound.getSexo(), personaFound.getDireccion(), personaFound.getCelular(),
                usuarioFound.getFechaRegistro(), usuarioFound.getFechaActualizacion(), usuarioFound.getFechaBaja(),
                rolFound.getId(), localFound.getId(), usuarioFound.getUsuario(),
                usuarioFound.getPassword(), usuarioFound.getFotoUsuario(), usuarioFound.isActivo());
    }

    @Override
    @Transactional(readOnly = true)
    public Empleado findByIdUsuario(Long idUsuario) throws Exception {
        if (!idUsuario.toString().matches("^\\d+$")) throw new Exception("El id es inválido");

        usuarioService.findById(idUsuario).orElseThrow(() ->
                new Exception("El usuario no existe"));

        return empleadoRepository.findByIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDTO findByIdLocalAndIdUsuario(Long idLocal, Long idUsuario) throws Exception {
        if (!idLocal.toString().matches("^\\d+$") || !idUsuario.toString().matches("^\\d+$"))
            throw new Exception("El id es inválido");

        Usuario usuarioFound = usuarioService.findById(idUsuario).get();
        Rol rolFound = rolRepository.findById(usuarioFound.getRol().getId()).get();
        Persona personaFound = personaRepository.findById(usuarioFound.getPersona().getId()).get();
        Local localFound = localRepository.findById(idLocal).get();
        return new PersonaDTO(personaFound.getNombre(), personaFound.getApellidoPaterno(),
                personaFound.getApellidoMaterno(), personaFound.getTipoDocumento(),
                personaFound.getNroDocumento(), personaFound.getSexo(), personaFound.getDireccion(),
                personaFound.getCelular(), usuarioFound.getFechaRegistro(), usuarioFound.getFechaActualizacion(),
                usuarioFound.getFechaBaja(), rolFound.getId(), localFound.getId(), usuarioFound.getUsuario(),
                usuarioFound.getPassword(), usuarioFound.getFotoUsuario(), usuarioFound.isActivo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(PersonaDTO personaDTO) throws Exception {
        Local localFound = localRepository.findById(personaDTO.getIdLocal()).orElseThrow(() ->
                new Exception("El local no existe"));

        Usuario usuarioNew = usuarioService.save(personaDTO);
        Empleado empleadoNew = new Empleado(usuarioNew.getId(), localFound);
        empleadoRepository.save(empleadoNew);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, PersonaDTO personaDTO, Usuario usuarioLogueado) throws Exception {
        if (!id.toString().matches("^\\d+$")) throw new Exception("El id es inválido");

        Empleado empleadoFound = empleadoRepository.findById(id).orElseThrow(() ->
                new Exception("El empleado no existe"));
        Local localFound = localRepository.findById(personaDTO.getIdLocal()).orElseThrow(() ->
                new Exception("El local no existe"));

        Usuario usuarioUpdate = usuarioService.update(empleadoFound.getIdUsuario(), personaDTO, usuarioLogueado);
        empleadoFound.setIdUsuario(usuarioUpdate.getId());
        empleadoFound.setLocal(localFound);
        empleadoRepository.save(empleadoFound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeEmpleadoState(Long id, boolean tipoOperacion) throws Exception {
        if (!id.toString().matches("^\\d+$")) throw new Exception("El id es inválido");

        Empleado empleadoFound = empleadoRepository.findById(id).orElseThrow(() ->
                new Exception("El empleado no existe"));

        usuarioService.changeUsuarioState(empleadoFound.getIdUsuario(), tipoOperacion);
        empleadoFound.setFechaActualizacion(LocalDateTime.now());

        if (!tipoOperacion) empleadoFound.setFechaBaja(LocalDateTime.now());

        empleadoRepository.save(empleadoFound);
    }
}
