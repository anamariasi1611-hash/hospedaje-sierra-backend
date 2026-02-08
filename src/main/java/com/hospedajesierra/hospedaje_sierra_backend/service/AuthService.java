package com.hospedajesierra.hospedaje_sierra_backend.service;

import com.hospedajesierra.hospedaje_sierra_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Valida las credenciales del empleado contra la base de datos usando Spring Security.
     * Si son correctas, genera y devuelve un token JWT firmado con HS256.
     * @param username nombre de usuario del empleado
     * @param password contraseña (se compara con hash BCrypt almacenado)
     * @return token JWT válido por 24 horas
     * @throws Bad Credentials si las credenciales son incorrectas
     */
    public String login(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return jwtService.generateToken(username);
    }
}
