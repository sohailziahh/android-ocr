package com.example.cnicreader.Activities;

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

import com.example.cnicreader.Activities.CameraActivity;
import com.example.cnicreader.Extraction.DocumentExtraction.Base.BaseDocumentExtractor;
import com.example.cnicreader.Extraction.DocumentExtraction.DocumentExtractor;
import com.example.cnicreader.Extraction.DocumentExtraction.Instances.CnicExtractor;
import com.example.cnicreader.MLModel.Instances.BaseTextRecognizer;
import com.example.cnicreader.MLModel.Instances.MLKitTextRecognizer;
import com.example.cnicreader.R;
import com.example.cnicreader.Representation.DocumentRepresentation.Base.BaseDocumentRepresentator;
import com.example.cnicreader.Representation.DocumentRepresentation.Instances.CnicRepresentator;
import com.example.cnicreader.databinding.BasicExerciseViewBinding;

import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import java.util.List;

public class MainActivity extends CameraActivity {


    protected RelativeLayout parentContainer;

    protected BasicExerciseViewBinding viewBinding;

    public static MainActivity mainActivity;


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
    BaseTextRecognizer textRecognizer;
    DocumentExtractor extraction;
    BaseDocumentExtractor cnic;
    BaseDocumentRepresentator setCnicText;




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        extraction = new BaseDocumentExtractor();
        cnic = new CnicExtractor(this);
        setCnicText = new CnicRepresentator(this);
        setCnicText.initializeViews();
        mainActivity = this;


    }



    public void document(List<TextBlock> textBlocks)
    {

            extraction.extract(cnic,textBlocks);
            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue());
                    detectedText.append("\n");
                }
            }
            setCnicText.setText(detectedText);
            setCnicText.saveData();

    }

    Bitmap currentBitmap;

    @Override
    public void processImage(Bitmap bitmap) {
        currentBitmap = bitmap;
        textRecognizer = new MLKitTextRecognizer(this);
        textRecognizer.textRecognition(bitmap);
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