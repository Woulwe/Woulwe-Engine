# Woulwe Engine 1.0.0

Woulwe Engine is an original C++20 game-engine foundation with a portable core, a CPU reference 3D renderer, a desktop sample and an Android native application shell.

## Included

- Engine lifecycle and scene object storage
- 3D vector/matrix math and perspective camera
- Orbit and dolly camera controls
- Indexed cube mesh and Wavefront OBJ position/face importer
- CPU triangle rasterizer with depth buffer, diffuse directional lighting and PPM output
- Platform-neutral keyboard/pointer input state
- Basic gravity-driven rigid bodies with ground contact and restitution
- Material parameter data type
- CMake desktop build and automated smoke tests
- Android application project with JNI native-core lifecycle
- GitHub Actions workflows for desktop tests and Android debug APK packaging

## Windows desktop build

Requirements: CMake 3.22+ and a C++20 compiler (Visual Studio 2022 or recent MinGW).

```bash
cmake -S . -B build -DCMAKE_BUILD_TYPE=Debug -DBUILD_TESTING=ON
cmake --build build --config Debug
ctest --test-dir build --output-on-failure
```

Run `WoulweEngineDemo`; it writes `woulwe_frame.ppm` to the working directory.

## Android APK

The Android Studio/Gradle project is in the repository root. It packages the native C++ core through CMake and JNI.

Build with JDK 17, Android SDK 35, NDK 27.2.12479018, CMake 3.22.1 and Gradle 8.9:

```bash
gradle --no-daemon :app:assembleDebug
```

The APK is produced at `app/build/outputs/apk/debug/app-debug.apk`. GitHub Actions uploads the debug APK as the `WoulweEngine-Android-debug` artifact.

## Current product boundary

This is a complete, buildable starter engine baseline, not a feature-equivalent replacement for Unreal or Unity. The Android app now opens an interactive OpenGL ES 2.0 scene editor: it renders a live 3D cube scene, allows orbiting the camera by dragging, adding cubes, toggling the ground grid and resetting the scene. The C++ core is initialized through JNI. The Android editor includes a prompt-based Woulwe Game Creator: the user describes the game in one text field, requests generation through the Leor Supabase Edge Function, and can save a playable HTML5 game directly into Android Downloads. If the Leor inference endpoint is unavailable, the app creates a local playable fallback instead. The current export is a self-contained HTML5 game; it is not a native APK/EXE package. The C++ software renderer remains an offline/reference renderer, not a real-time GPU renderer. OBJ import currently handles positions and polygon faces, not UV/material libraries. Physics currently handles gravity and a ground plane, not a full collision solver. These limits are explicit so the project does not advertise unimplemented systems as finished.

## License

See the repository license file when one is added by the project owner.
