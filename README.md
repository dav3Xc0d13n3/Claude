# AI Workspace

A modern, full-featured Android application for multi-provider AI interaction, workspace management, and developer tools. Built with **Kotlin**, **Jetpack Compose**, **Material 3**, and **Room Database**.

---

## 🌟 Key Features

- **Multi-Provider AI Chat**: Connect with Gemini, OpenAI, Claude, and custom API endpoints seamlessly.
- **Custom AI Agents & Skills**: Create, configure, and invoke custom AI personas and skills tailored for specific workflows.
- **Workspaces & Knowledge Base**: Organize documents, notes, and context into dedicated workspaces for accurate contextual querying.
- **Image & Voice Studio**: Generative visual tools and speech-to-text / text-to-speech audio interaction modes.
- **GitHub Integration**: Repository navigation and developer tools integration.
- **Plugins & Extensions**: Extend capabilities with custom API plugins and tools.
- **Local Persistence & Privacy**: Fast, offline-first local state and message storage powered by Room Database.
- **Modern Material 3 UI**: Clean, responsive layout with fluid animations and dynamic dark/light theme options.

---

## 🏗️ Architecture & Tech Stack

- **UI Framework**: Jetpack Compose with Material 3 components
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture
- **Database**: Room Database with KSP (Kotlin Symbol Processing)
- **Async & Reactive**: Kotlin Coroutines & `StateFlow`
- **Navigation**: Jetpack Navigation Compose
- **Networking**: OkHttp / Retrofit API Client with `BuildConfig` secret handling

---

## 📁 Package & Directory Structure

```text
app/src/main/java/com/example/
├── aiworkspace/
│   └── MainActivity.kt          # Main Activity entry point
├── data/
│   ├── api/                     # AI Client & REST services
│   ├── local/                   # Room Database & DAOs
│   ├── model/                   # Data models & entities
│   └── repository/              # Workspace & AI Repository
└── ui/
    ├── components/              # Reusable Compose UI components
    ├── screens/                 # Main screen composables
    ├── theme/                   # Material 3 colors, typography & theme
    └── viewmodel/               # WorkspaceViewModel & state management
```

---

## 🛠️ Build & Setup Instructions

### Prerequisites
- Android Studio Ladybug or later
- JDK 17
- Android SDK 35 (Minimum SDK 24)

### Building the Project

1. **Clone or Open the Project** in Android Studio.
2. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
3. **Build Release APK**:
   ```bash
   ./gradlew assembleRelease
   ```

---

## 🔐 API Keys & Environment Variables

API keys (such as Gemini, OpenAI, or Claude API keys) are configured dynamically within the application settings UI or passed securely through build secrets. Keys are stored safely in local encrypted storage.

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more details.
