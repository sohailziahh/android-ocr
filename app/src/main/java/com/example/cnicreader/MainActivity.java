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


    File cascadeFile;
    CascadeClassifier faceDetector;
    protected RelativeLayout parentContainer;
    protected OverlayView trackingOverlay;

    protected BasicExerciseViewBinding viewBinding;


    private static final int TEMPLATE_IMAGE = R.drawable.nic_image;
    /**
     * the result matrix
     */
    Mat result;
    /**
     * the camera image
     */
    Mat img;
    /**
     * the template image used for template matching
     * or for copying into the camera view
     */
    Mat templ;
    /**
     * the crop rectangle with the size of the template image
     */
    Rect rect;
    /**
     * selected area is the camera preview cut to the crop rectangle
     */
    Mat selectedArea;


    private TextView detectedTextView;
    ImageView imageView;

    public void initViews() {
        viewBinding = getViewBinding();

        parentContainer = findViewById(R.id.containerParent);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        parentContainer.addView(viewBinding.getRoot(), layoutParams);

        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        //trackingOverlay.addCallback(this::exerciseDraw);
    }


    public BasicExerciseViewBinding getViewBinding() {
        if (viewBinding == null)
            viewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.basic_exercise_view, null, false);
        return viewBinding;
    }

    TextView textView;


