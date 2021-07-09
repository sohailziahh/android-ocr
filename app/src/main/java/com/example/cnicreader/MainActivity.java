package com.example.cnicreader;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cnicreader.Base.BaseDocumentExtractor;
import com.example.cnicreader.Extraction.DocumentExtractor;
import com.example.cnicreader.Instances.CnicExtractor;
import com.example.cnicreader.databinding.BasicExerciseViewBinding;

import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import java.util.List;

public class MainActivity extends CameraActivity {


    protected RelativeLayout parentContainer;

    protected BasicExerciseViewBinding viewBinding;

    static Activity mainActivity;

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
            List<TextBlock> textBlocks = extraction.process(bitmap,textRecognizer);
            BaseDocumentExtractor cnic = new CnicExtractor(this);
            extraction.extract(cnic,textBlocks);
            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue());
                    detectedText.append("\n");
                }
            }
            // todo same thing as above.
            cnic.setText(detectedText);
            cnic.saveData();


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