package com.medac.geofichaje.controllers;

import com.medac.geofichaje.models.Empleado;
import com.medac.geofichaje.enums.EstadoFichaje;
import com.medac.geofichaje.models.Fichaje;
import com.medac.geofichaje.services.EmpleadoService;
import com.medac.geofichaje.services.FichajeService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class FichajeController {

    // Traemos los servicios que necesitamos para manejar la lógica de fichajes y empleados
    private final FichajeService fichajeService;
    private final EmpleadoService empleadoService;

    // Constructor: Inyección de Dependencias
    public FichajeController(FichajeService fichajeService, EmpleadoService empleadoService) {
        this.fichajeService = fichajeService;
        this.empleadoService = empleadoService;
    }

    // Mostrar la página de Login (index.html)
    @GetMapping("/")
    public String mostrarLogin() {
        return "index";
    }

    // Procesar el formulario de Login
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String dni, Model model) {
        // Mete al empleado en un Optional para manejar el caso de que no exista
        Optional<Empleado> empleadoOpt = empleadoService.findByDni(dni);

        // Si no existe, le devolvemos a la página de Login con un mensaje de error
        if (empleadoOpt.isEmpty()) {
            model.addAttribute("mensajeError", "El DNI introducido no existe en la base de datos.");
            return "index";
        }

        // Si existe, preparamos la "mochila" (Model) con sus datos para el panel
        Empleado empleado = empleadoOpt.get();
        cargarDatosPanel(empleado, model);
        return "panel";
    }

    // Procesar el botón de Fichar desde el Panel
    @PostMapping("/fichar")
    public String procesarFichaje(@RequestParam String dni,
                                @RequestParam double latitud,
                                @RequestParam double longitud,
                                @RequestParam EstadoFichaje estado,
                                Model model) {
        
        // Gracias al login y a la validación previa, sabemos que existe por lo que no hace falta el Optional aquí
        Empleado empleado = empleadoService.findByDni(dni).get();
        
        /* Verifica si el empleado esta a < 200 metros de la oficina y si ficha dentro del rango horario,
        además comprobará que no haya una entrada tras otra y así con las salidas, entonces al model se le
        añade un mensaje de éxito o error */
        try {
            fichajeService.registrarFichaje(empleado, latitud, longitud, estado);
            model.addAttribute("mensajeExito", "Fichaje de " + estado + " guardado correctamente.");
        } catch (Exception e) {
            // El mensaje de error se lo damos desde el Servicio, dependiendo de la validación que falle (distancia, horario, fichaje repetido)
            model.addAttribute("mensajeError", e.getMessage()); // Muestra el error de los 200m
        }

        // Volvemos a cargar los datos y le dejamos en el panel
        cargarDatosPanel(empleado, model);
        return "panel";
    }

    // Método para cargar los datos del panel de control del empleado (empleado, fichajes, días trabajados, horas trabajadas)
    private void cargarDatosPanel(Empleado empleado, Model model) {
        model.addAttribute("empleado", empleado);
        
        List<Fichaje> todosLosFichajes = fichajeService.findAll();
        // Como en nustro repositorio tenemos un método para buscar por empleado, podríamos usarlo directamente
        List<Fichaje> misFichajes = todosLosFichajes.stream() // Mediante Streams, recorremos la lista de fichajes
                .filter(f -> f.getEmpleado().getId().equals(empleado.getId())) // Función Lambda para filtrar por empleado
                .collect(Collectors.toList());
                
        model.addAttribute("fichajes", misFichajes);
        
        // Calculamos los días trabajados usando el método con Streams que hicimos en el Servicio
        long diasTrabajados = fichajeService.contarDiasTrabajados(empleado.getId());
        model.addAttribute("diasTrabajados", diasTrabajados);

        // Calculamos las horas trabajadas de la misma forma    
        String horasTrabajadas = fichajeService.calcularHorasTrabajadas(empleado.getId());
        model.addAttribute("horasTrabajadas", horasTrabajadas);
    }
}