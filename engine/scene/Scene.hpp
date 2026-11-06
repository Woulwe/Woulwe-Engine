#pragma once
#include <cstdint>
#include <string>
#include <vector>

namespace woulwe {
using ObjectId = std::uint64_t;

struct Transform {
    float position[3]{0.f, 0.f, 0.f};
    float rotation[3]{0.f, 0.f, 0.f};
    float scale[3]{1.f, 1.f, 1.f};
};

struct SceneObject {
    ObjectId id{};
    std::string name;
    Transform transform;
    bool active{true};
};

class Scene {
public:
    ObjectId createObject(std::string name);
    bool destroyObject(ObjectId id);
    [[nodiscard]] const std::vector<SceneObject>& objects() const noexcept;
private:
    ObjectId nextId_{1};
    std::vector<SceneObject> objects_;
};
} // namespace woulwe
