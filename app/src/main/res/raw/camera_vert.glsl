



attribute vec4 vPosition; // 接收世界坐标

attribute vec4 vCoord; // 接收纹理坐标

varying vec2 aCoord; // 传给片元着色器

uniform mat4 vMatrix; // 变换矩阵


void main() {
    // 以gl开头的变量都是内部变量
    // 顶点着色器需要给gl_Position赋值确定顶点最终位置
    // 渲染管线根据gl_Position进行图元装配
    gl_Position = vPosition; // 确定是绘制一个矩形
    aCoord = (vMatrix*vCoord).xy;
}