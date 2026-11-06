#include "core/Engine.hpp"
#include "scene/Scene.hpp"
#include "graphics/SoftwareRenderer.hpp"
#include <iostream>

int main() {
    woulwe::Engine engine({"Woulwe Engine — Graphics Demo",60});
    woulwe::Scene scene;
    engine.initialize();
    scene.createObject("Main Camera");
    scene.createObject("Directional Light");
    scene.createObject("Demo Cube");

    woulwe::graphics::SoftwareRenderer renderer(800,600);
    woulwe::graphics::Camera camera;
    renderer.clear();
    renderer.draw(woulwe::graphics::Mesh::cube(),camera);
    const bool saved=renderer.savePpm("woulwe_frame.ppm");

    engine.update(1.0/60.0);
    std::cout<<engine.config().applicationName<<"\n";
    std::cout<<"Scene objects: "<<scene.objects().size()<<"\n";
    std::cout<<"Software 3D renderer: "<<(saved?"frame saved":"failed")<<"\n";
    engine.shutdown();
    return saved?0:1;
}
