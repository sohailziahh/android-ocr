package com.example.cnicreader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class BaseDocumentExtractor extends DocumentExtractor{

    Activity mainActivity;
    BaseDocumentExtractor cnic;

    public BaseDocumentExtractor(Activity activity){
        this.mainActivity = activity;
        cnic = new CnicExtractor(mainActivity);
    }



    public void process(Bitmap bitmap, TextRecognizer textRecognizer) {
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

        // todo this shouldnt be called here, as each function should do one thing only.
        //thisCnic.imageCNIC(textBlocks);
         //   imageCNIC(textBlocks);

        extract(cnic,textBlocks);
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
    }


    public void setText(StringBuilder detectedText){

    }

    public void saveData(){

    }

    @Override
    public void imageToText(List<TextBlock> textBlocks){

    }

    @Override
    public void extract(BaseDocumentExtractor docType, List<TextBlock> textBlocks){
        docType.imageToText(textBlocks);



}



}
