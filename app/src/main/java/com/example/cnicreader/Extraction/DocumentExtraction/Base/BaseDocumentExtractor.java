package com.example.cnicreader.Extraction.DocumentExtraction.Base;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.example.cnicreader.Extraction.DocumentExtraction.DocumentExtractor;
import com.example.cnicreader.views.ImageCanvas;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.List;


public class BaseDocumentExtractor extends DocumentExtractor  {




    @Override
    public void setText(StringBuilder detectedText){

    }

    @Override
    public void saveData(){

    }

    @Override
    public void imageToText(List<TextBlock> textBlocks){

    }

    @Override
    public void alternateToText(List<TextBlock> textBlocks) {

    }

    @Override
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

        List<TextBlock> lineFrames = intersectionOverUnion(textBlocks);


        return lineFrames;
    }

    public List<TextBlock> intersectionOverUnion(List<TextBlock> textBlocks){
        List<TextBlock> lineFrames = new ArrayList<>();
        double x_overlap,y_overlap,overlapArea,unionArea;
        double score;
        
        
        for (int i = 1; i < textBlocks.size(); i++) {
            //intersection area
            x_overlap = Math.max(0, Math.min(textBlocks.get(i).getBoundingBox().right, textBlocks.get(i-1).getBoundingBox().right) - Math.max(textBlocks.get(i).getBoundingBox().left, textBlocks.get(i-1).getBoundingBox().left));
            y_overlap = Math.max(0, Math.min(textBlocks.get(i).getBoundingBox().bottom, textBlocks.get(i-1).getBoundingBox().bottom) - Math.max(textBlocks.get(i).getBoundingBox().top, textBlocks.get(i-1).getBoundingBox().top));
            overlapArea = x_overlap * y_overlap;

            Log.d("check", "method working");

            //union area
            unionArea = (textBlocks.get(i).getBoundingBox().height() * textBlocks.get(i).getBoundingBox().width()) +
                    (textBlocks.get(i-1).getBoundingBox().height() * textBlocks.get(i-1).getBoundingBox().width()) -
                    overlapArea;
            
            //intersection over union
            score = overlapArea/unionArea;
            
            if (score < 0.5)
                lineFrames.add(textBlocks.get(i));
                

            
            
        }
        return lineFrames;



    }










}
