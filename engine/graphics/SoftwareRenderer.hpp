#pragma once
#include "graphics/Camera.hpp"
#include "graphics/Mesh.hpp"
#include <cstdint>
#include <string>
#include <vector>

namespace woulwe::graphics {
struct Color { std::uint8_t r{},g{},b{}; };
class SoftwareRenderer {
public:
    SoftwareRenderer(int width,int height);
    void clear(Color color={18,22,32});
    void draw(const Mesh& mesh,const Camera& camera,Color tint={90,170,255});
    bool savePpm(const std::string& path) const;
    [[nodiscard]] int width() const noexcept { return width_; }
    [[nodiscard]] int height() const noexcept { return height_; }
private:
    void rasterize(Vec4 a,Vec4 b,Vec4 c,Color color);
    int width_,height_;
    std::vector<Color> colorBuffer_;
    std::vector<float> depthBuffer_;
};
} // namespace woulwe::graphics