//    static {
//        if (!OpenCVLoader.initDebug()) {
//            // Handle initialization error
//        }
//    }


    private void initFaceDetector() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
        File cascadeDir = getDir("cascade", MODE_PRIVATE);
        cascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");
        FileOutputStream fos = null;

        fos = new FileOutputStream(cascadeFile);


        byte[] buffer = new byte[4096];
        int byteRead;

        while ((byteRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, byteRead);
        }
        is.close();
        fos.close();


        faceDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());

        if (faceDetector.empty())
            faceDetector = null;
        else
            cascadeDir.delete();
    }


    private void initTemplateMatcher() {
        Mat bgr = null;
        try {
            bgr = Utils.loadResource(getApplicationContext(), TEMPLATE_IMAGE, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // convert the image to rgba
        templ = new Mat();
        Imgproc.cvtColor(bgr, templ, Imgproc.COLOR_BGR2GRAY);//Imgproc.COLOR_BGR2RGBA);

        // Imgproc.Canny(templ, templ, 50.0, 200.0);

        // init the crop rectangle, necessary for copying the image to the camera view
        rect = new Rect(0, 0, templ.width(), templ.height());

        // init the result matrix
        result = new Mat();
        img = new Mat();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        try {
//            initFaceDetector();
//            initTemplateMatcher();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        // imageView = (ImageView) findViewById(R.id.imageView1);
        setContentView(R.layout.activity_main);
        //textView = (TextView) findViewById(R.id.text_view);
        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        //trackingOverlay.addCallback(this::exerciseDraw);
        detectedTextView = (TextView) findViewById(R.id.detected_text);
        detectedTextView.setMovementMethod(new ScrollingMovementMethod());

        csvPath = this.getExternalFilesDir(csvDir).getAbsolutePath() + "/" + csvName;
    }


    Canvas canvas = null;

    public String csvName = "CNIC-DATA-" + new Timestamp(System.currentTimeMillis()) + ".csv";
    ;
    private String csvDir = "CNIC_DATA/";
    private String csvPath;

    ArrayList<String> dates = new ArrayList<>();
    String gender = "Deciding...";
    String name = "Deciding...";
    String fatherName = "Deciding...";
    String dateOfBirth = "Deciding...";
    String dateOfIssue = "Deciding...";
    String dateOfExpiry = "Deciding...";
    boolean containsDigit = true;
    String identityNumber = "Deciding...";
    String countryOfStay = "Deciding...";

    private void inspectFromBitmap(Bitmap bitmap) {

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
                if (diffOfTops != 0) {
                    return diffOfTops;
                }
                return diffOfLefts;
            });


            for (int i = 0; i < textBlocks.size(); i++) {
                TextBlock textBlock = textBlocks.get(i);

                if (name.equals("Deciding...") && (textBlock.getValue().equals(" Name ") || textBlock.getValue().equals(" Name") ||
                        textBlock.getValue().equals("Name ") || textBlock.getValue().equals("Name"))) {
                    String tempTextBlockValue = textBlocks.get(i + 1).getValue();
                    char[] characters = tempTextBlockValue.toCharArray();
                    containsDigit = false;
                    for (char c : characters) {
                        if (Character.isDigit(c))
                            containsDigit = true;
                    }
                    if (!containsDigit)
                        name = tempTextBlockValue;
                } else if (fatherName.equals("Deciding...") && (textBlock.getValue().equals(" Father Name ") || textBlock.getValue().equals(" Father Name") ||
                        textBlock.getValue().equals("Father Name ") || textBlock.getValue().equals("Father Name"))) {
                    String tempTextBlockValue = textBlocks.get(i + 1).getValue();
                    char[] characters = tempTextBlockValue.toCharArray();
                    containsDigit = false;
                    for (char c : characters) {
                        if (Character.isDigit(c))
                            containsDigit = true;
                    }
                    if (!containsDigit)
                        fatherName = tempTextBlockValue;
                } else if (gender.equals("Deciding...") && (textBlock.getValue().contains("M")) && (textBlock.getValue().indexOf("M") == 0) &&
                        textBlock.getValue().length() == 1) {
                    gender = "Male";
                } else if (gender.equals("Deciding...") && (textBlock.getValue().contains("F")) && (textBlock.getValue().indexOf("F") == 0)
                        && textBlock.getValue().length() == 1) {
                    gender = "Female";
                } else if (((identityNumber.equals("Deciding...")) || dateOfBirth.equals("Deciding...")) && (textBlock.getValue().contains("-")) && (textBlock.getValue().contains("."))) {
                    String string = textBlock.getValue();
                    //identityNumberCheck
                    if ((string.split(" ")[0].length() == 15) && (string.split(" ")[0].contains("-")))
                        identityNumber = string.split(" ")[0];
                    //dateOfBirthCheck
                    if ((string.split(" ")[1].length() == 10) && (string.split(" ")[1].contains("."))) {
                        if (textBlock.getValue().split("\\.").length == 3) {
                            if ((Integer.parseInt(string.split(" ")[1].split("\\.")[2])) < 2004)
                                dateOfBirth = string.split(" ")[1];
                        }
                    }

                } else if (dateOfBirth.equals("Deciding...") && (textBlock.getValue().length() == 10) && textBlock.getValue().contains(".")) {
                    if (textBlock.getValue().split("\\.").length == 3) {
                        // age cannot be less than 18
                        if ((Integer.parseInt(textBlock.getValue().split("\\.")[2])) < 2004)
                            dateOfBirth = textBlock.getValue();
                    }
                } else if ((identityNumber.equals("Deciding...")) && (textBlock.getValue().contains("-")) && (textBlock.getValue().length() == 15)) {
                    String string = textBlock.getValue();
                    if (string.substring(5).equals("-") && string.substring(13).equals("-"))
                        identityNumber = string;

                } else if ((dateOfExpiry.equals("Deciding...")) && !(dateOfIssue.equals("Deciding..."))) {
                    if (textBlock.getValue().contains(".") && textBlock.getValue().length() == 10 && !textBlock.getValue().equals(dateOfIssue))
                        try {
                            if (textBlock.getValue().split("\\.").length == 3) {
                                //because NADRA started issuing smartcards from 2012
                                if ((Integer.parseInt(textBlock.getValue().split("\\.")[2])) > 2022)
                                    dateOfExpiry = textBlock.getValue();
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                } else if ((dateOfIssue.equals("Deciding..."))) {

                    if (textBlock.getValue().contains(".") && textBlock.getValue().length() == 10 && !textBlock.getValue().equals(dateOfExpiry))
                        try {
                            if (textBlock.getValue().split("\\.").length == 3) {
                                if ((Integer.parseInt(textBlock.getValue().split("\\.")[2])) < 2021
                                        && (Integer.parseInt(textBlock.getValue().split("\\.")[2])) > 2004)
                                    dateOfIssue = textBlock.getValue();
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                }

            }

            if (!name.equals("Deciding..."))
                countryOfStay = "Pakistan";


            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue());
                    detectedText.append("\n");
                    //canvas.drawRect(textBlock.getBoundingBox(), paint );
                }
            }

            setText(detectedText);
            saveData();
        } finally {
            textRecognizer.release();
        }
    }


    boolean dataSaved = false;

    private void saveData() {

        if (!identityNumber.equals("Deciding...") && !dateOfBirth.equals("Deciding...") && !dateOfIssue.equals("Deciding...") &&
                !dateOfExpiry.equals("Deciding...") && !gender.equals("Deciding...") && !fatherName.equals("Deciding...") &&
                !name.equals("Deciding...") && !countryOfStay.equals("Deciding...")) {

            try {
                File directory = this.getExternalFilesDir(csvDir);

                if (!directory.exists()) {
                    if (!directory.mkdirs()) {
                        return;
                    }
                }
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.

                try {
                    Files.write(Paths.get(csvPath), Collections.singleton("Name: " + name + "\nFather Name: " + fatherName + "\nGender: " + gender +
                            "\nIdentity Number: " + identityNumber + "\nDate Of Birth: " + dateOfBirth +
                            "\nDate Of Issue: " + dateOfIssue + "\nDate Of Expiry: " + dateOfExpiry + "\nCountry Of Stay: " + countryOfStay));
                    dataSaved = true;
                    Log.d("file-sohail", csvPath);
                    runOnUiThread(() -> Toast.makeText(getBaseContext(), "Date stored in a CSV.", Toast.LENGTH_LONG).show());

                } catch (IOException e) {
                    Log.e("error", e.getMessage());
                }
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }

        }
    }


    Bitmap currentBitmap;

    @Override
    public void runOCR(Bitmap bitmap) {
        currentBitmap = bitmap;

        inspectFromBitmap(bitmap);

        //templateMatcher(mat);
        // cascadeRect(mat);
    }



    private void cascadeRect(Mat mat) {

        Core.flip(mat.t(), mat, 1);
//        Mat rgb = new Mat();
        int rgbHeight = mat.height();
        int absoluteFaceSize = (int) (rgbHeight * 0.1);
        MatOfRect faces = new MatOfRect();

        faceDetector.detectMultiScale(mat, faces, 1.1, 2, 2, new Size(absoluteFaceSize, absoluteFaceSize), new Size());

        canvRect = null;


        for (Rect rect : faces.toArray()) {
            Log.d("logface", "" + rect);
            canvRect = rect;

        }
        Core.flip(mat.t(), mat, 0);


        if (faces.toArray().length != 0)
            setText("FACE DETECTED!");

        else
            setText("FACE NOT DETECTED");
    }


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


    String stringBuilder;

    private void setText(StringBuilder message) {

        detectedTextView.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                stringBuilder = message +
                        "\n";
                // detectedTextView.setText(stringBuilder);
                detectedTextView.setText("Name: " + name + "\nFather Name: " + fatherName + "\nGender: " + gender +
                        "\nIdentity Number: " + identityNumber + "\nDate Of Birth: " + dateOfBirth +
                        "\nDate Of Issue: " + dateOfIssue + "\nDate Of Expiry: " + dateOfExpiry + "\nCountry Of Stay: " + countryOfStay);
            }
        });
    }

    public void onStart() {
        super.onStart();
    }


    // canvas drawing


    Rect canvRect;


    public void exerciseDraw(Canvas canvas) {

        //canvas.drawText("hereIam", 20, 300, paint);
    }
}