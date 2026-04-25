---
name: issue-completion-checks
description: Definición de los pasos obligatorios que deben seguirse al completar cada issue dentro del proyecto GenogramIA.
---

# Workflow al Finalizar un Issue

Este documento define los pasos obligatorios que deben seguirse al completar cada issue dentro del proyecto GenogramIA.

## Pasos de Finalización

Al terminar el desarrollo de una tarea o la resolución de un error (issue), se deben realizar las siguientes acciones en orden:

### 1. Sincronización (Sync)
- Asegurarse de que la rama local esté actualizada con la rama base (normalmente `master` o `develop`).
- Resolver cualquier conflicto de fusión si es necesario.
- Ejecutar `git pull` y `git push` para sincronizar los cambios locales con el repositorio remoto.

### 2. Construcción del Proyecto (Build)
1. Verificar que todos los tests pasen exitosamente.
2. Ejecutar el comando `./gradlew build` para validar que no hay errores de compilación introducidos por los cambios.
3. Ejecutar el comando `./gradlew :composeApp:assembleDebug` para validar que la aplicación se ejecuta correctamente en Android.
4. Ejecutar el comando `./gradlew :composeApp:run` para validar que la aplicación se ejecuta correctamente en Desktop.
5. Ejecutar el comando `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` para validar que la aplicación se ejecuta correctamente en Web.
6. **Fidelidad Visual**: Comparar el resultado final con los diseños en `.agents/screens` para asegurar que se han respetado los espaciados, colores y reglas del sistema de diseño.

### 3. Crear Pull Request (PR)
- Crear una solicitud de extracción (Pull Request) en GitHub para integrar los cambios en la rama principal.
- Incluir una descripción clara de los cambios realizados y referenciar el número del issue (ej. `Fixes #123`).
- Utilizar la herramienta `gh pr create` o la interfaz web de GitHub.

---

> [!IMPORTANT]
> No se debe considerar un issue como finalizado hasta que se hayan completado satisfactoriamente estos tres pasos.
