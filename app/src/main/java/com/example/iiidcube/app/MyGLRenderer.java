/*
 * Copyright (C) 2011 The Android Open Source Project
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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class must
 * override the OpenGL ES drawing lifecycle methods:
 * <ul>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

	private Xcube xcube;
	private Context context;

	private float mAngle;
	public float rx, ry, rz;

	public static float EffectFalg = 0.0f;

	// Lighting
	boolean lightingEnabled = false;
	private float[] lightAmbient = { 0.7f, 0.6f, 0.5f, 1.0f };
	private float[] lightDiffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] lightSpecular = { 0.3f, 0.0f, 1.0f, 1.0f };
	private float[] lightPosition = { 0.0f, 0.0f, 0.0f, 1.0f };
	static float[] smallMat = { 1.0f, 0.0f, 0.0f, 0.0f, 
								0.0f, 1.0f, 0.0f, 0.0f,
								0.0f, 0.0f, 1.0f, 0.0f, 
								0.0f, 0.0f, 0.0f, 1.0f };

	public MyGLRenderer(Context context) {
		this.context = context; // Get the application context (NEW)
		xcube = new Xcube();
	}

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig config) {
		
		GL11 gl = (GL11) gl10;
		
		// Set the background frame color
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		gl.glClearDepthf(1.0f); // Set depth's clear-value to farthest
		gl.glEnable(GL10.GL_DEPTH_TEST); // Enables depth-buffer for hidden surface removal
		gl.glDepthFunc(GL10.GL_LEQUAL); // The type of depth testing to do
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); // nice perspective view
		gl.glShadeModel(GL10.GL_SMOOTH); // Enable smooth shading of color
		gl.glDisable(GL10.GL_DITHER); // Disable dithering for better performance

		// each time the surface is created texture is setup
		xcube.loadTexture(gl, context); // Texture Image used
		gl.glEnable(GL10.GL_TEXTURE_2D); // Texture Enabled

		flag = 0;
	}

	@Override
	public void onDrawFrame(GL10 gl10) {

		GL11 gl = (GL11) gl10;
		if (EffectFalg == 0.0) {
			// Draw background color
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		}

		// Setting up the model view matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glLoadIdentity(); // reset the matrix to its default state

		// Setting up the camera view
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, lightAmbient, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, lightDiffuse, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, lightSpecular, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPosition, 0);
		gl.glEnable(GL10.GL_LIGHT1); // Enable Light 1 (NEW)
		gl.glEnable(GL10.GL_LIGHT0); // Enable the default Light 0
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glColor4f(0.5f, 0.6f, 1.0f, 0.5f); // Full brightness, 50% alpha

		// Blending Functios calls
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_BLEND); // Turn blending on (NEW)
		gl.glDisable(GL10.GL_DEPTH_TEST);

		// Applying rotation
		gl.glRotatef(mAngle, rx, ry, 0.0f);
		gl.glMultMatrixf(smallMat, 0);
		gl.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, smallMat, 0);
		

		// drawing the cube
		xcube.draw(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Adjust the viewport based on geometry changes
		// such as screen rotations
		gl.glViewport(0, 0, width, height);
		
		// make adjustments for screen ratio
		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION); // setting the matrix to projection
												// mode
		gl.glLoadIdentity(); // reset the matrix to default
		gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7); // applying the projection
													// matrix
		GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
	}

	/**
	 * Returns the rotation angle of the cube.
	 * 
	 * @return - A float representing the rotation angle.
	 */
	public float getAngle() {
		return mAngle;
	}

	/**
	 * Sets the rotation angle of the cube.
	 */
	public void setAngle(float angle) {
		mAngle = angle;
	}

	public float getRz() {
		return rz;
	}

	public void setRz(float rz) {
		this.rz = rz;
	}

	public float getRy() {
		return ry;
	}

	public void setRy(float ry) {
		this.ry = ry;
	}

	public float getRx() {
		return rx;
	}

	public void setRx(float rx) {
		this.rx = rx;
	}

	public void setEffectFalg(float EffectFalg) {
		this.EffectFalg = EffectFalg;
	}

}