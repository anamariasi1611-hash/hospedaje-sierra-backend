package com.hospedajesierra.hospedaje_sierra_backend.repository;

import com.hospedajesierra.hospedaje_sierra_backend.entity.DetalleCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Extiende JpaRepository para operaciones CRUD en DetalleCompra
public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Integer> {

    // Lista detalles por ID de compra
    List<DetalleCompra> findByCompraIdCompra(Integer idCompra);
}
