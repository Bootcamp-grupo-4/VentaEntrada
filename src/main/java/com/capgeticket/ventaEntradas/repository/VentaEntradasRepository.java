package com.capgeticket.ventaEntradas.repository;

import com.capgeticket.ventaEntradas.model.VentaEntrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaEntradasRepository extends JpaRepository<VentaEntrada,Long> {
}
