package com.hospedajesierra.hospedaje_sierra_backend.controller;

import com.hospedajesierra.hospedaje_sierra_backend.dto.LoginRequest;
import com.hospedajesierra.hospedaje_sierra_backend.dto.RegistroEmpleadoRequest;
import com.hospedajesierra.hospedaje_sierra_backend.entity.Empleado;
import com.hospedajesierra.hospedaje_sierra_backend.repository.EmpleadoRepository;
import com.hospedajesierra.hospedaje_sierra_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Define el controlador REST para autenticación
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    // Maneja el login y genera JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Autentica credenciales con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            // Establece autenticación en el contexto
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Genera token JWT
            String token = jwtService.generateToken(request.username());

            // Busca empleado por username
            Empleado empleado = empleadoRepository.findByNombreUsuario(request.username())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado tras autenticación"));

            // Prepara respuesta con token y datos de empleado
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", empleado.getNombreUsuario());
            response.put("nombreCompleto", empleado.getNombreCompleto());
            response.put("rol", empleado.getRol());
            response.put("email", empleado.getEmail());
            response.put("cedula", empleado.getCedula());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno en el servidor: " + e.getMessage());
        }
    }

    // Registra nuevo empleado, solo para ADMIN
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrarEmpleado(@RequestBody RegistroEmpleadoRequest request) {
        try {
            // Verifica unicidad de username, email y cedula
            if (empleadoRepository.findByNombreUsuario(request.nombreUsuario()).isPresent()) {
                return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso");
            }
            if (empleadoRepository.findByEmail(request.email()).isPresent()) {
                return ResponseEntity.badRequest().body("El email ya está registrado");
            }
            if (empleadoRepository.findByCedula(request.cedula()).isPresent()) {
                return ResponseEntity.badRequest().body("La cédula ya está registrada");
            }

            // Crea nuevo empleado
            Empleado nuevoEmpleado = new Empleado();
            nuevoEmpleado.setNombreUsuario(request.nombreUsuario());
            nuevoEmpleado.setNombreCompleto(request.nombreCompleto());
            nuevoEmpleado.setCedula(request.cedula());
            nuevoEmpleado.setEmail(request.email());
            nuevoEmpleado.setContrasena(passwordEncoder.encode(request.password()));
            nuevoEmpleado.setRol(request.rol() != null ? request.rol() : "EMPLEADO");

            // Guarda empleado
            Empleado guardado = empleadoRepository.save(nuevoEmpleado);

            return ResponseEntity.ok("Empleado registrado correctamente: " + guardado.getNombreUsuario());

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Error de integridad de datos (posible duplicado)");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar empleado: " + e.getMessage());
        }
    }

    // Obtiene datos del usuario actual
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Empleado> optionalEmpleado = empleadoRepository.findByNombreUsuario(username);
        if (optionalEmpleado.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Empleado empleado = optionalEmpleado.get();

        // Prepara mapa con datos de empleado
        Map<String, Object> data = new HashMap<>();
        data.put("username", empleado.getNombreUsuario());
        data.put("nombreCompleto", empleado.getNombreCompleto());
        data.put("rol", empleado.getRol());
        data.put("email", empleado.getEmail());
        data.put("cedula", empleado.getCedula());

        return ResponseEntity.ok(data);
    }

    // Lista todos los empleados, solo para ADMIN
    @GetMapping("/empleados")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Empleado>> listarEmpleados() {
        List<Empleado> empleados = empleadoRepository.findAll();
        // Elimina contraseñas de la respuesta
        empleados.forEach(emp -> emp.setContrasena(null));
        return ResponseEntity.ok(empleados);
    }

    // Elimina empleado por ID, solo para ADMIN
    @DeleteMapping("/empleados/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarEmpleado(@PathVariable Integer id) {
        Optional<Empleado> optionalEmpleado = empleadoRepository.findById(id);
        if (optionalEmpleado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Empleado empleado = optionalEmpleado.get();

        // Evita eliminar cuenta propia
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getName().equals(empleado.getNombreUsuario())) {
            return ResponseEntity.status(403).body("No puedes eliminar tu propia cuenta");
        }

        // Elimina empleado
        empleadoRepository.deleteById(id);
        return ResponseEntity.ok("Empleado eliminado correctamente");
    }

    // Resetea contraseña por ADMIN
    @PostMapping("/reset-by-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetPasswordByAdmin(@RequestBody Map<String, Object> request) {
        Integer id = (Integer) request.get("id");
        String nuevaPassword = (String) request.get("nuevaPassword");

        if (id == null || nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID y nueva contraseña son requeridos");
        }

        Optional<Empleado> optionalEmpleado = empleadoRepository.findById(id);
        if (optionalEmpleado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Empleado empleado = optionalEmpleado.get();

        // Evita resetear cuenta propia
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getName().equals(empleado.getNombreUsuario())) {
            return ResponseEntity.status(403).body("No puedes resetear tu propia contraseña");
        }

        // Actualiza contraseña codificada
        empleado.setContrasena(passwordEncoder.encode(nuevaPassword.trim()));
        empleadoRepository.save(empleado);

        return ResponseEntity.ok("Contraseña reseteada correctamente");
    }
}


