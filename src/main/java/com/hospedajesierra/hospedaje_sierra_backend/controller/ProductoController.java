package com.hospedajesierra.hospedaje_sierra_backend.controller;

import com.hospedajesierra.hospedaje_sierra_backend.dto.ProductoDto;
import com.hospedajesierra.hospedaje_sierra_backend.entity.Producto;
import com.hospedajesierra.hospedaje_sierra_backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

// Define el controlador REST para productos
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class ProductoController {

    private final ProductoRepository productoRepository;

    // Lista productos activos
    @GetMapping
    public ResponseEntity<List<ProductoDto>> listarProductos() {
        List<ProductoDto> productos = productoRepository.findByActivoTrue().stream()
                .map(p -> new ProductoDto(p.getIdProducto(), p.getNombre(), p.getPrecio()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }

    // Crea nuevo producto
    @PostMapping
    public ResponseEntity<ProductoDto> crearProducto(@RequestBody ProductoDto dto) {
        // Valida nombre y precio
        if (dto.nombre() == null || dto.nombre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        if (dto.precio() == null || dto.precio() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio debe ser mayor a 0");
        }
        if (dto.idProducto() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se debe enviar id al crear");
        }

        String nombreTrim = dto.nombre().trim();
        // Verifica unicidad de nombre
        if (productoRepository.existsByNombreIgnoreCase(nombreTrim)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un producto con ese nombre");
        }

        // Crea producto
        Producto producto = new Producto();
        producto.setNombre(nombreTrim);
        producto.setPrecio(dto.precio());
        producto.setActivo(true);

        // Guarda producto
        Producto guardado = productoRepository.save(producto);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ProductoDto(guardado.getIdProducto(), guardado.getNombre(), guardado.getPrecio())
        );
    }

    // Actualiza producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> actualizarProducto(
            @PathVariable Integer id,
            @RequestBody ProductoDto dto) {

        // Busca producto
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // Actualiza nombre si válido
        if (dto.nombre() != null && !dto.nombre().trim().isEmpty()) {
            String nuevoNombre = dto.nombre().trim();
            if (!nuevoNombre.equalsIgnoreCase(producto.getNombre()) &&
                    productoRepository.existsByNombreIgnoreCase(nuevoNombre)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un producto con ese nombre");
            }
            producto.setNombre(nuevoNombre);
        }

        // Actualiza precio si válido
        if (dto.precio() != null && dto.precio() > 0) {
            producto.setPrecio(dto.precio());
        }

        // Guarda cambios
        Producto actualizado = productoRepository.save(producto);

        return ResponseEntity.ok(new ProductoDto(
                actualizado.getIdProducto(),
                actualizado.getNombre(),
                actualizado.getPrecio()
        ));
    }

    // Elimina producto (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        // Busca producto
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // Marca como inactivo
        producto.setActivo(false);
        productoRepository.save(producto);

        return ResponseEntity.noContent().build();
    }
}
