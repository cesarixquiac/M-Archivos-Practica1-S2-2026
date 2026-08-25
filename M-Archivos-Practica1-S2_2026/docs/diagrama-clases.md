# Diagrama de Clases - Sistema de Gestión Clínica Médica

```mermaid
classDiagram
    direction LR

    %% ==================== PAQUETE ENTIDADES ====================
    namespace Entidades {
        class Paciente {
            <<entidad>>
            +int TAM_CUI = 13
            +int TAM_NOMBRES = 50
            +int TAM_FECHA_NAC = 10
            +int TAM_SEXO = 10
            +int TAM_TELEFONO = 15
            +int TAM_CORREO = 50
            +int TAM_TIPO_SANGRE = 10
            +int TAM_REGISTRO
            -String cui
            -String nombresApellidos
            -String fechaNacimiento
            -String sexo
            -String telefono
            -String correo
            -String tipoSangre
            -boolean activo
            +Paciente()
            +Paciente(cui, nombresApellidos, fechaNacimiento, sexo, telefono, correo, tipoSangre)
            +getCui() String
            +getNombresApellidos() String
            +getFechaNacimiento() String
            +getSexo() String
            +getTelefono() String
            +getCorreo() String
            +getTipoSangre() String
            +isActivo() boolean
            +setCui(cui) void
            +setNombresApellidos(nombres) void
            +setFechaNacimiento(fecha) void
            +setSexo(sexo) void
            +setTelefono(tel) void
            +setCorreo(correo) void
            +setTipoSangre(tipo) void
            +setActivo(activo) void
            +escribir(RandomAccessFile) void
            +leer(RandomAccessFile) Paciente
            +getTamanioRegistro() int
            +toString() String
        }

        class Medico {
            <<entidad>>
            +int TAM_UUID = 36
            +int TAM_NOMBRES = 50
            +int TAM_ESPECIALIDAD = 50
            +int TAM_TELEFONO = 15
            +int TAM_CORREO = 50
            +int TAM_HORARIO = 5
            +int TAM_REGISTRO
            -String idMedico
            -String nombresApellidos
            -String especialidad
            -String telefono
            -String correo
            -String horarioInicio
            -String horarioFin
            -boolean activo
            +Medico()
            +Medico(nombres, especialidad, telefono, correo, horInicio, horFin)
            +Medico(id, nombres, especialidad, telefono, correo, horInicio, horFin, activo)
            +getIdMedico() String
            +getNombresApellidos() String
            +getEspecialidad() String
            +getTelefono() String
            +getCorreo() String
            +getHorarioInicio() String
            +getHorarioFin() String
            +isActivo() boolean
            +setIdMedico(id) void
            +setNombresApellidos(n) void
            +setEspecialidad(e) void
            +setTelefono(t) void
            +setCorreo(c) void
            +setHorarioInicio(h) void
            +setHorarioFin(h) void
            +setActivo(a) void
            +escribir(RandomAccessFile) void
            +leer(RandomAccessFile) Medico
            +getTamanioRegistro() int
            +toString() String
        }

        class Cita {
            <<entidad>>
            +int TAM_UUID = 36
            +int TAM_CUI_PACIENTE = 13
            +int TAM_FECHA = 10
            +int TAM_HORA = 5
            +int TAM_MOTIVO = 100
            +int TAM_ESTADO = 15
            +int TAM_OBSERVACIONES = 150
            +int TAM_REGISTRO
            +String EST_PROGRAMADA = "Programada"
            +String EST_ATENDIDA = "Atendida"
            +String EST_CANCELADA = "Cancelada"
            -String idCita
            -String cuiPaciente
            -String idMedico
            -String fecha
            -String horaInicio
            -String motivo
            -String estado
            -String observaciones
            -boolean activa
            +Cita()
            +Cita(cuiPaciente, idMedico, fecha, horaInicio, motivo, observaciones)
            +Cita(id, cuiPaciente, idMedico, fecha, horaInicio, motivo, estado, obs, activa)
            +getIdCita() String
            +getCuiPaciente() String
            +getIdMedico() String
            +getFecha() String
            +getHoraInicio() String
            +getMotivo() String
            +getEstado() String
            +getObservaciones() String
            +isActiva() boolean
            +setIdCita(id) void
            +setCuiPaciente(cui) void
            +setIdMedico(id) void
            +setFecha(f) void
            +setHoraInicio(h) void
            +setMotivo(m) void
            +setEstado(e) void
            +setObservaciones(o) void
            +setActiva(a) void
            +marcarAtendida() void
            +cancelar() void
            +reprogramar() void
            +escribir(RandomAccessFile) void
            +leer(RandomAccessFile) Cita
            +getTamanioRegistro() int
            +toString() String
        }
    }

    %% ==================== PAQUETE ARCHIVOS (PERSISTENCIA) ====================
    namespace Archivos {
        class UtilidadesArchivo {
            <<utilidad estática>>
            +escribirString(RandomAccessFile, String, int) void$
            +leerString(RandomAccessFile, int) String$
        }

        class GestorArchivoPaciente {
            <<persistencia>>
            -String NOMBRE_ARCHIVO = "pacientes.dat"
            -RandomAccessFile archivo
            -long tamanioRegistro
            +GestorArchivoPaciente() throws IOException
            +crear(Paciente) boolean
            +buscarPorCui(String) Paciente
            +buscarPorNombre(String) List~Paciente~
            +buscarPorTipoSangre(String) List~Paciente~
            +listarTodos() List~Paciente~
            +listarTodosIncluyendoEliminados() List~Paciente~
            +actualizar(Paciente) boolean
            +eliminarLogico(String) boolean
            +eliminarFisico(String) boolean
            +reactivar(String) boolean
            +existeCui(String) boolean
            +contarActivos() int
            +cerrar() void
        }

        class GestorArchivoMedico {
            <<persistencia>>
            -String NOMBRE_ARCHIVO = "medicos.dat"
            -RandomAccessFile archivo
            -long tamanioRegistro
            +GestorArchivoMedico() throws IOException
            +crear(Medico) boolean
            +buscarPorId(String) Medico
            +buscarPorNombre(String) List~Medico~
            +buscarPorEspecialidad(String) List~Medico~
            +listarActivos() List~Medico~
            +listarInactivos() List~Medico~
            +listarTodos() List~Medico~
            +actualizar(Medico) boolean
            +cambiarEstado(String, boolean) boolean
            +desactivar(String) boolean
            +activar(String) boolean
            +existeId(String) boolean
            +contarActivos() int
            +cerrar() void
        }

        class GestorArchivoCita {
            <<persistencia>>
            -String NOMBRE_ARCHIVO = "citas.dat"
            -RandomAccessFile archivo
            -long tamanioRegistro
            +GestorArchivoCita() throws IOException
            +crear(Cita) boolean
            +buscarPorId(String) Cita
            +buscarPorPaciente(String) List~Cita~
            +buscarPorMedico(String) List~Cita~
            +buscarPorFecha(String) List~Cita~
            +buscarPorEstado(String) List~Cita~
            +buscarPorRangoFechas(String, String) List~Cita~
            +listarActivas() List~Cita~
            +listarTodas() List~Cita~
            +actualizar(Cita) boolean
            +existeSolapamiento(String, String, String, String) boolean
            +cancelar(String) boolean
            +marcarAtendida(String) boolean
            +eliminarLogico(String) boolean
            +existeId(String) boolean
            +contarActivas() int
            +contarPorEstado(String) int
            +cerrar() void
        }

        class GestorArchivoLog {
            <<persistencia>>
            -String NOMBRE_ARCHIVO = "logs.dat"
            -RandomAccessFile archivo
            -long tamanioRegistro
            +int TAM_TIMESTAMP = 19
            +int TAM_MODULO = 20
            +int TAM_OPERACION = 15
            +int TAM_ENTIDAD = 15
            +int TAM_ID_ENTIDAD = 36
            +int TAM_DETALLES = 200
            +int TAM_USUARIO = 30
            +GestorArchivoLog() throws IOException
            +registrar(modulo, operacion, entidad, idEntidad, detalles, usuario) void
            +listarTodos() List~LogEntry~
            +filtrarPorModulo(String) List~LogEntry~
            +filtrarPorOperacion(String) List~LogEntry~
            +filtrarPorEntidad(String) List~LogEntry~
            +filtrarPorFecha(String, String) List~LogEntry~
            +filtrarPorUsuario(String) List~LogEntry~
            +buscarPorIdEntidad(String) List~LogEntry~
            +exportarLogsCSV(List~LogEntry~, String) void
            +exportarTextoPlano(String, List~String[]~, String[], String) void
            +cerrar() void
        }

        class LogEntry {
            <<clase anidada estática>>
            +String timestamp
            +String modulo
            +String operacion
            +String entidad
            +String idEntidad
            +String detalles
            +String usuario
            +boolean activo
            +toString() String
        }
    }

    %% ==================== PAQUETE CONTROLADORES ====================
    namespace Controladores {
        class ControladorPaciente {
            <<controlador>>
            -GestorArchivoPaciente gestorPaciente
            -GestorArchivoLog gestorLog
            -String usuarioActual
            +ControladorPaciente(gestor, log, usuario)
            +crearPaciente(cui, nombres, fecha, sexo, tel, correo, sangre) void
            +buscarPorCui(String) Paciente
            +buscarPorNombre(String) List~Paciente~
            +buscarPorTipoSangre(String) List~Paciente~
            +listarTodos() List~Paciente~
            +actualizarPaciente(cuiOrig, nombres, fecha, sexo, tel, correo, sangre) boolean
            +eliminarPaciente(String) boolean
            +reactivarPaciente(String) boolean
            +contarActivos() int
            +existeCui(String) boolean
        }

        class ControladorMedico {
            <<controlador>>
            -GestorArchivoMedico gestorMedico
            -GestorArchivoLog gestorLog
            -String usuarioActual
            +ControladorMedico(gestor, log, usuario)
            +crearMedico(nombres, especialidad, tel, correo, horIni, horFin) void
            +buscarPorId(String) Medico
            +buscarPorNombre(String) List~Medico~
            +buscarPorEspecialidad(String) List~Medico~
            +listarActivos() List~Medico~
            +listarInactivos() List~Medico~
            +listarTodos() List~Medico~
            +actualizarMedico(id, nombres, especialidad, tel, correo, horIni, horFin) boolean
            +activarMedico(String) boolean
            +desactivarMedico(String) boolean
            +contarActivos() int
            +existeId(String) boolean
        }

        class ControladorCita {
            <<controlador>>
            -GestorArchivoCita gestorCita
            -GestorArchivoPaciente gestorPaciente
            -GestorArchivoMedico gestorMedico
            -GestorArchivoLog gestorLog
            -String usuarioActual
            +ControladorCita(gestorCita, gestorPaciente, gestorMedico, gestorLog, usuario)
            +programarCita(cui, idMedico, fecha, hora, motivo, obs) void
            +buscarPorId(String) Cita
            +buscarPorPaciente(String) List~Cita~
            +buscarPorMedico(String) List~Cita~
            +buscarPorFecha(String) List~Cita~
            +buscarPorRangoFechas(String, String) List~Cita~
            +buscarPorEstado(String) List~Cita~
            +listarActivas() List~Cita~
            +listarTodas() List~Cita~
            +cancelarCita(String) boolean
            +marcarAtendida(String) boolean
            +reprogramarCita(id, fecha, hora, medico, motivo, obs) boolean
            +actualizarDetalles(id, motivo, obs) boolean
            +eliminarCita(String) boolean
            +contarActivas() int
            +contarPorEstado(String) int
            +existeId(String) boolean
        }

        class ControladorReportes {
            <<controlador>>
            -GestorArchivoPaciente gestorPaciente
            -GestorArchivoMedico gestorMedico
            -GestorArchivoCita gestorCita
            -GestorArchivoLog gestorLog
            -String usuarioActual
            +ControladorReportes(gP, gM, gC, gL, usuario)
            +reporteCompletoPacientes() List~Paciente~
            +reportePorTipoSangre(String) List~Paciente~
            +reportePacientesConMasCitas() List~ReportePacienteCitas~
            +reportePacientesSinCitas() List~Paciente~
            +reporteCompletoMedicos() List~Medico~
            +reportePorEspecialidad(String) List~Medico~
            +reporteMedicosConMasCitas() List~ReporteMedicoCitas~
            +reporteMedicosCitasPorFecha(String) List~ReporteMedicoCitasFecha~
            +reporteCompletoCitas() List~Cita~
            +reportePorRangoFechas(String, String) List~Cita~
            +reportePorMedico(String) List~Cita~
            +reportePorPaciente(String) List~Cita~
            +reportePorEstado(String) List~Cita~
            +reporteCitasPorEspecialidad() List~ReporteCitasPorEspecialidad~
            +reporteLogs(modulo, operacion, entidad, fIni, fFin, usuario) List~LogEntry~
            +exportarPacientesCSV(List~Paciente~, String) void
            +exportarMedicosCSV(List~Medico~, String) void
            +exportarCitasCSV(List~Cita~, String) void
            +exportarLogsCSV(List~LogEntry~, String) void
            +exportarTextoPlano(String, List~String[]~, String[], String) void
        }

        class ControladorLogs {
            <<controlador>>
            -GestorArchivoLog gestorLog
            -String usuarioActual
            +ControladorLogs(gestor, usuario)
            +logCrear(modulo, entidad, id, detalles) void
            +logActualizar(modulo, entidad, id, detalles) void
            +logEliminar(modulo, entidad, id, detalles) void
            +logBuscar(modulo, entidad, id, detalles) void
            +logListar(modulo, entidad, detalles) void
            +logActivar(modulo, entidad, id, detalles) void
            +logDesactivar(modulo, entidad, id, detalles) void
            +logCancelar(modulo, entidad, id, detalles) void
            +logAtender(modulo, entidad, id, detalles) void
            +logExportar(formato, entidad, detalles) void
            +logLogin() void
            +logLogout() void
            +verTodos() List~LogEntry~
            +filtrarPorModulo(String) List~LogEntry~
            +filtrarPorOperacion(String) List~LogEntry~
            +filtrarPorEntidad(String) List~LogEntry~
            +filtrarPorFecha(String, String) List~LogEntry~
            +filtrarPorUsuario(String) List~LogEntry~
            +buscarPorIdEntidad(String) List~LogEntry~
            +getGestorLog() GestorArchivoLog
        }
    }

    %% ==================== PAQUETE GUI ====================
    namespace GUI {
        class VentanaPrincipal {
            <<JFrame>>
            -GestorArchivoPaciente gestorPaciente
            -GestorArchivoMedico gestorMedico
            -GestorArchivoCita gestorCita
            -GestorArchivoLog gestorLog
            -ControladorPaciente ctrlPaciente
            -ControladorMedico ctrlMedico
            -ControladorCita ctrlCita
            -ControladorReportes ctrlReportes
            -ControladorLogs ctrlLogs
            -DialogoPacientes dlgPacientes
            -DialogoMedicos dlgMedicos
            -DialogoCitas dlgCitas
            -DialogoReportes dlgReportes
            -DialogoLogs dlgLogs
            -String usuarioActual = "admin"
            +VentanaPrincipal() throws IOException
            -inicializarVentana() void
            -construirMenu() void
            -construirBarraHerramientas() void
            -construirBarraEstado() void
            -abrirPacientes() void
            -abrirMedicos() void
            -abrirCitas() void
            -abrirReportes() void
            -abrirLogs() void
            -cerrarAplicacion() void
            +main(String[]) void$
        }

        class DialogoPacientes {
            -ControladorPaciente controlador
            -DefaultTableModel modeloTabla
            -JButton btnNuevo, btnGuardar, btnEliminar, btnBuscar
            -boolean modoEdicion
            +DialogoPacientes(parent, ControladorPaciente)
            -cargarTabla() void
            -buscar() void
            -cargarEnFormulario(fila) void
            -nuevo() void
            -guardar() void
            -eliminar() void
            -limpiarFormulario() void
        }

        class DialogoMedicos {
            -ControladorMedico controlador
            -DefaultTableModel modeloTabla
            -JComboBox cmbFiltroEstado, cmbFiltroEspecialidad
            -boolean modoEdicion
            +DialogoMedicos(parent, ControladorMedico)
            -cargarEspecialidades() void
            -cargarTabla() void
            -filtrar() void
            -cargarEnFormulario(fila) void
            -nuevo() void
            -guardar() void
            -cambiarEstado(activar) void
            -limpiarFormulario() void
        }

        class DialogoCitas {
            -ControladorCita controlador
            -ControladorPaciente ctrlPaciente
            -ControladorMedico ctrlMedico
            -DefaultTableModel modeloTabla
            -boolean modoEdicion
            +DialogoCitas(parent, ControladorCita, ControladorPaciente, ControladorMedico)
            -cargarMedicosEnCombo() void
            -cargarTabla() void
            -filtrar() void
            -limpiarFiltros() void
            -cargarEnFormulario(fila) void
            -buscarIdCompleto(idCorta) String
            -nuevo() void
            -guardar() void
            -cambiarEstado(nuevoEstado) void
            -reprogramar() void
            -eliminar() void
            -verPaciente() void
            -verMedico() void
        }

        class DialogoReportes {
            -ControladorReportes controlador
            -DefaultTableModel modeloPacientes, modeloMedicos, modeloCitas, modeloEspecial
            -JTabbedPane tabs
            +DialogoReportes(parent, ControladorReportes)
            -cargarCombos() void
            -cargarReportePacientes() void
            -cargarReporteMedicos() void
            -cargarReporteCitas() void
            -cargarReportesEspeciales() void
            -exportarPacientesCSV() void
            -exportarPacientesTXT() void
            -exportarMedicosCSV() void
            -exportarMedicosTXT() void
            -exportarCitasCSV() void
            -exportarCitasTXT() void
            -exportarEspecialCSV() void
            -exportarEspecialTXT() void
            -exportarCSV(nombreDefecto, action) void
            -exportarTXT(nombreDefecto, action) void
        }

        class DialogoLogs {
            -ControladorLogs controlador
            -DefaultTableModel modeloTabla
            -JComboBox cmbModulo, cmbOperacion, cmbEntidad
            -JTextField txtFechaDesde, txtFechaHasta, txtUsuario, txtIdEntidad
            +DialogoLogs(parent, ControladorLogs)
            -cargarCombos() void
            -cargarLogs() void
            -limpiarFiltros() void
            -exportarCSV() void
            -exportarTXT() void
            -getLogsFiltrados() List~LogEntry~
        }
    }

    %% ==================== RELACIONES ENTRE CAPAS ====================

    %% Entidad Cita -> Paciente y Medico (composición de referencia)
    Cita *-- Paciente : "1 paciente -- 0..* citas"
    Cita *-- Medico : "1 médico -- 0..* citas"

    %% Entidades -> UtilidadesArchivo (dependencia de serialización)
    Paciente ..> UtilidadesArchivo : "escribir/leer"
    Medico ..> UtilidadesArchivo : "escribir/leer"
    Cita ..> UtilidadesArchivo : "escribir/leer"

    %% Gestores -> Entidades (realización: dependencia)
    GestorArchivoPaciente ..> Paciente : "persiste"
    GestorArchivoMedico ..> Medico : "persiste"
    GestorArchivoCita ..> Cita : "persiste"
    GestorArchivoLog ..> LogEntry : "persiste"

    %% Controladores -> Gestores (dependencia)
    ControladorPaciente --> GestorArchivoPaciente
    ControladorPaciente --> GestorArchivoLog
    ControladorMedico --> GestorArchivoMedico
    ControladorMedico --> GestorArchivoLog
    ControladorCita --> GestorArchivoCita
    ControladorCita --> GestorArchivoPaciente
    ControladorCita --> GestorArchivoMedico
    ControladorCita --> GestorArchivoLog
    ControladorReportes --> GestorArchivoPaciente
    ControladorReportes --> GestorArchivoMedico
    ControladorReportes --> GestorArchivoCita
    ControladorReportes --> GestorArchivoLog
    ControladorLogs --> GestorArchivoLog

    %% Controladores -> Entidades (dependencia de retorno)
    ControladorPaciente ..> Paciente
    ControladorMedico ..> Medico
    ControladorCita ..> Cita
    ControladorReportes ..> LogEntry

    %% GUI -> Controladores (dependencia)
    VentanaPrincipal *-- ControladorPaciente : "1"
    VentanaPrincipal *-- ControladorMedico : "1"
    VentanaPrincipal *-- ControladorCita : "1"
    VentanaPrincipal *-- ControladorReportes : "1"
    VentanaPrincipal *-- ControladorLogs : "1"
    VentanaPrincipal *-- GestorArchivoPaciente : "1"
    VentanaPrincipal *-- GestorArchivoMedico : "1"
    VentanaPrincipal *-- GestorArchivoCita : "1"
    VentanaPrincipal *-- GestorArchivoLog : "1"
    VentanaPrincipal o-- DialogoPacientes : "0..1"
    VentanaPrincipal o-- DialogoMedicos : "0..1"
    VentanaPrincipal o-- DialogoCitas : "0..1"
    VentanaPrincipal o-- DialogoReportes : "0..1"
    VentanaPrincipal o-- DialogoLogs : "0..1"

    %% Diálogos -> Controladores
    DialogoPacientes --> ControladorPaciente
    DialogoMedicos --> ControladorMedico
    DialogoCitas --> ControladorCita : "usa"
    DialogoCitas --> ControladorPaciente : "usa"
    DialogoCitas --> ControladorMedico : "usa"
    DialogoReportes --> ControladorReportes
    DialogoLogs --> ControladorLogs
```

