




attribute vec4 vPosition;

attribute vec4 vCoord;


varying vec2 aCoord;




void main() {
    gl_Position = vPosition;
    aCoord = vCoord.xy;
}