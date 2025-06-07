# proyectoFinalP319988

# Sistema de Gestión de Tareas

Este proyecto es un sistema de gestión de tareas desarrollado con Java, Spring Boot, y una arquitectura multidependencias. Permite crear, actualizar, eliminar, y deshacer tareas a través de una API REST, integrando estructuras de datos personalizadas, mensajería asíncrona con RabbitMQ, y persistencia en MySQL. El sistema está diseñado para ser escalable, modular, y fácil de mantener.

## Descripción Técnica

El proyecto está organizado en un módulo padre (`task-parent`) con cuatro submódulos:

- **task-core**: Implementa estructuras de datos (árbol binario, cola, pila) para soportar búsquedas, procesamiento ordenado, y deshacer acciones.
- **task-api**: Proporciona una API REST con endpoints para gestionar tareas, documentada con Swagger.
- **task-messaging**: Gestiona la mensajería asíncrona con RabbitMQ, usando un producer y un consumer para registrar acciones en el historial.
- **task-repository**: Define repositorios JPA para interactuar con la base de datos MySQL, manejando las tablas `tareas`, `historial`, y `usuarios`.

### Tecnologías Utilizadas

- **Lenguaje**: Java 17
- **Framework**: Spring Boot 3.x
- **Gestión de Dependencias**: Maven
- **Base de Datos**: MySQL
- **Mensajería**: RabbitMQ
- **Documentación API**: Swagger (OpenAPI 3)
- **Contenedores**: Docker (para MySQL y RabbitMQ)

### Arquitectura

El sistema sigue una arquitectura modular y desacoplada:
- **API REST**: Los usuarios interactúan vía endpoints como `POST /api/tareas`, `PUT /api/tareas/{id}`, `DELETE /api/tareas/{id}`, y `POST /api/tareas/Deshacer`.
- **Mensajería**: Cada acción (crear, actualizar, eliminar) genera un mensaje en la cola `historial.queue` de RabbitMQ, procesado por un consumidor para guardar en `historial`.
- **Persistencia**: Las tareas se almacenan en la tabla `tareas`, y las acciones en `historial`, asociadas a un usuario registrado.
- **Deshacer**: Solo las creaciones de tareas son deshacibles, usando una pila en `task-core`. Las eliminaciones se registran en `historial` pero no son reversibles.

## Estructuras de Datos Usadas

El módulo `task-core` implementa tres estructuras de datos personalizadas en el paquete `com.example.task.core.datastructure`:

1. **Árbol Binario (`Arbol.java`)**:
   - **Descripción**: Árbol binario de búsqueda que organiza tareas por su ID. Cada nodo contiene una `Tarea` y referencias a subárboles izquierdo (IDs menores) y derecho (IDs mayores).
   - **Métodos Principales**:
     - `insertar(Tarea tarea)`: Inserta una tarea recursivamente.
     - `buscar(Long id)`: Busca una tarea por ID de forma eficiente.
   - **Uso**: Permite búsquedas rápidas de tareas por ID, útil para verificar existencia antes de actualizaciones.

2. **Cola (`Cola.java`)**:
   - **Descripción**: Cola enlazada que almacena objetos `Historial`, representando acciones (crear, actualizar, eliminar). Usa nodos con un dato y un enlace al siguiente.
   - **Métodos Principales**:
     - `enqueue(Historial item)`: Añade una acción al final.
     - `dequeue()`: Extrae la acción del frente.
     - `isEmpty()`: Verifica si la cola está vacía.
   - **Uso**: Simula el procesamiento ordenado de acciones, reflejando el comportamiento de RabbitMQ en simulaciones.

3. **Pila (`Pila.java`)**:
   - **Descripción**: Pila dinámica basada en un arreglo que almacena objetos `Historial`. Crece automáticamente al llenarse (duplica capacidad).
   - **Métodos Principales**:
     - `push(Historial item)`: Apila una acción.
     - `pop()`: Desapila la última acción.
     - `isEmpty()`: Verifica si la pila está vacía.
     - `size()`: Devuelve el número de elementos.
   - **Uso**: Soporta la funcionalidad de deshacer creaciones de tareas en el endpoint `POST /api/tareas/Deshacer`.

## Requisitos Previos

- **Java 17**: Instalado y configurado (`JAVA_HOME`).
- **Maven 3.8+**: Para gestionar dependencias y compilar.
- **Docker**: Para ejecutar MySQL y RabbitMQ.
- **Git**: Para clonar el repositorio.
- **IDE (opcional)**: Eclipse, IntelliJ, o VS Code para desarrollo.

