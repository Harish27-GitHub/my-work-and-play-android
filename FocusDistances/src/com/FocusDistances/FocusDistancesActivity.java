package com.FocusDistances;

import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class FocusDistancesActivity extends Activity {
    /** Called when the activity is first created. */
    Camera camera;
    Camera.AutoFocusCallback mAutoFocusListener  =  
    	    new Camera.AutoFocusCallback() {    
    	    @Override  
    	    public void onAutoFocus(boolean success, Camera camera) {  
    	    	Log.d("camera", "autofocus complete");
        		Camera.Parameters param = camera.getParameters();
        		float[] distances = new float[3];
        		param.getFocusDistances(distances);
        		Log.d("camera", "near index = " + distances[Camera.Parameters.FOCUS_DISTANCE_NEAR_INDEX]);
        		Log.d("camera", "optimal index = " + distances[Camera.Parameters.FOCUS_DISTANCE_OPTIMAL_INDEX]);
        		Log.d("camera", "far index = " + distances[Camera.Parameters.FOCUS_DISTANCE_FAR_INDEX]);
    	    }
    	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new CameraPreview(this));
        
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    		camera.autoFocus(mAutoFocusListener);
    	}
		return super.onTouchEvent(event); 
	}    
    	    
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder holder;
     
        CameraPreview(Context context) {
            super(context);
            holder = getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
     
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    		configure( format,  width, height) ;
        }
        
        public void surfaceDestroyed(SurfaceHolder holder) {
            camera.stopPreview();
            camera.release();
        }

		public void surfaceCreated(SurfaceHolder holder) {
			camera = Camera.open();
			try {
				camera.setPreviewDisplay(holder);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			camera.startPreview();

			
		}
		protected void setPictureFormat(int format) {
			Camera.Parameters params = camera.getParameters();
			List<Integer> supported = params.getSupportedPictureFormats();
			if (supported != null) {
				for (int f : supported) {
					if (f == format) {
						params.setPreviewFormat(format);
						camera.setParameters(params);
						break;
					}
				}
			}
		}
		
		protected void setPreviewSize(int width, int height) {
			Camera.Parameters params = camera.getParameters();
			List<Camera.Size> supported = params.getSupportedPreviewSizes();
			if (supported != null) {
				for (Camera.Size size : supported) {
					if (size.width <= width && size.height <= height) {
						params.setPreviewSize(size.width, size.height);
						camera.setParameters(params);
						break;
					}
				}
			}
		}
		public void configure(int format, int width, int height) {
			camera.stopPreview();
			setPictureFormat(format);
			setPreviewSize(width, height);
			camera.startPreview();
		}
    }    
    
}