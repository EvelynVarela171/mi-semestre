# ADR-0009: Migración destructiva de Room durante el desarrollo

- **Estado:** Aceptada (temporal)
- **Fecha:** 2026-11-01

## Contexto

El esquema de la base local cambió varias veces (hoy va en la versión 5: se agregaron las entregas y los campos `importada`, `oculta` y `editada`). Escribir una migración para cada cambio tomaba tiempo durante el desarrollo.

## Decisión

Usar `fallbackToDestructiveMigration(dropAllTables = true)`: si cambia la versión, Room borra la base local y la vuelve a crear.

## Por qué es seguro aquí

Room es una copia: los datos de verdad están en Firestore ([ADR-0003](0003-offline-first-room-firestore.md)). Al borrarse la base local, el `Sincronizador` la vuelve a llenar desde la nube en segundos.

## Alternativas consideradas

| Opción | A favor | En contra |
|---|---|---|
| Migraciones manuales | No se borra nada | Una migración por cada cambio y más riesgo de errores durante el desarrollo |
| **Migración destructiva** | Rápida y sin pérdida real, porque la nube tiene los datos | Los cambios aún no subidos se perderían si justo cambia la versión |

## Consecuencias

- **Positivas:** se pudo cambiar el modelo rápido.
- **Negativas:** antes de publicar en Google Play hay que escribir migraciones reales (`Migration`) y probarlas con `MigrationTestHelper`. Queda como deuda técnica.
