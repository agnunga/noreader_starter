package com.ag.noreader.img;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.ag.noreader.util.Utilize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by A on 5/11/2014.
 */
public class CameraEngine {
    static final String TAG = "DBG_" + CameraUtils.class.getName();
    private MyTessOCR mTessOCR;
    boolean on;
    Camera camera;
    SurfaceHolder surfaceHolder;

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
        }
    };

    public boolean isOn() {
        return on;
    }

    private CameraEngine(SurfaceHolder surfaceHolder){
        this.surfaceHolder = surfaceHolder;
    }

    static public CameraEngine New(SurfaceHolder surfaceHolder){
        Log.d(TAG, "Creating camera engine");
        return  new CameraEngine(surfaceHolder);
    }

    public List<String> previewer(final Context context, final FocusBoxView focusBox) {
        if (camera == null)
            return null;

        final List<String> resultList = new ArrayList<>();
        if (isOn()) {
            mTessOCR = new MyTessOCR(context);
            Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    Camera.Parameters parameters = camera.getParameters();
                    Camera.Size size = parameters.getPreviewSize();
                    YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    yuvImage.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, os);
                    byte[] jpegByteArray = os.toByteArray();
                    Bitmap bitmap_cropped = Tools.getFocusedBitmap(context, camera, jpegByteArray, focusBox.getBox());
//                    Bitmap bmp_uncropped = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);
                    String imageContent = mTessOCR.detectText(bitmap_cropped);
                    resultList.addAll(Utilize.getListOfContent(imageContent));
                    Log.d(TAG, "resultList size \n\n" + resultList.size() + " \n\nPREVIEWS ::::::: cropped bitmap W = " + bitmap_cropped.getWidth() + "\n CONTENT::: " + imageContent);// + " bmp WIDTH " + bmp_uncropped.getWidth());
                }

            };
            if (resultList.size() >= 3) {
                String resolved = Utilize.getDorminantContent(resultList);
                Log.d(TAG, "\n\n\n RESOLVED CONTENT::: " + resolved + "\n\n\n");
                camera.stopPreview();
                previewCallback = null;
            }
            camera.setPreviewCallback(previewCallback);
            camera.startPreview();
        }
        return resultList;
    }

    public void requestFocus() {
        if (camera == null)
            return;

        if (isOn()) {
            camera.autoFocus(autoFocusCallback);
        }
    }

    public void start() {

        Log.d(TAG, "Entered CameraEngine - start()");
        this.camera = CameraUtils.getCamera();

        if (this.camera == null)
            return;

        Log.d(TAG, "Got camera hardware");

        try {

            this.camera.setPreviewDisplay(this.surfaceHolder);
            this.camera.setDisplayOrientation(90);
            this.camera.startPreview();

            on = true;

            Log.d(TAG, "CameraEngine preview started");

        } catch (IOException e) {
            Log.e(TAG, "Error in setPreviewDisplay");
        }
    }

    public void stop(){

        if(camera != null){
            //this.autoFocusEngine.stop();
            camera.release();
            camera = null;
        }

        on = false;

        Log.d(TAG, "`CameraEngine Stopped");
    }

    public void takeShot(Camera.ShutterCallback shutterCallback,
                         Camera.PictureCallback rawPictureCallback,
                         Camera.PictureCallback jpegPictureCallback ){
        if(isOn()){
            camera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
        }else {
            Log.e("CAM", "Cannot open camera");
        }
    }
}

class CameraUtils {

    static final String TAG = "DBG_ " + CameraUtils.class.getName();

    public static boolean deviceHasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera getCamera() {
        try {
            return Camera.open();
        } catch (Exception e) {
            Log.e(TAG, "Cannot getCamera()");
            return null;
        }
    }

    public static List<String> formatString(String content){
        List<String> list = new ArrayList<>();

        return list;
    }
}
