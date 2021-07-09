package com.example.cnicreader.Extraction;

import android.graphics.Bitmap;

import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.List;

public interface DocumentExtractorInterface {
    abstract public List<TextBlock> process(Bitmap bitmap, TextRecognizer textRecognizer);
}
