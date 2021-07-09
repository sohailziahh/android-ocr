package com.example.cnicreader.Extraction.DocumentExtraction.Base;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.example.cnicreader.Extraction.DocumentExtraction.DocumentExtractor;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.List;


public class BaseDocumentExtractor extends DocumentExtractor {

    public List<TextBlock> process(Bitmap bitmap, TextRecognizer textRecognizer) {
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

        return textBlocks;
    }


    @Override
    public void setText(StringBuilder detectedText){

    }

    @Override
    public void saveData(){

    }

    @Override
    public void imageToText(List<TextBlock> textBlocks){

    }

    public void initializeViews(){

    }

    @Override
    public void extract(BaseDocumentExtractor docType, List<TextBlock> textBlocks){
        docType.imageToText(textBlocks);



}



}
