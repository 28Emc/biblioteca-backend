package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Empresa;
import com.biblioteca.backend.model.Local.Local;
import com.biblioteca.backend.repository.LocalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalServiceImpl implements ILocalService {

    @Autowired
    private LocalRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Local> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Local> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Local> findByDireccion(String direccion) {
        return repository.findByDireccion(direccion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Local> findByEmpresa(Empresa empresa) {
        return repository.findByEmpresa(empresa);
    }

    @Override
    @Transactional
    public Local save(Local local) {
        return repository.save(local);
    }

    @Override
    @Transactional
    public boolean existsByDireccion(String direccion) {
        return repository.existsByDireccion(direccion);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Local> fetchByIdWithEmpresaAndUsuario(Long idEmpresa, Long idUsuario) {
        return repository.fetchByIdWithEmpresaAndUsuario(idEmpresa, idUsuario);
    }

}