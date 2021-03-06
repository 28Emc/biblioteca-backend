package com.biblioteca.backend.service;

import java.util.List;
import java.util.Optional;
import com.biblioteca.backend.model.Empresa;
import com.biblioteca.backend.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaServiceImpl implements IEmpresaService {

    @Autowired
    private EmpresaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Empresa> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> findByRucAndIsActivo(String ruc, boolean isActivo) {
        return repository.findByRucAndIsActivo(ruc, isActivo);
    }

    /*
     * @Override
     * @Transactional(readOnly = true) public Optional<Empresa>
     * fetchByIdWithLocales(Long id){ return repository.fetchByIdWithLocales(id); }
     */

}