package com.medac.geofichaje.services;

import com.medac.geofichaje.models.Empleado;
import com.medac.geofichaje.repositories.EmpleadoRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService implements GenericService<Empleado, Long> {

    // Traemos a nuestro repositorio
    private final EmpleadoRepository empleadoRepository;

    // Constructor: Inyección de Dependencias
    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public List<Empleado> findAll() {
        return empleadoRepository.findAll();
    }

    @Override
    public Optional<Empleado> findById(Long id) {
        return empleadoRepository.findById(id);
    }

    @Override
    public Empleado save(Empleado entity) {
        return empleadoRepository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        empleadoRepository.deleteById(id);
    }

    // Nuestro método personalizado del repositorio
    public Optional<Empleado> findByDni(String dni) {
        return empleadoRepository.findByDni(dni);
    }
}