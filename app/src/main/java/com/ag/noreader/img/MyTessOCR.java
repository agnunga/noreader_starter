package com.ag.noreader.img;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.ag.noreader.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyTessOCR {
    private String datapath;
    private TessBaseAPI mTess;
    Context context;
    private String TAG = this.getClass().getSimpleName();
    private static final String tessdir = "tesseract";
    private static final String subdir = "tessdata";
    private static final String filename = "eng.traineddata";
    private static String trainedDataPath;
    private static String tesseractFolder;
    private static boolean initiated;

    public static String getTesseractFolder() {
        return tesseractFolder;
    }

    public static String getTrainedDataPath(){
        return initiated ? trainedDataPath : null;
    }

    public MyTessOCR(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public String detectText(Bitmap bitmap) {
        Log.d(TAG, "Initialization of TessBaseApi");
        initTessTrainedData(context);
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        String path = getTesseractFolder();
        Log.d(TAG, "Tess folder: " + path);
        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(path, "eng");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-qwertyuiop[]}{POIU" +
                "YTREWQasdASDfghFGHjklJKLl;L:'\"\\|~`xcvXCVbnmBNM,./<>?");
        tessBaseAPI.setPageSegMode(TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
        Log.d(TAG, "Ended initialization of TessEngine and Running inspection on bitmap");
        tessBaseAPI.setImage(bitmap);
        String inspection = tessBaseAPI.getUTF8Text();
        String msg = (inspection.length() > 1)? inspection : "No Data";
        Log.d(TAG, "Got data ::::::::::::::::::::::::::::::::::: " + msg);
        tessBaseAPI.end();
        System.gc();
        return inspection;
    }

    public void stopRecognition() {
        mTess.stop();
    }

    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }

    private void copyFile(Context context) {
        AssetManager assetManager = context.getAssets();
        Log.d("COPY", "copyFile ::::: ");
        try {
            InputStream in = assetManager.open("tessdata/eng.traineddata");
            OutputStream out = new FileOutputStream(datapath + "/tessdata/" + "eng.traineddata");
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);

            while (read != -1) {
                out.write(buffer, 0, read);
                read = in.read(buffer);
            }
        } catch (Exception e) {
            Log.d("mylog", "couldn't copy with the following error : "+e.toString());
        }
    }

    public boolean saveBitmap(Bitmap bm){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/NoReader/");
        myDir.mkdirs();
//        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
//        String fname = "Image-" + n + ".jpg";
        String fname = "CredoPic_sm.jpg";
        File file = new File(myDir, fname);
        Log.i(TAG, "" + file);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Bitmap RemoveNoise(Bitmap bmap) {
        for (int x = 0; x < bmap.getWidth(); x++) {
            for (int y = 0; y < bmap.getHeight(); y++) {
                int pixel = bmap.getPixel(x, y);
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);
                if (R < 162 && G < 162 && B < 162)
                    bmap.setPixel(x, y, Color.BLACK);
            }
        }
        for (int  x = 0; x < bmap.getWidth(); x++) {
            for (int y = 0; y < bmap.getHeight(); y++) {
                int pixel = bmap.getPixel(x, y);
                int R = Color.red(pixel);
                int G = Color.green(pixel);
                int B = Color.blue(pixel);
                if (R > 162 && G > 162 && B > 162)
                    bmap.setPixel(x, y, Color.WHITE);
            }
        }
        return bmap;
    }

    public void initTessTrainedData(Context context){

        if(initiated)
            return;

        File appFolder = context.getFilesDir();
        File folder = new File(appFolder, tessdir);
        if(!folder.exists())
            folder.mkdir();
        tesseractFolder = folder.getAbsolutePath();

        File subfolder = new File(folder, subdir);
        if(!subfolder.exists())
            subfolder.mkdir();

        File file = new File(subfolder, filename);
        trainedDataPath = file.getAbsolutePath();
        Log.d(TAG, "Trained data filepath: " + trainedDataPath);

        if(!file.exists()) {

            try {
                FileOutputStream fileOutputStream;
                byte[] bytes = readRawTrainingData(context);
                if (bytes == null)
                    return;
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
                initiated = true;
                Log.d(TAG, "Prepared training data file");
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Error opening training data file\n" + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "Error opening training data file\n" + e.getMessage());
            }
        }
        else{
            initiated = true;
        }
    }

    private byte[] readRawTrainingData(Context context){

        try {
            InputStream fileInputStream = context.getResources()
                    .openRawResource(R.raw.eng_traineddata);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            int bytesRead;

            while (( bytesRead = fileInputStream.read(b))!=-1){
                bos.write(b, 0, bytesRead);
            }

            fileInputStream.close();
            return bos.toByteArray();

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error reading raw training data file\n"+e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Error reading raw training data file\n" + e.getMessage());
        }
        return null;
    }

}


