package com.solucionesEmpresariales.demo.service;

import com.solucionesEmpresariales.demo.entity.Rol;
import com.solucionesEmpresariales.demo.enums.RolNombre;
import com.solucionesEmpresariales.demo.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class RolService {

    @Autowired
    RolRepository rolRepository;

    public Optional<Rol> getByRolNombre(RolNombre rolNombre){
        return rolRepository.findByRolNombre(rolNombre);
    }
}