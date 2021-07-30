package com.biblioteca.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.biblioteca.backend.model.Categoria.Categoria;
import com.biblioteca.backend.model.Categoria.DTO.CategoriaDTO;
import com.biblioteca.backend.model.Libro.Libro;
import com.biblioteca.backend.repository.CategoriaRepository;
import com.biblioteca.backend.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaServiceImpl implements ICategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findById(Long id) throws Exception {
        if (!id.toString().matches("^\\d+$")) throw new Exception("El id es inválido");

        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findByNombre(String categoria) {
        return categoriaRepository.findByNombre(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findByNombreLikeIgnoreCase(String categoria) {
        return categoriaRepository.findByNombreLikeIgnoreCase(categoria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(CategoriaDTO categoriaDTO) throws Exception {
        Optional<Categoria> categoriaEncontrada = categoriaRepository.findByNombre(categoriaDTO.getNombre());

        if (categoriaEncontrada.isPresent()) throw new Exception("La categoría ya existe");

        Categoria categoriaNew = new Categoria(categoriaDTO.getNombre(), categoriaDTO.getDescripcion());
        categoriaRepository.save(categoriaNew);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, CategoriaDTO categoriaDTO) throws Exception {
        Categoria categoriaFound = findById(id).orElseThrow(() -> new Exception("La categoría no existe"));
        categoriaFound.setNombre(categoriaDTO.getNombre());
        categoriaFound.setDescripcion(categoriaDTO.getDescripcion());
        categoriaRepository.save(categoriaFound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeCategoriaState(Long id, boolean tipoOperacion) throws Exception {

        if (!id.toString().matches("^\\d+$")) throw new Exception("El id es inválido");

        Categoria categoriaFound = findById(id).orElseThrow(() -> new Exception("La categoría no existe"));

        if (!categoriaFound.isActivo()) throw new Exception("La categoría ya está deshabilitada");

        List<Libro> libros = libroRepository.findByCategoria(categoriaFound.getNombre());
        List<Libro> librosFiltered = libros
                .stream()
                .filter(libroFilter -> libroFilter.getFechaBaja() != null)
                .collect(Collectors.toList());

        if (librosFiltered.size() > 0) {
            librosFiltered.forEach(libro -> {
                if (libro.isActivo()) {
                    libro.setActivo(tipoOperacion);
                    libroRepository.save(libro);
                }
            });
        }

        categoriaFound.setActivo(false);
        categoriaRepository.save(categoriaFound);
    }

}