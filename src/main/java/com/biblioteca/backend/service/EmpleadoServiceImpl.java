package com.biblioteca.backend.service;

import com.biblioteca.backend.model.Empleado.Empleado;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.model.Persona.DTO.PersonaDTO;
import com.biblioteca.backend.model.Persona.Persona;
import com.biblioteca.backend.model.Rol.Rol;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.repository.core.EmpleadoRepository;
import com.biblioteca.backend.repository.core.LocalRepository;
import com.biblioteca.backend.repository.security.PersonaRepository;
import com.biblioteca.backend.repository.security.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            personasDTO.add(new PersonaDTO(empleado.getId(), personaFound.getNombre(), personaFound.getApellidoPaterno(),
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
    public PersonaDTO findPersonaById(Long id) throws Exception {
        Empleado empleadoFound = empleadoRepository
                .findById(id)
                .orElseThrow(() -> new Exception("El empleado no existe"));
        Usuario usuarioFound = usuarioService
                .findById(empleadoFound.getIdUsuario())
                .orElseThrow(() -> new Exception("El usuario no existe"));
        Rol rolFound = rolRepository
                .findById(usuarioFound.getRol().getId())
                .orElseThrow(() -> new Exception("El rol no existe"));
        Persona personaFound = personaRepository
                .findById(usuarioFound.getPersona().getId())
                .orElseThrow(() -> new Exception("La persona no existe"));
        Local localFound = localRepository
                .findById(empleadoFound.getLocal().getId())
                .orElseThrow(() -> new Exception("El local no existe"));

        return new PersonaDTO(empleadoFound.getId(), personaFound.getNombre(), personaFound.getApellidoPaterno(),
                personaFound.getApellidoMaterno(), personaFound.getTipoDocumento(), personaFound.getNroDocumento(),
                personaFound.getSexo(), personaFound.getDireccion(), personaFound.getCelular(),
                usuarioFound.getFechaRegistro(), usuarioFound.getFechaActualizacion(), usuarioFound.getFechaBaja(),
                rolFound.getId(), localFound.getId(), usuarioFound.getUsuario(),
                usuarioFound.getPassword(), usuarioFound.getFotoUsuario(), usuarioFound.isActivo());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> findById(Long idEmpleado) throws Exception {
        return empleadoRepository.findById(idEmpleado);
    }

    @Override
    @Transactional(readOnly = true)
    public Empleado findByIdUsuario(Long idUsuario) throws Exception {
        usuarioService
                .findById(idUsuario)
                .orElseThrow(() -> new Exception("El usuario no existe"));

        return empleadoRepository.findByIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDTO findByIdLocalAndIdUsuario(Long idLocal, Long idUsuario) throws Exception {
        Empleado empleadoFound = findByIdUsuario(idUsuario);

        Usuario usuarioFound = usuarioService
                .findById(idUsuario)
                .orElseThrow(() -> new Exception("El usuario no existe"));
        Rol rolFound = rolRepository
                .findById(usuarioFound.getRol().getId())
                .orElseThrow(() -> new Exception("El rol no existe"));
        Persona personaFound = personaRepository
                .findById(usuarioFound.getPersona().getId())
                .orElseThrow(() -> new Exception("La persona no existe"));

        if (!empleadoFound.getLocal().getId().equals(idLocal)) {
            throw new Exception("El empleado no pertenece a dicho local");
        }

        Local localFound = localRepository
                .findById(idLocal)
                .orElseThrow(() -> new Exception("El local no existe"));

        return new PersonaDTO(empleadoFound.getId(), personaFound.getNombre(), personaFound.getApellidoPaterno(),
                personaFound.getApellidoMaterno(), personaFound.getTipoDocumento(),
                personaFound.getNroDocumento(), personaFound.getSexo(), personaFound.getDireccion(),
                personaFound.getCelular(), usuarioFound.getFechaRegistro(), usuarioFound.getFechaActualizacion(),
                usuarioFound.getFechaBaja(), rolFound.getId(), localFound.getId(), usuarioFound.getUsuario(),
                usuarioFound.getPassword(), usuarioFound.getFotoUsuario(), usuarioFound.isActivo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Empleado empleado) {
        empleadoRepository.save(empleado);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAdmin(PersonaDTO personaDTO) throws Exception {
        Local localFound = localRepository
                .findById(personaDTO.getIdLocal())
                .orElseThrow(() -> new Exception("El local no existe"));
        Usuario usuarioNew = usuarioService.saveAdmin(personaDTO);
        Empleado empleadoNew = new Empleado(usuarioNew.getId(), localFound);
        empleadoRepository.save(empleadoNew);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, PersonaDTO personaDTO, Usuario usuarioLogueado) throws Exception {
        Empleado empleadoFound = empleadoRepository
                .findById(id)
                .orElseThrow(() -> new Exception("El empleado no existe"));
        Local localFound = localRepository
                .findById(personaDTO.getIdLocal())
                .orElseThrow(() -> new Exception("El local no existe"));
        Usuario usuarioUpdate = usuarioService.update(empleadoFound.getIdUsuario(), personaDTO, usuarioLogueado);
        empleadoFound.setIdUsuario(usuarioUpdate.getId());
        empleadoFound.setLocal(localFound);
        empleadoRepository.save(empleadoFound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeEmpleadoState(Long id, boolean tipoOperacion) throws Exception {
        Empleado empleadoFound = empleadoRepository
                .findById(id)
                .orElseThrow(() -> new Exception("El empleado no existe"));
        usuarioService.changeUsuarioState(empleadoFound.getIdUsuario(), tipoOperacion);
        empleadoFound.setFechaActualizacion(LocalDateTime.now());

        if (!tipoOperacion) empleadoFound.setFechaBaja(LocalDateTime.now());

        empleadoRepository.save(empleadoFound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Empleado empleado) {
        empleadoRepository.delete(empleado);
    }
}
