package com.hospedajesierra.hospedaje_sierra_backend.security;

import com.hospedajesierra.hospedaje_sierra_backend.entity.Empleado;
import com.hospedajesierra.hospedaje_sierra_backend.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Implementa UserDetailsService para cargar usuarios en Spring Security
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmpleadoRepository empleadoRepository;

    // Carga usuario por username para autenticación
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca empleado por nombre de usuario
        Empleado empleado = empleadoRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Construye UserDetails con username, contraseña y rol
        return User.builder()
                .username(empleado.getNombreUsuario())
                .password(empleado.getContrasena())
                .authorities("ROLE_" + empleado.getRol())
                .build();
    }
}
