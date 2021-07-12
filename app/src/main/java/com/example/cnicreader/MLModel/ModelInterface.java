package com.example.cnicreader.MLModel;

import android.graphics.Bitmap;

import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.List;

public interface ModelInterface {

    List<TextBlock> process(Bitmap bitmap, TextRecognizer textRecognizer);
}
