# 1. Visión y alcance

## Visión

> Para estudiantes universitarios que llevan sus notas, fechas y dudas en lugares distintos, **Mi Semestre** es una app Android que reúne materias, notas, promedio y entregas en un solo lugar y, con la ayuda de Lumi, les dice qué hacer primero cada día. A diferencia de una agenda o una hoja de cálculo, calcula cuánto necesitan para aprobar, se conecta con el calendario de su universidad y les responde con sus propios datos.

## El problema

| Situación actual | Consecuencia |
|---|---|
| Las notas están en una hoja de cálculo o en la cabeza | No se sabe cuánto falta para aprobar |
| Las fechas están en Canvas, en el chat del curso o en papel | Se olvidan entregas |
| Cada materia pesa distinto según sus créditos | El promedio del semestre se calcula mal |
| Para pedir ayuda se abre otro chat sin contexto | La respuesta es genérica |

## Usuarios

### Persona principal (ficticia): Valentina, 21 años

- Estudia una carrera virtual o presencial con 3 a 6 materias por semestre.
- Usa Canvas, pero no lo revisa todos los días.
- Quiere saber rápido "cómo voy" y "qué hago hoy", sin llenar formularios largos.
- Usa el celular más que el computador y a veces no tiene internet.

### Actores del sistema

| Actor | Tipo | Rol |
|---|---|---|
| Estudiante | Humano | Usa todas las funciones de la app |
| Firebase Authentication | Sistema externo | Verifica la identidad |
| Cloud Firestore | Sistema externo | Guarda y sincroniza los datos en la nube |
| Gemini (Firebase AI Logic) | Sistema externo | Genera las respuestas y frases de Lumi |
| Calendario iCal (Canvas, Google, Outlook) | Sistema externo | Entrega las fechas de las actividades |
| Android (WorkManager y notificaciones) | Sistema | Ejecuta la actualización y el aviso diario |

## Objetivos

| Objetivo | Cómo se mide |
|---|---|
| Saber el estado académico en segundos | Inicio muestra promedio, avance y prioridad sin tocar nada |
| No olvidar entregas | Importación automática del calendario y aviso diario a las 8:00 |
| Saber cuánto se necesita para aprobar | Cada materia muestra la nota mínima en lo que falta |
| Funcionar sin internet | Todo se lee y se guarda en el teléfono; se sincroniza al volver la conexión |
| Ayuda con contexto | El asistente responde con las materias, notas y entregas reales |

## Alcance

### Dentro del alcance (versión 1.0)

- Cuenta con correo y contraseña, y datos sincronizados en la nube.
- Materias con créditos y notas con porcentaje.
- Promedio por materia, promedio del semestre y nota necesaria para aprobar con 3.0 (escala de 0 a 5).
- Entregas manuales e importadas desde cualquier calendario iCal.
- Sugerencia de materias a partir del calendario.
- Recordatorio diario.
- Lumi: plan del día, asistente con Gemini, reacciones, niveles y logros.

### Fuera del alcance

- Notas importadas desde Canvas (el calendario iCal no las incluye; ver [ADR-0006](adr/0006-importar-calendario-ical.md)).
- Versión para iOS o web.
- Compartir datos entre estudiantes.
- Modo oscuro.

Lo que quedó fuera se lista como trabajo futuro en las [historias de usuario](02-historias-de-usuario.md#trabajo-futuro).

## Supuestos y restricciones

- **Plazo:** proyecto individual con entrega el 16/11/2026.
- **Costo cero:** Firebase en plan gratuito (Spark) y Gemini en nivel gratuito; por eso se usa un modelo liviano (`gemini-3.5-flash-lite`) y frases cortas en caché.
- **Escala de notas:** de 0.0 a 5.0, aprobación con 3.0.
- **Dispositivos:** Android 8.0 (API 26) o superior.

## Glosario

| Término | Significado |
|---|---|
| **Materia** | Asignatura del semestre, con nombre y créditos |
| **Nota** | Evaluación de una materia, con nombre, porcentaje (peso) y valor opcional |
| **Peso** | Porcentaje que vale una nota dentro de su materia |
| **Promedio evaluado** | Promedio de la materia considerando solo lo ya calificado |
| **Nota necesaria** | Nota mínima que hace falta en lo que queda para llegar a 3.0 |
| **Entrega** | Actividad con fecha límite, manual o importada |
| **Entrega importada** | La que viene del calendario iCal; al borrarla se oculta para que no vuelva |
| **Plan del día** | Prioridad que Inicio muestra cada día |
| **Lumi** | La mascota y asistente de la app |
| **iCal / ICS** | Formato estándar de calendarios que publican Canvas, Google y Outlook |
| **Offline-first** | Diseño en el que la app funciona primero con datos locales y sincroniza después |
