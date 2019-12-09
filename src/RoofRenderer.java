import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.PMVMatrix;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class RoofRenderer {

    private int[] vaoRoof;  // Names of vertex array objects
    private int[] vboRoof;	// Names of vertex buffer objects
    private int[] iboRoof;	// Names of index buffer objects
    PMVMatrix pmvRoof;

    private final String shaderPath = ".\\resources\\";

    private final String vertexShader3FileName = "O1_Basic.vert";
    private final String fragmentShader3FileName = "O1_Basic.frag";

    private ShaderProgram shaderProgram3;

    public PMVMatrix getPmvRoof() {
        return pmvRoof;
    }

    public void setPmvRoof(PMVMatrix pmvRoof) {
        this.pmvRoof = pmvRoof;
    }

    public void init(GLAutoDrawable drawable){
        GL3 gl = drawable.getGL().getGL3();

        // BEGIN: Preparing scene
        // BEGIN: Allocating vertex array objects and buffers for each object
        int noOfObjects = 1;
        // create vertex array objects for noOfObjects objects (VAO)
        vaoRoof = new int[noOfObjects];
        gl.glGenVertexArrays(noOfObjects, vaoRoof, 0);
        if (vaoRoof[0] < 1)
            System.err.println("Error allocating vertex array object (VAO).");

        // create vertex buffer objects for noOfObjects objects (VBO)
        vboRoof = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, vboRoof, 0);
        if (vboRoof[0] < 1)
            System.err.println("Error allocating vertex buffer object (VBO).");

        // create index buffer objects for noOfObjects objects (IBO)
        iboRoof = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, iboRoof, 0);
        if (iboRoof[0] < 1)
            System.err.println("Error allocating index buffer object.");
        // END: Allocating vertex array objects and buffers for each object

        initRoof(gl);

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

    public void initRoof(GL3 gl){

        // BEGIN: Prepare roof for drawing (object 3)
        // create data for rendering a roof using an index array into a vertex array
        gl.glBindVertexArray(vaoRoof[0]);
        shaderProgram3 = new ShaderProgram(gl);
        shaderProgram3.loadShaderAndCreateProgram(shaderPath,
                vertexShader3FileName, fragmentShader3FileName);

        float[] color3 = {0.8f, 0.8f, 0.1f};
        float[] roofVertices = Roof.makeVertices(0.8f, 1.1f, 0.5f, color3);
        int[] roofIndices = Roof.makeIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboRoof[0]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, roofVertices.length * 4,
                FloatBuffer.wrap(roofVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboRoof[0]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, roofIndices.length * 4,
                IntBuffer.wrap(roofIndices), GL.GL_STATIC_DRAW);

        // Activate and arrange vertex buffer object data for the vertex shader
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9*4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9*4, 3*4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9*4, 6*4);
        // END: Prepare roof for drawing
    }

    public void displayRoof(GL3 gl,PMVMatrix pmvMatrix) {
        gl.glUseProgram(shaderProgram3.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix)
        // to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glBindVertexArray(vaoRoof[0]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, Box.noOfIndicesForBox(), GL.GL_UNSIGNED_INT, 0);
    }

    public void reshapeRoof(GLAutoDrawable drawable, int x, int y, int width, int height, PMVMatrix pmvMatrix) {
        GL3 gl = drawable.getGL().getGL3();

        pmvMatrix.glMatrixMode(PMVMatrix.GL_PROJECTION);
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluPerspective(45f, (float) width/ (float) height, 0.01f, 10000f);
    }

    public void disposeRoof(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderProgram3.deleteShaderProgram();

        // deactivate VAO and VBO
        gl.glBindVertexArray(0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);

        gl.glDisable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_DEPTH_TEST);
    }
}
