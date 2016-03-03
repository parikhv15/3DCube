/*
zaq * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 1.0/1.1.
 */
public class Xcube {

	private final FloatBuffer vertexBuffer;
	private final ShortBuffer drawListBuffer;
	private final FloatBuffer texBuffer;
	private int numFaces = 6;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	

	private float[] squareCoords = { // Vertices of the 6 faces
	// FRONT
			-0.5f, -0.5f, 0.5f, // 0. left-bottom-front
			0.5f, -0.5f, 0.5f, // 1. right-bottom-front
			-0.5f, 0.5f, 0.5f,
			// 2. left-top-front
			0.5f, 0.5f, 0.5f, // 3. right-top-front
			// BACK
			0.5f, -0.5f, -0.5f, // 6. right-bottom-back
			-0.5f, -0.5f, -0.5f, // 4. left-bottom-back
			0.5f, 0.5f, -0.5f, // 7. right-top-back
			-0.5f, 0.5f, -0.5f, // 5. left-top-back
			// LEFT
			-0.5f, -0.5f, -0.5f, // 4. left-bottom-back
			-0.5f, -0.5f, 0.5f, // 0. left-bottom-front
			-0.5f, 0.5f, -0.5f, // 5. left-top-back
			-0.5f, 0.5f, 0.5f, // 2. left-top-front
			// RIGHT
			0.5f, -0.5f, 0.5f, // 1. right-bottom-front
			0.5f, -0.5f, -0.5f, // 6. right-bottom-back
			0.5f, 0.5f, 0.5f, // 3. right-top-front
			0.5f, 0.5f, -0.5f, // 7. right-top-back
			// TOP
			-0.5f, 0.5f, 0.5f, // 2. left-top-front
			0.5f, 0.5f, 0.5f, // 3. right-top-front
			-0.5f, 0.5f, -0.5f, // 5. left-top-back
			0.5f, 0.5f, -0.5f, // 7. right-top-back
			// BOTTOM
			-0.5f, -0.5f, -0.5f, // 4. left-bottom-back
			0.5f, -0.5f, -0.5f, // 6. right-bottom-back
			-0.5f, -0.5f, 0.5f, // 0. left-bottom-front
			0.5f, -0.5f, 0.5f // 1. right-bottom-front
	};

	float[] texCoords = { // Texture coords for the above face (NEW)
	0.0f, 1.0f, // A. left-bottom (NEW)
			1.0f, 1.0f, // B. right-bottom (NEW)
			0.0f, 0.0f, // C. left-top (NEW)
			1.0f, 0.0f // D. right-top (NEW)
	};
	private int[] imageFileIDs = { // Image file IDs
			R.drawable.six , R.drawable.one, R.drawable.three, R.drawable.four,
			R.drawable.five, R.drawable.two};
/*	private int[] imageFileIDs = { // Image file IDs
			R.drawable.glass, R.drawable.glass, R.drawable.glass, R.drawable.glass,
			R.drawable.glass, R.drawable.glass};
			*/

	private int[] textureIDs = new int[numFaces];

	Bitmap[] bitmap;

	private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
	private float[][] color = { // Colors of the 6 faces
	{ 1.0f, 0.5f, 0.0f, 1.0f }, // 0. orange
	{ 1.0f, 0.0f, 1.0f, 1.0f }, // 1. violet
	{ 0.0f, 1.0f, 0.0f, 1.0f }, // 2. green
	{ 0.0f, 0.0f, 1.0f, 1.0f }, // 3. blue
	{ 1.0f, 0.0f, 0.0f, 1.0f }, // 4. red
	{ 1.0f, 1.0f, 0.0f, 1.0f } // 5. yellow
	};

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 */
	public Xcube() {
		// initialize vertex byte buffer for shape coordinates
		// (# of coordinate values * 4 bytes per float)
		ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(squareCoords);
		vertexBuffer.position(0);

		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);// (# of coordinate values * 2 bytes per short)
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(drawOrder);
		drawListBuffer.position(0);

		// Initialize ByteBudder for the Texture 
		ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4 * 6);
		tbb.order(ByteOrder.nativeOrder());
		texBuffer = tbb.asFloatBuffer();
		for (int face = 0; face < 6; face++) {
			texBuffer.put(texCoords);
		}
		texBuffer.position(0);
	}

	/**
	 * Encapsulates the OpenGL ES instructions for drawing this shape.
	 * 
	 * @param gl
	 *            - The OpenGL ES context in which to draw this shape.
	 */
	public void draw(GL10 gl) {
		// Since this shape uses vertex arrays, enable them
		gl.glFrontFace(GL10.GL_CCW); // Front face in counter-clockwise orientation
		gl.glEnable(GL10.GL_CULL_FACE); // Enable cull face
		gl.glCullFace(GL10.GL_BACK);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	
		gl.glVertexPointer( COORDS_PER_VERTEX, GL10.GL_FLOAT, 0, vertexBuffer);// point to vertex data:

		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer); // Define texture-coords buffer

		for (int face = 0; face < numFaces; face++) {
			gl.glColor4f(color[face][0], color[face][1], color[face][2], color[face][3]);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[face]);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, face * 4, 4);
		}

		// Disable vertex array drawing to avoid conflicts with shapes that don't use it
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY); // Disable texture-coords-array
	}

	public void loadTexture(GL10 gl, Context context) {
		gl.glGenTextures(6, textureIDs, 0); // Generate texture-ID array

		bitmap = new Bitmap[numFaces];
		for (int face = 0; face < numFaces; face++) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[face]); // Bind to texture ID
			// Set up texture filters
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR);

			// Read and decode input as bitmap
			bitmap[face] = BitmapFactory.decodeStream(context.getResources().openRawResource(imageFileIDs[face]));
		}

		// Build Texture from loaded bitmap for the currently-bind texture ID
		for (int face = 0; face < numFaces; face++) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[face]);
			
			// Build Texture from loaded bitmap for the currently-bind texture ID
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap[face], 0);
			bitmap[face].recycle();
		}
	}
}