# GenogramIA

GenogramIA is a multi-platform application (Android, iOS, Desktop, and Web) designed for the creation and management of genograms (family trees with medical history). The primary goal is to provide a fluid, infinite canvas where users can trace their family relationships and detect health patterns over generations.

## 🌟 Key Features

- **Infinite Canvas:** Navigate large family trees freely with pan and zoom capabilities.
- **Medical History Tracking:** Add and track medical conditions across family members to identify hereditary health patterns.
- **Multi-Platform:** Available on Android, iOS, Desktop (Windows/macOS/Linux), and Web.
- **Authentication & Guest Mode:** 
  - **Authenticated:** Securely save your genograms to the cloud via Firebase.
  - **Guest Mode:** Try the app without registering (data is saved locally in memory during the session).
- **Social Login:** Support for Google and Apple authentication.

## 🛠 Tech Stack

- **Language:** Kotlin
- **Framework:** Kotlin Multiplatform (KMP) & Compose Multiplatform
- **UI:** Compose Graphics / Canvas API (for the infinite canvas) & Material Design 3
- **Backend / Database:** Firebase (Auth, Firestore, Storage)
- **Networking:** Ktor
- **Dependency Injection:** Koin
- **AI Integration:** Integration with Gemini/OpenAI for genealogical data processing and medical suggestions.

## 🏗 Architecture

The project strictly follows **Clean Architecture** principles to maximize code sharing across platforms:
- **Data:** Concrete implementations of repositories, DTOs, Firebase, and Ktor.
- **Domain:** Pure use cases, domain models, and repository interfaces. 100% Kotlin code with no external framework dependencies.
- **Presentation:** ViewModels and UI using Compose. `StateFlow` is used to expose immutable states to the UI.

Most of the codebase resides in the `shared` module, minimizing platform-specific code (`androidApp`, `iosApp`, etc.).

## 📚 Documentation

For more detailed information, please refer to the following documents:
- [`REQUIREMENTS.md`](REQUIREMENTS.md): Detailed user stories and functional requirements (Gherkin format).
- [`TECH_STACK.md`](TECH_STACK.md): Comprehensive breakdown of the technologies used.
- [`AGENTS.md`](AGENTS.md): Guidelines and roles for AI agents interacting with this repository.

## 🚀 Getting Started

*(Instructions for setting up the local development environment, installing dependencies, and running the application on different platforms will be added here).*

## 🤝 Contributing & AI Agent Guidelines

If you are an AI agent or a developer contributing to this project, please ensure you read and adhere to the rules defined in [`AGENTS.md`](AGENTS.md). 

Key rules include:
1. Maintain strict separation of concerns using Clean Architecture.
2. Use Material Design 3 for UI components and native Canvas API for the genogram.
3. Prioritize immutability, Coroutines/Flow, and write code in English (UI text in Spanish).
4. Support both Authenticated (Firebase) and Guest (in-memory) persistence modes.
