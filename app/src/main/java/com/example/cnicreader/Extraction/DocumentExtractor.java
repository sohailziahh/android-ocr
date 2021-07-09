package com.example.cnicreader.Extraction;

import android.graphics.Bitmap;

import com.example.cnicreader.Base.BaseDocumentExtractor;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.util.List;


abstract public class DocumentExtractor {

    abstract public List<TextBlock> process(Bitmap bitmap, TextRecognizer textRecognizer);

    abstract public void extract(BaseDocumentExtractor docType, List<TextBlock> textBlocks);

    abstract public void imageToText(List<TextBlock> textBlocks);

    abstract public void setText(StringBuilder detectedText);

    abstract public void saveData();


}
