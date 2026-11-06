#include "graphics/SoftwareRenderer.hpp"
#include <algorithm>
#include <cmath>
#include <fstream>

namespace woulwe::graphics {
SoftwareRenderer::SoftwareRenderer(int width,int height)
 : width_(std::max(1,width)),height_(std::max(1,height)),
   colorBuffer_(static_cast<std::size_t>(width_)*height_),
   depthBuffer_(static_cast<std::size_t>(width_)*height_,1.f) {}

void SoftwareRenderer::clear(Color color) {
    std::fill(colorBuffer_.begin(),colorBuffer_.end(),color);
    std::fill(depthBuffer_.begin(),depthBuffer_.end(),1.f);
}

void SoftwareRenderer::draw(const Mesh& mesh,const Camera& camera,Color tint) {
    const Mat4 vp=camera.viewProjection(static_cast<float>(width_)/height_);
    for(std::size_t i=0;i+2<mesh.indices.size();i+=3) {
        const auto ia=mesh.indices[i],ib=mesh.indices[i+1],ic=mesh.indices[i+2];
        if(ia>=mesh.vertices.size()||ib>=mesh.vertices.size()||ic>=mesh.vertices.size()) continue;
        const auto project=[&](const Vertex& v) {
            return vp.transform({v.position.x,v.position.y,v.position.z,1.f});
        };
        const Vec4 a=project(mesh.vertices[ia]),b=project(mesh.vertices[ib]),c=project(mesh.vertices[ic]);
        if(a.w<=0.f||b.w<=0.f||c.w<=0.f) continue;
        const Vec3 normal=(mesh.vertices[ib].position-mesh.vertices[ia].position)
            .cross(mesh.vertices[ic].position-mesh.vertices[ia].position).normalized();
        const float light=.18f+.82f*std::max(0.f,normal.dot(Vec3{-.4f,.8f,.5f}.normalized()));
        const Color shaded{
          static_cast<std::uint8_t>(tint.r*light),
          static_cast<std::uint8_t>(tint.g*light),
          static_cast<std::uint8_t>(tint.b*light)};
        rasterize(a,b,c,shaded);
    }
}

void SoftwareRenderer::rasterize(Vec4 a,Vec4 b,Vec4 c,Color color) {
    const auto screen=[&](Vec4 p) {
        const float iw=1.f/p.w;
        return Vec3{(p.x*iw*.5f+.5f)*(width_-1),
                    (1.f-(p.y*iw*.5f+.5f))*(height_-1),p.z*iw};
    };
    const Vec3 v0=screen(a),v1=screen(b),v2=screen(c);
    const float area=(v1.x-v0.x)*(v2.y-v0.y)-(v1.y-v0.y)*(v2.x-v0.x);
    if(std::abs(area)<1e-6f) return;
    const int minX=std::max(0,static_cast<int>(std::floor(std::min({v0.x,v1.x,v2.x}))));
    const int maxX=std::min(width_-1,static_cast<int>(std::ceil(std::max({v0.x,v1.x,v2.x}))));
    const int minY=std::max(0,static_cast<int>(std::floor(std::min({v0.y,v1.y,v2.y}))));
    const int maxY=std::min(height_-1,static_cast<int>(std::ceil(std::max({v0.y,v1.y,v2.y}))));
    for(int y=minY;y<=maxY;++y) for(int x=minX;x<=maxX;++x) {
        const float px=x+.5f,py=y+.5f;
        const float w0=((v1.x-px)*(v2.y-py)-(v1.y-py)*(v2.x-px))/area;
        const float w1=((v2.x-px)*(v0.y-py)-(v2.y-py)*(v0.x-px))/area;
        const float w2=1.f-w0-w1;
        if(w0<0.f||w1<0.f||w2<0.f) continue;
        const float depth=w0*v0.z+w1*v1.z+w2*v2.z;
        const auto index=static_cast<std::size_t>(y)*width_+x;
        if(depth<0.f||depth>1.f||depth>=depthBuffer_[index]) continue;
        depthBuffer_[index]=depth;
        colorBuffer_[index]=color;
    }
}

bool SoftwareRenderer::savePpm(const std::string& path) const {
    std::ofstream out(path,std::ios::binary);
    if(!out) return false;
    out<<"P6\n"<<width_<<" "<<height_<<"\n255\n";
    for(const auto& c:colorBuffer_) {
        const char rgb[]={static_cast<char>(c.r),static_cast<char>(c.g),static_cast<char>(c.b)};
        out.write(rgb,3);
    }
    return static_cast<bool>(out);
}
} // namespace woulwe::graphics
