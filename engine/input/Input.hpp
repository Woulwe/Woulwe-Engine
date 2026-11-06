#pragma once
#include <array>
#include <cstddef>

namespace woulwe {
enum class Key : std::size_t {
    W, A, S, D, Space, Escape, Up, Down, Left, Right, Count
};

class Input {
public:
    void setKey(Key key,bool pressed) noexcept { keys_[static_cast<std::size_t>(key)]=pressed; }
    [[nodiscard]] bool keyDown(Key key) const noexcept { return keys_[static_cast<std::size_t>(key)]; }
    void setPointer(float x,float y) noexcept { pointerX_=x; pointerY_=y; }
    void setPointerDelta(float x,float y) noexcept { deltaX_=x; deltaY_=y; }
    void setPointerButton(bool pressed) noexcept { pointerButton_=pressed; }
    [[nodiscard]] float pointerX() const noexcept { return pointerX_; }
    [[nodiscard]] float pointerY() const noexcept { return pointerY_; }
    [[nodiscard]] float pointerDeltaX() const noexcept { return deltaX_; }
    [[nodiscard]] float pointerDeltaY() const noexcept { return deltaY_; }
    [[nodiscard]] bool pointerButtonDown() const noexcept { return pointerButton_; }
    void endFrame() noexcept { deltaX_=0.f; deltaY_=0.f; }
private:
    std::array<bool,static_cast<std::size_t>(Key::Count)> keys_{};
    float pointerX_{},pointerY_{},deltaX_{},deltaY_{};
    bool pointerButton_{};
};
}
