package com.example.flex.shapefactory;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Flex on 3/21/2016.
 */
public class Rectangle {

    private class Vertex {
        float x, y, z;

        Vertex() {
            x = 0;
            y = 0;
            z = 0;
        }

        Vertex(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    //Start opengl es code section
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    //Start drawing data section
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    private Vertex topLeft;
    private Vertex botLeft;
    private Vertex botRight;
    private Vertex topRight;
    private float rectangleCoords[] = new float[12];

    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
    private float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };

    private final int COORDS_PER_VERTEX = 3;
    private final int VERTEX_COUNT = rectangleCoords.length / COORDS_PER_VERTEX;
    private final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;

    //Object data
    private float length;
    private float width;
    private float xCenter;
    private float yCenter;

    public Rectangle() {

        //center of viewport
        xCenter = 0.0f;
        yCenter = 0.0f;

        length = 0.5f;
        width = 0.25f;

        Vertex topLeft = new Vertex(xCenter - (length/2), yCenter + (width/2), 0.0f);
        Vertex botLeft = new Vertex(xCenter - (length/2), yCenter - (width/2), 0.0f);
        Vertex botRight = new Vertex(xCenter + (length/2), yCenter - (width/2), 0.0f);
        Vertex topRight = new Vertex(xCenter + (length/2), yCenter + (width/2), 0.0f);

        populateRectangleCoordinates(topLeft, botLeft, botRight, topRight);

        //this has to be called AFTER rectangleCoords is populated correctly
        initOpenGLES();
    }

    public Rectangle(float x, float y, float l, float w) {

        xCenter = x;
        yCenter = y;

        length = l;
        width = w;

        Vertex topLeft = new Vertex(xCenter - (length/2), yCenter + (width/2), 0.0f);
        Vertex botLeft = new Vertex(xCenter - (length/2), yCenter - (width/2), 0.0f);
        Vertex botRight = new Vertex(xCenter + (length/2), yCenter - (width/2), 0.0f);
        Vertex topRight = new Vertex(xCenter + (length/2), yCenter + (width/2), 0.0f);

        populateRectangleCoordinates(topLeft, botLeft, botRight, topRight);

        //this has to be called AFTER rectangleCoords is populated correctly
        initOpenGLES();
    }

    public void draw(float[] mvpMatrix) {

        GLES20.glUseProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, vertexBuffer);

        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        ShapeFactoryGLRenderer.checkGlError("glUniformLocation");

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        ShapeFactoryGLRenderer.checkGlError("glUniformMatrix4fv");

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    private void initOpenGLES() {
        //initialize vertex buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(rectangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(rectangleCoords);
        vertexBuffer.position(0);

        //initialize draw list buffer
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());

        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        //create the shaders
        int vertexShader = ShapeFactoryGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = ShapeFactoryGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //create and setup the opengl es program
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    //Populates the rectangleCoords array with vertices information
    private void populateRectangleCoordinates(Vertex topLeft, Vertex botLeft, Vertex botRight, Vertex topRight) {

        rectangleCoords[0] = topLeft.x;
        rectangleCoords[1] = topLeft.y;
        rectangleCoords[2] = topLeft.z;

        rectangleCoords[3] = botLeft.x;
        rectangleCoords[4] = botLeft.y;
        rectangleCoords[5] = botLeft.z;

        rectangleCoords[6] = botRight.x;
        rectangleCoords[7] = botRight.y;
        rectangleCoords[8] = botRight.z;

        rectangleCoords[9] = topRight.x;
        rectangleCoords[10] = topRight.y;
        rectangleCoords[11] = topRight.z;
    }
}