## Descripción del Diagrama de Clases

### Arquitectura en Capas
El sistema sigue una **arquitectura en 4 capas** (separación de responsabilidades):

1. **Capa de Entidades** (`entidades`): Modelo de dominio - `Paciente`, `Medico`, `Cita`. Cada entidad sabe serializarse a `RandomAccessFile` mediante los métodos `escribir()` y `leer()`, definiendo campos de **longitud fija** para registros de tamaño constante.

2. **Capa de Persistencia** (`Archivos`): Gestores de archivos binarios (`GestorArchivoPaciente`, `GestorArchivoMedico`, `GestorArchivoCita`, `GestorArchivoLog`) que implementan el patrón DAO con `RandomAccessFile`. Incluye `UtilidadesArchivo` como helper estático para leer/escribir strings de longitud fija.

3. **Capa de Lógica de Negocio** (`controladores`): Controladores que validan reglas de negocio, orquestan la persistencia y registran auditoría (`ControladorPaciente`, `ControladorMedico`, `ControladorCita`, `ControladorReportes`, `ControladorLogs`).

4. **Capa de Presentación** (`gui`): `VentanaPrincipal` (JFrame) que compone los gestores y controladores, y lanza diálogos modales (`DialogoPacientes`, `DialogoMedicos`, `DialogoCitas`, `DialogoReportes`, `DialogoLogs`).

