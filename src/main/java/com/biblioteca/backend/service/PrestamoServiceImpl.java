package com.biblioteca.backend.service;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.biblioteca.backend.model.Empleado.Empleado;
import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Prestamo.DTO.PrestamoDTO;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.repository.core.PrestamoRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrestamoServiceImpl implements IPrestamoService {

    @Autowired
    private PrestamoRespository prestamoRespository;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IEmpleadoService empleadoService;

    @Autowired
    private ILibroService libroService;

    @Override
    @Transactional(readOnly = true)
    public List<Prestamo> findAllByAdmin(Usuario usuarioLogueado) {
        List<Prestamo> prestamos = new ArrayList<>();
        switch (usuarioLogueado.getRol().getAuthority()) {
            case "ROLE_ADMIN":
                prestamos = prestamoRespository.findAll();
                break;
            case "ROLE_EMPLEADO":
                prestamos = prestamoRespository
                        .findAll()
                        .stream()
                        .filter(prestamo ->
                                prestamo.getEmpleado().getId().equals(usuarioLogueado.getId()))
                        .collect(Collectors.toList());
                break;
            case "ROLE_USUARIO":
                prestamos = prestamoRespository
                        .findAll()
                        .stream()
                        .filter(prestamo ->
                                prestamo.getIdUsuario().equals(usuarioLogueado.getId()))
                        .collect(Collectors.toList());
                break;
        }
        return prestamos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prestamo> findAll() {
        return prestamoRespository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Prestamo> findById(Long id) {
        return prestamoRespository.findById(id);
    }

	/*@Override
	@Transactional(readOnly = true)
	public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(Long idEmpleado) {
		return repository.fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(idEmpleado);
	}*/

    @Override
    public String mostrarFechaAmigable(LocalDateTime fecha) {
        // ARMANDO FECHA MAS AMIGABLE AL USUARIO CON TIME
        Locale esp = new Locale("es", "PE");
        // Obtienes el dia, mes y año actuales
        //LocalDate fechaFinal = Instant.ofEpochMilli(fecha.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        String diaNum = String.valueOf(fecha.getDayOfMonth());
        // Mejorando cadena de dia
        String dia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, esp);
        String diaMayus = dia.substring(0, 1).toUpperCase();
        String demasLetrasDia = dia.substring(1);
        String diaFinal = diaMayus + demasLetrasDia;
        // Mejorando cadena de mes
        String mes = fecha.getMonth().getDisplayName(TextStyle.FULL, esp);
        String mesMayus = mes.substring(0, 1).toUpperCase();
        String demasLetrasMes = mes.substring(1);
        String mesFinal = mesMayus + demasLetrasMes;
        String anio = String.valueOf(fecha.getYear());
        return diaFinal + ", " + diaNum + " de " + mesFinal + " " + anio;
    }

	/*@Override
	@Transactional(readOnly = true)
	public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUser(Long id) {
		return repository.fetchByIdWithLibroWithUsuarioWithEmpleadoPerUser(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleado(Long idLocal) {
		return repository.fetchByIdWithLibroWithUsuarioWithEmpleado(idLocal);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserPendientes(Long id) {
		return repository.fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserPendientes(id);
	}*/

    @Override
    @Transactional
    public Prestamo saveFromUser(PrestamoDTO prestamoDTO, Usuario usuarioLogueado) throws Exception {
        Prestamo prestamo = findById(prestamoDTO.getId())
                .orElseThrow(() -> new Exception("El préstamo no existe"));
        Usuario usuarioFound = new Usuario();
        Usuario usuarioEmpleado;
        Empleado empleadoFound = new Empleado();

        Libro libroFound = libroService
                .findById(prestamoDTO.getIdLibro())
                .orElseThrow(() -> new Exception("El libro no existe"));

        if (libroFound.isActivo()) {
            if (libroFound.getStock() > 0) {
                libroFound.setStock(libroFound.getStock() - 1);
                libroService.save(libroFound);
            } else {
                throw new Exception("El libro seleccionado no tiene stock suficiente");
            }
        }

        switch (usuarioLogueado.getRol().getAuthority()) {
            case "ROLE_ADMIN":
            case "ROLE_EMPLEADO":
                usuarioFound = usuarioService
                        .findById(prestamoDTO.getIdUsuario())
                        .orElseThrow(() -> new Exception("El usuario no existe"));
                usuarioEmpleado = usuarioService
                        .findById(prestamoDTO.getIdEmpleado())
                        .orElseThrow(() -> new Exception("El usuario no existe"));
                empleadoFound = empleadoService
                        .findByIdUsuario(usuarioEmpleado.getId());

                if (empleadoFound == null || !usuarioEmpleado.isActivo()) {
                    throw new Exception("El empleado no existe o no se encuentra activo");
                }

                if (usuarioEmpleado.getRol().getAuthority().equals("ROLE_EMPLEADO") &&
                        !libroFound.getLocal().getId().equals(empleadoFound.getLocal().getId())) {
                    throw new Exception("El empleado no tiene acceso a este recurso");
                }

                break;
            case "ROLE_USUARIO":
                usuarioFound = usuarioService
                        .findById(prestamoDTO.getIdUsuario())
                        .orElseThrow(() -> new Exception("El usuario no existe"));

                if (!usuarioFound.isActivo()) {
                    throw new Exception("El usuario no se encuentra activo");
                }

                prestamo.setEmpleado(null);
                break;
        }

        prestamo.setIdUsuario(usuarioFound.getId());
        prestamo.setEmpleado(empleadoFound);
        prestamo.setLibro(libroFound);
        prestamo.setObservaciones(prestamoDTO.getObservaciones());
        prestamo.setFechaDevolucion(prestamoDTO.getFechaDevolucion());
        return save(prestamo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Prestamo save(Prestamo prestamo) {
        return prestamoRespository.save(prestamo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Prestamo update(Long id, PrestamoDTO prestamoDTO, String estado) throws Exception {
        Prestamo prestamoFound = findById(id)
                .orElseThrow(() -> new Exception("El préstamo no existe"));
        prestamoFound.setObservaciones(prestamoDTO.getObservaciones());

        if (estado.equals("E2")) {
            prestamoFound.setFechaPrestamo(LocalDateTime.now());
            prestamoFound.setFechaDevolucion(prestamoDTO.getFechaDevolucion());
        }

        if (estado.equals("E3") || estado.equals("E4")) {

            if (estado.equals("E4") &&
                    (prestamoFound.getEstado().equals("E3") || prestamoFound.getEstado().equals("E4"))) {
                throw new Exception("El estado actual del préstamo no permite dar de baja. Estado actual: " +
                        formatEstadoPrestamo(prestamoFound.getEstado()));
            }

            prestamoFound.setFechaDevolucion(prestamoDTO.getFechaDevolucion());
            prestamoFound.setFechaBaja(null);

            Libro libroFound = libroService
                    .findById(prestamoDTO.getIdLibro())
                    .orElseThrow(() -> new Exception("El libro no existe"));

            if (estado.equals("E4")) {
                libroFound.setStock(libroFound.getStock() + 1);
                prestamoFound.setFechaBaja(LocalDateTime.now());
            }

            libroService.save(libroFound);
        }

        prestamoFound.setEstado(estado);
        return save(prestamoFound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        prestamoRespository.deleteById(id);
    }

    public String formatEstadoPrestamo(String estado) {
        String estadoFormat = "";

        switch (estado) {
            case "E1":
                estadoFormat = "PENDIENTE DE VALIDACIÓN";
                break;
            case "E2":
                estadoFormat = "CONFIRMADO";
                break;
            case "E3":
                estadoFormat = "DEVUELTO";
                break;
            case "E4":
                estadoFormat = "ANULADO";
                break;
        }

        return estadoFormat;
    }

	/*@Override
	@Transactional(readOnly = true)
	public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserAll(Long id) {
		return repository.fetchByIdWithLibroWithUsuarioWithEmpleadoPerUserAll(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibro(Long idLibro) {
		return repository.fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibro(idLibro);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Prestamo> fetchWithLibroWithUsuarioWithEmpleado() {
		return repository.fetchWithLibroWithUsuarioWithEmpleado();
	}*/

    /*
     * @Override
     *
     * @Transactional(readOnly = true) public List<Prestamo>
     * fetchWithLibroWithUsuarioWithEmpleado(Long idLibro) { return
     * repository.fetchWithLibroWithUsuarioWithEmpleado(idLibro); }
     */

	/*@Override
	@Transactional(readOnly = true)
	public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibroAndLocal(Long idLibro, Long idLocal) {
		return repository.fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibroAndLocal(idLibro, idLocal);
	}*/

}