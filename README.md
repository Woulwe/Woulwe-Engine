# Woulwe Engine

Original cross-platform game engine for Windows and Android.

## Milestone 0.3.0 — Asset and camera foundation

- C++20 engine core and scene objects
- 3D vector/matrix math and perspective camera
- Camera orbit and dolly controls
- Indexed cube mesh and Wavefront OBJ geometry import
- CPU triangle rasterizer, depth buffer and directional diffuse lighting
- PPM frame output, CMake smoke tests and GitHub Actions CI

### OBJ importer scope
The first importer reads OBJ vertex positions (v) and polygon faces (f), including positive/negative position indices and slash-separated face tokens. Polygons are fan-triangulated and receive generated flat normals. UVs, material libraries and glTF are future work.

## Build (Windows / desktop)
Requirements: CMake 3.22+ and a C++20 compiler.

```bash
cmake -S . -B build -DCMAKE_BUILD_TYPE=Release -DBUILD_TESTING=ON
cmake --build build --config Release
ctest --test-dir build --output-on-failure
```

Run WoulweEngineDemo. It writes woulwe_frame.ppm in the working directory.

## Android
The shared engine core is platform-independent and the Android NDK entry point is under platform/android. The software renderer is a portable reference renderer; an Android app shell and GPU surface integration are still required before an installable Android build is available.

## Roadmap
1. Core and scene model
2. Graphics foundation: math, camera, mesh, rasterizer, depth and light
3. Asset/camera foundation (current): OBJ import, orbit/dolly camera, CI tests
4. Vulkan backend for Windows and Android
5. Materials, textures, glTF import and editor viewport
6. Input, physics, animation, audio, scripting, AI, multiplayer and packaging

## Architecture rule
Keep engine logic platform-independent. Platform APIs belong under platform/; rendering backends belong under engine/graphics/.
