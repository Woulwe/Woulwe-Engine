cmake_minimum_required(VERSION 3.22)
project(WoulweEngine VERSION 0.1.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

add_library(woulwe_core
    engine/core/Engine.cpp
    engine/scene/Scene.cpp
)
target_include_directories(woulwe_core PUBLIC engine)

if(ANDROID)
    add_library(woulwe_android SHARED platform/android/AndroidEntry.cpp)
    target_link_libraries(woulwe_android PRIVATE woulwe_core android log)
else()
    add_executable(WoulweEngineDemo samples/desktop/main.cpp)
    target_link_libraries(WoulweEngineDemo PRIVATE woulwe_core)
endif()
