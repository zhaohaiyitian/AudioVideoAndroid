#extension GL_OES_EGL_image_external : require

varying vec2 aCoord;
uniform samplerExternalOES vTexture; // uniform相当于java/c中的全局变量

void main() {
    // 确定每个像素的最终颜色
    // 可以与纹理结合，实现纹理映射
    // 通过它可以实现各种颜色特效，如光照，高亮等
    gl_FragColor = texture2D(vTexture, aCoord);
}