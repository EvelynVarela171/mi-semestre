# ADR-0004: Firebase Authentication con correo y contraseña

- **Estado:** Aceptada
- **Fecha:** 2026-11-01

## Contexto

Cada estudiante necesita su propio espacio de datos, y la sincronización requiere identificar al usuario.

## Decisión

Usar **Firebase Authentication** con **correo y contraseña**. El `uid` de la cuenta es la raíz de todos los datos en Firestore (`usuarios/{uid}/...`).

## Alternativas consideradas

| Opción | A favor | En contra |
|---|---|---|
| Inicio con Google | Un toque para entrar | Requiere configurar la huella SHA-1 y no todos quieren usar su cuenta personal |
| Servidor propio | Control total | Hay que programar y mantener la seguridad, las contraseñas y el servidor |
| **Correo y contraseña en Firebase** | Gratis, seguro e integrado con las reglas de Firestore | El usuario debe recordar otra contraseña |

## Consecuencias

- **Positivas:** las reglas de Firestore usan `request.auth.uid` para aislar los datos; la sesión se recuerda sola.
- **Negativas:** todavía no hay recuperación de contraseña dentro de la app.
- **Relacionado:** HU-01, HU-02, RNF-03.
