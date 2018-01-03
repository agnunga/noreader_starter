package com.ag.noreader;

import android.app.Activity;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.noreader.img.MyTessOCR;

import java.io.File;

public class MainActivity extends MyBaseActivity {

    private MyTessOCR mTessOCR;
    private static final int MIN_SIZE = 5000 * 1024;

    private TextView mTextMessage;
    private ImageView mImageView;
    private Button button1;

    ImageView imageView;
    TextView username;

    private static final int TAKE_PICTURE = 1;
    private static final int TAKE_PICTURE2 = 2;
    private Uri imageUri;

    public void takePhoto2(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "/NoReader/CredoPic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    public void takePhoto(View view) {
        //Intent intent = new Intent(CameraActivity);
//        File photo = new File(Environment.getExternalStorageDirectory(), "/NoReader/CredoPic.jpg");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                Uri.fromFile(photo));
//        imageUri = Uri.fromFile(photo);
//        startActivityForResult(intent, TAKE_PICTURE);

        Intent intent = new Intent(this, CameraActivity.class);
        EditText editText = (EditText) findViewById(R.id.input_email);
        String message = editText.getText().toString();
        intent.putExtra(Notification.EXTRA_MESSAGES, message);
        startActivity(intent);


        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Log.d("TAG", bitmap.getWidth()+" by "+bitmap.getHeight());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        Log.d("TAG ", " IMAGE SIZE : "+byteSizeOf(bitmap));

                        if(byteSizeOf(bitmap) > MIN_SIZE){
                            bitmap = getScaledBitmap(bitmap);
                        }
                        Log.d("TAG ", " IMAGE SIZE AFTER : "+byteSizeOf(bitmap));
                        bitmap = mTessOCR.RemoveNoise(bitmap);
                        Log.d("TAG ", " AFTER REMOVING NOISE : "+byteSizeOf(bitmap));

                        imageView.setImageBitmap(bitmap);
                        Toast.makeText(this, selectedImage.toString(),
                                Toast.LENGTH_LONG).show();

                        mTessOCR = new MyTessOCR(MainActivity.this);

//                        String imageContent = mTessOCR.getOCRResult(bitmap);
                        String imageContent = mTessOCR.detectText(bitmap);
                        username.setText(imageContent);
                        Log.d("TAG ", " SMALLER PIC SAVED?  : " +mTessOCR.saveBitmap(bitmap));

                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
    }

    public static Bitmap getScaledBitmap(Bitmap b)
    {
        int reqWidth = b.getWidth()/2;
        int reqHeight = b.getHeight()/2;
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(
                0, 0, b.getWidth(), b.getHeight()), new RectF(0, 0, reqWidth, reqHeight), Matrix.ScaleToFit.CENTER);
        Bitmap reduced_bitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
        if(byteSizeOf(reduced_bitmap) > MIN_SIZE) {
            return getScaledBitmap(reduced_bitmap);
        } else {
            return reduced_bitmap;
        }
    }

    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.image_1);
        mTessOCR = new MyTessOCR(MainActivity.this);

        imageView = (ImageView) findViewById(R.id.image_1);
        username = (TextView) findViewById(R.id.input_email);



        byte[] byteArray = getIntent().getByteArrayExtra("image");
        if(byteArray != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Log.d("TAG", "Image Loaded " + bitmap.getWidth() + " by " + bitmap.getHeight());

            String imageContent = mTessOCR.detectText(bitmap);
            Log.d("NUMBERS : ", "  NUMBERS from image : " + imageContent);
            username.setText(imageContent);

        }else{
            Log.d("TAG", "byteArray is null " );
        }
/*

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.credopic);
        Bitmap image3 = BitmapFactory.decodeResource(getResources(), R.drawable.another);

        String photoPath = Environment.getExternalStorageDirectory()+ "/NoReader/CredoPic.jpg";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap image2 = BitmapFactory.decodeFile(photoPath, options);

        String photoPath2 = Environment.getExternalStorageDirectory()+ "/NoReader/CredoPic.jpg";
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inSampleSize = 8;
        Bitmap image22 = BitmapFactory.decodeFile(photoPath2, options2);

        String temp = mTessOCR.detectText(image);
        String temp2 = mTessOCR.detectText(image2);
        String temp3 = mTessOCR.detectText(image3);
        Log.d("NUMBERS 1 : ", temp+"  NUMBERS 2 : " + temp2+"  NUMBERS 3 : " + temp3);
*/

    }

    public void openCam(View v){
        takePhoto(v);
    }
}
