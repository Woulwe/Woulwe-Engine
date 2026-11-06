# Woulwe Engine

Original cross-platform game engine for Windows and Android.

## Milestone 0.2.0 — Woulwe Graphics foundation

The repository now includes:
- C++20 engine core and scene objects
- 3D vector and matrix math
- Perspective camera and view-projection matrix
- Indexed cube mesh
- CPU triangle rasterizer with depth buffer and directional diffuse lighting
- PPM frame output for renderer smoke-testing

## Build (Windows / desktop)
Requirements: CMake 3.22+ and a C++20 compiler.

```bash
cmake -S . -B build -DCMAKE_BUILD_TYPE=Release
cmake --build build --config Release
```

Run `WoulweEngineDemo`. It writes `woulwe_frame.ppm` in the working directory.
Open that PPM image with an image viewer to inspect the rendered test scene.

## Android
The shared engine core is platform-independent and the Android NDK entry point
is under `platform/android`. The current software renderer is a portable
reference renderer; an Android app shell and GPU surface integration are still
required before an installable Android build is available.

## Roadmap
1. Core and scene model
2. Graphics foundation (current): math, camera, mesh, rasterizer, depth, light
3. Vulkan backend for Windows and Android, with surface/window integration
4. Materials, textures, model import, and editor viewport
5. Input, physics, animation, audio, scripting, AI and multiplayer
6. Editor and Android/Windows packaging

## Architecture rule
Keep engine logic platform-independent. Platform APIs belong under
`platform/`; rendering backends belong under `engine/graphics/`.
