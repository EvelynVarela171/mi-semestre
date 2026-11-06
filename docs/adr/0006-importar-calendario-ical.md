# ADR-0006: Importar entregas desde iCal en vez de la API de Canvas

- **Estado:** Aceptada
- **Fecha:** 2026-11-02

## Contexto

Las entregas de la universidad están en Canvas. Se quería que llegaran solas, y que la idea sirviera también para otras universidades y calendarios.

## Decisión

Leer el **enlace iCal** que publica cualquier calendario (Canvas, Google Calendar, Outlook) con un lector propio (`leerCalendario`), sin librerías externas:

- Se aceptan `webcal://` y `https://`; la descarga se limita a 5 MB y 15 segundos.
- Cada evento se identifica con `cal-` más el SHA-256 de su `UID`, para actualizarlo sin duplicarlo.
- Se importan los eventos desde hoy hasta 180 días; las entregas editadas a mano no se tocan.
- Se limpian los nombres de curso de Canvas (`nombreDeMateria`).

## Alternativas consideradas

| Opción | A favor | En contra |
|---|---|---|
| API REST de Canvas con token | Trae también notas y estado de entrega | El token da acceso a toda la cuenta, algunas universidades lo bloquean y solo sirve para Canvas |
| Librería iCal4j | Lector completo | Pesada para Android y más de lo necesario |
| **iCal con lector propio** | Funciona con cualquier calendario, sin permisos extra, liviano y probado | No trae notas ni sabe si ya se entregó en Canvas |

## Consecuencias

- **Positivas:** sirve para cualquier universidad; las pruebas de `CalendarioTest` cubren el lector y las reglas de importación.
- **Negativas:** las notas siguen siendo manuales; si Canvas cambia el formato del título, la limpieza de nombres podría fallar.
- **Relacionado:** HU-10, HU-11, HU-20.
