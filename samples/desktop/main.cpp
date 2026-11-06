#include "core/Engine.hpp"
#include "scene/Scene.hpp"
#include <iostream>

int main() {
    woulwe::Engine engine({"Woulwe Engine — Core Demo", 60});
    woulwe::Scene scene;

    engine.initialize();
    scene.createObject("Main Camera");
    scene.createObject("Directional Light");
    scene.createObject("Player");

    for (int frame = 0; frame < 3; ++frame) engine.update(1.0 / 60.0);

    std::cout << engine.config().applicationName << "\n";
    std::cout << "Scene objects: " << scene.objects().size() << "\n";
    std::cout << "Core initialized successfully.\n";
    engine.shutdown();
    return 0;
}
