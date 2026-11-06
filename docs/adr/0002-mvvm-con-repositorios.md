# ADR-0002: Arquitectura MVVM con repositorios

- **Estado:** Aceptada
- **Fecha:** 2026-10-30

## Contexto

La app combina varias fuentes de datos (Room, Firestore, Gemini, un calendario remoto) y la lógica de notas debía poder probarse sin un teléfono.

## Decisión

Usar **MVVM** (*Model-View-ViewModel*) con una capa de **repositorios**:

- **View:** pantallas Compose que solo muestran estado y envían eventos.
- **ViewModel:** guarda el estado de la pantalla como `StateFlow` y sobrevive a la rotación.
- **Repositorio:** única puerta a cada fuente de datos.
- **Lógica pura:** cálculos en funciones u objetos sin Android (`CalculadoraNotas`, `planDelDia`, `cambiosDeCalendario`).

## Alternativas consideradas

| Opción | A favor | En contra |
|---|---|---|
| Todo en la Activity | Rápido al inicio | Imposible de probar y se pierde el estado al girar |
| MVI | Estado único y predecible | Más código para una app de este tamaño |
| Clean Architecture con casos de uso | Muy desacoplada | Demasiadas capas para un proyecto individual con plazo corto |
| **MVVM + repositorios** | Recomendada por Google, simple y fácil de probar | Los ViewModel crean sus repositorios a mano (sin inyección de dependencias) |

## Consecuencias

- **Positivas:** la lógica de negocio tiene 37 pruebas unitarias que corren sin emulador; cambiar Firestore por otra nube solo toca los repositorios.
- **Negativas:** sin Hilt, las dependencias se crean dentro de cada ViewModel; para probar los ViewModel habría que inyectarlas. Queda como deuda técnica.
- **Relacionado:** [ADR-0003](0003-offline-first-room-firestore.md), [Buenas prácticas](../05-buenas-practicas.md).
