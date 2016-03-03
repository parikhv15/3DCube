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

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen. This view
 * can also be used to capture touch events, such as a user interacting with
 * drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

	private final MyGLRenderer mRenderer;

	public MyGLSurfaceView(Context context) {
		super(context);

		// Set the Renderer for drawing on the GLSurfaceView
		mRenderer = new MyGLRenderer(context);
		setRenderer(mRenderer);

		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	private final float TOUCH_SCALE_FACTOR = 180.0f / 360;
	private float mPreviousX;
	private float mPreviousY;
	private float deltaX, deltaY;

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, we are only
		// interested in events where the touch position changed.

		float x = e.getX();
		float y = e.getY();

		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:

			float dx = x - mPreviousX;
			float dy = y - mPreviousY;

			//Rolling ball rotation
			float nx = -dy;
			float ny = dx;

			float scale = (float) Math.sqrt(nx * nx + ny * ny);

			if (scale > 0.1) {

				if (scale < 1e-7)
					return true;

				mRenderer
						.setAngle(TOUCH_SCALE_FACTOR
								* scale); // = 180.0f / 320

				 nx = nx / scale;
				 ny = ny / scale;

				Log.d("nx", String.valueOf(nx));
				Log.d("ny", String.valueOf(ny));
				
				//setting the axis of roation
				mRenderer.setRx(nx);
				mRenderer.setRy(ny);
				

			}

			requestRender();
		}

		
		mPreviousX = x;
		mPreviousY = y;

		return true;
	}

}
