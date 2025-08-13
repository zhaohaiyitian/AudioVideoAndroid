#extension GL_OES_EGL_image_external : require

// 顶点程序传过来的
varying vec2 aCoord;
// 采样器
uniform samplerExternalOES vTexture; // uniform相当于java/c中的全局变量

void main() {
    // 确定每个像素的最终颜色
    // 可以与纹理结合，实现纹理映射
    // 通过它可以实现各种颜色特效，如光照，高亮等
    vec4 tc = texture2D(vTexture,aCoord);
    float x = aCoord.x; // 分屏操作
    if(x < 0.5) {
        x+=0.25;
    } else {
        x-=0.25;
    }
    gl_FragColor = texture2D(vTexture, vec2(x,aCoord.y)); // texture2D 内置函数
}