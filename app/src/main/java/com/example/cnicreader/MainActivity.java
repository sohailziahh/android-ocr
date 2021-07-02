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
    Cnic thisCnic;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisCnic = new Cnic(this);
    }



    private void convertToTextBlocks(Bitmap bitmap)
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

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
                Log.d("ocr-sohail", "" + textBlock.getValue() + " - " + textBlock.getValue().length());
            }

            textBlocks.sort((o1, o2) -> {
                int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                if (diffOfTops != 0)
                {
                    return diffOfTops;
                }
                return diffOfLefts;
            });

            // todo this shouldnt be called here, as each function should do one thing only.
            thisCnic.imageCNIC(textBlocks);
//            imageCNIC(textBlocks);
            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue());
                    detectedText.append("\n");
                }
            }
            // todo same thing as above.
            thisCnic.setText(detectedText);
            thisCnic.saveData();
        } finally
        {
            textRecognizer.release();
        }
    }
//        private void imageCNIC (List<TextBlock> textBlocks) {
//
//            for (int i = 0; i < textBlocks.size(); i++) {
//                TextBlock textBlock = textBlocks.get(i);
//                String s = textBlock.getValue();
//                s.trim();
//                if (name.equals("Deciding...") && (s.equals("Name"))) {
//                    String tempTextBlockValue = textBlocks.get(i + 1).getValue();
//                    char[] characters = tempTextBlockValue.toCharArray();
//                    containsDigit = false;
//                    for (char c : characters) {
//                        if (Character.isDigit(c))
//                            containsDigit = true;
//                    }
//                    if (!containsDigit)
//                        name = tempTextBlockValue;
//                } else if (fatherName.equals("Deciding...") && (s.equals("Father Name"))) {
//                    String tempTextBlockValue = textBlocks.get(i + 1).getValue();
//                    char[] characters = tempTextBlockValue.toCharArray();
//                    containsDigit = false;
//                    for (char c : characters) {
//                        if (Character.isDigit(c))
//                            containsDigit = true;
//                    }
//                    if (!containsDigit)
//                        fatherName = tempTextBlockValue;
//                } else if (gender.equals("Deciding...")
//                        && (textBlock.getValue().contains("M"))
//                        && (textBlock.getValue().indexOf("M") == 0)
//                        && (textBlock.getValue().length() == 1)) {
//                    gender = "Male";
//                } else if (gender.equals("Deciding...")
//                        && (textBlock.getValue().contains("F"))
//                        && (textBlock.getValue().indexOf("F") == 0)
//                        && textBlock.getValue().length() == 1) {
//                    gender = "Female";
//                } else if (((identityNumber.equals("Deciding..."))
//                        || dateOfBirth.equals("Deciding..."))
//                        && (textBlock.getValue().contains("-"))
//                        && (textBlock.getValue().contains("."))) {
//
//                    String string = textBlock.getValue();
//                    //identityNumberCheck
//                    if ((string.split(" ")[0].length() == 15) && (string.split(" ")[0].contains("-")))
//                        identityNumber = string.split(" ")[0];
//                    //dateOfBirthCheck
//                    if ((string.split(" ")[1].length() == 10) && (string.split(" ")[1].contains("."))) {
//                        if (textBlock.getValue().split("\\.").length == 3) {
//                            // age cannot be less than 18
//                            if ((Integer.parseInt(string.split(" ")[1].split("\\.")[2])) < 2004)
//                                dateOfBirth = string.split(" ")[1];
//                        }
//                    }
//                } else if (dateOfBirth.equals("Deciding...")
//                        && (textBlock.getValue().length() == 10)
//                        && (textBlock.getValue().contains("."))) {
//                    if (textBlock.getValue().split("\\.").length == 3) {
//                        // age cannot be less than 18
//                        if ((Integer.parseInt(textBlock.getValue().split("\\.")[2])) < 2004)
//                            dateOfBirth = textBlock.getValue();
//                    }
//                } else if ((identityNumber.equals("Deciding..."))
//                        && (textBlock.getValue().contains("-"))
//                        && (textBlock.getValue().length() == 15)) {
//                    String string = textBlock.getValue();
//                    if (string.substring(5).equals("-") && string.substring(13).equals("-"))
//                        identityNumber = string;
//                } else if ((dateOfExpiry.equals("Deciding...")) && !(dateOfIssue.equals("Deciding..."))) {
//                    if (textBlock.getValue().contains(".")
//                            && (textBlock.getValue().length() == 10)
//                            && (!textBlock.getValue().equals(dateOfIssue)))
//                        try {
//                            if (textBlock.getValue().split("\\.").length == 3)
//                            {
//                                //because NADRA started issuing smartcards from 2012
//                                if ((Integer.parseInt(textBlock.getValue().split("\\.")[2])) > 2022)
//                                    dateOfExpiry = textBlock.getValue();
//                            }
//                        } catch (NumberFormatException e) {
//                            e.printStackTrace();
//                        }
//                } else if ((dateOfIssue.equals("Deciding..."))) {
//                    if (textBlock.getValue().contains(".")
//                            && (textBlock.getValue().length() == 10)
//                            && (!textBlock.getValue().equals(dateOfExpiry)))
//                        try {
//                            if (textBlock.getValue().split("\\.").length == 3) {
//                                if ((Integer.parseInt(textBlock.getValue().split("\\.")[2])) < 2021
//                                        && (Integer.parseInt(textBlock.getValue().split("\\.")[2])) > 2004)
//                                    dateOfIssue = textBlock.getValue();
//                            }
//                        } catch (NumberFormatException e) {
//                            e.printStackTrace();
//                        }
//                }
//            }
//            if (!name.equals("Deciding..."))
//                countryOfStay = "Pakistan";
//
//    }

    boolean dataSaved = false;

    //    private void saveData() {
//
//        if (!identityNumber.equals("Deciding...")
//                && (!dateOfBirth.equals("Deciding..."))
//                && (!dateOfIssue.equals("Deciding..."))
//                && (!dateOfExpiry.equals("Deciding..."))
//                && (!gender.equals("Deciding..."))
//                && (!fatherName.equals("Deciding..."))
//                && (!name.equals("Deciding..."))
//                && (!countryOfStay.equals("Deciding..."))) {
//
//            try {
//                File directory = this.getExternalFilesDir(csvDir);
//
//                if (!directory.exists()) {
//                    if (!directory.mkdirs()) {
//                        return;
//                    }
//                }
//                // If you require it to make the entire directory path including parents,
//                // use directory.mkdirs(); here instead.
//
//                try {
//                    Files.write(Paths.get(csvPath), Collections.singleton("Name: " + name + "\nFather Name: " + fatherName + "\nGender: " + gender +
//                            "\nIdentity Number: " + identityNumber + "\nDate Of Birth: " + dateOfBirth +
//                            "\nDate Of Issue: " + dateOfIssue + "\nDate Of Expiry: " + dateOfExpiry + "\nCountry Of Stay: " + countryOfStay));
//                    dataSaved = true;
//                    Log.d("file-sohail", csvPath);
//                    runOnUiThread(() -> Toast.makeText(getBaseContext(), "Date stored in a CSV.", Toast.LENGTH_LONG).show());
//
//                } catch (IOException e) {
//                    Log.e("error", e.getMessage());
//                }
//            } catch (Exception e) {
//                Log.e("error", e.getMessage());
//            }
//
//        }
//    }
    Bitmap currentBitmap;

    @Override
    public void processImage(Bitmap bitmap) {
        currentBitmap = bitmap;
        convertToTextBlocks(bitmap);
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