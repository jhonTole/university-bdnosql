# Sistema de Gestión Universitaria de Eventos (University BD-NoSQL)

Sistema de escritorio desarrollado en **JavaFX 21** con **Gradle** y **MongoDB**, diseñado para la administración integral de eventos universitarios, control de asistencia, inscripción de participantes y gestión de espacios físicos en la **Universidad Santo Tomás**.

---

## Tabla de Contenidos
- [Características Principales](#-características-principales)
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Requisitos Previos](#-requisitos-previos)
- [Configuración de la Base de Datos (MongoDB)](#-configuración-de-la-base-de-datos-mongodb)
  - [1. Iniciar MongoDB](#1-iniciar-mongodb)
  - [2. Cargar Datos de Inicialización (Seed Data)](#2-cargar-datos-de-inicialización-seed-data)
  - [3. Variable de Entorno MONGODB_URI (Opcional)](#3-variable-de-entorno-mongodb_uri-opcional)
- [Modelo de Datos NoSQL](#-modelo-de-datos-nosql)
- [Cómo Ejecutar el Proyecto](#-cómo-ejecutar-el-proyecto)
  - [Opción A: Desde IntelliJ IDEA](#opción-a-desde-intellij-idea-recomendado)
  - [Opción B: Desde la Terminal (Gradle Wrapper)](#opción-b-desde-la-terminal-gradle-wrapper)
- [Solución de Problemas Frecuentes](#-solución-de-problemas-frecuentes)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)

---

## Características Principales

1. **Gestión de Eventos (`Eventos`)**:
  - Registro, edición y consulta de eventos institucionales.
  - Control de capacidad, fechas, horas de inicio y fin, categorías y asignación a un espacio físico específico.

2. **Gestión de Asistentes (`Asistentes`)**:
  - Registro de participantes con validación de nombre, documento de identidad y correo institucional (`@ustatunja.edu.co`).
  - Mantenimiento y actualización de datos de contacto.

3. **Inscripciones (`Inscripciones`)**:
  - Vinculación de asistentes a eventos.
  - Registro de fecha de inscripción y estados (`Activa`, `Cancelada`, etc.).

4. **Gestión de Espacios (`Espacios / Venues`)**:
  - Administración de auditorios, salas y aulas de la universidad.
  - Definición de capacidades máximas y tipos de espacio físico.

---

## Arquitectura del Proyecto

El proyecto implementa un patrón arquitectónico en capas desacopladas (**Layered Architecture / MVC**):

- **`entities`**: Clases de dominio mapeadas a documentos BSON de MongoDB mediante **MongoJack**.
- **`repository`**: Capa de persistencia y acceso a datos utilizando colecciones tipadas (`JacksonMongoCollection`).
- **`service`**: Capa de lógica de negocio y reglas de validación.
- **`controller`**: Coordinadores entre la interfaz gráfica y los servicios/repositorios.
- **`view`**: Vistas construidas con componentes JavaFX (`TableView`, `TabPane`, formularios modales, estilos CSS).
- **`FXApplication.java`**: Punto de entrada de la aplicación, configuración del cliente MongoDB y ensamblado de pestañas.

---

## Estructura del Proyecto

```text
univercidad/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── .gitignore
├── README.md
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── mongo-init/
│   └── init.js
└── src/
    └── main/
        ├── java/dev/integrador/university/
        │   ├── FXApplication.java
        │   ├── entities/
        │   │   ├── Attendee.java
        │   │   ├── Event.java
        │   │   ├── Registration.java
        │   │   └── Venue.java
        │   ├── model/
        │   │   ├── AttendeeModel.java
        │   │   ├── EventModel.java
        │   │   ├── RegistrationModel.java
        │   │   └── VenueModel.java
        │   ├── repository/
        │   │   ├── AttendeeRepository.java
        │   │   ├── EventRepository.java
        │   │   ├── RegistrationRepository.java
        │   │   └── VenueRepository.java
        │   ├── service/
        │   │   ├── AttendeeService.java
        │   │   ├── EventService.java
        │   │   ├── RegistrationService.java
        │   │   └── VenueService.java
        │   ├── controller/
        │   │   ├── AttendeeController.java
        │   │   ├── EventController.java
        │   │   ├── RegistrationController.java
        │   │   └── VenueController.java
        │   └── view/
        │       ├── DashboardView.java
        │       ├── attendee/
        │       │   ├── AttendeeView.java
        │       │   ├── NewAttendeeFormView.java
        │       │   └── UpdateAttendeeFormView.java
        │       ├── event/
        │       │   ├── EventView.java
        │       │   ├── NewEventFormView.java
        │       │   └── UpdateEventFormView.java
        │       ├── registration/
        │       │   ├── RegistrationView.java
        │       │   ├── NewRegistrationFormView.java
        │       │   └── UpdateRegistrationFormView.java
        │       └── venue/
        │           ├── VenueView.java
        │           ├── NewVenueFormView.java
        │           └── UpdateVenueFormView.java
        └── resources/
            ├── css/
            │   └── styles.css
            └── img/
                ├── attendeesGroup.png
                ├── eventsCalendar.png
                ├── registrationsList.png
                └── venueSpace.png

```

---

## Requisitos Previos

Asegúrate de contar con las siguientes herramientas instaladas:

- **JDK 21** Java Development Kit 21 o superior.
- **MongoDB Server** v5.0 o superior o Docker con la imagen de MongoDB.
- **MongoDB Compass** o **Mongosh**.
- **IntelliJ IDEA** (Community o Ultimate Edition).

---

## Configuración de la Base de Datos (MongoDB)

### 1. Iniciar MongoDB

La aplicación espera conectarse a una instancia local de MongoDB en el puerto estándar:
```text
mongodb://127.0.0.1:27017
```

- **En Windows (Servicio local)**:
  Verifica que el servicio esté activo desde la consola de servicios (`services.msc`) o ejecutando en PowerShell como Administrador:
  ```powershell
  Start-Service MongoDB
  ```

- **Vía Docker**:
  Si prefieres usar Docker:
  ```bash
  docker run -d --name mongodb-university -p 27017:27017 mongo:7.0
  ```

---

### 2. Cargar Datos de Inicialización (Seed Data)

El proyecto incluye el script `mongo-init/init.js` para crear la base de datos `universitydb` e insertar registros de referencia vinculados entre sí.

#### Opción 1: Mediante `mongosh` (Terminal)
Desde la carpeta del proyecto:
```bash
mongosh "mongodb://127.0.0.1:27017/universitydb" mongo-init/init.js
```

#### Opción 2: Mediante MongoDB Compass
1. Conéctate a `mongodb://127.0.0.1:27017`.
2. Abre la base de datos `universitydb`.
3. Abre el script `mongo-init/init.js` en un editor de texto, copia su contenido y pégalo en la pestaña **`_MONGOSH`** integrada en la parte inferior de Compass, luego presiona `Enter`.

---

### 3. Variable de Entorno `MONGODB_URI` (Opcional)

Por defecto, la aplicación utiliza:
```java
//mongodb://127.0.0.1:27017/?serverSelectionTimeoutMS=3000
```
Si tu servidor MongoDB tiene credenciales, usa un puerto distinto o estás usando MongoDB Atlas en la nube, puedes definir la variable de entorno del sistema antes de ejecutar:

- **En Windows (PowerShell)**:
  ```powershell
  $env:MONGODB_URI="mongodb://usuario:password@localhost:27017/?serverSelectionTimeoutMS=3000"
 ```
- **En Linux / macOS (Bash)**:
  ```bash
  export MONGODB_URI="mongodb://usuario:password@localhost:27017/?serverSelectionTimeoutMS=3000"
  ```

---

## Modelo de Datos NoSQL

Base de datos: **`universitydb`**

| Colección | Descripción | Campos Clave |
| :--- | :--- | :--- |
| **`venues`** | Espacios y auditorios universitarios | `_id`, `name`, `type`, `capacity` |
| **`events`** | Eventos académicos y conferencias | `_id`, `name`, `description`, `category`, `date`, `startTime`, `endTime`, `capacity`, `venueId` |
| **`attendees`** | Estudiantes, docentes y participantes | `_id`, `name`, `documentNumber`, `email` |
| **`registrations`** | Inscripciones activas de participantes a eventos | `_id`, `attendeeId`, `eventId`, `registrationDate`, `status` |

---

## Cómo Ejecutar el Proyecto

### Desde IntelliJ IDEA

1. Abre **IntelliJ IDEA**.
2. Abre el proyecto.
3. Asegúrate de configurar el JDK:
  - Ve a **File** → **Project Structure** → **Project**.
  - En **SDK**, selecciona **Java 21**.
4. Sincroniza las dependencias con Gradle:
  - Abre la pestaña lateral de **Gradle** (en el margen derecho).
  - Haz clic en el botón de recarga (**Reload All Gradle Projects**).
5. Ejecuta la aplicación:
  - En la pestaña de Gradle, navega a:
    `university-bdnosql-main` → **Tasks** → **application** → haz doble clic en **`run`**.
  - O bien, abre el archivo `FXApplication.java` (`src/main/java/dev/integrador/university/FXApplication.java`) y presiona el ícono verde de **Play** junto a `public static void main(String[] args)`.

---

##  Solución de Problemas Frecuentes

### 1. Cuadro de diálogo emergente: *"Error fatal - Conexión fallida a la base de datos"*
- **Causa**: MongoDB no está iniciado o no responde en `127.0.0.1:27017` dentro de los 3 segundos de timeout configurados.
- **Solución**:
  1. Verifica que MongoDB esté activo (`mongosh` o revisando el servicio de Windows / contenedor Docker).
  2. Si usas MongoDB Atlas o credenciales, define la variable de entorno `MONGODB_URI`.

### 2. Error: *Java version mismatch / Unsupported class file version*
- **Causa**: El proyecto está compilado para Java 21 (`languageVersion.set(JavaLanguageVersion.of(21))`), pero tu entorno tiene configurada una versión anterior (ej. Java 17 o Java 8).
- **Solución**:
  - En IntelliJ: **Settings / Preferences** → **Build, Execution, Deployment** → **Build Tools** → **Gradle** → **Gradle JVM**: Selecciona JDK 21.

### 3. Error con librerías gráficas de JavaFX
- Las librerías de JavaFX (`javafx.controls`) son descargadas y gestionadas automáticamente por Gradle a través del plugin oficial `org.openjfx.javafxplugin` versión 0.1.0, por lo que no es necesario instalar JavaFX manualmente en el sistema operativo.

---

## Tecnologías Utilizadas

- **Lenguaje**: Java 21
- **Interfaz de Usuario**: JavaFX 21 (módulos `javafx.controls`)
- **Base de Datos NoSQL**: MongoDB
- **Driver MongoDB**: `org.mongodb:mongodb-driver-sync:5.11.1`
- **Mapeo Objeto-Documento**: `org.mongojack:mongojack:5.1.0`
- **Logging**: `org.slf4j:slf4j-nop:2.0.19`
- **Pruebas**: JUnit 5 (Jupiter Platform)
- **Gestor de Construcción**: Gradle (Kotlin DSL `build.gradle.kts`)
- **Entorno de Desarrollo**: IntelliJ IDEA