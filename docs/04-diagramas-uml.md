# 4. Diagramas UML

Todos los diagramas siguen la notación UML y están escritos en Mermaid, así que GitHub los dibuja y se pueden editar como texto. Los nombres coinciden con las clases y funciones del código.

| # | Diagrama | Tipo UML | Para qué sirve |
|---|---|---|---|
| 4.1 | [Casos de uso](#41-diagrama-de-casos-de-uso) | Comportamiento | Qué puede hacer cada actor |
| 4.2 | [Clases del dominio](#42-diagrama-de-clases-del-dominio) | Estructura | Entidades y sus relaciones |
| 4.3 | [Clases por capas](#43-diagrama-de-clases-por-capas) | Estructura | ViewModels, repositorios y DAO |
| 4.4 | [Entidad-relación de Room](#44-modelo-entidad-relación-de-la-base-local) | Estructura | Tablas de la base local |
| 4.5 | [Secuencia: conectar el calendario](#45-diagrama-de-secuencia-conectar-el-calendario) | Interacción | Importación iCal paso a paso |
| 4.6 | [Secuencia: preguntar a Lumi](#46-diagrama-de-secuencia-preguntar-a-lumi) | Interacción | Llamada a Gemini con contexto |
| 4.7 | [Secuencia: plan del día](#47-diagrama-de-secuencia-plan-del-día) | Interacción | Prioridad, caché y respaldo local |
| 4.8 | [Estados de una entrega](#48-diagrama-de-estados-de-una-entrega) | Comportamiento | Ciclo de vida de una entrega |
| 4.9 | [Estados de la nube](#49-diagrama-de-estados-de-la-sincronización) | Comportamiento | Indicador de sincronización |
| 4.10 | [Actividad: elegir la prioridad](#410-diagrama-de-actividad-elegir-la-prioridad-del-día) | Comportamiento | Algoritmo de `planDelDia` |
| 4.11 | [Actividad: aviso diario](#411-diagrama-de-actividad-aviso-diario) | Comportamiento | Trabajo de WorkManager |
| 4.12 | [Componentes](#412-diagrama-de-componentes) | Estructura | Módulos y dependencias |
| 4.13 | [Despliegue](#413-diagrama-de-despliegue) | Estructura | Dónde corre cada parte |

---

## 4.1 Diagrama de casos de uso

Mermaid no tiene un tipo propio de casos de uso, así que se dibuja con la misma notación: actores a los lados, casos de uso como óvalos dentro del límite del sistema y relaciones `«include»` y `«extend»`.

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
flowchart LR
    EST(("👩‍🎓<br/>Estudiante"))

    subgraph SISTEMA["Sistema · Mi Semestre"]
        direction TB
        CU1(["Registrarse"])
        CU2(["Iniciar sesión"])
        CU3(["Gestionar materias"])
        CU4(["Registrar notas"])
        CU5(["Ver nota necesaria"])
        CU6(["Ver promedio del semestre"])
        CU7(["Gestionar entregas"])
        CU8(["Conectar calendario"])
        CU9(["Crear materias sugeridas"])
        CU10(["Ver plan del día"])
        CU11(["Preguntar a Lumi"])
        CU12(["Ver perfil y logros"])
        CU13(["Cerrar sesión"])
        CU14(["Sincronizar datos"])
        CU15(["Recibir recordatorio"])
        CU16(["Actualizar calendario"])
    end

    AUTH(("Firebase<br/>Auth"))
    FS(("Cloud<br/>Firestore"))
    GEM(("Gemini"))
    CAL(("Calendario<br/>iCal"))
    AND(("Android<br/>WorkManager"))

    EST --- CU1 & CU2 & CU3 & CU4 & CU6 & CU7 & CU8 & CU10 & CU11 & CU12 & CU13

    CU4 -. "«include»" .-> CU5
    CU8 -. "«include»" .-> CU16
    CU9 -. "«extend»" .-> CU8
    CU3 -. "«include»" .-> CU14
    CU4 -. "«include»" .-> CU14
    CU7 -. "«include»" .-> CU14

    CU1 --- AUTH
    CU2 --- AUTH
    CU14 --- FS
    CU10 --- GEM
    CU11 --- GEM
    CU16 --- CAL
    AND --- CU15
    AND --- CU16

    classDef interfaz fill:#E6DDFF,stroke:#A78BFA,stroke-width:2px,color:#20104F
    classDef logica fill:#FFE3D3,stroke:#FFB38A,stroke-width:2px,color:#5C2200
    classDef datos fill:#D9F8E6,stroke:#5CC98D,stroke-width:2px,color:#0F4D2C
    classDef nube fill:#DDF1FF,stroke:#5AB4EE,stroke-width:2px,color:#0B3A5C
    classDef lumi fill:#FFE0EC,stroke:#FF5C8A,stroke-width:2px,color:#5C0A26
    classDef actor fill:#20104F,stroke:#20104F,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FFF4D6,stroke:#E0A800,stroke-width:2px,color:#4D3800
    classDef extremo fill:#A78BFA,stroke:#3B2A80,stroke-width:2px,color:#FFFFFF
    class EST actor
    class AUTH,FS,GEM,CAL,AND nube
    class CU10,CU11 lumi
    class CU1,CU2,CU3,CU4,CU5,CU6,CU7,CU8,CU9,CU12,CU13,CU14,CU15,CU16 interfaz
```

| Caso de uso | Actor principal | Historia |
|---|---|---|
| Registrarse · Iniciar sesión | Estudiante | HU-01, HU-02 |
| Gestionar materias · Registrar notas | Estudiante | HU-04, HU-05 |
| Ver nota necesaria · Ver promedio | Estudiante | HU-06, HU-07 |
| Gestionar entregas | Estudiante | HU-08, HU-09 |
| Conectar y actualizar calendario | Estudiante, WorkManager | HU-10 |
| Crear materias sugeridas | Estudiante | HU-11 |
| Recibir recordatorio | WorkManager | HU-12 |
| Ver plan del día · Preguntar a Lumi | Estudiante | HU-13, HU-14 |
| Ver perfil y logros · Cerrar sesión | Estudiante | HU-16, HU-17, HU-18 |
| Sincronizar datos | Firestore | HU-03 |

---

## 4.2 Diagrama de clases del dominio

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
classDiagram
    direction LR

    class Usuario {
        +String nombre
        +String correo
        +String primerNombre
        +String iniciales
    }

    class Materia {
        +String id
        +String nombre
        +Int creditos
    }

    class Nota {
        +String id
        +String materiaId
        +String nombre
        +Int peso
        +Double? valor
        +Long creadaEn
    }

    class Entrega {
        +String id
        +String nombre
        +String materia
        +LocalDate fecha
        +Boolean entregada
        +Boolean importada
        +Boolean oculta
        +Boolean editada
    }

    class DatosPerfil {
        +String universidad
        +String semestre
    }

    class EventoCalendario {
        +String uid
        +String nombre
        +String materia
        +LocalDate fecha
    }

    class PlanDelDia {
        +String titulo
        +String detalle
        +AnimoLumi animo
        +Entrega? entrega
    }

    class AnimoLumi {
        <<enumeration>>
        Contenta
        Tranquila
        Atenta
        Preocupada
    }

    class ResumenSemestre {
        +Double? promedio
        +Int avance
    }

    class CalculadoraNotas {
        <<object>>
        +resumenSemestre(materias, notas) ResumenSemestre
        +pesoEvaluado(notas) Int
        +promedioEvaluado(notas) Double?
        +notaNecesaria(notas, meta) Double?
    }

    Usuario "1" --> "0..*" Materia : tiene
    Usuario "1" --> "0..*" Entrega : tiene
    Usuario "1" --> "1" DatosPerfil : tiene
    Materia "1" *-- "0..*" Nota : compone
    Materia "1" ..> "0..*" Entrega : se relaciona por nombre
    EventoCalendario ..> Entrega : se convierte en
    PlanDelDia --> "0..1" Entrega : prioriza
    PlanDelDia --> AnimoLumi
    CalculadoraNotas ..> Materia : usa
    CalculadoraNotas ..> Nota : usa
    CalculadoraNotas ..> ResumenSemestre : crea
```

**Decisiones del modelo**

- `Nota` **compone** `Materia` (rombo lleno): al borrar la materia se borran sus notas (`ForeignKey.CASCADE`).
- `Entrega` guarda el **nombre** de la materia, no su `id`, porque las entregas importadas llegan antes de que exista la materia; se relacionan por nombre sin distinguir mayúsculas.
- `valor` es nulo mientras la nota no esté calificada.
- `oculta` y `editada` implementan el borrado suave y el respeto a las ediciones ([ADR-0010](adr/0010-borrado-suave-de-importadas.md)).

---

## 4.3 Diagrama de clases por capas

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
classDiagram
    direction TB

    namespace Presentacion {
        class InicioViewModel {
            +StateFlow resumen
            +StateFlow entregas
            +StateFlow perfil
            +String? fraseDelPlan
            +pedirFrase(plan)
            +guardarPerfil(datos)
        }
        class MateriaViewModel {
            +Flow materia
            +Flow notas
            +Flow entregas
            +agregar(nombre, peso, valor)
            +actualizar(nota)
            +borrar(nota)
        }
        class EntregasViewModel {
            +StateFlow entregas
            +StateFlow materiasSugeridas
            +Boolean conectado
            +agregar(nombre, materia, fecha)
            +editar(entrega, ...)
            +cambiar(entrega)
            +conectar(enlace)
            +actualizar()
            +desconectar()
            +crearMaterias(creditos)
        }
        class AsistenteViewModel {
            +List~Mensaje~ mensajes
            +Boolean pensando
            +enviar(pregunta)
        }
        class LoginViewModel {
            +Boolean cargando
            +String? error
            +entrar(correo, clave)
            +registrar(nombre, correo, clave)
        }
    }

    namespace Datos {
        class MateriaRepository {
            +Flow materias
            +agregar(materia)
            +borrar(materia)
        }
        class NotaRepository {
            +Flow todas
            +notas(materiaId) Flow
            +agregar(nota)
            +actualizar(nota)
            +borrar(nota)
        }
        class EntregaRepository {
            +Flow entregas
            +guardar(entrega)
            +borrar(entrega)
            +borrarImportadas()
        }
        class CalendarioRepository {
            +Boolean conectado
            +conectar(texto) Int
            +actualizar() Int
            +desconectar()
        }
        class LumiRepository {
            +responder(pregunta, contexto) Flow
            +fraseDelDia(prioridad) String?
        }
        class SesionRepository {
            +Usuario? usuario
            +entrar(correo, clave)
            +registrar(nombre, correo, clave)
            +salir()
        }
        class PerfilRepository {
            +Flow datos
            +guardar(datos)
        }
        class NubeRepository {
            +guardar(...)
            +borrar(...)
        }
        class Sincronizador {
            <<singleton>>
            +StateFlow estado
            +iniciar()
        }
        class FraseDelPlan {
            +obtener(hoy, plan) String?
        }
        class EnlaceCalendario {
            +String? url
            +String? dueno
            +Set materiasIgnoradas
            +olvidar()
        }
    }

    namespace Persistencia {
        class AppDatabase {
            <<singleton>>
            +materiaDao()
            +notaDao()
            +entregaDao()
            +sincronizacionDao()
        }
        class MateriaDao {
            <<interface>>
        }
        class NotaDao {
            <<interface>>
        }
        class EntregaDao {
            <<interface>>
        }
        class SincronizacionDao {
            <<interface>>
            +reemplazar(materias, notas, entregas)
            +borrarTodo()
        }
    }

    InicioViewModel --> MateriaRepository
    InicioViewModel --> NotaRepository
    InicioViewModel --> EntregaRepository
    InicioViewModel --> PerfilRepository
    InicioViewModel --> FraseDelPlan
    MateriaViewModel --> NotaRepository
    MateriaViewModel --> MateriaRepository
    EntregasViewModel --> EntregaRepository
    EntregasViewModel --> CalendarioRepository
    EntregasViewModel --> MateriaRepository
    AsistenteViewModel --> LumiRepository
    LoginViewModel --> SesionRepository

    FraseDelPlan --> LumiRepository
    CalendarioRepository --> EntregaRepository
    CalendarioRepository --> EnlaceCalendario
    MateriaRepository --> MateriaDao
    MateriaRepository --> NubeRepository
    NotaRepository --> NotaDao
    NotaRepository --> NubeRepository
    EntregaRepository --> EntregaDao
    EntregaRepository --> NubeRepository
    Sincronizador --> SincronizacionDao
    Sincronizador --> EnlaceCalendario
    AppDatabase ..> MateriaDao : crea
    AppDatabase ..> NotaDao : crea
    AppDatabase ..> EntregaDao : crea
    AppDatabase ..> SincronizacionDao : crea
```

---

## 4.4 Modelo entidad-relación de la base local

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
erDiagram
    MATERIAS ||--o{ NOTAS : "tiene (borrado en cascada)"
    MATERIAS {
        string id PK
        string nombre
        int creditos
    }
    NOTAS {
        string id PK
        string materiaId FK
        string nombre
        int peso
        double valor "nulo si no está calificada"
        long creadaEn
    }
    ENTREGAS {
        string id PK "cal-SHA256 si es importada"
        string nombre
        string materia "nombre de la materia"
        string fecha "LocalDate como texto ISO"
        boolean entregada
        boolean importada
        boolean oculta
        boolean editada
    }
```

Base `mi_semestre.db`, versión 5. Las fechas se guardan como texto con `Convertidores`. En Firestore la estructura es la misma, bajo `usuarios/{uid}/`.

---

## 4.5 Diagrama de secuencia: conectar el calendario

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
sequenceDiagram
    autonumber
    actor E as Estudiante
    participant P as EntregasScreen
    participant VM as EntregasViewModel
    participant CR as CalendarioRepository
    participant ICS as Servidor iCal
    participant ER as EntregaRepository
    participant DB as Room
    participant FS as Firestore
    participant EC as EnlaceCalendario

    E->>P: pega el enlace y toca Conectar
    P->>VM: conectar(enlace)
    VM->>CR: conectar(texto)
    CR->>CR: normalizarEnlace(texto)
    alt enlace inválido
        CR-->>VM: CalendarioInvalido
        VM-->>P: "Ese enlace no parece ser de un calendario"
    else enlace válido
        CR->>ICS: GET (máx. 5 MB, 15 s)
        ICS-->>CR: texto .ics
        CR->>CR: leerCalendario() → eventos
        CR->>ER: todas()
        ER-->>CR: entregas actuales
        CR->>CR: cambiosDeCalendario(actuales, eventos, hoy)
        CR->>ER: guardar(cambios)
        ER->>DB: upsert
        ER->>FS: set() en usuarios/{uid}/entregas
        CR->>EC: url = enlace
        CR-->>VM: cantidad de cambios
        VM-->>P: conectado = true, "N entregas nuevas o actualizadas"
        DB-->>P: Flow emite la lista nueva
    end
```

---

## 4.6 Diagrama de secuencia: preguntar a Lumi

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
sequenceDiagram
    autonumber
    actor E as Estudiante
    participant VM as AsistenteViewModel
    participant DB as Room
    participant C as contextoParaLumi
    participant L as LumiRepository
    participant AC as App Check
    participant G as Gemini

    E->>VM: enviar("¿Qué debo priorizar?")
    VM->>VM: agrega el mensaje y pensando = true
    VM->>DB: materias, notas y entregas (first())
    DB-->>VM: datos actuales
    VM->>C: contextoParaLumi(hoy, materias, notas, entregas)
    C-->>VM: texto con el contexto
    VM->>L: responder(pregunta, contexto)
    L->>AC: token de la app
    AC-->>L: token válido
    L->>G: sendMessageStream(contexto + pregunta)
    loop mientras llegan fragmentos
        G-->>L: fragmento de texto
        L-->>VM: emite el texto
        VM-->>E: la respuesta crece en pantalla
    end
    alt cuota agotada
        G-->>VM: QuotaExceededException
        VM-->>E: "Ya respondí muchas preguntas por hoy"
    else sin conexión
        G-->>VM: IOException
        VM-->>E: "No pude responder. Revisa tu internet"
    end
    VM->>VM: pensando = false
```

---

## 4.7 Diagrama de secuencia: plan del día

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
sequenceDiagram
    autonumber
    participant P as InicioScreen
    participant VM as InicioViewModel
    participant PD as planDelDia
    participant F as FraseDelPlan
    participant SP as SharedPreferences
    participant L as LumiRepository

    P->>PD: planDelDia(hoy, entregas)
    PD-->>P: PlanDelDia(título, detalle, ánimo, entrega)
    P->>P: muestra título y detalle local
    P->>VM: pedirFrase(plan)
    VM->>VM: cancela el pedido anterior
    VM->>F: obtener(hoy, plan)
    alt no hay entrega prioritaria
        F-->>VM: null
    else ya hay frase de hoy para esa entrega
        F->>SP: clave == "hoy|id"
        SP-->>F: frase guardada
        F-->>VM: frase
    else hay que pedirla
        F->>L: fraseDelDia("Taller de Física")
        alt respuesta válida (≤ 120 caracteres)
            L-->>F: "Revisa primero las fórmulas…"
            F->>SP: guarda clave y frase
            F-->>VM: frase
        else error o respuesta larga
            L-->>F: null
            F-->>VM: null
        end
    end
    VM-->>P: fraseDelPlan
    P->>P: muestra frase ?: detalle local
```

---

## 4.8 Diagrama de estados de una entrega

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
stateDiagram-v2
    [*] --> Pendiente : creada a mano
    [*] --> Pendiente : importada del calendario

    state Pendiente {
        [*] --> AlDia
        AlDia --> Urgente : faltan 3 días o menos
        Urgente --> Vencida : pasa la fecha
    }

    Pendiente --> Entregada : marcar lista (Lumi celebra)
    Entregada --> Pendiente : marcar pendiente
    Pendiente --> Editada : editar nombre, materia o fecha
    Editada --> Pendiente : el calendario ya no la sobrescribe

    Pendiente --> Oculta : borrar (si es importada)
    Entregada --> Oculta : borrar (si es importada)
    Pendiente --> [*] : borrar (si es manual)
    Entregada --> [*] : borrar (si es manual)
    Oculta --> [*] : desconectar calendario
```

| Estado | Cómo se ve en la app |
|---|---|
| Al día | Etiqueta durazno con "N días" |
| Urgente | Etiqueta fucsia: "Hoy", "Mañana" o "N días" |
| Vencida | Etiqueta fucsia "Vencida"; Lumi preocupada en Inicio |
| Entregada | Pestaña "Entregadas" |
| Oculta | No se muestra, pero evita que el calendario la vuelva a crear |

---

## 4.9 Diagrama de estados de la sincronización

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
stateDiagram-v2
    [*] --> Conectando : inicia sesión
    Conectando --> Sincronizado : llegan los datos del servidor
    Conectando --> SinConexion : datos solo desde la caché
    Sincronizado --> Pendiente : el usuario guarda un cambio
    Pendiente --> Sincronizado : Firestore confirma la escritura
    Sincronizado --> SinConexion : se pierde internet
    SinConexion --> Pendiente : guarda un cambio sin internet
    SinConexion --> Sincronizado : vuelve internet
    Pendiente --> SinConexion : error del servidor
    Sincronizado --> [*] : cierra sesión
```

`estadoDe(hayPendientes, soloCache)` decide el estado: lo pendiente gana sobre la falta de conexión (prueba `loPendienteGanaSobreLaFaltaDeConexion`).

---

## 4.10 Diagrama de actividad: elegir la prioridad del día

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
flowchart TD
    A(["Inicio"]) --> B["Tomar entregas no entregadas<br/>ordenadas por fecha"]
    B --> C{"¿Hay vencidas?"}
    C -- Sí --> C1["Prioridad: la vencida más antigua<br/>Lumi preocupada"]
    C -- No --> D{"¿Algo vence hoy?"}
    D -- Sí --> D1["Prioridad: la de hoy<br/>Lumi preocupada"]
    D -- No --> E{"¿Quedan pendientes?"}
    E -- No --> E1["'Vas al día'<br/>Lumi contenta"]
    E -- Sí --> F["Filtrar las de los próximos 7 días"]
    F --> G{"¿Alguna tiene % en el nombre?"}
    G -- Sí --> G1["Elegir la de mayor %"]
    G -- No --> G2["Elegir la más cercana"]
    G1 --> H{"¿Faltan 3 días o menos?"}
    G2 --> H
    H -- Sí --> H1["Lumi atenta"]
    H -- No --> H2["Lumi tranquila"]
    C1 & D1 & E1 & H1 & H2 --> Z(["PlanDelDia"])

    classDef interfaz fill:#E6DDFF,stroke:#A78BFA,stroke-width:2px,color:#20104F
    classDef logica fill:#FFE3D3,stroke:#FFB38A,stroke-width:2px,color:#5C2200
    classDef datos fill:#D9F8E6,stroke:#5CC98D,stroke-width:2px,color:#0F4D2C
    classDef nube fill:#DDF1FF,stroke:#5AB4EE,stroke-width:2px,color:#0B3A5C
    classDef lumi fill:#FFE0EC,stroke:#FF5C8A,stroke-width:2px,color:#5C0A26
    classDef actor fill:#20104F,stroke:#20104F,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FFF4D6,stroke:#E0A800,stroke-width:2px,color:#4D3800
    classDef extremo fill:#A78BFA,stroke:#3B2A80,stroke-width:2px,color:#FFFFFF
    class A,Z extremo
    class C,D,E,G,H decision
    class B,F,G1,G2 interfaz
    class C1,D1 lumi
    class E1,H1,H2 lumi
```

---

## 4.11 Diagrama de actividad: aviso diario

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
flowchart TD
    A(["WorkManager despierta<br/>~8:00"]) --> B{"¿Hay sesión iniciada?"}
    B -- No --> Z(["Terminar"])
    B -- Sí --> C{"¿Hay calendario conectado?"}
    C -- Sí --> C1["Actualizar calendario<br/>si falla, sigue igual"]
    C1 --> D
    C -- No --> D{"¿Ya avisó hoy o están<br/>desactivadas las notificaciones?"}
    D -- Sí --> Z
    D -- No --> E["Buscar pendientes de hoy y mañana"]
    E --> F{"¿Hay alguna?"}
    F -- No --> Z
    F -- Sí --> G["Una notificación por entrega"]
    G --> H["Guardar la fecha del último aviso"]
    H --> Z

    classDef interfaz fill:#E6DDFF,stroke:#A78BFA,stroke-width:2px,color:#20104F
    classDef logica fill:#FFE3D3,stroke:#FFB38A,stroke-width:2px,color:#5C2200
    classDef datos fill:#D9F8E6,stroke:#5CC98D,stroke-width:2px,color:#0F4D2C
    classDef nube fill:#DDF1FF,stroke:#5AB4EE,stroke-width:2px,color:#0B3A5C
    classDef lumi fill:#FFE0EC,stroke:#FF5C8A,stroke-width:2px,color:#5C0A26
    classDef actor fill:#20104F,stroke:#20104F,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FFF4D6,stroke:#E0A800,stroke-width:2px,color:#4D3800
    classDef extremo fill:#A78BFA,stroke:#3B2A80,stroke-width:2px,color:#FFFFFF
    class A,Z extremo
    class B,C,D,F decision
    class C1,E,G,H interfaz
```

---

## 4.12 Diagrama de componentes

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
flowchart TB
    subgraph APP["«componente» App Android"]
        direction TB
        subgraph UI["«subsistema» ui"]
            PANT["Pantallas Compose"]
            COMP["componentes<br/>LumiAnimada · ReaccionLumi · Pildora"]
            TEMA["theme"]
        end
        NAV["«componente» navegacion"]
        subgraph DATA["«subsistema» data"]
            REPOS["Repositorios"]
            LOGICA["Lógica pura<br/>CalculadoraNotas · Calendario · PlanDelDia"]
            ROOMC["Room<br/>AppDatabase y DAO"]
            SYNC["Sincronizador"]
            WORK["Recordatorios"]
        end
    end

    AUTH_I(("API Auth"))
    FS_I(("API Firestore"))
    AI_I(("API AI Logic"))
    HTTP_I(("HTTPS iCal"))
    NOTIF_I(("Notificaciones"))

    NAV --> PANT
    PANT --> COMP & TEMA
    PANT --> REPOS
    REPOS --> LOGICA
    REPOS --> ROOMC
    SYNC --> ROOMC
    WORK --> REPOS

    REPOS -.-> AUTH_I & FS_I & AI_I & HTTP_I
    SYNC -.-> FS_I
    WORK -.-> NOTIF_I

    classDef interfaz fill:#E6DDFF,stroke:#A78BFA,stroke-width:2px,color:#20104F
    classDef logica fill:#FFE3D3,stroke:#FFB38A,stroke-width:2px,color:#5C2200
    classDef datos fill:#D9F8E6,stroke:#5CC98D,stroke-width:2px,color:#0F4D2C
    classDef nube fill:#DDF1FF,stroke:#5AB4EE,stroke-width:2px,color:#0B3A5C
    classDef lumi fill:#FFE0EC,stroke:#FF5C8A,stroke-width:2px,color:#5C0A26
    classDef actor fill:#20104F,stroke:#20104F,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FFF4D6,stroke:#E0A800,stroke-width:2px,color:#4D3800
    classDef extremo fill:#A78BFA,stroke:#3B2A80,stroke-width:2px,color:#FFFFFF
    class PANT,COMP,TEMA,NAV interfaz
    class REPOS,SYNC,WORK datos
    class LOGICA logica
    class ROOMC datos
    class AUTH_I,FS_I,AI_I,HTTP_I,NOTIF_I nube
    class COMP lumi
```

---

## 4.13 Diagrama de despliegue

```mermaid
%%{init: {'theme': 'base', 'themeVariables': {'primaryColor': '#E6DDFF', 'primaryTextColor': '#251B45', 'primaryBorderColor': '#A78BFA', 'secondaryColor': '#FFE3D3', 'tertiaryColor': '#FBF8FF', 'lineColor': '#7C6BC4', 'textColor': '#251B45', 'clusterBkg': '#FBF8FF', 'clusterBorder': '#A78BFA', 'edgeLabelBackground': '#FFFFFF', 'actorBkg': '#E6DDFF', 'actorBorder': '#A78BFA', 'actorTextColor': '#251B45', 'signalColor': '#3B2A80', 'signalTextColor': '#251B45', 'labelBoxBkgColor': '#FFE3D3', 'labelBoxBorderColor': '#FFB38A', 'noteBkgColor': '#FFE3D3', 'noteBorderColor': '#FFB38A', 'noteTextColor': '#251B45', 'activationBkgColor': '#FFE0EC', 'activationBorderColor': '#FF5C8A', 'cScale0': '#E6DDFF', 'cScale1': '#FFE3D3', 'cScale2': '#A6F0C6', 'cScale3': '#8FD3FF', 'cScale4': '#FFA8D2', 'cScale5': '#FFB38A', 'cScale6': '#A78BFA', 'cScaleLabel0': '#251B45', 'cScaleLabel1': '#251B45', 'cScaleLabel2': '#251B45', 'cScaleLabel3': '#251B45', 'cScaleLabel4': '#251B45', 'cScaleLabel5': '#251B45', 'cScaleLabel6': '#251B45'}}}%%
flowchart LR
    subgraph CEL["«dispositivo» Teléfono Android 8.0+"]
        subgraph APK["«artefacto» app-debug.apk / app-release.apk"]
            APPX["Mi Semestre"]
        end
        SQLITE[("«base» SQLite<br/>mi_semestre.db")]
        PREFS[("«archivo» SharedPreferences<br/>calendario · plan · lumi · recordatorios")]
        WMX["«servicio» WorkManager"]
        APPX --- SQLITE
        APPX --- PREFS
        WMX --- APPX
    end

    subgraph GCP["«nube» Google Firebase · proyecto mi-semestre-lumi"]
        AUTHX["Authentication"]
        FSX[("Cloud Firestore<br/>usuarios/{uid}")]
        ACX["App Check"]
        AIX["AI Logic"]
    end

    GEMX["«servicio» Gemini<br/>gemini-3.5-flash-lite"]
    CANVAS["«servidor» Canvas / Google / Outlook<br/>feed iCal"]

    APPX -- "HTTPS" --> AUTHX
    APPX -- "gRPC sobre HTTPS<br/>caché offline" --> FSX
    APPX -- "HTTPS + token" --> ACX
    APPX -- "HTTPS streaming" --> AIX
    AIX --> GEMX
    APPX -- "HTTPS GET" --> CANVAS

    classDef interfaz fill:#E6DDFF,stroke:#A78BFA,stroke-width:2px,color:#20104F
    classDef logica fill:#FFE3D3,stroke:#FFB38A,stroke-width:2px,color:#5C2200
    classDef datos fill:#D9F8E6,stroke:#5CC98D,stroke-width:2px,color:#0F4D2C
    classDef nube fill:#DDF1FF,stroke:#5AB4EE,stroke-width:2px,color:#0B3A5C
    classDef lumi fill:#FFE0EC,stroke:#FF5C8A,stroke-width:2px,color:#5C0A26
    classDef actor fill:#20104F,stroke:#20104F,stroke-width:2px,color:#FFFFFF
    classDef decision fill:#FFF4D6,stroke:#E0A800,stroke-width:2px,color:#4D3800
    classDef extremo fill:#A78BFA,stroke:#3B2A80,stroke-width:2px,color:#FFFFFF
    class APPX lumi
    class SQLITE,PREFS,WMX datos
    class AUTHX,FSX,ACX,AIX nube
    class GEMX,CANVAS nube
```
