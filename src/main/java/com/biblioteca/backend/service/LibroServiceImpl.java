package com.biblioteca.backend.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.biblioteca.backend.model.Empleado.Empleado;
import com.biblioteca.backend.model.Libro.DTO.LibroDTO;
import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Usuario.Usuario;
import com.biblioteca.backend.repository.core.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroServiceImpl implements ILibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private ILocalService localService;

    @Autowired
    private IEmpleadoService empleadoService;

    @Autowired
    private ICategoriaService categoriaService;

    @Override
    @Transactional(readOnly = true)
    public List<Libro> findAll() {
        return libroRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> findAllDistinct() {
        return findAll()
                .stream()
                .filter(distinctByKey(Libro::getTitulo))
                .collect(Collectors.toList());
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Libro> findById(Long id) {
        return libroRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByTituloLikeIgnoreCaseAndLocalAndIsActivo(String term, Long id, Boolean isActivo) {
        return libroRepository
                .findAll()
                .stream()
                .filter(libro -> libro.getTitulo().equalsIgnoreCase(term) &&
                        libro.getLocal().getId().equals(id) &&
                        libro.isActivo())
                .collect(Collectors.toList());
    }

    /*@Override
    @Transactional(readOnly = true)
    public List<Libro> fetchByIdWithLocalesAndEmpleado(Long idLocal, Long idEmpleado) {
        return libroRepository.fetchByIdWithLocalesAndEmpleado(idLocal, idEmpleado);
    }*/

    @Override
    @Transactional(readOnly = true)
    public List<Libro> fetchByIdWithLocales(Long idLocal) {
        //return libroRepository.fetchByIdWithLocales(idLocal);
        return libroRepository
                .findAll()
                .stream()
                .filter(libro -> libro.getLocal().getId().equals(idLocal))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByTituloGroup() {
        return libroRepository.findByTituloGroup();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByTituloLikeIgnoreCase(String titulo) {
        //return libroRepository.findByTituloLikeIgnoreCase(titulo);
        return libroRepository
                .findAll()
                .stream()
                .filter(libro -> libro.getTitulo().equalsIgnoreCase(titulo))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Libro> findByTituloAndLocalAndIsActivo(String term, Long id, Boolean isActivo) {
        //return libroRepository.findByTituloAndLocalAndIsActivo(term, id, isActivo);
        return libroRepository
                .findAll()
                .stream()
                .filter(libro -> libro.getTitulo().equals(term) &&
                        libro.getLocal().getId().equals(id) &&
                        libro.isActivo())
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByCategoriaAndLocal(String categoria, Long idLocal) {
        //return libroRepository.findByCategoriaAndLocal("%" + categoria + "%", localId);
        return libroRepository
                .findAll()
                .stream()
                .filter(libro -> libro.getCategoria().getNombre().equalsIgnoreCase(categoria) &&
                        libro.getLocal().getId().equals(idLocal) &&
                        libro.isActivo())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByIsActivo(boolean isActivo) {
        //return libroRepository.findByIsActivo(isActivo);
        return libroRepository
                .findAll()
                .stream()
                .filter(Libro::isActivo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByLocalAndIsActivo(Long idLocal, boolean isActivo) {
        //return libroRepository.findByLocalAndIsActivo(idLocal, isActivo);
        return libroRepository
                .findAll()
                .stream()
                .filter(libro -> libro.getLocal().getId().equals(idLocal) && libro.isActivo())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> fetchWithCategoriaWithLocal() {
        return libroRepository.fetchWithCategoriaWithLocal();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByLocal(Long idLocal) {
        //return libroRepository.findByLocal(idLocal);
        return libroRepository
                .findAll()
                .stream()
                .filter(libro -> libro.getLocal().getId().equals(idLocal))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByCategoria(String categoria) {
        //return libroRepository.findByCategoria(categoria);
        return libroRepository
                .findAll()
                .stream()
                .filter(libro -> libro.getCategoria().getNombre().equals(categoria))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Libro libro) {
        libroRepository.save(libro);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Libro> findByTituloAndLocal(String term, Long id) {
        //return libroRepository.findByTituloAndLocal(term, id);
        return libroRepository
                .findAll()
                .stream()
                .filter(libro -> libro.getTitulo().equals(term) &&
                        libro.getLocal().getId().equals(id))
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Libro> findByTituloDistinct(String term) {
        return libroRepository.findByTituloDistinct("%" + term + "%");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAdmin(LibroDTO libroDTO, Usuario empleadoLogueado) throws Exception {
        Libro libro = new Libro();
        Empleado empleadoFound = empleadoService.findByIdUsuario(empleadoLogueado.getId());
        localService
                .findById(libroDTO.getIdLocal())
                .orElseThrow(() -> new Exception("El local no existe"));

        if (empleadoLogueado.getRol().getAuthority().contains("EMPLEADO") &&
                !libroDTO.getIdLocal().equals(empleadoFound.getLocal().getId())) {
            throw new Exception("El empleado solo puede registrar libros de su propio local");
        }

        if (findByTituloLikeIgnoreCase(libroDTO.getTitulo()).stream().findFirst().isPresent()) {
            throw new Exception("El libro ya existe");
        }

        if (!libro.validateIsbn13(libroDTO.getISBN())) {
            throw new Exception("El código ISBN es invalido");
        }

        libro.setCategoria(categoriaService.findById(libroDTO.getIdCategoria()).orElseThrow(
                () -> new Exception("La categoría no existe")));
        libro.setLocal(localService.findById(libroDTO.getIdLocal()).orElseThrow(
                () -> new Exception("El local no existe")));

        libro.setISBN(libroDTO.getISBN());
        libro.setTitulo(libroDTO.getTitulo());
        libro.setAutor(libroDTO.getAutor());
        libro.setDescripcion(libroDTO.getDescripcion());
        libro.setStock(libroDTO.getStock());
        libro.setFotoLibro(libroDTO.getFotoLibro());
        libro.setFechaPublicacion(libroDTO.getFechaPublicacion());

        libroRepository.save(libro);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, LibroDTO libroDTO, Usuario empleadoLogueado) throws Exception {
        Libro libroFound = findById(id)
                .orElseThrow(() -> new Exception("El libro no existe"));
        Empleado empleadoFound = empleadoService.findByIdUsuario(empleadoLogueado.getId());
        localService
                .findById(libroDTO.getIdLocal())
                .orElseThrow(() -> new Exception("El local no existe"));

        if (empleadoLogueado.getRol().getAuthority().contains("EMPLEADO") &&
                !libroDTO.getIdLocal().equals(empleadoFound.getLocal().getId())) {
            throw new Exception("El empleado solo puede modificar libros de su propio local");
        }

        if (!libroFound.validateIsbn13(libroDTO.getISBN())) {
            throw new Exception("El código ISBN es invalido");
        }

        libroFound.setCategoria(categoriaService.findById(libroDTO.getIdCategoria()).orElseThrow(
                () -> new Exception("La categoría no existe")));
        libroFound.setLocal(localService.findById(libroDTO.getIdLocal()).orElseThrow(
                () -> new Exception("El local no existe")));

        libroFound.setISBN(libroDTO.getISBN());
        libroFound.setTitulo(libroDTO.getTitulo());
        libroFound.setAutor(libroDTO.getAutor());
        libroFound.setDescripcion(libroDTO.getDescripcion());
        libroFound.setStock(libroDTO.getStock());
        libroFound.setFotoLibro(libroDTO.getFotoLibro());
        libroFound.setFechaPublicacion(libroDTO.getFechaPublicacion());

        libroRepository.save(libroFound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeLibroState(Long id, boolean tipoOperacion, Usuario usuarioLogueado) throws Exception {
        Libro libroFound = findById(id)
                .orElseThrow(() -> new Exception("El libro no existe"));
        Empleado empleadoFound = empleadoService.findByIdUsuario(usuarioLogueado.getId());

        if (usuarioLogueado.getRol().getAuthority().contains("EMPLEADO") &&
                !libroFound.getLocal().getId().equals(empleadoFound.getLocal().getId())) {
            throw new Exception("El empleado solo puede modificar libros de su propio local");
        }

        libroFound.setActivo(tipoOperacion);
        libroRepository.save(libroFound);
    }
}