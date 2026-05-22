package com.medac.geofichaje.repositories;

import com.medac.geofichaje.models.Fichaje;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FichajeRepository extends JpaRepository<Fichaje, Long> {
    
    // Spring lee el prefijo "findBy" y el sufijo "EmpleadoId" para generar la consulta automáticamente
    List<Fichaje> findByEmpleadoId(Long empleadoId);
}