#pragma once
#include "graphics/Math.hpp"
#include <cstdint>
#include <vector>

namespace woulwe::graphics {
struct Vertex { Vec3 position; Vec3 normal; };
struct Mesh {
    std::vector<Vertex> vertices;
    std::vector<std::uint32_t> indices;
    static Mesh cube();
};
} // namespace woulwe::graphics
