# AI Workspace (Android Application & Platform)

A world-class AI assistant workspace inspired by Claude, ChatGPT, Cursor, and GitHub Copilot. Built with modern Kotlin, Jetpack Compose, and Material Design 3, **AI Workspace** brings multi-provider AI intelligence, model switching, agent execution, GitHub integration, custom plugins, skills, and memory into a single unified mobile environment.

---

## 🌟 Key Features

- **Bring Your Own Keys (BYOK):** Multi-provider AI model support (NVIDIA, Gemini, OpenAI, Anthropic, Groq, OpenRouter).
- **Auto Model Detection & Live Switching:** Automatically discover supported models from configured API keys and switch models mid-conversation seamlessly.
- **GitHub Integration:** Connect repositories, view files, write pull requests, and commit code directly from mobile.
- **Plugins & Skills Store:** Enable web search, code execution, image generation, system tools, and custom prompt skills.
- **Autonomous Agent Workflows:** Deploy multi-step autonomous AI agents for complex task planning and execution.
- **Image & Voice Studios:** Native multimodal camera attachment support, audio transcription, and voice assistant mode.
- **Memory & Knowledge Bases:** Contextual memory management with cross-session retrieval and document uploads.
- **Multi-Workspace & Project Management:** Organize chats, prompt snippets, code files, and workflows into isolated project workspaces.
- **Modern Android UX:** Full edge-to-edge layout, AMOLED dark mode, fluid gestures, BackHandler support, dynamic keyboard resizing, and clean typography.

---

## 🛠️ Local Development & Build Setup

### Prerequisites
- **JDK 17** (Temurin or OpenJDK recommended)
- **Android Studio Jellyfish / Ladybug or newer**
- **Android SDK API 34+**
- **Node.js 20+** (Optional, for web/capacitor tooling)

### Building Local Debug APK
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/ai-workspace.git
   cd ai-workspace
   ```
2. Assemble the debug APK using Gradle:
   ```bash
   # Using Gradle wrapper or installed gradle
   gradle assembleDebug
   ```
3. Find the generated debug APK at:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## ⚙️ API Key Security & Configuration

All API keys (Gemini, OpenAI, Anthropic, Groq, NVIDIA, OpenRouter, GitHub) are managed **locally on device** via the in-app **API Key Manager / Settings panel**. They are stored securely in local device storage and are **never hardcoded or exposed** in git repositories or build logs.

---

## 🚀 Automated GitHub Actions CI/CD Pipeline

The project includes pre-configured GitHub Actions workflows in `.github/workflows/`:

### 1. Build & Package Android APK (`.github/workflows/build-apk.yml`)
- **Triggers:** Push to `main`/`master`, pull requests, or manual `workflow_dispatch`.
- **Outputs:**
  - `AI-Workspace-debug.apk`
  - `AI-Workspace-release.apk`
- **Automated Releases:** Automatically publishes a new GitHub Release with attached APK artifacts when changes are pushed to `main` or triggered manually.

### 2. Continuous Integration (`.github/workflows/ci.yml`)
- Runs code quality checks, linting, and Android unit tests (`gradle testDebugUnitTest`).

---

## 🔐 Configuring Release Signing Secrets in GitHub

To produce signed production release APKs in GitHub Actions, add the following secrets in your repository settings (**Settings > Secrets and variables > Actions**):

| Secret Name | Description |
|---|---|
| `ANDROID_KEYSTORE_BASE64` | Base64-encoded string of your Android `.keystore` / `.jks` file |
| `ANDROID_KEYSTORE_PASSWORD` | Password for your keystore store file |
| `ANDROID_KEY_ALIAS` | Key alias name inside the keystore (e.g., `upload`) |
| `ANDROID_KEY_PASSWORD` | Key password for the specified alias |

> **Note:** If signing secrets are not configured, the GitHub Actions pipeline automatically falls back to an unsigned/testing release APK without failing the build.

---

## 📱 Application Metadata & Configuration

- **Application ID:** `com.example.aiworkspace`
- **Application Name:** `AI Workspace`
- **Target SDK:** 36 (Android 14/15 ready)
- **Minimum SDK:** 24 (Android 7.0+)
- **Architecture:** Kotlin, Jetpack Compose, ViewModel, StateFlow, Material3
