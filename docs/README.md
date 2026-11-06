# Documentación de Mi Semestre

Documentación técnica y funcional de la app. El [README principal](../README.md) explica qué es la app y cómo instalarla; aquí está el detalle de cómo se pensó, se diseñó y se construyó.

## Índice

| # | Documento | Qué contiene |
|---|---|---|
| 1 | [Visión y alcance](01-vision-y-alcance.md) | Problema, usuarios, objetivos, alcance y glosario |
| 2 | [Historias de usuario](02-historias-de-usuario.md) | 20 historias con criterios de aceptación, prioridad y estado |
| 3 | [Requisitos](03-requisitos.md) | Requisitos funcionales y no funcionales, con trazabilidad |
| 4 | [Diagramas UML](04-diagramas-uml.md) | Casos de uso, clases, secuencia, estados, actividad, componentes y despliegue |
| 5 | [Buenas prácticas](05-buenas-practicas.md) | Principios aplicados, convenciones, pruebas, seguridad y deuda técnica |
| 6 | [Historial del proyecto](06-historial.md) | Cómo avanzó el desarrollo, commit por commit |
| 7 | [Decisiones de arquitectura (ADR)](adr/README.md) | Por qué se eligió cada tecnología y diseño |

## Cómo leer esta carpeta

- **Para entender el producto:** empieza por la visión y las historias de usuario.
- **Para entender el código:** ve a los diagramas UML y después a los ADR.
- **Para evaluar la calidad:** revisa buenas prácticas y requisitos no funcionales.

Los diagramas están escritos en [Mermaid](https://mermaid.js.org), así que GitHub los dibuja directamente y se actualizan como texto junto al código.

## Convenciones

- Las historias se identifican como **HU-01**, los requisitos como **RF-01** / **RNF-01** y las decisiones como **ADR-0001**.
- Los nombres de clases y funciones aparecen `así` y coinciden con el código en `app/src/main/java/com/evelynvarela/misemestre/`.
- Estado de una historia: ✅ hecha · 🔜 trabajo futuro.
