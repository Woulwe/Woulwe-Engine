#pragma once
#include "graphics/Math.hpp"
#include <algorithm>
#include <cmath>
namespace woulwe::graphics {
class Camera {
public:
 Vec3 position{0.f,0.f,3.f}; Vec3 target{0.f,0.f,0.f};
 float verticalFovRadians{1.04719755f}; float nearPlane{0.1f}; float farPlane{1000.f};
 void orbit(float yawDelta,float pitchDelta) {
  const Vec3 offset=position-target; const float distance=std::max(0.05f,offset.length());
  const float yaw=std::atan2(offset.x,offset.z)+yawDelta;
  const float pitch=std::clamp(std::asin(std::clamp(offset.y/distance,-1.f,1.f))+pitchDelta,-1.553343f,1.553343f);
  const float horizontal=std::cos(pitch)*distance;
  position=target+Vec3{std::sin(yaw)*horizontal,std::sin(pitch)*distance,std::cos(yaw)*horizontal};
 }
 void dolly(float delta) { const Vec3 direction=(position-target).normalized(); position=target+direction*std::max(0.05f,(position-target).length()+delta); }
 Mat4 viewProjection(float aspect) const {
  const Vec3 forward=(target-position).normalized(); Vec3 right=forward.cross({0.f,1.f,0.f}).normalized();
  if(right.length()<1e-5f) right={1.f,0.f,0.f}; const Vec3 up=right.cross(forward).normalized();
  Mat4 view=Mat4::identity();
  view.m[0]=right.x; view.m[4]=right.y; view.m[8]=right.z;
  view.m[1]=up.x; view.m[5]=up.y; view.m[9]=up.z;
  view.m[2]=-forward.x; view.m[6]=-forward.y; view.m[10]=-forward.z;
  view.m[12]=-right.dot(position); view.m[13]=-up.dot(position); view.m[14]=forward.dot(position);
  const Mat4 projection=Mat4::perspective(verticalFovRadians,aspect,nearPlane,farPlane); Mat4 result{};
  for(int c=0;c<4;++c) for(int r=0;r<4;++r) for(int k=0;k<4;++k) result.m[c*4+r]+=projection.m[k*4+r]*view.m[c*4+k];
  return result;
 }
};
}
