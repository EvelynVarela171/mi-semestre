# ADR-0005: Gemini mediante Firebase AI Logic y App Check

- **Estado:** Aceptada
- **Fecha:** 2026-11-02

## Contexto

Lumi necesita un modelo de lenguaje para el asistente y el plan del día. Una clave de API dentro del APK puede extraerse y usarse por otros, y el proyecto debe costar cero.

## Decisión

- Llamar a **Gemini (`gemini-3.5-flash-lite`)** a través de **Firebase AI Logic**, sin clave de API en el código.
- Proteger las llamadas con **App Check**: proveedor de depuración en *debug* y Play Integrity en *release*.
- Usar dos configuraciones con instrucciones distintas: una para el chat (máximo 120 palabras, sin hacer la tarea) y otra para la frase del día (máximo 12 palabras, sin saludo).
- Guardar la frase del día en caché por fecha y entrega para no repetir llamadas.

## Alternativas consideradas

| Opción | A favor | En contra |
|---|---|---|
| API de Gemini directa con clave | Simple | La clave queda expuesta en el APK |
| Servidor propio que llame a Gemini | Clave segura | Hay que mantener un servidor |
| Otro proveedor de IA | — | De pago o sin integración con Firebase |
| **Firebase AI Logic + App Check** | Sin clave en la app, nivel gratuito, mismo proyecto de Firebase | Cada emulador de desarrollo necesita registrar su token de App Check |

## Consecuencias

- **Positivas:** respuestas con contexto real; el modelo liviano cabe en la cuota gratuita; si Gemini falla, la app muestra un texto local.
- **Negativas:** la cuota gratuita puede agotarse (se muestra un mensaje claro) y el nivel gratuito depende de Google; el escaneo HTTPS de algunos antivirus corta las respuestas en el emulador.
- **Relacionado:** HU-13, HU-14, RNF-04, RNF-05, RNF-08.