## Cómo Ejecutar el Sistema

Sigue estos pasos para configurar y ejecutar el sistema en tu máquina local.

### 1. Clonar el Repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd task-parent
```

### 2. Configurar MySQL y RabbitMQ con Docker

1. Asegúrate de que Docker esté corriendo.
2. Usa el siguiente `docker-compose.yml` para iniciar MySQL y RabbitMQ:

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: taskdb
      MYSQL_USER: taskuser
      MYSQL_PASSWORD: taskpass
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
volumes:
  mysql-data:
```

3. Guarda el archivo en la raíz del proyecto (`task-parent`).
4. Ejecuta:

```bash
docker-compose up -d
```

5. Verifica que los servicios estén corriendo:
   - MySQL: Conéctate con un cliente (por ejemplo, MySQL Workbench) a `localhost:3306`, usuario `taskuser`, contraseña `taskpass`.
   - RabbitMQ: Abre `http://localhost:15672` (usuario: `guest`, contraseña: `guest`).

### 3. Compilar el Proyecto

1. Navega a la raíz del proyecto:

```bash
cd task-parent
```

2. Compila e instala las dependencias:

```bash
mvn clean install
```

### 4. Ejecutar el Módulo `task-api`

1. Navega al submódulo `task-api`:

```bash
cd task-api
```

2. Inicia la aplicación:

```bash
mvn spring-boot:run > logs-api.txt
```

3. Verifica que la API esté corriendo en `http://localhost:8080/swagger-ui.html`.

### 5. (Opcional) Ejecutar el Módulo `task-messaging`

1. Abre otra terminal y navega a `task-messaging`:

```bash
cd task-parent/task-messaging
```

2. Inicia el consumidor:

```bash
mvn spring-boot:run > logs-messaging.txt
```

3. Verifica en RabbitMQ (`http://localhost:15672`) que `historial.queue` tenga un consumidor activo.

### 6. Probar la API

1. Abre `http://localhost:8080/swagger-ui.html`.
2. Prueba los endpoints:
   - `POST /api/tareas`: Crea una tarea (por ejemplo, `{"descripcion": "Tarea 1", "usuario_id": 1, "completada": false}`).
   - `PUT /api/tareas/{id}`: Actualiza una tarea.
   - `DELETE /api/tareas/{id}`: Elimina una tarea (no deshacible).
   - `POST /api/tareas/Deshacer`: Deshace la última creación.
3. Verifica en MySQL:
   ```sql
   docker exec -it task-parent-mysql-1 mysql -utaskuser -ptaskpass -Dtaskdb
   SELECT * FROM tareas;
   SELECT * FROM historial;
   ```
4. Verifica en RabbitMQ (`http://localhost:15672`):
   - Cola `historial.queue` debería estar vacía (`Ready: 0`) con el consumidor activo.
   - Durante simulaciones (consumidor desactivado), acumula mensajes (`Ready: N`).

### 7. Simulación de Cola (Opcional)

Para simular acumulación de mensajes:
1. Comenta `@RabbitListener` en `task-messaging/src/main/java/com/example/task/messaging/consumer/HistorialConsumer.java`.
2. Reconstruye el proyecto (`mvn clean install`).
3. Crea tareas con `POST /api/tareas`.
4. Verifica en RabbitMQ que `historial.queue` acumule mensajes (`Ready: N`).
5. Usa `POST /api/tareas/Deshacer` para deshacer creaciones.
6. Reactiva `@RabbitListener`, reconstruye, y verifica que los mensajes se procesen (`Ready: 0`, registros en `historial`).

## Notas Adicionales

- **Historial**: Todas las acciones (crear, actualizar, eliminar) se registran en la tabla `historial` para auditoría.
- **Deshacer**: Solo las creaciones son deshacibles. Las eliminaciones se registran pero no son reversibles.
- **Base de Datos**: Asegúrate de que la tabla `usuarios` tenga al menos un registro (`id=1`) para pruebas.
- **Errores**: Consulta `logs-api.txt` y `logs-messaging.txt` si encuentras problemas.

## Contribuir

1. Clona el repositorio.
2. Crea una rama para tu funcionalidad (`git checkout -b feature/nueva-funcionalidad`).
3. Realiza tus cambios y haz commit (`git commit -m "Añadir nueva funcionalidad"`).
4. Sube la rama (`git push origin feature/nueva-funcionalidad`).
5. Abre un pull request.


## Contacto

Para dudas o sugerencias, contacta al equipo en [enijl1@miumg.edu.gt].

---
Desarrollado por Eddin Fernando Nij  - Proyecto Final, 2025
