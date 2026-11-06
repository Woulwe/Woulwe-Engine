#include "physics/Physics.hpp"
#include <algorithm>

namespace woulwe::physics {
Body& World::addBody(Body body) {
    body.mass=std::max(0.0001f,body.mass);
    body.radius=std::max(0.001f,body.radius);
    bodies_.push_back(body);
    return bodies_.back();
}
void World::step(float deltaSeconds) {
    const float dt=std::clamp(deltaSeconds,0.f,0.05f);
    for (auto& body:bodies_) {
        if (body.isStatic) continue;
        const Vec3 acceleration=gravity+body.acceleration;
        body.velocity=body.velocity+acceleration*dt;
        body.position=body.position+body.velocity*dt;
        const float floor=groundHeight+body.radius;
        if (body.position.y<floor) {
            body.position.y=floor;
            if (body.velocity.y<0.f) body.velocity.y=-body.velocity.y*std::clamp(body.restitution,0.f,1.f);
            if (body.velocity.y<0.05f) body.velocity.y=0.f;
        }
    }
}
}
