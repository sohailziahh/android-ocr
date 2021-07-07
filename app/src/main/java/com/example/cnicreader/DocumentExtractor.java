package com.example.cnicreader;

import android.graphics.Bitmap;

import com.google.android.gms.vision.text.TextRecognizer;


abstract public class DocumentExtractor {

    abstract void convertToTextBlocks(Bitmap bitmap, TextRecognizer textRecognizer);

    abstract void extract(BaseDocumentExtractor docType);


}
