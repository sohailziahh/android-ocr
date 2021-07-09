package com.example.cnicreader.Extraction.DocumentExtraction;

import android.graphics.Bitmap;

import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.List;

public interface DocumentExtractorInterface {

     List<TextBlock> process(Bitmap bitmap, TextRecognizer textRecognizer);
}