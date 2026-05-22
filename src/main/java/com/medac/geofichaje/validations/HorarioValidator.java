package com.medac.geofichaje.validations;

import java.time.LocalDateTime;

@FunctionalInterface
public interface HorarioValidator {
    boolean validar(LocalDateTime horaFichaje);
}