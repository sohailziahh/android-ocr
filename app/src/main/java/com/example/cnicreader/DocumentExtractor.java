package com.example.cnicreader;

import android.graphics.Bitmap;

import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.util.List;


abstract public class DocumentExtractor {

    abstract void process(Bitmap bitmap, TextRecognizer textRecognizer);

    abstract void extract(BaseDocumentExtractor docType,List<TextBlock> textBlocks);

    abstract void imageToText(List<TextBlock> textBlocks);


}
