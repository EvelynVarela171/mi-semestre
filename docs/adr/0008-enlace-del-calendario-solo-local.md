# ADR-0008: El enlace del calendario se guarda solo en el teléfono

- **Estado:** Aceptada
- **Fecha:** 2026-11-02

## Contexto

El enlace iCal de Canvas es privado: quien lo tenga puede ver todas las actividades del estudiante. Al principio se borraba al cerrar sesión, y el calendario aparecía "desconectado" cada vez que se volvía a entrar.

## Decisión

- Guardar el enlace solo en las `SharedPreferences` privadas de la app (`EnlaceCalendario`), nunca en Firestore ni en Gemini.
- Guardar junto al enlace el `uid` de su dueña. Al iniciar sesión, si la cuenta es otra (`enlaceDeOtraCuenta`), se olvida; si es la misma, se conserva.

## Alternativas consideradas

| Opción | A favor | En contra |
|---|---|---|
| Guardarlo en Firestore | Se recupera en otro teléfono | El enlace privado sale del dispositivo |
| Borrarlo siempre al cerrar sesión | Máxima privacidad | Hay que pegarlo de nuevo cada vez |
| **Local y con dueña** | Privado y cómodo para la misma cuenta | En otro teléfono hay que pegarlo otra vez |

## Consecuencias

- **Positivas:** el enlace no sale del teléfono y otra cuenta no puede usarlo.
- **Negativas:** al reinstalar la app o cambiar de teléfono hay que conectarlo de nuevo.
- **Relacionado:** HU-10, HU-18, RNF-06.
