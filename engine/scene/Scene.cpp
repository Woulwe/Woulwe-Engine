#include "scene/Scene.hpp"
#include <algorithm>
#include <utility>

namespace woulwe {
ObjectId Scene::createObject(std::string name) {
    const ObjectId id = nextId_++;
    objects_.push_back(SceneObject{id, std::move(name), {}, true});
    return id;
}
bool Scene::destroyObject(ObjectId id) {
    const auto oldSize = objects_.size();
    std::erase_if(objects_, [id](const SceneObject& object) { return object.id == id; });
    return objects_.size() != oldSize;
}
const std::vector<SceneObject>& Scene::objects() const noexcept { return objects_; }
} // namespace woulwe
