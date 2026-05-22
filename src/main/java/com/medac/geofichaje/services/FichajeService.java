package com.medac.geofichaje.services;

import com.medac.geofichaje.models.Empleado;
import com.medac.geofichaje.enums.EstadoFichaje;
import com.medac.geofichaje.models.Fichaje;
import com.medac.geofichaje.repositories.FichajeRepository;
import com.medac.geofichaje.validations.DistanciaValidator;
import com.medac.geofichaje.validations.HorarioValidator;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FichajeService implements GenericService<Fichaje, Long> {

    // Coordenadas de la empresa
    private final double LAT_OFICINA = 37.8802566; 
    private final double LON_OFICINA = -4.8040947;

    // Traemos a nuestro repositorio
    private final FichajeRepository fichajeRepository;

    // Constructor: Inyección de Dependencias
    public FichajeService(FichajeRepository fichajeRepository) {
        this.fichajeRepository = fichajeRepository;
    }

    @Override
    public List<Fichaje> findAll() { 
        return fichajeRepository.findAll(); 
    }

    @Override
    public Optional<Fichaje> findById(Long id) { 
        return fichajeRepository.findById(id); 
    }

    @Override
    public Fichaje save(Fichaje entity) { 
        return fichajeRepository.save(entity); 
    }
        
    @Override
    public void deleteById(Long id) { 
        fichajeRepository.deleteById(id); 
    }

    // Fichar con validación de distancia (Uso de Lambda)
    public Fichaje registrarFichaje(Empleado empleado, double latUsuario, double lonUsuario, EstadoFichaje estado) {

        // =========================================================================
        // Validación de Distancia (< 200 metros)
        // =========================================================================
        
        // LAMBDA: Le damos cuerpo a la interfaz funcional DistanciaValidator
        DistanciaValidator validador = (latOfi, lonOfi, latUsu, lonUsu) -> {

            // Fórmula matemática (Haversine) para calcular distancia en metros entre dos puntos GPS
            // 1. Convertir todas las coordenadas de grados a radianes para que Java pueda trabajar con funciones trigonométricas
            double latUsuRad = Math.toRadians(latUsu);
            double latOfiRad = Math.toRadians(latOfi);
            double lonUsuRad = Math.toRadians(lonUsu);
            double lonOfiRad = Math.toRadians(lonOfi);

            double radioTierra = 6371000.0; // Radio de la Tierra en metros

            // 2. Calcular la parte interna de la fórmula (conocida tradicionalmente como 'a')
            double a =  Math.pow(Math.sin((latUsuRad - latOfiRad) / 2), 2) +
                        Math.cos(latOfiRad) * Math.cos(latUsuRad) *
                        Math.pow(Math.sin((lonUsuRad - lonOfiRad) / 2), 2);

            // 3. Calcular la distancia final
            double distancia = 2 * radioTierra * Math.asin(Math.sqrt(a));

            // 4. Evaluar la condición (<= 200 metros)
            return distancia <= 200.0;
        };

        // Usamos nuestro validador
        boolean estaCerca = validador.validar(LAT_OFICINA, LON_OFICINA, latUsuario, lonUsuario);

        if (!estaCerca) {
            // Si devuelve false, cortamos la ejecución y lanzamos un error
            throw new RuntimeException("Error: Estás a más de 200 metros de la oficina.");
        }

        // =========================================================================
        // Validación de Horario Laboral (6 AM - 10 PM)
        // =========================================================================
        
        HorarioValidator validadorHorario = horaFichaje -> {
            int hora = horaFichaje.getHour();
            // Permitir fichajes entre 6 AM (6) y 10 PM (22)
            return hora >= 6 && hora < 22;
        };

        boolean horarioValido = validadorHorario.validar(LocalDateTime.now());
        if (!horarioValido) {
            throw new RuntimeException("Error: Solo puedes fichar entre las 6:00 AM y las 10:00 PM.");
        }

        // =========================================================================
        // Comprobar que no ficha dos ENTRADAS o dos SALIDAS seguidas
        // =========================================================================
        
        // 1. Obtenemos todo el historial de este empleado
        List<Fichaje> historial = fichajeRepository.findByEmpleadoId(empleado.getId());

        // 2. Usamos STREAMS para buscar cuál fue su ÚLTIMO fichaje cronológicamente
        Optional<Fichaje> ultimoFichaje = historial.stream()
                .max((f1, f2) -> f1.getFechaHora().compareTo(f2.getFechaHora()));

        // 3. Comprobamos si hay algún fichaje previo y si el estado es el mismo que intenta ahora
        if (estado == EstadoFichaje.ENTRADA) {
            // Si intenta ENTRAR: Comprobamos si ya estaba dentro
            if (ultimoFichaje.isPresent() && ultimoFichaje.get().getEstado() == EstadoFichaje.ENTRADA) {
                throw new RuntimeException("Error: Ya estás trabajando. Debes registrar una SALIDA antes de volver a entrar.");
            }
        } else if (estado == EstadoFichaje.SALIDA) {
            // Si intenta SALIR: OBLIGATORIAMENTE tuvo que haber entrado antes
            if (ultimoFichaje.isEmpty()) {
                throw new RuntimeException("Error: No puedes registrar una SALIDA porque aún no has empezado a trabajar hoy.");
            }
            // Comprobamos si ya estaba feura
            if (ultimoFichaje.get().getEstado() == EstadoFichaje.SALIDA) {
                throw new RuntimeException("Error: Ya has salido de trabajar. Debes registrar una ENTRADA primero.");
            }
        }

        // Si ha pasado la validación, creamos el fichaje
        Fichaje.Coordenadas ubicacion = new Fichaje.Coordenadas(latUsuario, lonUsuario);
        Fichaje nuevoFichaje = new Fichaje(empleado, LocalDateTime.now(), estado, ubicacion);

        return fichajeRepository.save(nuevoFichaje);
    }

    // Uso de Streams para procesar colecciones y obtener los días trabajados
    public long contarDiasTrabajados(Long empleadoId) {
        List<Fichaje> fichajesDelEmpleado = fichajeRepository.findByEmpleadoId(empleadoId);

        // STREAM: Creamos una "cinta transportadora" con los fichajes
        long diasFichados = fichajesDelEmpleado.stream() // Aplicamos un Stream a la lista de fichajes
            .filter(fichaje -> fichaje.getEstado() == EstadoFichaje.ENTRADA) // Mediante una Lambda, filtramos solo los fichajes de ENTRADA
            .count(); // Contamos cuántos han quedado después del filtro

        return diasFichados;
    }

    // Uso de Streams para procesar colecciones y obtener las horas trabajadas en formato HH:mm:ss
    public String calcularHorasTrabajadas(Long empleadoId) {
        // Obtenemos todos los fichajes de este empleado
        List<Fichaje> todosLosFichajes = fichajeRepository.findByEmpleadoId(empleadoId);

        // STREAM 1: Filtramos y ordenamos solo las ENTRADAS
        List<Fichaje> entradas = todosLosFichajes.stream()
                .filter(f -> f.getEstado() == EstadoFichaje.ENTRADA) // Lambda para filtrar
                .sorted((f1, f2) -> f1.getFechaHora().compareTo(f2.getFechaHora())) // Lambda para ordenar por fecha
                .toList();

        // STREAM 2: Filtramos y ordenamos solo las SALIDAS
        List<Fichaje> salidas = todosLosFichajes.stream()
                .filter(f -> f.getEstado() == EstadoFichaje.SALIDA)
                .sorted((f1, f2) -> f1.getFechaHora().compareTo(f2.getFechaHora()))
                .toList();

        // Usamos Duration para no perder minutos ni segundos en la suma total
        java.time.Duration tiempoTotal = java.time.Duration.ZERO;

        // Emparejamos las ENTRADAS con las SALIDAS para que no de error si hay fichajes incompletos a la hora de comparar
        int paresCompletos = Math.min(entradas.size(), salidas.size());
        
        for (int i = 0; i < paresCompletos; i++) {
            LocalDateTime horaEntrada = entradas.get(i).getFechaHora();
            LocalDateTime horaSalida = salidas.get(i).getFechaHora();
            
            // Calculamos la duración completa del turno y la acumulamos
            java.time.Duration duracionTurno = java.time.Duration.between(horaEntrada, horaSalida);
            tiempoTotal = tiempoTotal.plus(duracionTurno);
        }

        // Extraemos las partes para el formato final
        long horas = tiempoTotal.toHours();
        int minutos = tiempoTotal.toMinutesPart();
        int segundos = tiempoTotal.toSecondsPart();

        // Devolvemos el total de horas trabajadas formateado como HH:mm:ss
        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }
}