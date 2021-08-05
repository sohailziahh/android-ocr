package com.example.cnicreader.MLModel.Instances;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.cnicreader.Activities.MainActivity;
import com.example.cnicreader.MLModel.Base.BaseTextRecognizer;
import com.google.android.gms.vision.text.TextRecognizer;

public class MLKitTextRecognizer extends BaseTextRecognizer {



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


            MainActivity.mainActivity.document(bitmap, textRecognizer);
        }finally
        {
            textRecognizer.release();
        }
    }


}
