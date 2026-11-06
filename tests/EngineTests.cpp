#include "graphics/Camera.hpp"
#include "graphics/Mesh.hpp"
#include "graphics/SoftwareRenderer.hpp"
#include <cassert>
#include <cmath>
#include <fstream>

int main() {
    using namespace woulwe::graphics;
    const Mesh cube=Mesh::cube();
    assert(cube.vertices.size()==8);
    assert(cube.indices.size()==36);

    Camera camera;
    const float initialDistance=(camera.position-camera.target).length();
    camera.orbit(0.4f,0.2f);
    assert(std::abs((camera.position-camera.target).length()-initialDistance)<1e-4f);
    camera.dolly(-1.f);
    assert((camera.position-camera.target).length()<initialDistance);

    const char* fixture="woulwe_test_triangle.obj";
    {
        std::ofstream obj(fixture);
        obj<<"v 0 0 0\nv 1 0 0\nv 0 1 0\nf 1 2 3\n";
    }
    Mesh imported;
    assert(imported.loadObj(fixture));
    assert(imported.vertices.size()==3);
    assert(imported.indices.size()==3);
    std::remove(fixture);

    SoftwareRenderer renderer(64,64);
    renderer.clear();
    renderer.draw(cube,camera);
    assert(renderer.savePpm("woulwe_test_frame.ppm"));
    std::remove("woulwe_test_frame.ppm");
    return 0;
}
