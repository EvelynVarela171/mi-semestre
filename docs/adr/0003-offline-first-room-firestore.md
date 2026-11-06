# ADR-0003: Offline-first con Room como fuente de verdad y Firestore para sincronizar

- **Estado:** Aceptada
- **Fecha:** 2026-11-01

## Contexto

Los estudiantes usan el celular en el bus o en lugares sin buena señal. Además, los datos no deben perderse al cambiar de teléfono.

## Decisión

- La interfaz **solo lee de Room**.
- Cada escritura va **primero a Room** y después a **Firestore**, que deja en cola lo que no pudo subir.
- El `Sincronizador` escucha las tres colecciones del usuario en Firestore y, cuando llegan cambios, **reemplaza** el contenido de Room en una sola transacción (`SincronizacionDao.reemplazar`).
- Un indicador muestra el estado: conectando, sincronizado, cambios por subir o sin conexión.

## Alternativas consideradas

| Opción | A favor | En contra |
|---|---|---|
| Solo Room | Simple y offline | Los datos se pierden al cambiar de teléfono |
| Solo Firestore con su caché | Menos código | Consultas locales limitadas y más difícil de probar |
| **Room + Firestore** | Respuesta inmediata, funciona offline y hay respaldo en la nube | Dos copias de los datos que hay que mantener iguales |

## Consecuencias

- **Positivas:** la pantalla se actualiza al instante; sin internet todo sigue funcionando; otro teléfono recupera los datos al iniciar sesión.
- **Negativas:** reemplazar todo en cada cambio es simple pero no es lo más eficiente con muchos datos (aceptable para un semestre); si dos teléfonos editan lo mismo sin conexión, gana la última escritura.
- **Relacionado:** HU-03, RNF-01, RNF-02, [ADR-0009](0009-migracion-destructiva-de-room.md).
