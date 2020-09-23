package com.solucionesEmpresariales.demo.service;

import com.solucionesEmpresariales.demo.entity.Usuario;
import com.solucionesEmpresariales.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos(){
        List<Usuario> lista = usuarioRepository.findAll();
        return lista;
    }

    public Optional<Usuario> getByNombreUsuario(String nu){
        return usuarioRepository.findByNombreUsuario(nu);
    }

    public boolean existePorNombre(String nu){
        return usuarioRepository.existsByNombreUsuario(nu);
    }

    public  boolean existePorEmail(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public boolean existePorId(Long id){
        return usuarioRepository.existsById(id);
    }

    public void guardar(Usuario usuario){
        usuarioRepository.save(usuario);
    }

    public Optional<Usuario> obtenerPorId(Long id){
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerPorNombre(String nu){
        return usuarioRepository.findByNombreUsuario(nu);
    }

    public void borrar(Long id){
        usuarioRepository.deleteById(id);
    }
}
