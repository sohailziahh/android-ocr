package com.example.cnicreader;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cnicreader.databinding.BasicExerciseViewBinding;
import com.example.cnicreader.views.OverlayView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends CameraActivity {


    protected RelativeLayout parentContainer;

    protected BasicExerciseViewBinding viewBinding;

    //private TextView detectedTextView;

    public void initViews()
    {
        viewBinding = getViewBinding();
        parentContainer = findViewById(R.id.containerParent);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        parentContainer.addView(viewBinding.getRoot(), layoutParams);
    }


    public BasicExerciseViewBinding getViewBinding()
    {
        if (viewBinding == null)
            viewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.basic_exercise_view, null, false);
        return viewBinding;
    }

    TextView textView;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       
    }



    private void textRecognition(Bitmap bitmap)
    {

        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        try {
            if (!textRecognizer.isOperational()) {
                new AlertDialog.
                        Builder(this).
                        setMessage("Text recognizer could not be set up on your device").show();
                return;
            }

            Log.d("ocr-sohail", "-------");
            DocumentExtractor extraction = new BaseDocumentExtractor();
            extraction.convertToTextBlocks(bitmap,textRecognizer);


        } finally
        {
            textRecognizer.release();
        }
    }

    Bitmap currentBitmap;

    @Override
    public void processImage(Bitmap bitmap) {
        currentBitmap = bitmap;
        textRecognition(bitmap);
    }


    String stringBuilder;

    private void setText(String message) {

        textView.post(new Runnable() {
            @Override
            public void run() {
                stringBuilder = message +
                        "\n";
                //textView.setText(stringBuilder);
            }
        });
    }



    public void onStart()
    {
        super.onStart();
    }
}