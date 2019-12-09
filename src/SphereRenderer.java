import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.PMVMatrix;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class SphereRenderer {

    private int[] vaoSphere;  // Names of vertex array objects
    private int[] vboSphere;	// Names of vertex buffer objects
    private int[] iboSphere;	// Names of index buffer objects

    //PMVMatrix pmvSphere;

    private Sphere sphere0;

    private final String shaderPath = ".\\resources\\";

    private final String vertexShader0FileName = "O1_Basic.vert";
    private final String fragmentShader0FileName = "O1_Basic.frag";

    private ShaderProgram shaderProgram0;           //Hyp. Shader Fenster

    /*public PMVMatrix getPmvSphere() {
        return pmvSphere;
    }*/

    /*public void setPmvSphere(PMVMatrix pmvSphere) {
        this.pmvSphere = pmvSphere;
    }*/

    public void init(GLAutoDrawable drawable){
        GL3 gl = drawable.getGL().getGL3();

        // BEGIN: Preparing scene
        // BEGIN: Allocating vertex array objects and buffers for each object
        int noOfObjects = 1;
        // create vertex array objects for noOfObjects objects (VAO)
        vaoSphere = new int[noOfObjects];
        gl.glGenVertexArrays(noOfObjects, vaoSphere, 0);
        if (vaoSphere[0] < 1)
            System.err.println("Error allocating vertex array object (VAO).");

        // create vertex buffer objects for noOfObjects objects (VBO)
        vboSphere = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, vboSphere, 0);
        if (vboSphere[0] < 1)
            System.err.println("Error allocating vertex buffer object (VBO).");

        // create index buffer objects for noOfObjects objects (IBO)
        iboSphere = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, iboSphere, 0);
        if (iboSphere[0] < 1)
            System.err.println("Error allocating index buffer object.");
        // END: Allocating vertex array objects and buffers for each object

        initSphere(gl);

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

    public void initSphere(GL3 gl){
        // BEGIN: Prepare a sphere for drawing (object 0)
        // create sphere data for rendering a sphere using an index array into a vertex array
        gl.glBindVertexArray(vaoSphere[0]);
        // Shader program for object 0
        shaderProgram0 = new ShaderProgram(gl);
        shaderProgram0.loadShaderAndCreateProgram(shaderPath,
                vertexShader0FileName, fragmentShader0FileName);

        float[] color0 = {0.8f, 0.1f, 0.1f};
        sphere0 = new Sphere(64, 64);
        float[] sphereVertices = sphere0.makeVertices(0.5f, color0);
        int[] sphereIndices = sphere0.makeIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboSphere[0]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, sphereVertices.length * 4,
                FloatBuffer.wrap(sphereVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboSphere[0]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, sphereIndices.length * 4,
                IntBuffer.wrap(sphereIndices), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // Defining input variables for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9*4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9*4, 3*4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9*4, 6*4);
        // END: Prepare sphere for drawing
    }

    public void displaySphere(GL3 gl, PMVMatrix pmvMatrix) {
        gl.glUseProgram(shaderProgram0.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix)
        // to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glBindVertexArray(vaoSphere[0]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, Box.noOfIndicesForBox(), GL.GL_UNSIGNED_INT, 0);
    }

    public void reshapeSphere(GLAutoDrawable drawable, int x, int y, int width, int height, PMVMatrix pmvMatrix) {
        GL3 gl = drawable.getGL().getGL3();

        pmvMatrix.glMatrixMode(PMVMatrix.GL_PROJECTION);
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluPerspective(45f, (float) width/ (float) height, 0.01f, 10000f);
    }

    public void disposeSphere(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderProgram0.deleteShaderProgram();

        // deactivate VAO and VBO
        gl.glBindVertexArray(0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);

        gl.glDisable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_DEPTH_TEST);

    }
}
