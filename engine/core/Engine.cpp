#include "core/Engine.hpp"
#include <algorithm>
#include <stdexcept>
#include <utility>

namespace woulwe {
Engine::Engine(EngineConfig config) : config_(std::move(config)) {
    config_.targetUpdatesPerSecond =
        std::max<std::uint32_t>(1, config_.targetUpdatesPerSecond);
}
void Engine::initialize() {
    if (initialized_) return;
    initialized_ = true;
}
void Engine::update(double deltaSeconds) {
    if (!initialized_) throw std::logic_error("Woulwe Engine is not initialized");
    if (deltaSeconds < 0.0) deltaSeconds = 0.0;
    // TODO: tick systems, update scene graph, dispatch events.
}
void Engine::shutdown() { initialized_ = false; }
} // namespace woulwe
