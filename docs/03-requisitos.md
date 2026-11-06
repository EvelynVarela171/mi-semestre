# 3. Requisitos

## Requisitos funcionales

| ID | Requisito | Historia |
|---|---|---|
| RF-01 | El sistema permite registrarse e iniciar sesión con correo y contraseña. | HU-01, HU-02 |
| RF-02 | El sistema recuerda la sesión iniciada al volver a abrir la app. | HU-02 |
| RF-03 | El sistema guarda materias, notas, entregas y perfil en la nube, separados por usuario. | HU-03 |
| RF-04 | El sistema permite crear y borrar materias con nombre y créditos; al borrar una materia se borran sus notas. | HU-04 |
| RF-05 | El sistema permite crear, editar y borrar notas con nombre, peso (%) y valor opcional de 0.0 a 5.0. | HU-05 |
| RF-06 | El sistema calcula el promedio evaluado y la nota necesaria para llegar a 3.0 en cada materia. | HU-06 |
| RF-07 | El sistema calcula el promedio del semestre ponderado por créditos y el porcentaje de avance. | HU-07 |
| RF-08 | El sistema permite crear, editar, marcar como lista, desmarcar y borrar entregas. | HU-08, HU-09 |
| RF-09 | El sistema filtra las entregas por día de la semana actual. | HU-09 |
| RF-10 | El sistema importa entregas desde un enlace iCal (`webcal://` o `https://`) de los próximos 180 días. | HU-10 |
| RF-11 | El sistema actualiza el calendario conectado de forma manual y una vez al día. | HU-10, HU-12 |
| RF-12 | El sistema no sobrescribe entregas editadas a mano ni vuelve a mostrar importadas que se borraron. | HU-09, HU-10 |
| RF-13 | El sistema sugiere crear las materias del calendario que aún no existen. | HU-11 |
| RF-14 | El sistema envía a las 8:00 una notificación por cada entrega pendiente de hoy o mañana, una vez al día; si la app se abre después de esa hora sin aviso previo, lo envía al abrir. | HU-12 |
| RF-15 | El sistema elige una prioridad diaria: vencidas, luego de hoy, luego la de mayor peso en 7 días. | HU-13 |
| RF-16 | El sistema genera con Gemini un primer paso para la prioridad y lo guarda por el resto del día. | HU-13 |
| RF-17 | El sistema responde preguntas del estudiante usando sus materias, notas y entregas como contexto. | HU-14 |
| RF-18 | El sistema muestra reacciones de Lumi al registrar notas, completar entregas y subir de nivel. | HU-15, HU-16 |
| RF-19 | El sistema calcula puntos, niveles y logros. | HU-16 |
| RF-20 | El sistema permite editar universidad y semestre del carné. | HU-17 |
| RF-21 | Al cerrar sesión, el sistema borra los datos locales; si entra otra cuenta, olvida el enlace del calendario. | HU-18 |

## Requisitos no funcionales

| ID | Categoría | Requisito | Cómo se cumple |
|---|---|---|---|
| RNF-01 | Disponibilidad | La app funciona sin internet para leer y guardar datos. | Room como fuente de verdad y caché offline de Firestore ([ADR-0003](adr/0003-offline-first-room-firestore.md)) |
| RNF-02 | Rendimiento | Los cambios se ven en pantalla al instante, sin esperar a la red. | La interfaz observa `Flow` de Room; la nube se actualiza en segundo plano |
| RNF-03 | Seguridad | Un usuario no puede leer ni escribir datos de otro. | Reglas de Firestore con `request.auth.uid == uid` |
| RNF-04 | Seguridad | La API de Gemini solo acepta llamadas desde la app. | Firebase App Check ([ADR-0005](adr/0005-gemini-con-firebase-ai-logic.md)) |
| RNF-05 | Privacidad | Gemini no recibe el correo ni el enlace del calendario. | `contextoParaLumi` solo arma materias, notas y entregas |
| RNF-06 | Privacidad | El enlace del calendario no sale del teléfono. | `EnlaceCalendario` en `SharedPreferences` privadas ([ADR-0008](adr/0008-enlace-del-calendario-solo-local.md)) |
| RNF-07 | Robustez | Un calendario enorme o lento no bloquea la app. | Límite de 5 MB y 15 s, lectura en `Dispatchers.IO` |
| RNF-08 | Robustez | Si Gemini falla, la app sigue funcionando. | Texto local de respaldo en el plan y mensaje claro en el asistente |
| RNF-09 | Costo | El proyecto funciona sin pagar servicios. | Firebase Spark, Gemini en nivel gratuito, frase del día en caché |
| RNF-10 | Compatibilidad | Funciona desde Android 8.0 (API 26). | `minSdk = 26`, probado en API 37 |
| RNF-11 | Usabilidad | Las acciones frecuentes se hacen con un gesto. | Deslizar para marcar o borrar, días tocables, preguntas sugeridas |
| RNF-12 | Usabilidad | Los textos están en español claro y con singular y plural correctos. | Revisado en cada pantalla ("1 crédito", "1 entrega nueva") |
| RNF-13 | Accesibilidad | Los botones de solo ícono se pueden leer con TalkBack. | `contentDescription` como "Marcar lista: …" y "Borrar: …" |
| RNF-14 | Mantenibilidad | La lógica de negocio se puede probar sin Android. | Funciones puras en `data/` con 37 pruebas unitarias |
| RNF-15 | Batería | Las tareas en segundo plano no se ejecutan de más. | Un único trabajo periódico diario con WorkManager ([ADR-0007](adr/0007-workmanager-recordatorios.md)) |

## Matriz de trazabilidad

Relaciona cada historia con el código que la implementa y la prueba que la cubre.

| Historia | Código principal | Prueba automática | Verificada en emulador |
|---|---|---|---|
| HU-01, HU-02 | `SesionRepository`, `LoginViewModel` | — | ✅ |
| HU-03 | `Sincronizador`, `NubeRepository` | `SincronizacionTest` | ✅ |
| HU-04, HU-05 | `MateriaRepository`, `NotaRepository` | — | ✅ |
| HU-06 | `CalculadoraNotas.notaNecesaria` | `CalculadoraNotasTest` | ✅ |
| HU-07 | `CalculadoraNotas.resumenSemestre` | `ResumenSemestreTest` | ✅ |
| HU-08, HU-09 | `EntregaRepository`, `EntregasViewModel` | `CalendarioTest` (editadas y ocultas) | ✅ |
| HU-10 | `Calendario.kt`, `CalendarioRepository` | `CalendarioTest` | ✅ |
| HU-11 | `materiasSinCrear` | `CalendarioTest` | ✅ |
| HU-12 | `RecordatorioWorker` | Prueba manual en emulador (05/11) | Hecha: llegó "Mañana entregas: …" al abrir y no se repitió |
| HU-13 | `planDelDia`, `FraseDelPlan` | `PlanDelDiaTest` | ✅ |
| HU-14 | `contextoParaLumi`, `AsistenteViewModel` | `ContextoLumiTest` | ✅ |
| HU-15, HU-16 | `ReaccionLumi`, `PerfilScreen` | — | ✅ |
| HU-17 | `PerfilRepository` | — | ✅ |
| HU-18 | `Sincronizador`, `enlaceDeOtraCuenta` | `CalendarioTest` + prueba manual (05/11) | Hecha: al salir se vació la base local y el enlace se conservó para la misma cuenta |
