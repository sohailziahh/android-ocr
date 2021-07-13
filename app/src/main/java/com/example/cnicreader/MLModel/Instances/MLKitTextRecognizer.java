package com.example.cnicreader.MLModel.Instances;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.example.cnicreader.Activities.MainActivity;
import com.example.cnicreader.Extraction.DocumentExtraction.Base.BaseDocumentExtractor;
import com.example.cnicreader.Extraction.DocumentExtraction.DocumentExtractor;
import com.example.cnicreader.Extraction.DocumentExtraction.Instances.CnicExtractor;
import com.example.cnicreader.MLModel.ModelInterface;
import com.example.cnicreader.Representation.DocumentRepresentation.Base.BaseDocumentRepresentator;
import com.example.cnicreader.Representation.DocumentRepresentation.Instances.CnicRepresentator;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.List;

public class MLKitTextRecognizer extends BaseTextRecognizer  {



    Activity mainActivity;
    MainActivity docProcessing;

    public MLKitTextRecognizer(Activity activity){
        this.mainActivity = activity;


    }

    public void textRecognition(Bitmap bitmap)
    {

        TextRecognizer textRecognizer = new TextRecognizer.Builder(mainActivity).build();
        try {
            if (!textRecognizer.isOperational()) {
                new AlertDialog.
                        Builder(mainActivity).
                        setMessage("Text recognizer could not be set up on your device").show();
                return;
            }

            Log.d("ocr-sohail", "-------");

            List<TextBlock> textBlocks = process(bitmap, textRecognizer);
            MainActivity.mainActivity.document(textBlocks);
        }finally
        {
            textRecognizer.release();
        }
    }

    @Override
    public List<TextBlock> process(Bitmap bitmap, TextRecognizer textRecognizer) {

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
        List<TextBlock> textBlocks = new ArrayList<>();

        for (int i = 0; i < origTextBlocks.size(); i++) {
            Log.d("check","hello");
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

        return textBlocks;
    }
}
