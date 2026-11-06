# Decisiones de arquitectura (ADR)

Un **ADR** (*Architecture Decision Record*) registra una decisión importante: qué problema había, qué se eligió, qué otras opciones se consideraron y qué consecuencias trae. Así, quien lea el código sabe *por qué* está hecho así y no solo *cómo*.

Se usa el formato de Michael Nygard: **Contexto → Decisión → Alternativas → Consecuencias**. Un ADR aceptado no se edita: si la decisión cambia, se escribe uno nuevo que lo reemplaza.

| ADR | Decisión | Estado |
|---|---|---|
| [0001](0001-kotlin-y-jetpack-compose.md) | Kotlin y Jetpack Compose para la interfaz | Aceptada |
| [0002](0002-mvvm-con-repositorios.md) | Arquitectura MVVM con repositorios | Aceptada |
| [0003](0003-offline-first-room-firestore.md) | Offline-first: Room como fuente de verdad y Firestore para sincronizar | Aceptada |
| [0004](0004-firebase-authentication.md) | Firebase Authentication con correo y contraseña | Aceptada |
| [0005](0005-gemini-con-firebase-ai-logic.md) | Gemini mediante Firebase AI Logic y App Check | Aceptada |
| [0006](0006-importar-calendario-ical.md) | Importar entregas desde iCal en vez de la API de Canvas | Aceptada |
| [0007](0007-workmanager-recordatorios.md) | WorkManager para la actualización y el aviso diario | Aceptada |
| [0008](0008-enlace-del-calendario-solo-local.md) | El enlace del calendario se guarda solo en el teléfono | Aceptada |
| [0009](0009-migracion-destructiva-de-room.md) | Migración destructiva de Room durante el desarrollo | Aceptada (temporal) |
| [0010](0010-borrado-suave-de-importadas.md) | Borrado suave de entregas importadas | Aceptada |

Para una decisión nueva, copia la [plantilla](plantilla.md) con el número siguiente.
