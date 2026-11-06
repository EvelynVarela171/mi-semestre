# ADR-0007: WorkManager para la actualización y el aviso diario

- **Estado:** Aceptada
- **Fecha:** 2026-11-02

## Contexto

El calendario debe actualizarse y el aviso de las 8:00 debe llegar aunque la app esté cerrada, sin gastar batería.

## Decisión

Programar con **WorkManager** un único trabajo periódico (`RecordatorioWorker`) cada 24 horas, con un retraso inicial calculado para que caiga a las 8:00. El trabajo:

1. No hace nada si no hay sesión iniciada.
2. Actualiza el calendario conectado.
3. Envía una notificación por cada entrega pendiente de hoy o mañana, una sola vez por día.

## Alternativas consideradas

| Opción | A favor | En contra |
|---|---|---|
| `AlarmManager` exacto | Hora exacta | Requiere un permiso especial desde Android 12 y gasta más batería |
| Notificaciones push desde un servidor | Centralizado | Necesita un servidor |
| **WorkManager** | Sobrevive a reinicios y respeta el ahorro de batería | La hora puede variar unos minutos |

## Consecuencias

- **Positivas:** un solo trabajo diario (`ExistingPeriodicWorkPolicy.KEEP`), sin permisos especiales.
- **Negativas:** Android puede retrasar el aviso en modo de ahorro de batería.
- **Relacionado:** HU-12, RNF-15.
