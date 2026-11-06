#pragma once
#include <cmath>

namespace woulwe::graphics {
struct Vec3 {
    float x{}, y{}, z{};
    Vec3 operator+(const Vec3& v) const { return {x+v.x,y+v.y,z+v.z}; }
    Vec3 operator-(const Vec3& v) const { return {x-v.x,y-v.y,z-v.z}; }
    Vec3 operator*(float s) const { return {x*s,y*s,z*s}; }
    float dot(const Vec3& v) const { return x*v.x+y*v.y+z*v.z; }
    Vec3 cross(const Vec3& v) const {
        return {y*v.z-z*v.y,z*v.x-x*v.z,x*v.y-y*v.x};
    }
    float length() const { return std::sqrt(dot(*this)); }
    Vec3 normalized() const {
        const float l=length(); return l>1e-6f ? *this*(1.0f/l) : Vec3{};
    }
};
struct Vec4 { float x{}, y{}, z{}, w{}; };
struct Mat4 {
    float m[16]{};
    static Mat4 identity() {
        Mat4 r{}; r.m[0]=r.m[5]=r.m[10]=r.m[15]=1.f; return r;
    }
    static Mat4 perspective(float fovRadians,float aspect,float nearPlane,float farPlane) {
        Mat4 r{};
        const float f=1.f/std::tan(fovRadians*.5f);
        r.m[0]=f/aspect; r.m[5]=f;
        r.m[10]=(farPlane+nearPlane)/(nearPlane-farPlane);
        r.m[11]=-1.f;
        r.m[14]=(2.f*farPlane*nearPlane)/(nearPlane-farPlane);
        return r;
    }
    Vec4 transform(const Vec4& v) const {
        return {
          m[0]*v.x+m[4]*v.y+m[8]*v.z+m[12]*v.w,
          m[1]*v.x+m[5]*v.y+m[9]*v.z+m[13]*v.w,
          m[2]*v.x+m[6]*v.y+m[10]*v.z+m[14]*v.w,
          m[3]*v.x+m[7]*v.y+m[11]*v.z+m[15]*v.w
        };
    }
};
} // namespace woulwe::graphics
