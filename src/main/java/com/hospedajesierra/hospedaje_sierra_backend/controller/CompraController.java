package com.hospedajesierra.hospedaje_sierra_backend.controller;

import com.hospedajesierra.hospedaje_sierra_backend.dto.AgregarDetalleDto;
import com.hospedajesierra.hospedaje_sierra_backend.dto.CompraDto;
import com.hospedajesierra.hospedaje_sierra_backend.dto.DetalleCompraDto;
import com.hospedajesierra.hospedaje_sierra_backend.entity.*;
import com.hospedajesierra.hospedaje_sierra_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Define el controlador REST para compras
@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class CompraController {

    private final CompraRepository compraRepo;
    private final DetalleCompraRepository detalleRepo;
    private final ProductoRepository productoRepo;
    private final ReservaRepository reservaRepo;
    private final EmpleadoRepository empleadoRepo;
    private final FacturaReservaRepository facturaRepo;

    // Obtiene o crea compra para una reserva
    @GetMapping("/por-reserva/{idReserva}")
    public ResponseEntity<CompraDto> obtenerOCrearCompra(@PathVariable Integer idReserva) {
        Optional<Compra> compraOpt = compraRepo.findByReservaIdReserva(idReserva);

        Compra compra;
        if (compraOpt.isPresent()) {
            compra = compraOpt.get();
        } else {
            // Busca reserva
            Reserva reserva = reservaRepo.findById(idReserva)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

            // Obtiene empleado actual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assert auth != null;
            String username = auth.getName();

            Empleado empleado = empleadoRepo.findByNombreUsuario(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empleado no encontrado"));

            // Crea nueva compra
            compra = new Compra();
            compra.setReserva(reserva);
            compra.setEmpleado(empleado);
            compra.setFechaCompra(LocalDateTime.now());
            compra.setTotalCompra(0.0);

            // Guarda compra
            compra = compraRepo.save(compra);
        }

        return ResponseEntity.ok(toDto(compra));
    }

    // Agrega detalle a compra y actualiza total
    @PostMapping("/{idCompra}/detalles")
    public ResponseEntity<DetalleCompraDto> agregarDetalle(
            @PathVariable Integer idCompra,
            @RequestBody AgregarDetalleDto dto) {

        // Valida entrada
        if (dto == null || dto.idProducto() == null || dto.cantidad() == null || dto.cantidad() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos inválidos: producto y cantidad ≥ 1 son requeridos");
        }

        // Busca compra
        Compra compra = compraRepo.findById(idCompra)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada"));

        // Busca producto
        Producto producto = productoRepo.findById(dto.idProducto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // Crea detalle
        DetalleCompra detalle = new DetalleCompra();
        detalle.setCompra(compra);
        detalle.setProducto(producto);
        detalle.setCantidad(dto.cantidad());
        detalle.setSubtotalProducto(dto.cantidad() * producto.getPrecio());

        // Guarda detalle
        detalle = detalleRepo.save(detalle);

        // Actualiza total compra
        double nuevoTotal = compra.getTotalCompra() + detalle.getSubtotalProducto();
        compra.setTotalCompra(nuevoTotal);
        compraRepo.save(compra);

        // Actualiza factura asociada
        FacturaReserva factura = facturaRepo.findByReservaIdReserva(compra.getReserva().getIdReserva())
                .orElse(null);

        if (factura != null) {
            factura.setTotalServicios(compra.getTotalCompra());
            factura.setTotalFinal(factura.getCostoHabitacion() + factura.getTotalServicios());
            facturaRepo.save(factura);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(toDetalleDto(detalle));
    }

    // Obtiene detalles de una compra
    @GetMapping("/detalles/{idCompra}")
    public ResponseEntity<List<DetalleCompraDto>> getDetallesCompra(@PathVariable Integer idCompra) {
        Compra compra = compraRepo.findById(idCompra)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra no encontrada"));

        List<DetalleCompra> detalles = detalleRepo.findByCompraIdCompra(idCompra);

        // Convierte a DTOs
        List<DetalleCompraDto> dtos = detalles.stream()
                .map(d -> new DetalleCompraDto(
                        d.getIdDetalle(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getSubtotalProducto()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // Convierte compra a DTO
    private CompraDto toDto(Compra compra) {
        String nombreEmpleado = compra.getEmpleado().getNombreCompleto();
        if (nombreEmpleado == null || nombreEmpleado.trim().isEmpty()) {
            nombreEmpleado = compra.getEmpleado().getNombreUsuario();
        }

        return new CompraDto(
                compra.getIdCompra(),
                compra.getReserva().getIdReserva(),
                compra.getFechaCompra().toString(),
                compra.getTotalCompra(),
                nombreEmpleado
        );
    }

    // Convierte detalle a DTO
    private DetalleCompraDto toDetalleDto(DetalleCompra detalle) {
        return new DetalleCompraDto(
                detalle.getIdDetalle(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getSubtotalProducto()
        );
    }
}
