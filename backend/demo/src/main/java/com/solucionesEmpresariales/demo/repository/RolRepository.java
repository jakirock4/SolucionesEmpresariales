package com.solucionesEmpresariales.demo.repository;

import com.solucionesEmpresariales.demo.entity.Rol;
import com.solucionesEmpresariales.demo.enums.RolNombre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByRolNombre(RolNombre rolNombre);
}
