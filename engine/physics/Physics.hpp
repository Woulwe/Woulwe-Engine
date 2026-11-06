#pragma once
#include "graphics/Math.hpp"
#include <vector>

namespace woulwe::physics {
using woulwe::graphics::Vec3;
struct Body {
    Vec3 position{};
    Vec3 velocity{};
    Vec3 acceleration{};
    float mass{1.f};
    float radius{0.5f};
    bool isStatic{false};
    float restitution{0.15f};
};
class World {
public:
    Vec3 gravity{0.f,-9.81f,0.f};
    float groundHeight{0.f};
    Body& addBody(Body body);
    void step(float deltaSeconds);
    [[nodiscard]] const std::vector<Body>& bodies() const noexcept { return bodies_; }
private:
    std::vector<Body> bodies_;
};
}
