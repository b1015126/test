package com.example.b1015126.npverifier;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NPVerifier extends AppCompatActivity {
    static {
        System.loadLibrary("opencv_java3");
    }

    private Uri _imageUri;
    static Bitmap bmp;
    static Bitmap dsc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_npverifier);
    }
    public void onCameraImageClick(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 2000);
            return;
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");  // （1）
        Date now = new Date(System.currentTimeMillis());  // （1）
        String nowStr = dateFormat.format(now);  // （1）
        String fileName = "UseCameraActivityPhoto_" + nowStr +".jpg";  // （1）

        ContentValues values = new ContentValues();  // （2）
        values.put(MediaStore.Images.Media.TITLE, fileName);  // （3）
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");  // （4）

        ContentResolver resolver = getContentResolver();  // （5）
        _imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  // （6）


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri);  // （8）
        startActivityForResult(intent, 200);





    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 200 && resultCode == RESULT_OK) {
            //撮影したUri画像をbitmapに変換
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), _imageUri);
            }catch (IOException e) {
                //例外が起きた際に例外情報を出力
                e.printStackTrace();
            }

            Mat mat = new Mat();
            //bitmapをmatに変換
            Utils.bitmapToMat(bmp,mat);
            //グレースケール変換
            Imgproc.cvtColor(mat,mat, Imgproc.COLOR_RGB2GRAY);
            //360*360にトリミング
            Rect tri = new Rect((mat.width()/2) - 180,(mat.height()/2) - 180,360,360);
            mat = new Mat(mat,tri);
            dsc= Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
            //matをbitmapに変換
            Utils.matToBitmap(mat, dsc);
            //画面に編集した画像(bitmap)を表示
            ImageView ivCamera = (ImageView) findViewById(R.id.ivCamera);  // （2）
            ivCamera.setImageBitmap(dsc);
        }
    }





}


