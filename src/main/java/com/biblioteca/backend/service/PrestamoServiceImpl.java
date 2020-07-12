package com.biblioteca.backend.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import com.biblioteca.backend.model.Prestamo.Prestamo;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.repository.PrestamoRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrestamoServiceImpl implements IPrestamoService {

	@Autowired
	private PrestamoRespository repository;

	@Override
	@Transactional
	public Prestamo save(Prestamo prestamo) {
		return repository.save(prestamo);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Prestamo> findById(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(Long idEmpleado) {
		return repository.fetchByIdWithLibroWithUsuarioWithEmpleadoPerEmpleado(idEmpleado);
	}

	@Override
	public String mostrarFechaAmigable(Date fecha) {
		// ARMANDO FECHA MAS AMIGABLE AL USUARIO CON TIME
		Locale esp = new Locale("es", "PE");
		// Obtienes el dia, mes y año actuales
		LocalDate fechaFinal = Instant.ofEpochMilli(fecha.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		String diaNum = String.valueOf(fechaFinal.getDayOfMonth());
		// Mejorando cadena de dia
		String dia = fechaFinal.getDayOfWeek().getDisplayName(TextStyle.FULL, esp);
		String diaMayus = dia.substring(0, 1).toUpperCase();
		String demasLetrasDia = dia.substring(1);
		String diaFinal = diaMayus + demasLetrasDia;
		// Mejorando cadena de mes
		String mes = fechaFinal.getMonth().getDisplayName(TextStyle.FULL, esp);
		String mesMayus = mes.substring(0, 1).toUpperCase();
		String demasLetrasMes = mes.substring(1);
		String mesFinal = mesMayus + demasLetrasMes;
		String anio = String.valueOf(fechaFinal.getYear());
		return diaFinal + ", " + diaNum + " de " + mesFinal + " " + anio;
	}

	@Override
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
	}

	@Override
	@Transactional
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
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
	}

	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public List<Prestamo>
	 * fetchWithLibroWithUsuarioWithEmpleado(Long idLibro) { return
	 * repository.fetchWithLibroWithUsuarioWithEmpleado(idLibro); }
	 */

	@Override
	@Transactional(readOnly = true)
	public List<Prestamo> fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibroAndLocal(Long idLibro, Long idLocal) {
		return repository.fetchByIdWithLibroWithUsuarioWithEmpleadoPerLibroAndLocal(idLibro, idLocal);
	}

}