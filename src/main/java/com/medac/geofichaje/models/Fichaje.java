package com.medac.geofichaje.models;

import com.medac.geofichaje.enums.EstadoFichaje;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Fichaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado", nullable = false)
    private Empleado empleado;

    @Column(name = "fechaHora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoFichaje estado;

    @Embedded
    private Coordenadas ubicacion;

    public Fichaje() {
    }

    public Fichaje(Empleado empleado, LocalDateTime fechaHora, EstadoFichaje estado, Coordenadas ubicacion) {
        this.empleado = empleado;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.ubicacion = ubicacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public EstadoFichaje getEstado() {
        return estado;
    }

    public void setEstado(EstadoFichaje estado) {
        this.estado = estado;
    }

    public Coordenadas getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Coordenadas ubicacion) {
        this.ubicacion = ubicacion;
    }

    // Clase embebida para guardar las coordenadas GPS del fichaje
    @Embeddable
    public static class Coordenadas {

        @Column(name = "latitud", nullable = false)
        private Double latitud;

        @Column(name = "longitud", nullable = false)
        private Double longitud;

        public Coordenadas() {
        }

        public Coordenadas(Double latitud, Double longitud) {
            this.latitud = latitud;
            this.longitud = longitud;
        }

        public Double getLatitud() {
            return latitud;
        }

        public void setLatitud(Double latitud) {
            this.latitud = latitud;
        }

        public Double getLongitud() {
            return longitud;
        }

        public void setLongitud(Double longitud) {
            this.longitud = longitud;
        }

    }
}