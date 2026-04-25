---
name: issue-start-automation
description: Procedimiento automatizado para iniciar un nuevo issue creando una rama siguiendo la nomenclatura GitFlow.
---

# Workflow al Iniciar un Issue

Este documento define los pasos obligatorios que deben seguirse al comenzar a trabajar en un nuevo issue dentro del proyecto GenogramIA.

## Pasos de Inicio

Antes de realizar cualquier cambio en el código para un nuevo issue, se deben realizar las siguientes acciones en orden:

### 1. Preparación de la Rama Base
- Asegurarse de estar en la rama `master`.
- Ejecutar `git checkout master`.
- Obtener los últimos cambios con `git pull origin master`.

### 2. Creación de la Rama de Trabajo
- Crear una nueva rama siguiendo la nomenclatura de GitFlow.
- El nombre de la rama debe tener el formato: `<tipo>/<número-issue>-<resumen-del-título-en-minúsculas-y-con-guiones>`.
- El tipo puede ser: `feature`, `task`, `bugfix`, `hotfix`, `release`.
- Ejemplo: Para el issue #12 "Implementar login con Firebase", la rama sería `feature/12-implementar-login-firebase`.
- Ejecutar `git checkout -b <nombre-rama>`.

### 3. Revisión Visual
- Revisar los diseños y prototipos en el directorio `.agents/screens` para asegurar que la implementación coincida con la visión visual del proyecto.
- Si el issue afecta a una pantalla existente, buscar el archivo `.png` correspondiente en `.agents/screens`.

### 4. Publicación Inicial (Opcional)
- Se recomienda publicar la rama inmediatamente para que sea visible en el repositorio remoto: `git push -u origin <nombre-rama>`.

---

> [!IMPORTANT]
> Siempre se debe trabajar en una rama dedicada al issue actual. Nunca se deben realizar commits directamente en `master`.
