```
División Ciencias de la Ingeniería
Ingeniería en Ciencias y Sistemas
Manejo e Implementación de Archivos
```
# Sistema de gestión de Clínica Médica

## Objetivo General

Desarrollar un sistema de gestión para una clínica médica mediante Java Swing, que
permita registrar y administrar pacientes, médicos y citas médicas, implementando diferentes
estrategias de almacenamiento en archivos para garantizar la persistencia, integridad y
recuperación de la información entre las distintas ejecuciones de la aplicación.

## Objetivos Específicos

1. Implementar mecanismos de persistencia de datos utilizando archivos, permitiendo
    almacenar y recuperar la información de pacientes, médicos y citas médicas entre
    distintas ejecuciones de la aplicación.
2. Aplicar operaciones fundamentales sobre archivos, tales como inserción, búsqueda,
    modificación y eliminación de registros, garantizando la integridad y consistencia de
    la información almacenada.
3. Desarrollar estructuras de datos y relaciones entre entidades que permitan gestionar de
    forma eficiente la información de pacientes, médicos y citas médicas mediante
    programación orientada a objetos.
4. Desarrollar consultas y reportes que permitan recuperar información almacenada en
    los archivos mediante diferentes criterios de búsqueda y filtrado.
5. Aplicar buenas prácticas de programación, incluyendo el manejo de excepciones y la
    separación de responsabilidades, para desarrollar una aplicación robusta y mantenible.


```
División Ciencias de la Ingeniería
Ingeniería en Ciencias y Sistemas
Manejo e Implementación de Archivos
```
## Descripción

Una clínica médica privada ha iniciado un proceso de modernización de sus
operaciones con el objetivo de reemplazar los registros físicos por un sistema informático que
facilite la administración de la información clínica. Para ello, la empresa lo ha contratado
como desarrollador de software con la finalidad de diseñar e implementar una aplicación de
escritorio que permita gestionar de forma eficiente la información de pacientes, médicos y
citas médicas.
El sistema deberá permitir registrar, consultar, modificar y eliminar información de las
diferentes entidades que conforman la clínica, garantizando en todo momento la integridad y
consistencia de los datos. De igual manera, deberá validar las relaciones existentes entre
pacientes, médicos y citas para evitar inconsistencias durante la operación del sistema.
Como parte de los requerimientos del proyecto, toda la información deberá persistir
entre las distintas ejecuciones de la aplicación mediante el uso de archivos, administrando de
forma independiente la información correspondiente a cada entidad.

### Módulo de Pacientes

El módulo de pacientes permitirá administrar la información de las personas que
reciben atención médica dentro de la clínica.
Cada paciente deberá contener, como mínimo, la siguiente información:
● Número de identificación personal (Debe ser único).
● Nombres y apellidos.
● Fecha de nacimiento.
● Sexo.
● Número de teléfono.
● Correo electrónico.
● Tipo de sangre.


```
División Ciencias de la Ingeniería
Ingeniería en Ciencias y Sistemas
Manejo e Implementación de Archivos
El módulo deberá permitir:
● Registrar nuevos pacientes.
● Consultar el listado completo de pacientes.
● Buscar pacientes por número de identificación, nombre o apellido.
● Modificar la información de un paciente existente.
● Eliminar pacientes.
Requisitos del paciente:
● El número de identificación no podrá repetirse.
● Los nombres y apellidos serán obligatorios.
● El correo electrónico es opcional.
```
### Módulo de Médicos

El módulo de médicos permitirá administrar la información de los profesionales
encargados de atender a los pacientes.
Cada médico deberá contener, como mínimo, la siguiente información:
● Identificador único de tipo UUID (generado automáticamente).
● Nombres y apellidos.
● Especialidad.
● Número de teléfono.
● Correo electrónico.
● Horario de inicio de atención.
● Horario de finalización de atención.
● Estado del médico: activo o inactivo.
El módulo deberá permitir:
● Registrar nuevos médicos.
● Consultar el listado completo de médicos.


```
División Ciencias de la Ingeniería
Ingeniería en Ciencias y Sistemas
Manejo e Implementación de Archivos
● Buscar médicos por UUID, nombre, apellido o especialidad.
● Modificar la información de un médico.
● Activar o desactivar médicos.
● Consultar médicos activos.
● Consultar médicos inactivos.
● Consultar médicos por especialidad.
Requisitos para los médicos:
● El UUID deberá ser único.
● Los nombres, apellidos y especialidad serán obligatorios.
● El correo electrónico es opcional.
● La modificación del horario de atención no deberá generar inconsistencias con
citas previamente programadas.
```
### Módulo de Citas

El módulo de citas permitirá programar, consultar y administrar las atenciones
médicas realizadas en la clínica.
Cada cita deberá contener, como mínimo, la siguiente información:
● Identificador único de tipo UUID, generado automáticamente.
● Número de identificación del paciente.
● UUID del médico.
● Fecha de la cita.
● Hora de inicio.
● Motivo de la consulta.
● Estado de la cita.
● Observaciones.


