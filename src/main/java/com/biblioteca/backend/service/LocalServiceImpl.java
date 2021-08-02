package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.biblioteca.backend.model.Categoria.Categoria;
import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.model.Local.DTO.LocalDTO;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.repository.core.LocalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalServiceImpl implements ILocalService {

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private IEmpresaService empresaService;

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private ILibroService libroService;

    @Override
    @Transactional(readOnly = true)
    public List<Local> findAll() {
        return localRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Local> findById(Long id) {
        return localRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Local> findByDireccion(String direccion) {
        return localRepository.findByDireccion(direccion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Local> findByIdEmpresa(Long idEmpresa) {
        return localRepository.findByIdEmpresa(idEmpresa);
    }

    @Override
    @Transactional
    public boolean existsByDireccion(String direccion) {
        return localRepository.existsByDireccion(direccion);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Local save(LocalDTO localDTO) throws Exception {
        if (findByDireccion(localDTO.getDireccion().trim()).isPresent()) {
            throw new Exception("La categoría ya existe");
        }

        empresaService
                .findById(localDTO.getIdEmpresa())
                .orElseThrow(() -> new Exception("La empresa no existe"));
        Local local = new Local(localDTO.getIdEmpresa(), localDTO.getDireccion().trim());
        return localRepository.save(local);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, LocalDTO localDTO) throws Exception {
        Local localFound = findById(id)
                .orElseThrow(() -> new Exception("El local no existe"));

        if (findByDireccion(localDTO.getDireccion().trim()).isPresent()) {
            throw new Exception("El local con esa dirección ya existe");
        }

        empresaService
                .findById(localDTO.getIdEmpresa())
                .orElseThrow(() -> new Exception("La empresa no existe"));
        localFound.setDireccion(localDTO.getDireccion().trim());
        localFound.setIdEmpresa(localDTO.getIdEmpresa());
        localRepository.save(localFound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeLocalState(Long id, boolean tipoOperacion) throws Exception {
        Local localFound = findById(id)
                .orElseThrow(() -> new Exception("El local no existe"));
        List<Libro> libros = libroService.findByLocal(localFound.getId());
        List<Libro> librosFiltered = libros
                .stream()
                .filter(libroFilter -> libroFilter.getFechaBaja() != null)
                .collect(Collectors.toList());

        if (librosFiltered.size() > 0) {
            librosFiltered.forEach(libro -> {
                if (libro.isActivo()) {
                    libro.setActivo(tipoOperacion);
                    libroService.save(libro);
                }
            });
        }

        localFound.setActivo(tipoOperacion);
        localRepository.save(localFound);
    }

    /*@Override
    @Transactional(readOnly = true)
    public Optional<Local> fetchByIdWithEmpresaAndUsuario(Long idEmpresa, Long idUsuario) {
        return localRepository.fetchByIdWithEmpresaAndUsuario(idEmpresa, idUsuario);
    }*/

}