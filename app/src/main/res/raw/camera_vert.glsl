



attribute vec4 vPosition;

attribute vec4 vCoord;

varying vec2 aCoord;

uniform mat4 vMatrix;


void main() {
    // 以gl开头的变量都是内部变量
    // 顶点着色器需要给gl_Position赋值确定顶点最终位置
    // 渲染管线根据gl_Position进行图元装配
    gl_Position = vPosition;
    aCoord = (vMatrix*vCoord).xy;
}