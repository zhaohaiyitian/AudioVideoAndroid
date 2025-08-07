#extension GL_OES_EGL_image_external : require

varying vec2 aCoord;
uniform samplerExternalOES vTexture;

void main() {
    gl_FragColor = texture2D(vTexture, aCoord);
}