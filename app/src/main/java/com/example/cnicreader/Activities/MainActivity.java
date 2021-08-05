package com.example.cnicreader.Activities;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cnicreader.Extraction.DocumentExtraction.Base.BaseDocumentExtractor;
import com.example.cnicreader.Extraction.DocumentExtraction.DocumentExtractor;
import com.example.cnicreader.Extraction.DocumentExtraction.Instances.BaseCnicExtractor;
import com.example.cnicreader.MLModel.Base.BaseTextRecognizer;
import com.example.cnicreader.MLModel.Instances.MLKitTextRecognizer;
import com.example.cnicreader.R;
import com.example.cnicreader.Representation.DocumentRepresentation.Base.BaseDocumentRepresentator;
import com.example.cnicreader.Representation.DocumentRepresentation.Instances.BaseCnicRepresentator;
import com.example.cnicreader.databinding.BasicExerciseViewBinding;

import com.example.cnicreader.views.OverlayView;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;


import java.util.List;

public class MainActivity extends CameraActivity {


    protected RelativeLayout parentContainer;

    protected BasicExerciseViewBinding viewBinding;

    public static MainActivity mainActivity;

    ImageView drawingImageView;

    public OverlayView ov;

    Paint paint;

    Rect lineFrame;

    //private TextView detectedTextView;

    public void initViews()
    {
        viewBinding = getViewBinding();
        parentContainer = findViewById(R.id.containerParent);
        ov = findViewById(R.id.tracking_overlay);
        ov.addCallback(this::drawImage);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        parentContainer.addView(viewBinding.getRoot(), layoutParams);

    }

    public void drawImage(Canvas canvas){
        ov.postInvalidate();
        Log.d("check2","i am here");
    }


    public BasicExerciseViewBinding getViewBinding()
    {
        if (viewBinding == null)
            viewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.basic_exercise_view, null, false);
        return viewBinding;
    }

    TextView textView;

    BaseTextRecognizer textRecognizer;
    BaseDocumentExtractor cnic;
    BaseDocumentRepresentator setCnicText;
    DocumentExtractor extraction;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paint = new Paint();
        cnic = new BaseCnicExtractor(this);
        //dCard = new CardExtractor(this);
        setCnicText = new BaseCnicRepresentator(this);
        //setCardText = new CardRepresentator(this);
        extraction = new BaseDocumentExtractor();
        setCnicText.initializeViews();



        mainActivity = this;

    }


long start;
    public void document(Bitmap bitmap, TextRecognizer textRecognizer)
    {
        Canvas canvas = new Canvas(bitmap);


        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        List<TextBlock> textBlocks = extraction.process(bitmap, textRecognizer);
            extract(cnic,textBlocks);
            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {

                if (textBlock != null && textBlock.getValue() != null) {

                    lineFrame = textBlock.getBoundingBox();
                    canvas.drawRect(lineFrame,paint);


                    detectedText.append(textBlock.getValue());
                    detectedText.append("\n");
                }

            }
            setText(setCnicText,detectedText);
            saveData(setCnicText);


    }

    Bitmap currentBitmap;

    @Override
    public void processImage(Bitmap bitmap) {
        currentBitmap = bitmap;
        textRecognizer = new MLKitTextRecognizer(this);
        start = System.nanoTime();
        textRecognizer.textRecognition(bitmap);
        long end = System.nanoTime();
        long elapsed = end - start;
        Log.d("exec", Long.toString(elapsed));

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

    public void extract(BaseDocumentExtractor docType, List<TextBlock> textBlocks){

        docType.alternateToText(textBlocks);



    }
    public void setText(BaseDocumentRepresentator set,StringBuilder detectedText){
        set.setText(detectedText);
    }

    public void saveData(BaseDocumentRepresentator set){
        set.saveData();
    }
}