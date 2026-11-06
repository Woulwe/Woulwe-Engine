#include <jni.h>
#include <android/log.h>
#include "core/Engine.hpp"

namespace {
woulwe::Engine* g_engine = nullptr;
}
extern "C" JNIEXPORT void JNICALL
Java_com_woulwe_engine_WoulweNative_nativeCreate(JNIEnv*, jclass) {
    if (!g_engine) {
        g_engine = new woulwe::Engine({"Woulwe Android Game", 60});
        g_engine->initialize();
        __android_log_print(ANDROID_LOG_INFO, "WoulweEngine", "Core initialized");
    }
}
extern "C" JNIEXPORT void JNICALL
Java_com_woulwe_engine_WoulweNative_nativeDestroy(JNIEnv*, jclass) {
    if (g_engine) {
        g_engine->shutdown();
        delete g_engine;
        g_engine = nullptr;
    }
}
