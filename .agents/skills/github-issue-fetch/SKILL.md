---
name: github-issue-fetch
description: >
  Procedimiento obligatorio para obtener los detalles de un issue de GitHub con `gh`
  antes de iniciar, implementar o cerrar cualquier issue. Aplica en todos los contextos
  donde se mencionen: crear ramas para issues, implementar issues o cerrar/mergear issues.
user-invocable: false
triggers:
  - crear rama para issue
  - feature branch
  - issue-start-automation
  - implementar issue
  - iniciar issue
  - cerrar issue
  - merge pull request
  - issue-completion-checks
---

# Fetch de Issue en GitHub (Obligatorio)

Este documento define el paso **previo y obligatorio** que debe ejecutarse **siempre** que
el agente vaya a:

- Crear una rama de trabajo para un issue (antes de `issue-start-automation`)
- Implementar un issue (antes de escribir cualquier código)
- Cerrar o mergear un issue (antes de `issue-completion-checks` o `merge-pull-request`)

---

## Paso 0 — Obtener los Detalles del Issue con `gh`

> [!IMPORTANT]
> Este paso es obligatorio y debe completarse **antes** de cualquier otra acción relacionada
> con el issue. Nunca asumas el contenido de un issue a partir del número o título solamente.

### Comandos a ejecutar

```bash
# 1. Obtener el resumen del issue
gh issue view <número-del-issue>

# 2. Obtener el cuerpo completo en JSON (para parsear etiquetas, milestone, etc.)
gh issue view <número-del-issue> --json number,title,body,labels,state,assignees,milestone
```

Sustituye `<número-del-issue>` por el número real proporcionado por el usuario.

### Información que debes extraer

Tras ejecutar los comandos anteriores, identifica y anota:

| Campo | Descripción |
|---|---|
| `title` | Título completo del issue (incluye el código de US, ej. `[US-011]`) |
| `body` | Escenario Gherkin con el comportamiento esperado |
| `labels` | Tipo de trabajo: `feature`, `bugfix`, `task`, etc. |
| `state` | Debe ser `OPEN`; si está `CLOSED`, notifica al usuario antes de continuar |

### Mapeo de etiqueta a tipo de rama

| Label | Tipo de rama GitFlow |
|---|---|
| `feature` | `feature` |
| `bug` / `bugfix` | `bugfix` |
| `task` / `chore` | `task` |
| `hotfix` | `hotfix` |

---

## Cuándo aplicar este skill

| Situación | ¿Aplica este skill? |
|---|---|
| El usuario dice "implementa el issue #N" | ✅ Sí |
| El usuario dice "crea la rama para el issue #N" | ✅ Sí |
| El usuario dice "cierra / mergea el issue #N" | ✅ Sí |
| El usuario ya ha pegado el contenido completo del issue en el chat | ⚠️ Opcional (verifica igualmente con `gh` si hay dudas) |

---

## Ejemplo completo

Para el issue #33:

```bash
gh issue view 33
gh issue view 33 --json number,title,body,labels,state,assignees,milestone
```

Resultado esperado (resumen):

```
title:  [US-011] Scenario: Guest user creates a new tree
state:  OPEN
labels: feature
```

Con esta información ya puedes:
1. Determinar el tipo de rama → `feature`
2. Extraer el código de historia de usuario → `US-011`
3. Consultar `REQUIREMENTS.md` buscando `## US-011` para obtener todos los escenarios
4. Continuar con el skill correspondiente (`issue-start-automation`, implementación, o `merge-pull-request`)

---

> [!NOTE]
> Si el usuario no proporciona el número del issue, pregúntale antes de ejecutar cualquier
> comando. Nunca inventes ni asumas el número de issue.
