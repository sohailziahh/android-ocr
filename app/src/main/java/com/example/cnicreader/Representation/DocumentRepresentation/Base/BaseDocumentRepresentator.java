package com.example.cnicreader.Representation.DocumentRepresentation.Base;

import android.app.Activity;

import com.example.cnicreader.Representation.DocumentRepresentation.DocumentRepresentator;

import java.sql.Timestamp;

public class BaseDocumentRepresentator extends DocumentRepresentator {


    public boolean dataSaved;
    public String csvName = "CNIC-DATA-" + new Timestamp(System.currentTimeMillis()) + ".csv";
    public String csvDir = "CNIC_DATA/";
    public String csvPath;

    public BaseDocumentRepresentator(Activity activity){
        csvPath = activity.getExternalFilesDir(csvDir).getAbsolutePath() + "/" + csvName;
    }

    @Override
    public void setText(StringBuilder message){

    }

    public void saveData(){

    }

    public void initializeViews(){

    }
}
