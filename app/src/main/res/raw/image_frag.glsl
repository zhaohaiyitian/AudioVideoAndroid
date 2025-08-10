varying highp vec2 textureCoordinate;
uniform sampler2D inputImageTexture;//0图层

void main() {
    gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
}