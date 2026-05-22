# Sistema de Geo-Fichaje Corporativo 🚀

Este proyecto consiste en una aplicación web Full-Stack construida con **Spring Boot 3** y **Java 25** para la gestión y control de presencia laboral. El sistema implementa un mecanismo de seguridad avanzado que utiliza la geolocalización en tiempo real del navegador (lado del cliente) y validaciones matemáticas en el servidor (Fórmula de Haversine) para restringir los fichajes a un radio permitido de 200 metros respecto al centro de trabajo.

## 📋 Descripción del Proyecto

El **Sistema de Geo-Fichaje Corporativo** resuelve el problema del fraude en el registro horario en entornos de teletrabajo o movilidad. A diferencia de las soluciones tradicionales, este sistema no permite un registro "ciego"; requiere obligatoriamente que el dispositivo del empleado comparta sus coordenadas GPS. El backend procesa esta información de forma síncrona y, mediante el uso intensivo de la programación funcional de Java, valida tanto la proximidad física como la coherencia de los estados de la jornada laboral (impidiendo duplicidades de entrada o salidas huérfanas).

---

## ✨ Características Principales

* **Autenticación Eficiente:** Acceso rápido y seguro mediante la validación del DNI del empleado contra la base de datos MySQL.
* **Geolocalización Nativa:** Integración con la API Geolocation de HTML5 para capturar coordenadas GPS exactas (Latitud y Longitud) con el consentimiento del usuario.
* **Validación de Radio Geográfico:** Implementación del algoritmo de Haversine en el servidor para bloquear fichajes realizados a más de 200 metros de la oficina.
* **Máquina de Estados Robusta:** Control estricto del ciclo de vida del fichaje, impidiendo estados incoherentes como registrar dos entradas consecutivas o una salida sin una entrada previa.
* **Procesamiento Funcional:** Uso de la API de Streams, Expresiones Lambda y la clase `Optional` de Java para el filtrado dinámico del historial y el cálculo automatizado de las horas trabajadas.
* **Navegación Segura:** Implementación del patrón Post-Redirect-Get (PRG) para evitar el reenvío accidental de formularios al refrescar la pantalla.

---

## 🛠️ Stack Tecnológico

* **Backend:** Java 25, Spring Boot 3, Spring Data JPA (Hibernate), Maven.
* **Base de Datos:** MySQL (Persistencia relacional).
* **Frontend:** Thymeleaf (Renderizado dinámico en servidor), HTML5, CSS3, JavaScript (ES6+).
* **APIs e Integraciones:** HTML5 Geolocation API, Google Maps API (Opcional para visualización).

---

## 🏗️ Arquitectura del Software

La aplicación sigue el patrón arquitectónico **MVC (Modelo-Vista-Controlador)** y está estructurada en un diseño limpio por capas:

1.  **Capa de Vista (Thymeleaf/HTML/JS):** Captura las acciones del usuario y los datos del hardware (GPS).
2.  **Capa de Controlador (`@Controller` / `@RestController`):** Expone los Endpoints (POST `/login`, POST `/fichar`, GET `/panel`) e interactúa con los DTOs.
3.  **Capa de Servicio (`@Service`):** Contiene el núcleo de la lógica de negocio, validaciones geográficas y de estado.
4.  **Capa de Repositorio (`@Repository`):** Abstracción de datos que extiende de `JpaRepository` para operaciones CRUD optimizadas.

---

## 🚀 Instalación y Configuración Local

### Prerrequisitos
* **Java SE Development Kit (JDK) 25** instalado.
* **Apache Maven 3.x**.
* Servidor **MySQL** activo (ej. a través de XAMPP / phpMyAdmin).

### Pasos para la ejecución

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/tu-usuario/sistema-geo-fichaje.git
    cd sistema-geo-fichaje
    ```

2.  **Configurar la Base de Datos:**
    Crea una base de datos en tu servidor MySQL llamada `geofichaje`. Abre el archivo `src/main/resources/application.properties` y ajusta las credenciales si es necesario:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/geofichaje?serverTimezone=UTC
    spring.datasource.username=tu_usuario
    spring.datasource.password=tu_contraseña
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```

3.  **Compilar y construir el proyecto con Maven:**
    ```bash
    mvn clean install
    ```

4.  **Ejecutar la aplicación:**
    ```bash
    mvn spring-boot:run
    ```

5.  **Acceso Web:**
    Abre tu navegador de preferencia e introduce la URL local: `http://localhost:8080`

---

## 👤 Autor

* **Ferran Cutillas Castillo** - *Desarrollador del Proyecto* - 1º DAW (Desarrollo de Aplicaciones Web) - MEDAC, Junio 2026.