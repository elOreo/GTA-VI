import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.PMVMatrix;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public  class BoxRenderer {

    private int[] vaoBox;  // Names of vertex array objects
    private int[] vboBox;	// Names of vertex buffer objects
    private int[] iboBox;	// Names of index buffer objects

    //PMVMatrix pmvHouse;

    private final String shaderPath = ".\\resources\\";

    private final String vertexShader1FileName = "O1_Basic.vert";
    private final String fragmentShader1FileName = "O1_Basic.frag";

    private ShaderProgram shaderProgram1;           //Hyp. Shader Fenster

    /*public PMVMatrix getPmvHouse() {
        return pmvHouse;
    }*/

    /*public void setPmvHouse(PMVMatrix pmvHouse) {
        this.pmvHouse = pmvHouse;
    }*/

    public void init(GLAutoDrawable drawable){
        GL3 gl = drawable.getGL().getGL3();

        // BEGIN: Preparing scene
        // BEGIN: Allocating vertex array objects and buffers for each object
        int noOfObjects = 1;
        // create vertex array objects for noOfObjects objects (VAO)
        vaoBox = new int[noOfObjects];
        gl.glGenVertexArrays(noOfObjects, vaoBox, 0);
        if (vaoBox[0] < 1)
            System.err.println("Error allocating vertex array object (VAO).");

        // create vertex buffer objects for noOfObjects objects (VBO)
        vboBox = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, vboBox, 0);
        if (vboBox[0] < 1)
            System.err.println("Error allocating vertex buffer object (VBO).");

        // create index buffer objects for noOfObjects objects (IBO)
        iboBox = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, iboBox, 0);
        if (iboBox[0] < 1)
            System.err.println("Error allocating index buffer object.");
        // END: Allocating vertex array objects and buffers for each object

        initBox(gl);

        // Switch on back face culling
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);
//        gl.glCullFace(GL.GL_FRONT);
        // Switch on depth test
        gl.glEnable(GL.GL_DEPTH_TEST);

        // defining polygon drawing mode
//        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, gl.GL_FILL);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, gl.GL_LINE);
    }

    public void initBox(GL3 gl){

        // BEGIN: Prepare cube for drawing (object 1)
        gl.glBindVertexArray(vaoBox[0]);
        shaderProgram1 = new ShaderProgram(gl);
        shaderProgram1.loadShaderAndCreateProgram(shaderPath,
                vertexShader1FileName, fragmentShader1FileName);

        float[] color1 = {0.1f, 0.1f, 0.8f};
        float[] cubeVertices = Box.makeBoxVertices(0.8f,0.4f,0.5f, color1);

        int[] cubeIndices = Box.makeBoxIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboBox[0]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeVertices.length * 4,
                FloatBuffer.wrap(cubeVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboBox[0]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, cubeIndices.length * 4,
                IntBuffer.wrap(cubeIndices), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), color (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9 * 4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9 * 4, 3 * 4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9 * 4, 6 * 4);
        // END: Prepare cube for drawing
    }

    public void displayBox(GL3 gl, PMVMatrix pmvMatrix) {
        gl.glUseProgram(shaderProgram1.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix)
        // to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glBindVertexArray(vaoBox[0]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, Box.noOfIndicesForBox(), GL.GL_UNSIGNED_INT, 0);
    }

    public void reshapeBox(GLAutoDrawable drawable, int x, int y, int width, int height, PMVMatrix pmvMatrix) {
        GL3 gl = drawable.getGL().getGL3();

        pmvMatrix.glMatrixMode(PMVMatrix.GL_PROJECTION);
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluPerspective(45f, (float) width/ (float) height, 0.01f, 10000f);
    }

    public void disposeBox(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderProgram1.deleteShaderProgram();

        // deactivate VAO and VBO
        gl.glBindVertexArray(0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);

        gl.glDisable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_DEPTH_TEST);

    }
}
