package com.hospedajesierra.hospedaje_sierra_backend.controller;

import com.hospedajesierra.hospedaje_sierra_backend.entity.Huesped;
import com.hospedajesierra.hospedaje_sierra_backend.repository.HuespedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Define el controlador REST para huéspedes
@RestController
@RequestMapping("/api/huespedes")
@RequiredArgsConstructor
public class HuespedController {

    private final HuespedRepository huespedRepository;

    // Lista todos los huéspedes
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<List<Huesped>> listarHuespedes() {
        List<Huesped> huespedes = huespedRepository.findAll();
        return ResponseEntity.ok(huespedes);
    }

    // Obtiene huésped por cédula
    @GetMapping("/cedula/{cedula}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Huesped> obtenerPorCedula(@PathVariable String cedula) {
        return huespedRepository.findByCedula(cedula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crea nuevo huésped si no existe
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Huesped> crearHuesped(@RequestBody Huesped huesped) {
        // Valida cédula
        if (huesped.getCedula() == null || huesped.getCedula().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Verifica existencia
        if (huespedRepository.findByCedula(huesped.getCedula()).isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Guarda huésped
        Huesped nuevo = huespedRepository.save(huesped);
        return ResponseEntity.ok(nuevo);
    }
}
