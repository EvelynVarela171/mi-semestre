# ADR-0001: Kotlin y Jetpack Compose para la interfaz

- **Estado:** Aceptada
- **Fecha:** 2026-10-28

## Contexto

La app es nativa de Android, la asignatura trabaja con Android Studio y se necesitaban animaciones propias (Lumi, confeti, reacciones) sin depender de imágenes.

## Decisión

Escribir toda la app en **Kotlin** con **Jetpack Compose** y Material 3, sin archivos XML de diseño.

## Alternativas consideradas

| Opción | A favor | En contra |
|---|---|---|
| Vistas XML clásicas | Mucha documentación antigua | Más código, animaciones más difíciles, Google ya no lo recomienda para apps nuevas |
| Flutter | Una sola base para Android e iOS | Otro lenguaje (Dart) y se aleja del enfoque del curso |
| **Compose** | Interfaz declarativa, vistas previas, animaciones simples, estado reactivo | Curva de aprendizaje de la recomposición |

## Consecuencias

- **Positivas:** Lumi se dibuja con código (`LumiAnimada`) y reacciona al estado; las pantallas tienen `@Preview`; la interfaz se redibuja sola al cambiar un `StateFlow`.
- **Negativas:** hay que cuidar qué se recalcula en cada recomposición (por eso se usa `remember`).
- **Relacionado:** [ADR-0002](0002-mvvm-con-repositorios.md).
