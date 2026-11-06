#pragma once
#include "graphics/SoftwareRenderer.hpp"

namespace woulwe::graphics {
struct Material {
    Color baseColor{180,180,190};
    float metallic{0.f};
    float roughness{0.7f};
    float ambientOcclusion{1.f};
    void sanitize() noexcept {
        metallic=metallic<0.f?0.f:(metallic>1.f?1.f:metallic);
        roughness=roughness<0.04f?0.04f:(roughness>1.f?1.f:roughness);
        ambientOcclusion=ambientOcclusion<0.f?0.f:(ambientOcclusion>1.f?1.f:ambientOcclusion);
    }
};
}
