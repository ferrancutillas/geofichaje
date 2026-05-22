package com.medac.geofichaje.validations;

@FunctionalInterface
public interface DistanciaValidator {
    boolean validar(double latOficina, double lonOficina, double latFichaje, double lonFichaje);
}