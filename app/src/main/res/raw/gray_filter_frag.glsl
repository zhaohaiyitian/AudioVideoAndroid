#extension GL_OES_EGL_image_external : require

varying vec2 aCoord;
uniform samplerExternalOES vTexture;

void main() {
    // 获取纹理颜色
    vec4 color = texture2D(vTexture, aCoord);
    
    // 计算灰度值 (使用加权平均法)
    float gray = color.r * 0.299 + color.g * 0.587 + color.b * 0.114;
    
    // 设置灰度颜色
    gl_FragColor = vec4(gray, gray, gray, color.a);
}