# Stack Tecnológico - GenogramIA

Este documento detalla las tecnologías seleccionadas para el desarrollo de **GenogramIA**, enfocado en una solución multiplataforma nativa y escalable.

## 🚀 Core del Desarrollo
- **Compose Multiplatform (Jetpack Compose)**: Framework principal para compartir la lógica de UI y de negocio entre múltiples plataformas (Android, iOS, Desktop y Web).
- **Kotlin**: Lenguaje de programación único para todo el proyecto, garantizando seguridad de tipos y modernidad.
- **Kotlin Multiplatform (KMP)**: Para compartir el código de datos, modelos y lógica de red.

## 🎨 Interfaz y Gráficos
- **Compose Graphics / Canvas API**: Implementación del lienzo infinito del genograma utilizando las capacidades nativas de dibujo de Compose para un rendimiento óptimo en todas las plataformas.
- **Material3**: Sistema de diseño para una estética moderna y consistente.

## ☁️ Infraestructura y Backend
- **Firebase**:
    - **Cloud Firestore**: Base de Datos NoSQL en tiempo real para gestionar la estructura de los genogramas.
    - **Firebase Storage**: Almacenamiento de archivos y medios asociados a los perfiles.
    - **Firebase Auth**: Autenticación multiplataforma.
- **Ktor**: Cliente HTTP para integración con APIs externas (IA).

## 🧠 Inteligencia Artificial (IA)
- **Integración API (Gemini/OpenAI)**: Procesamiento de datos genealógicos y generación de sugerencias médicas a partir del historial, consumido mediante servicios en Kotlin.

## 🛠️ Herramientas y DevOps
- **GitHub Actions**: Automatización de flujos de Integración Continua y Despliegue Continuo (CI/CD) para todas las plataformas.
- **Koin**: Inyección de dependencias ligera y específica para Kotlin.

## 📦 Despliegue
- **Android**: Google Play Store.
- **iOS**: Apple App Store.
- **Desktop**: Ejecutables nativos (Windows/macOS/Linux).
- **Web**: Despliegue mediante GitHub Pages o Firebase Hosting.
