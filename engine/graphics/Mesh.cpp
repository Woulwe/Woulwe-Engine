#include "graphics/Mesh.hpp"
#include <fstream>
#include <sstream>
#include <string>
#include <utility>
#include <vector>
namespace woulwe::graphics {
Mesh Mesh::cube() {
 Mesh mesh;
 const Vec3 p[]={{-1,-1,-1},{1,-1,-1},{1,1,-1},{-1,1,-1},{-1,-1,1},{1,-1,1},{1,1,1},{-1,1,1}};
 for(const auto& v:p) mesh.vertices.push_back({v,v.normalized()});
 mesh.indices={0,2,1,0,3,2,4,5,6,4,6,7,0,1,5,0,5,4,3,7,6,3,6,2,0,4,7,0,7,3,1,2,6,1,6,5};
 return mesh;
}
bool Mesh::loadObj(const std::string& path) {
 std::ifstream input(path); if(!input) return false;
 std::vector<Vec3> positions; Mesh parsed; std::string line;
 while(std::getline(input,line)) {
  std::istringstream stream(line); std::string kind; stream>>kind;
  if(kind.empty()||kind[0]=='#') continue;
  if(kind=="v") { Vec3 p; if(!(stream>>p.x>>p.y>>p.z)) return false; positions.push_back(p); }
  else if(kind=="f") {
   std::vector<std::uint32_t> face; std::string token;
   while(stream>>token) {
    const auto slash=token.find('/'); const std::string text=token.substr(0,slash);
    if(text.empty()) return false; int objIndex=0;
    try { objIndex=std::stoi(text); } catch(...) { return false; }
    const int resolved=objIndex>0?objIndex-1:static_cast<int>(positions.size())+objIndex;
    if(resolved<0||resolved>=static_cast<int>(positions.size())) return false;
    face.push_back(static_cast<std::uint32_t>(resolved));
   }
   if(face.size()<3) return false;
   for(std::size_t i=1;i+1<face.size();++i) {
    const Vec3 a=positions[face[0]],b=positions[face[i]],c=positions[face[i+1]];
    const Vec3 n=(b-a).cross(c-a).normalized(); const auto base=static_cast<std::uint32_t>(parsed.vertices.size());
    parsed.vertices.push_back({a,n}); parsed.vertices.push_back({b,n}); parsed.vertices.push_back({c,n});
    parsed.indices.insert(parsed.indices.end(),{base,base+1,base+2});
   }
  }
 }
 if(parsed.indices.empty()) return false; *this=std::move(parsed); return true;
}
}
