
package com.eva.eva.service;

import com.eva.eva.dto.*;
import com.eva.eva.model.Usuario;
import com.eva.eva.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse registrar(RegistroRequest request) {

        if (usuarioRepository.existsByCorreo(request.correo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo ya está registrado"
            );
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setCorreo(request.correo());
        usuario.setPassword(
                passwordEncoder.encode(request.password())
        );

        Usuario guardado = usuarioRepository.save(usuario);

        return new AuthResponse(
                "Usuario registrado correctamente",
                guardado.getId(),
                guardado.getNombre(),
                guardado.getCorreo()
        );
    }

    public AuthResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByCorreo(request.correo())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Correo o contraseña incorrectos"
                ));

        if (!passwordEncoder.matches(
                request.password(),
                usuario.getPassword())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo o contraseña incorrectos"
            );
        }

        return new AuthResponse(
                "Inicio de sesión exitoso",
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo()
        );
    }
}