### Persistencia con RandomAccessFile (clave para la defensa)
- **Registros de longitud fija**: cada entidad define constantes `TAM_*` para el tamaño en caracteres de cada campo y `TAM_REGISTRO` para el total en bytes.
- **Cálculo del registro**: `(suma de TAM_campos) * 2 + 2` bytes. El `*2` es porque `writeChars()`/`readChar()` usan 2 bytes por carácter (UTF-16); el `+2` corresponde al `char` de bandera `activo` ('1'/'0').
- **Acceso aleatorio**: `calcularPosicion(indice) = indice * tamanioRegistro` permite saltar directamente a cualquier registro sin recorrer el archivo.
- **Eliminación lógica**: la bandera `activo`/`activa` marca el registro como eliminado sin borrarlo físicamente, preservando las posiciones.
- **Detección de solapamiento**: `GestorArchivoCita.existeSolapamiento()` convierte horas a minutos desde medianoche y asume duración fija de 30 min por cita.

### Inyección de Dependencias
`VentanaPrincipal` construye los gestores y los inyecta en los controladores en el constructor (línea `new ControladorX(gestor, gestorLog, usuario)`). Esto desacopla la GUI de la lógica de negocio y facilita el testeo.

### DTOs de Reportes (`ControladorReportes`)
Las clases anidadas `ReportePacienteCitas`, `ReporteMedicoCitas`, `ReporteMedicoCitasFecha`, `ReporteCitasPorEspecialidad` son DTOs (Data Transfer Objects) inmutables que transportan los agregados calculados hacia la GUI.

### Relaciones de multiplicidad
- Un `Paciente` (1) tiene 0..* `Cita`s.
- Un `Medico` (1) tiene 0..* `Cita`s.
- `VentanaPrincipal` compone 1 de cada controlador y gestor (composición fuerte - ciclo de vida acoplado).
- `VentanaPrincipal` posee 0..1 de cada diálogo (creados bajo demanda y reutilizados).

### Paquetes Java correspondientes
```
com.mycompany.m.archivos.practica1.s2_2026
├── entidades        (Paciente, Medico, Cita)
├── Archivos         (GestorArchivo*, UtilidadesArchivo, LogEntry)
├── controladores    (Controlador*)
├── gui              (VentanaPrincipal, Dialogo*)
└── (raíz)           (MArchivosPractica1S2_2026 - no es el main real)
```
El punto de entrada real es `gui.VentanaPrincipal.main()` según el `pom.xml`.
