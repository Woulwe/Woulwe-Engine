#pragma once
#include "graphics/Math.hpp"
#include <cstdint>
#include <string>
#include <vector>
namespace woulwe::graphics {
struct Vertex { Vec3 position; Vec3 normal; };
struct Mesh {
 std::vector<Vertex> vertices;
 std::vector<std::uint32_t> indices;
 static Mesh cube();
 // Loads OBJ v/f geometry; false leaves this mesh unchanged.
 bool loadObj(const std::string& path);
};
}
