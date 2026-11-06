#pragma once
#include <cstdint>
#include <string>

namespace woulwe {
struct EngineConfig {
    std::string applicationName{"Woulwe Game"};
    std::uint32_t targetUpdatesPerSecond{60};
};

class Engine {
public:
    explicit Engine(EngineConfig config = {});
    void initialize();
    void update(double deltaSeconds);
    void shutdown();
    [[nodiscard]] bool isInitialized() const noexcept { return initialized_; }
    [[nodiscard]] const EngineConfig& config() const noexcept { return config_; }

private:
    EngineConfig config_;
    bool initialized_{false};
};
} // namespace woulwe