```
División Ciencias de la Ingeniería
Ingeniería en Ciencias y Sistemas
Manejo e Implementación de Archivos
Los estados permitidos para una cita serán:
● Programada.
● Atendida.
● Cancelada.
El módulo deberá permitir:
● Programar nuevas citas.
● Consultar el listado completo de citas.
● Buscar citas por UUID.
● Consultar citas por paciente.
● Consultar citas por médico.
● Consultar citas por fecha.
● Consultar citas por estado.
● Cancelar una cita.
● Marcar una cita como atendida.
● Modificar el motivo u observaciones de una cita.
● Eliminar una cita.
```
### Módulo de Reportes

El módulo de reportes permitirá consultar y presentar la información almacenada en el
sistema mediante filtros y criterios de búsqueda. Los reportes deberán mostrarse dentro de la
aplicación mediante tablas de Java Swing. También podrá solicitarse su exportación a
archivos de texto o CSV (Al menos tres de los reportes deberán poder exportarse a archivos
de texto o CSV).
El sistema deberá generar los siguientes reportes:

#### Reportes de pacientes

```
● Reporte completo de pacientes.
● Reporte de pacientes por tipo de sangre.
● Reporte de pacientes con mayor cantidad de citas.
● Reporte de pacientes que nunca han tenido una cita.
```

```
División Ciencias de la Ingeniería
Ingeniería en Ciencias y Sistemas
Manejo e Implementación de Archivos
```
#### Reportes de médicos

```
● Reporte completo de médicos.
● Reporte de médicos por especialidad.
● Reporte de médicos con mayor cantidad de citas.
● Reporte de médicos con citas programadas para una fecha específica.
```
#### Reportes de citas

```
● Reporte completo de citas.
● Reporte de citas por rango de fechas.
● Reporte de citas por médico.
● Reporte de citas por paciente.
● Reporte de citas por estado.
● Reporte de cantidad de citas por especialidad.
```
#### Reportes de logs

```
● Se debe presentar un reporte de logs con todas las interacciones realizadas por
los usuarios en cada uno de los módulos (Creación, actualización, eliminación,
etc).
```

```
División Ciencias de la Ingeniería
Ingeniería en Ciencias y Sistemas
Manejo e Implementación de Archivos
```
## Importante

```
● El lenguaje de programación a utilizar será Java (versión 21) utilizando Java Swing
para la interfaz gráfica, utilizando RandomAccessFile para manejo de archivos.
● Como mínimo el estudiante deberá tener implementados los siguientes módulos:
Módulo de médicos y módulo de citas. En caso de no tenerlos implementados, se
omitirá la calificación del estudiante.
● La persistencia de la información deberá realizarse mediante archivos. No está
permitido el uso de sistemas gestores de bases de datos (MySQL, PostgreSQL,
SQLite, MongoDB, entre otros).
● Todas las operaciones sobre los archivos deberán realizarse mediante código
desarrollado por el estudiante. No está permitido utilizar librerías externas destinadas
a abstraer el manejo de archivos.
● El código deberá ser claro, legible y documentado cuando sea necesario. Las malas
prácticas de programación serán penalizadas.
● Las copias totales o parciales entre estudiantes serán sancionadas con una calificación
de 0 puntos y se notificará a las autoridades pertinentes.
● El uso de herramientas de inteligencia artificial para generar parcial o totalmente la
solución de la práctica no está permitido. El estudiante deberá ser capaz de explicar y
defender el funcionamiento de la aplicación durante la calificación.
● La práctica constituye un requisito para tener derecho a realizar el proyecto siguiente
del curso. Además, deberá obtener una calificación mayor a 0 puntos para cumplir
con dicho requisito.
● La calificación se realizará preferentemente en la computadora del revisor. En caso de
no ser posible, podrá realizarse en la computadora del estudiante.
● Durante la revisión, el revisor podrá solicitar la explicación del funcionamiento de
cualquier parte del código, la defensa de conceptos relacionados con la
implementación, la realización de modificaciones sobre la aplicación o la
implementación de pequeñas funcionalidades adicionales relacionadas con los
requisitos de la práctica. La incapacidad para explicar el funcionamiento del código,
justificar las decisiones de diseño o realizar las modificaciones solicitadas podrá dar
```

División Ciencias de la Ingeniería
Ingeniería en Ciencias y Sistemas
Manejo e Implementación de Archivos
lugar a una penalización en la calificación, cuya magnitud dependerá de la
complejidad del requerimiento y del nivel de dominio demostrado por el estudiante.


```
División Ciencias de la Ingeniería
Ingeniería en Ciencias y Sistemas
Manejo e Implementación de Archivos
```
## Entrega

La fecha límite de entrega será el 25 de agosto de 2026 a las 16:00. La práctica deberá
entregarse mediante un repositorio privado de Git, creado y administrado a través de GitHub.
El repositorio deberá contener los siguientes componentes:
● Código fuente completo del proyecto.
● Documentación técnica (Diagramas UML, mínimo: diagrama de clases,
diagrama de secuencias y casos de uso).
● Manual de usuario.
● Archivo ejecutable en formato JAR.
El repositorio deberá permanecer privado y deberá incluir como colaboradores al
auxiliar y al docente encargados de la calificación. Es responsabilidad del estudiante verificar
que ambos usuarios tengan acceso antes del cierre de la entrega.
No se aceptarán entregas enviadas por correo electrónico, servicios de
almacenamiento externo u otros medios distintos a GitHub. Tampoco se tomarán en cuenta
cambios, archivos o confirmaciones realizadas después de la fecha y hora límite.

## Calificación

```
Pendiente de definir
```

