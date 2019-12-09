import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.PMVMatrix;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ConeRenderer {

    private int[] vaoCone;  // Names of vertex array objects
    private int[] vboCone;	// Names of vertex buffer objects
    private int[] iboCone;	// Names of index buffer objects
    PMVMatrix pmvCone;

    private Cone cone0;

    private final String shaderPath = ".\\resources\\";

    private final String vertexShader2FileName = "O1_Basic.vert";
    private final String fragmentShader2FileName = "O1_Basic.frag";

    private ShaderProgram shaderProgram2;           //Hyp. Shader Fenster

    public PMVMatrix getPmvCone() {
        return pmvCone;
    }

    public void setPmvCone(PMVMatrix pmvCone) {
        this.pmvCone = pmvCone;
    }

    public void init(GLAutoDrawable drawable){
        GL3 gl = drawable.getGL().getGL3();

        // BEGIN: Preparing scene
        // BEGIN: Allocating vertex array objects and buffers for each object
        int noOfObjects = 1;
        // create vertex array objects for noOfObjects objects (VAO)
        vaoCone = new int[noOfObjects];
        gl.glGenVertexArrays(noOfObjects, vaoCone, 0);
        if (vaoCone[0] < 1)
            System.err.println("Error allocating vertex array object (VAO).");

        // create vertex buffer objects for noOfObjects objects (VBO)
        vboCone = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, vboCone, 0);
        if (vboCone[0] < 1)
            System.err.println("Error allocating vertex buffer object (VBO).");

        // create index buffer objects for noOfObjects objects (IBO)
        iboCone = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, iboCone, 0);
        if (iboCone[0] < 1)
            System.err.println("Error allocating index buffer object.");
        // END: Allocating vertex array objects and buffers for each object

        initCone(gl);

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

    public void initCone(GL3 gl){

        // BEGIN: Prepare cone (frustum) for drawing (object 2)
        // create cone (frustum) data for rendering a cone (frustum) using an index array into a vertex array
        gl.glBindVertexArray(vaoCone[0]);
        shaderProgram2 = new ShaderProgram(gl);
        shaderProgram2.loadShaderAndCreateProgram(shaderPath,
                vertexShader2FileName, fragmentShader2FileName);

        float[] color2 = {0.2f, 0.8f, 0.2f};
        cone0 = new Cone(64);
        float[] coneVertices = cone0.makeVertices(0.2f, 0.6f, 1f, color2);
        int[] coneIndices = cone0.makeIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboCone[0]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, coneVertices.length * 4,
                FloatBuffer.wrap(coneVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboCone[0]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, coneIndices.length * 4,
                IntBuffer.wrap(coneIndices), GL.GL_STATIC_DRAW);

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
        // END: Prepare cone (frustum) for drawing
    }

    public void displayCone(GL3 gl, PMVMatrix pmvMatrix) {
        gl.glUseProgram(shaderProgram2.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix)
        // to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glBindVertexArray(vaoCone[0]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, cone0.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    public void reshapeCone(GLAutoDrawable drawable, int x, int y, int width, int height, PMVMatrix pmvMatrix) {
        GL3 gl = drawable.getGL().getGL3();

        pmvMatrix.glMatrixMode(PMVMatrix.GL_PROJECTION);
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluPerspective(45f, (float) width/ (float) height, 0.01f, 10000f);
    }

    public void disposeCone(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderProgram2.deleteShaderProgram();

        // deactivate VAO and VBO
        gl.glBindVertexArray(0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);

        gl.glDisable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_DEPTH_TEST);

    }
}
