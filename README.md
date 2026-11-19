# Sistema de Gestión Universitaria de Eventos (University BD-NoSQL)

Sistema de escritorio desarrollado en **JavaFX 21** con **Gradle** y **MongoDB**, diseñado para la administración integral de eventos universitarios, control de asistencia, inscripción de participantes, gestión de espacios físicos y análisis de capacidad en la **Universidad Santo Tomás**.

---

## Tabla de Contenidos
- [Características Principales](#-características-principales)
- [Módulo Analítico: Gráfica de Dispersión y Regresión Lineal](#-módulo-analítico-gráfica-de-dispersión-y-regresión-lineal)
  - [¿Qué representa la gráfica?](#qué-representa-la-gráfica)
  - [Ejes y visualización temporal](#ejes-y-visualización-temporal)
  - [Modelo matemático de regresión lineal](#modelo-matemático-de-regresión-lineal)
  - [Interpretación de tendencias](#interpretación-de-tendencias)
  - [Implementación técnica en JavaFX](#implementación-técnica-en-javafx)
- [Novedades y Mejoras Recientes](#-novedades-y-mejoras-recientes)
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Requisitos Previos](#-requisitos-previos)
- [Configuración de la Base de Datos (MongoDB)](#-configuración-de-la-base-de-datos-mongodb)
- [Modelo de Datos NoSQL](#-modelo-de-datos-nosql)
- [Cómo Ejecutar el Proyecto](#-cómo-ejecutar-el-proyecto)
- [Solución de Problemas Frecuentes](#-solución-de-problemas-frecuentes)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)

---

## Características Principales

1. **Dashboard Interactivo y Analítico (`Inicio`)**:
   - **Indicadores Clave (KPIs)** en tiempo real: conteo global de eventos, participantes, inscripciones y espacios registrados.
   - **Lista de Próximos Eventos**: visualización cronológica continua con desplazamiento suave (`ScrollPane`), indicando nombre, lugar, fecha y hora de inicio.
   - **Gráfica de Dispersión y Línea de Tendencia**: análisis predictivo del aforo de los eventos a lo largo del tiempo.

2. **Gestión de Eventos (`Eventos`)**:
   - Registro, edición y eliminación de eventos académicos o institucionales.
   - Control de aforo máximo, horarios (inicio y fin), categorías y fecha programada.
   - Selección dinámica de espacios físicos vinculados desde la base de datos con validaciones preventivas.

3. **Gestión de Asistentes (`Asistentes`)**:
   - Registro de participantes con validación de nombre, documento de identidad y correo institucional.
   - Filtros de búsqueda en vivo por coincidencia de texto.

4. **Inscripciones (`Inscripciones`)**:
   - Registro y control del estado de las inscripciones vinculando asistentes a las actividades correspondientes.

5. **Gestión de Espacios (`Espacios / Venues`)**:
   - Administración de aulas magnas, auditorios, salas de cómputo y laboratorios con control estricto de capacidad.

---

## Módulo Analítico: Gráfica de Dispersión y Regresión Lineal

El panel de inicio incorpora un componente gráfico avanzado denominado **Dispersión y tendencia de eventos**, cuyo propósito es ofrecer una lectura visual sobre cómo se proyecta la concurrencia y tamaño de los eventos planificados.

### ¿Qué representa la gráfica?
Cada punto naranja en el plano cartesiano representa un evento programado en el sistema:
* **Eje Horizontal (X) - Tiempo:** Proyecta la fecha programada de cada evento.
* **Eje Vertical (Y) - Capacidad:** Indica el número de cupos o aforo estimado para dicho evento.
* **Tooltips interactivos:** Al pasar el cursor sobre cualquier punto, se despliega una ventana flotante con el nombre del evento, fecha exacta y cupos registrados.

### Ejes y visualización temporal
Internamente, el eje temporal procesa la distancia en días con respecto a la fecha actual (`ChronoUnit.DAYS.between(today, eventDate)`). Para garantizar una experiencia de usuario clara y natural, se implementó un formateador personalizado (`StringConverter<Number>`) que traduce automáticamente esos desplazamientos numéricos a **fechas del calendario (`dd/MM`)**, tales como `19/11`, `20/11`, `25/11`.

### Modelo matemático de regresión lineal
Para determinar la línea de tendencia, el sistema aplica el método analítico de **Mínimos Cuadrados Ordinarios (Ordinary Least Squares - OLS)**. 

La recta sigue la ecuación general:

$$y = m \cdot x + b$$

Donde:
* **$y$**: Capacidad proyectada (número de personas).
* **$x$**: Posición temporal relativa en días.
* **$m$ (Pendiente)**: Tasa de cambio o ritmo al que crece o disminuye el aforo proyectado por día.
* **$b$ (Intersección)**: Valor base de capacidad estimada en el día cero.

Las fórmulas aplicadas en el código son:

$$m = \frac{N \sum (x \cdot y) - (\sum x)(\sum y)}{N \sum (x^2) - (\sum x)^2}$$

$$b = \frac{\sum y - m \sum x}{N}$$

Donde $N$ es el total de eventos válidos que tienen fecha y capacidad asignada.

### Interpretación de tendencias
Una vez calculada la pendiente $m$, el sistema clasifica automáticamente el comportamiento y despliega una conclusión en negrita en la parte inferior de la tarjeta:

* **Tendencia Creciente ($m > 0.05$):** Los eventos futuros apuntan a un mayor tamaño de audiencia.  
  *Ejemplo:* `Tendencia CRECIENTE (+17.7 cupos/día). Línea roja punteada.`
* **Tendencia Decreciente ($m < -0.05$):** Los eventos más cercanos concentran los mayores aforos y las fechas lejanas proyectan actividades más reducidas.  
  *Ejemplo:* `Tendencia DECRECIENTE (-12.0 cupos/día). Línea roja punteada.`
* **Tendencia Constante / Estable ($-0.05 \le m \le 0.05$):** Los aforos se mantienen homogéneos en el tiempo.  
  *Ejemplo:* `Tendencia CONSTANTE (estable). Línea roja punteada.`

> [!NOTE]
> **Condición matemática mínima:** Para calcular una recta de regresión lineal se requieren al menos **2 eventos en fechas distintas** ($N \ge 2$ y $x_{\max} > x_{\min}$). Si solo existe 1 evento registrado, el denominador de la fórmula se anula ($0/0$), por lo que el sistema oculta la recta de manera segura e invita al usuario a registrar más fechas.

### Implementación técnica en JavaFX
Debido a que el control `ScatterChart` de JavaFX no dibuja líneas de unión entre puntos, se diseñó la clase interna **`TrendScatterChart`**:
1. Hereda de `ScatterChart<Number, Number>`.
2. Añade un nodo `javafx.scene.shape.Line` con estilo punteado (`strokeDashArray 6.0, 4.0`) y color carmesí (`#E11D48`) dentro de `getPlotChildren()`.
3. Sobrescribe el ciclo de diseño `layoutPlotChildren()` para recalcular en tiempo real las coordenadas píxel (`getXAxis().getDisplayPosition()` y `getYAxis().getDisplayPosition()`), asegurando que la línea se mantenga perfectamente calibrada incluso al redimensionar la ventana o cambiar la resolución.

---

## Novedades y Mejoras Recientes

- **Sincronización Dinámica de Espacios en Eventos**:
  - El selector (`ComboBox<Venue>`) de espacios en el formulario de creación de eventos consulta en tiempo real `venueRepository.findAll()`, garantizando que cualquier espacio recién creado aparezca al instante sin requerir reiniciar la aplicación.
  - Validación preventiva: Si no existen espacios en la base de datos, el sistema muestra una ventana emergente de aviso impidiendo inconsistencias relacionales.
- **Optimización del Listado de Próximos Eventos**:
  - Eliminación de límites fijos (`.limit(3)`), permitiendo listar la totalidad de los eventos venideros.
  - Integración de un `ScrollPane` con barra de desplazamiento vertical nativa y fondo transparente.
  - Ajuste de contraste tipográfico (`-fx-text-fill: #111827`) para evitar que el texto desaparezca en temas de fondo claro.
- **Consistencia de Claves en MongoDB**:
  - Corrección de asignaciones de identificadores para que MongoDB asigne siempre un `ObjectId` único a cada documento nuevo (`events.venueId` vs `events.eventId`), evitando colisiones por clave duplicada (`E11000`).

---

## Arquitectura del Proyecto

El proyecto implementa un patrón arquitectónico en capas desacopladas (**Layered Architecture / MVC**):

- **`entities`**: Clases de dominio POJO anotadas con `@ObjectId` e `@Id` para el mapeo objeto-documento BSON con **MongoJack**.
- **`repository`**: Capa de persistencia directa contra MongoDB usando `JacksonMongoCollection`.
- **`service`**: Capa lógica encargada de validaciones de reglas de negocio, formatos de hora y control de excepciones.
- **`model`**: Propiedades observables JavaFX (`ListProperty`, `ObjectProperty`) para enlace de datos bidireccional reactivo.
- **`controller`**: Orquestadores de flujo entre repositorios, servicios y vistas.
- **`view`**: Interfaz de usuario estructurada en clases `Builder<Region>` con maquetación en código Java puro y hojas de estilo CSS (`styles.css`).
- **`FXApplication.java`**: Inicialización del ciclo de vida JavaFX, conexión MongoDB y menú de navegación lateral.

---

## Estructura del Proyecto

```text
university/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── README.md
├── src/
│   └── main/
│       ├── java/dev/integrador/university/
│       │   ├── FXApplication.java
│       │   ├── entities/
│       │   │   ├── Attendee.java
│       │   │   ├── Event.java
│       │   │   ├── Registration.java
│       │   │   └── Venue.java
│       │   ├── model/
│       │   │   ├── AttendeeModel.java
│       │   │   ├── EventModel.java
│       │   │   ├── RegistrationModel.java
│       │   │   └── VenueModel.java
│       │   ├── repository/
│       │   │   ├── AttendeeRepository.java
│       │   │   ├── EventRepository.java
│       │   │   ├── RegistrationRepository.java
│       │   │   └── VenueRepository.java
│       │   ├── service/
│       │   │   ├── AttendeeService.java
│       │   │   ├── EventService.java
│       │   │   ├── RegistrationService.java
│       │   │   └── VenueService.java
│       │   ├── controller/
│       │   │   ├── AttendeeController.java
│       │   │   ├── EventController.java
│       │   │   ├── RegistrationController.java
│       │   │   └── VenueController.java
│       │   └── view/
│       │       ├── DashboardView.java
│       │       ├── attendee/
│       │       │   ├── AttendeeView.java
│       │       │   ├── NewAttendeeFormView.java
│       │       │   └── UpdateAttendeeFormView.java
│       │       ├── event/
│       │       │   ├── EventView.java
│       │       │   ├── NewEventFormView.java
│       │       │   └── UpdateEventFormView.java
│       │       ├── registration/
│       │       │   ├── RegistrationView.java
│       │       │   ├── NewRegistrationFormView.java
│       │       │   └── UpdateRegistrationFormView.java
│       │       └── venue/
│       │           ├── VenueView.java
│       │           ├── NewVenueFormView.java
│       │           └── UpdateVenueFormView.java
│       └── resources/
│           ├── css/
│           │   └── styles.css
│           └── img/
│               ├── attendeesGroup.png
│               ├── eventsCalendar.png
│               ├── home.png
│               ├── registrationsList.png
│               └── venueSpace.png
```

---

## Requisitos Previos

- **JDK 21** (Java Development Kit 21 o superior, preferiblemente Eclipse Adoptium Temurin o OpenJDK).
- **MongoDB Server** (v5.0 o superior en ejecución local en el puerto `27017` o clúster en MongoDB Atlas).
- **Gradle** (gestionado automáticamente mediante el wrapper `./gradlew`).
- **IntelliJ IDEA** (recomendado para desarrollo y ejecución).

---

## Configuración de la Base de Datos (MongoDB)

### Iniciar MongoDB localmente
El sistema espera por defecto una instancia local:
```text
mongodb://127.0.0.1:27017
```

- **En Windows (Servicio)**:
  Asegúrate de que el servicio esté corriendo desde `services.msc` o ejecutando en PowerShell:
  ```powershell
  Start-Service MongoDB
  ```
- **Con Docker**:
  ```bash
  docker run -d --name mongo-university -p 27017:27017 mongo:7.0
  ```

### Variable de Entorno `MONGODB_URI` (Opcional)
Si necesitas conectarte a una base de datos remota o con credenciales:
- **PowerShell (Windows)**:
  ```powershell
  $env:MONGODB_URI="mongodb+srv://usuario:password@cluster.mongodb.net/universitydb"
  ```
- **Bash (Linux/macOS)**:
  ```bash
  export MONGODB_URI="mongodb+srv://usuario:password@cluster.mongodb.net/universitydb"
  ```

---

## Modelo de Datos NoSQL

Base de datos: **`universitydb`**

| Colección | Descripción | Campos Clave |
| :--- | :--- | :--- |
| **`venues`** | Espacios y auditorios universitarios | `_id`, `name`, `type`, `capacity` |
| **`events`** | Eventos académicos y conferencias | `_id`, `name`, `description`, `category`, `date`, `startTime`, `endTime`, `capacity`, `venueId` |
| **`attendees`** | Estudiantes, docentes y participantes | `_id`, `name`, `documentNumber`, `email` |
| **`registrations`** | Inscripciones de participantes a eventos | `_id`, `attendeeId`, `eventId`, `registrationDate`, `status` |

---

## Cómo Ejecutar el Proyecto

### Opción 1: Desde IntelliJ IDEA (Recomendado)
1. Abre la carpeta del proyecto en IntelliJ IDEA.
2. Verifica en **File → Project Structure → Project** que el SDK seleccionado sea **Java 21**.
3. En la barra lateral derecha, haz clic en **Gradle → Tasks → application → run**.
4. O abre [FXApplication.java](src/main/java/dev/integrador/university/FXApplication.java) y presiona el botón verde de **Play** junto a `public static void main(String[] args)`.

### Opción 2: Desde la Terminal
```powershell
# En Windows
.\gradlew run

# En Linux / macOS
./gradlew run
```

---

## Solución de Problemas Frecuentes

1. **"Error fatal - Conexión fallida a la base de datos"**:
   - MongoDB no está activo en `127.0.0.1:27017`. Inicia el servicio local antes de arrancar la aplicación.
2. **"No se visualiza la línea de tendencia en la gráfica"**:
   - Recuerda que la regresión matemática requiere registrar al menos **2 eventos en fechas distintas** para calcular la pendiente $m$.
3. **"E11000 duplicate key error"**:
   - Ocurre si se intenta forzar un ID existente en MongoDB. El sistema ya está parametrizado para generar ObjectIds automáticos al crear documentos nuevos.

---

## Tecnologías Utilizadas

- **Lenguaje**: Java 21
- **UI Framework**: JavaFX 21 (módulos `javafx.controls`)
- **Base de Datos NoSQL**: MongoDB
- **Mapeo Objeto-Documento**: MongoJack 5.1.0 + Jackson Databind
- **Driver de Conexión**: MongoDB Driver Sync 5.11.1
- **Motor de Compilación**: Gradle (Kotlin DSL)
- **Registro de Eventos (Logging)**: SLF4J (NOP provider)
