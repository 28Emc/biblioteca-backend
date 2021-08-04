package com.biblioteca.backend.service;

import com.biblioteca.backend.model.Empleado.Empleado;
import com.biblioteca.backend.model.Persona.DTO.PersonaDTO;
import com.biblioteca.backend.model.Usuario.Usuario;

import java.util.List;
import java.util.Optional;

public interface IEmpleadoService {

    List<PersonaDTO> findAll();

    List<PersonaDTO> findActivos();

    PersonaDTO findPersonaById(Long id) throws Exception;

    Optional<Empleado> findById(Long idEmpleado) throws Exception;

    Empleado findByIdUsuario(Long idUsuario) throws Exception;

    PersonaDTO findByIdLocalAndIdUsuario(Long idLocal, Long idUsuario) throws Exception;

    void save(Empleado empleado);

    void saveAdmin(PersonaDTO personaDTO) throws Exception;

    void update(Long id, PersonaDTO personaDTO, Usuario usuarioLogueado) throws Exception;

    void changeEmpleadoState(Long id, boolean tipoOperacion) throws Exception;

    void delete(Empleado empleado);

}
