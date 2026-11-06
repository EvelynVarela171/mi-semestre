# ADR-0010: Borrado suave de entregas importadas

- **Estado:** Aceptada
- **Fecha:** 2026-11-02

## Contexto

Si el estudiante borra una entrega que vino del calendario, la siguiente actualización la volvería a crear, porque el evento sigue en Canvas.

## Decisión

- Al borrar una entrega **importada**, no se elimina: se marca `oculta = true` y deja de mostrarse (`EntregaDao.verVisibles`).
- Al **editar** una entrega se marca `editada = true` y el calendario ya no la sobrescribe.
- Las entregas **manuales** sí se borran de verdad.

## Alternativas consideradas

| Opción | A favor | En contra |
|---|---|---|
| Borrar de verdad | Simple | La entrega vuelve en la próxima actualización |
| Lista aparte de eventos ignorados | No toca la tabla | Otro dato más que sincronizar |
| **Campo `oculta` en la entrega** | Se sincroniza junto con la entrega en todos los teléfonos | La fila sigue ocupando espacio |

## Consecuencias

- **Positivas:** lo que el usuario borra o edita se respeta, incluso en otros teléfonos.
- **Negativas:** al desconectar el calendario se borran todas las importadas, ocultas incluidas.
- **Relacionado:** HU-09, RF-12, prueba `noRepiteNiDevuelveLoQueYaEstaOculto`.
