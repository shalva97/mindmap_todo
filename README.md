# MindMap Todo - Kotlin Multiplatform Project

A full-stack Kotlin Multiplatform application consisting of:

## Projects

### `composeApp` `/composeApp/src/`
- **Main shared source** (`commonMain`) - Code shared across all platforms
- Platform-specific code:
  - `androidMain` - Android-specific implementation
  - `jvmMain` - JVM/Desktop implementation
  - `webMain` - Web (Wasm) implementation

### `shared` `/shared/src/`
- Shared library with Kotlin Multiplatform support
- Targets: Android, JVM, JavaScript (browser), Wasm
- Uses Kotlin serialization JSON for data interchange

### `server` `/server/src/main/kotlin/`
- Ktor server backend
- Netty-based networking
- Logback logging

## Target Platforms

- **Android** - Compose UI apps
- **Desktop (JVM)** - Desktop applications  
- **Web** - Wasm for modern browsers, JS for legacy support
- **Server** - Backend API service

## Quick Start

### Build All Targets (Debug)
```shell
# Windows
.\gradlew.bat :composeApp:assembleDebug

# macOS/Linux
./gradlew :composeApp:assembleDebug
```

### Run Desktop Application (JVM)
```shell
# Windows
.\gradlew.bat :composeApp:run

# macOS/Linux
./gradlew :composeApp:run
```

For more build targets (Web), see the full README at https://github.com/JetBrains/compose-multiplatform
