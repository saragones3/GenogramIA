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
- Revisa la skill `design` (`.agents/skills/design`) para el sistema de diseño propio del proyecto.
- **Lienzo Infinito**: Para el árbol genealógico, utiliza **Canvas API** nativo de Compose. Debe soportar gestos de arrastre (pan) y zoom (pinch-to-zoom).
- Los estados de UI deben ser inmutables.
- **Previews**: Toda pantalla o componente de UI en Compose debe incluir obligatoriamente una función de `@Preview` para facilitar la visualización y el desarrollo rápido. En la preview debe usar el tema de la app, `GenogramiaTheme`.
- **Patrón de Pantallas (Screen Pattern)**: Los composables principales de cada pantalla (terminados en `Screen`) deben actuar como "State Holders". Deben recibir el ViewModel, extraer el estado (`StateFlow.collectAsState()`) y pasarlo junto con las lambdas de eventos a un sub-composable privado (ej. `RegistrationContent`). **Está terminantemente prohibido pasar el ViewModel a los sub-composables de contenido o componentes pequeños**, para garantizar que las Previews funcionen sin dependencias complejas y para facilitar el testing de UI.

### 3. Buenas Prácticas en Kotlin
- Prefiere la inmutabilidad (`val` sobre `var`, colecciones inmutables como `List` en lugar de `MutableList` para exponer datos).
- Usa Corrutinas (`suspend functions`) y `Flow` para operaciones asíncronas y reactividad.
- Escribe código seguro contra nulos y utiliza las características funcionales de Kotlin.
- Escribe el código (nombres de variables, clases, métodos) en **inglés**.
- No debe haber ningún texto "hardcodeado", deben estar todos alojados en los recursos.
- **Orden y Visibilidad de Funciones**: Las funciones públicas deben ir al principio del archivo/clase y las privadas al final. Por defecto, todas las funciones deben ser `private` a no ser que se utilicen fuera de su archivo o clase, en cuyo caso serán públicas.

### 4. Persistencia y Modo Invitado
- Soporta dos modos de almacenamiento según `REQUIREMENTS.md`:
  - **Autenticado**: Guarda en la nube vía Firebase Firestore.
  - **Invitado**: Guarda temporalmente en memoria.
- Utiliza el patrón Repositorio para abstraer de dónde provienen o dónde se guardan los datos.

### 5. Gestión de Dependencias
- Utiliza únicamente dependencias estables. Queda estrictamente prohibido el uso de versiones alpha, beta o release candidate (RC) en el archivo de catálogo de versiones o archivos build.gradle.

### 6. Calidad de Código y Linting
- **Spotless**: Antes de realizar un commit o abrir una Pull Request, es **obligatorio** ejecutar `./gradlew spotlessApply` para asegurar que el código cumple con las reglas de formato (ktlint).
- **Detekt**: El código debe pasar el análisis estático sin errores. Ejecuta `./gradlew detekt` para verificar la calidad del código y detectar code smells.
- Estos checks se ejecutan automáticamente en el pipeline de CI/CD para cada Pull Request a la rama `master`.

### 7. Testing Automático
- **Obligatoriedad**: Siempre que se cree o modifique un **ViewModel**, **UseCase** o **Repository**, es estrictamente obligatorio escribir o actualizar sus pruebas unitarias correspondientes en `commonTest` (o en su source set correspondiente si es código específico de plataforma).
- **Fakes vs Mocks**: Fomenta el uso de implementaciones _Fake_ de las interfaces en lugar de librerías de _Mocking_ cuando sea posible, para mantener las pruebas robustas y fácilmente portables en KMP.
- **Corrutinas**: Usa `kotlinx-coroutines-test` (`runTest`, `UnconfinedTestDispatcher`) y `Turbine` para probar `Flow` y `StateFlow`.

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
