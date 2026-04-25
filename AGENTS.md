# 🤖 Instrucciones para Agentes de IA (AGENTS.md)

Este documento define el contexto, las reglas y las directrices arquitectónicas para cualquier agente de inteligencia artificial (ej. Antigravity, Cursor, Windsurf, Cline) que interactúe con el código fuente de **GenogramIA**.

## 📌 Contexto del Proyecto
**GenogramIA** es una aplicación multiplataforma (Android, iOS, Desktop, Web) para la creación y gestión de genogramas (árboles genealógicos con historial médico). El objetivo principal es ofrecer un lienzo infinito y fluido donde los usuarios puedan trazar sus relaciones familiares y detectar patrones de salud.

## 🛠 Stack Tecnológico Principal
- **Lenguaje**: Kotlin
- **Framework**: Kotlin Multiplatform (KMP) y Compose Multiplatform
- **UI**: Compose Graphics / Canvas API (para el lienzo infinito) y Material 3
- **Backend/DB**: Firebase (Auth, Firestore, Storage)
- **Red/API**: Ktor
- **Inyección de Dependencias**: Koin

## 📏 Reglas y Directrices de Código

### 1. Arquitectura Multiplataforma
- Mantén una estricta separación de responsabilidades usando Clean Architecture.
- **Data**: Implementaciones concretas de repositorios, DTOs, Firebase y Ktor.
- **Domain**: Casos de uso puros, modelos de dominio, interfaces de repositorios. Código 100% Kotlin sin dependencias de frameworks externos.
- **Presentation**: ViewModels y UI en Compose. Usa `StateFlow` para exponer estados inmutables a la UI.
- Maximiza el código en el módulo compartido (`shared`). El código específico de plataforma (`androidApp`, `iosApp`, etc.) debe ser mínimo.

### 2. UI y Compose
- Utiliza **Material Design 3** para los componentes estándar. Revisa la skill `material-3` (`.agents/skills/material-3`) para más detalles.
- **Lienzo Infinito**: Para el árbol genealógico, utiliza **Canvas API** nativo de Compose. Debe soportar gestos de arrastre (pan) y zoom (pinch-to-zoom).
- Los estados de UI deben ser inmutables. 
- Revisa la skill `design` (`.agents/skills/design`) para el sistema de diseño propio del proyecto.
- **Previews**: Toda pantalla o componente de UI en Compose debe incluir obligatoriamente una función de `@Preview` para facilitar la visualización y el desarrollo rápido.

### 3. Buenas Prácticas en Kotlin
- Prefiere la inmutabilidad (`val` sobre `var`, colecciones inmutables como `List` en lugar de `MutableList` para exponer datos).
- Usa Corrutinas (`suspend functions`) y `Flow` para operaciones asíncronas y reactividad.
- Escribe código seguro contra nulos y utiliza las características funcionales de Kotlin.
- Escribe el código (nombres de variables, clases, métodos) en **inglés**. La interfaz de usuario (textos visibles) debe ser en español.
- No debe haber ningún texto "hardcodeado", deben estar todos alojados en los recursos.

### 4. Persistencia y Modo Invitado
- Soporta dos modos de almacenamiento según `REQUIREMENTS.md`:
  - **Autenticado**: Guarda en la nube vía Firebase Firestore.
  - **Invitado**: Guarda temporalmente en memoria.
- Utiliza el patrón Repositorio para abstraer de dónde provienen o dónde se guardan los datos.

## 🎭 Roles de Agentes

Cuando se te asigne una tarea en este proyecto, adopta uno de los siguientes enfoques según el contexto:

- **Compose UI Agent**: Enfocado en renderizado de gráficos (Canvas), animaciones, gestión de gestos táctiles complejos e implementación de componentes Material 3.
- **KMP Architecture Agent**: Enfocado en la estructura de módulos, inyección de dependencias (Koin), modelos de datos y lógica de negocio multiplataforma.
- **Cloud/Backend Agent**: Enfocado en la integración segura y eficiente con Firebase y llamadas de red usando Ktor.

## 📝 Pasos antes de implementar
1. Lee `REQUIREMENTS.md` para entender la historia de usuario exacta.
2. Revisa `TECH_STACK.md` para asegurar que las bibliotecas propuestas coinciden.
3. **Revisión de Diseños**: Es **obligatorio** consultar el directorio `.agents/screens` para ver los mockups de las pantallas afectadas y asegurar la fidelidad visual.
4. Asegúrate de tener en cuenta que el código debe compilar para Android, iOS, Desktop y Web. Evita APIs exclusivas de la JVM (como `java.util.Date` o `java.io.File`) en el código compartido; usa alternativas KMP (como `kotlinx-datetime` o `okio`).
