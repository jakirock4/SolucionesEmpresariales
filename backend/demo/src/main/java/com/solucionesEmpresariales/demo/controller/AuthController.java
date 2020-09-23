package com.solucionesEmpresariales.demo.controller;

import com.solucionesEmpresariales.demo.DTO.JwtDTO;
import com.solucionesEmpresariales.demo.DTO.LoginUsuario;
import com.solucionesEmpresariales.demo.DTO.Mensaje;
import com.solucionesEmpresariales.demo.DTO.NuevoUsuario;
import com.solucionesEmpresariales.demo.entity.Rol;
import com.solucionesEmpresariales.demo.entity.Usuario;
import com.solucionesEmpresariales.demo.enums.RolNombre;
import com.solucionesEmpresariales.demo.security.JWT.JwtProvider;
import com.solucionesEmpresariales.demo.service.RolService;
import com.solucionesEmpresariales.demo.service.UsuarioService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    RolService rolService;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/nuevo")
    public ResponseEntity<?> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity(new Mensaje("campos vacíos o email inválido"), HttpStatus.BAD_REQUEST);
        if(usuarioService.existePorNombre(nuevoUsuario.getNombreUsuario()))
            return new ResponseEntity(new Mensaje("ese nombre ya existe"), HttpStatus.BAD_REQUEST);
        if(usuarioService.existePorEmail(nuevoUsuario.getEmail()))
            return new ResponseEntity(new Mensaje("ese email ya existe"), HttpStatus.BAD_REQUEST);
        Usuario usuario =
                new Usuario(nuevoUsuario.getNombre(), nuevoUsuario.getNombreUsuario(), nuevoUsuario.getEmail(),
                        passwordEncoder.encode(nuevoUsuario.getPassword()));
        Set<String> rolesStr = nuevoUsuario.getRoles();
        Set<Rol> roles = new HashSet<>();
        for (String rol : rolesStr) {
            switch (rol) {
                case "admin":
                    Rol rolAdmin = rolService.getByRolNombre(RolNombre.ROLE_ADMIN).get();
                    roles.add(rolAdmin);
                    break;
                default:
                    Rol rolUser = rolService.getByRolNombre(RolNombre.ROLE_USER).get();
                    roles.add(rolUser);
            }
        }
        usuario.setRoles(roles);
        usuarioService.guardar(usuario);
        return new ResponseEntity(new Mensaje("usuario guardado"), HttpStatus.CREATED);
    }

    @PutMapping("/actualizar/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@RequestBody Usuario usuario, @PathVariable("id") Long id){
        if(!usuarioService.existePorId(id))
            return new ResponseEntity(new Mensaje("no existe ese usuario"), HttpStatus.NOT_FOUND);
        if(StringUtils.isBlank(usuario.getNombre()))
            return new ResponseEntity(new Mensaje("el nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        if(StringUtils.isBlank(usuario.getNombreUsuario()))
            return new ResponseEntity(new Mensaje("el nombre de usuario es obligatorio"), HttpStatus.BAD_REQUEST);
        if(usuarioService.existePorNombre(usuario.getNombre()) &&
                usuarioService.obtenerPorNombre(usuario.getNombreUsuario()).get().getId() != id)
            return new ResponseEntity(new Mensaje("ese nombre ya existe"), HttpStatus.BAD_REQUEST);
        Usuario uaurioUpdate = usuarioService.obtenerPorId(id).get();
        uaurioUpdate.setNombre(usuario.getNombre());
        uaurioUpdate.setNombreUsuario(usuario.getNombreUsuario());
        uaurioUpdate.setEmail(usuario.getEmail());

        usuarioService.guardar(uaurioUpdate);
        return new ResponseEntity(new Mensaje("usuario actualizado"), HttpStatus.CREATED);
    }

    @DeleteMapping("/borrar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id){
        if(!usuarioService.existePorId(id))
            return new ResponseEntity(new Mensaje("no existe el usaurio"), HttpStatus.NOT_FOUND);
        usuarioService.borrar(id);
        return new ResponseEntity(new Mensaje("usuario eliminado"), HttpStatus.OK);
    }

    @GetMapping("/lista")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> getLista(){
        List<Usuario> lista = usuarioService.obtenerTodos();
        return new ResponseEntity<List<Usuario>>(lista, HttpStatus.OK);
    }

    @GetMapping("/detalle/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> getOne(@PathVariable Long id){
        if(!usuarioService.existePorId(id))
            return new ResponseEntity(new Mensaje("no existe ese producto"), HttpStatus.NOT_FOUND);
        Usuario usuario = usuarioService.obtenerPorId(id).get();
        return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
    }


    //@CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/login")
    public ResponseEntity<JwtDTO> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity(new Mensaje("campos vacíos o email inválido"), HttpStatus.BAD_REQUEST);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUsuario.getNombreUsuario(), loginUsuario.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        JwtDTO jwtDTO = new JwtDTO(jwt, userDetails.getUsername(), userDetails.getAuthorities());
        return new ResponseEntity<JwtDTO>(jwtDTO, HttpStatus.OK);
    }
}