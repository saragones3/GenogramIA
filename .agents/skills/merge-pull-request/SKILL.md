---
name: merge-pull-request
description: Procedimiento automatizado para mergear la pull request asociada a la rama actual.
user-invocable: true
---

# Workflow al Terminar un Issue

Este documento define los pasos obligatorios que deben seguirse al cerrar una pull request dentro del proyecto GenogramIA.

## Pasos de Inicio

Para cerrar una pull request, se deben realizar las siguientes acciones en orden:

### 1.Merge de la Pull Request y cierre de la rama
- Mergear la pull request.
- Eliminar la rama en local y remoto.

### 2. Cerrar la Issue relacionada
# Reemplaza <ISSUE_NUMBER> con el identificador de la issue
- Ejecutar `gh issue close <ISSUE_NUMBER> --reason "completed"`.

---
