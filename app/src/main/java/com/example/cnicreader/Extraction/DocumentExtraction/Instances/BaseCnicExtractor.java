package com.example.cnicreader.Extraction.DocumentExtraction.Instances;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.text.method.ScrollingMovementMethod;


import com.example.cnicreader.Extraction.DocumentExtraction.Base.BaseDocumentExtractor;
import com.google.android.gms.vision.text.TextBlock;
import com.example.cnicreader.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class BaseCnicExtractor extends BaseDocumentExtractor {


    public String csvName = "CNIC-DATA-" + new Timestamp(System.currentTimeMillis()) + ".csv";
    private String csvDir = "CNIC_DATA/";
    Calendar year = Calendar.getInstance();
    int curYear = year.get(Calendar.YEAR);

    static public String gender = "Deciding...";
    static public String name = "Deciding...";
    static public String fatherName = "Deciding...";
    static public String dateOfBirth = "Deciding...";
    static public String dateOfIssue = "Deciding...";
    static public String dateOfExpiry = "Deciding...";
    static public String identityNumber = "Deciding...";
    static public String countryOfStay = "Deciding...";
    boolean containsDigit = true;

    Activity mainActivity;

        public BaseCnicExtractor(Activity activity){
            this.mainActivity = activity;


//
//
    }



    @Override
    public void imageToText (List<TextBlock> textBlocks) {

        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.get(i);
            String s = textBlock.getValue();
            s = s.trim();
            if (name.equals("Deciding...") && (s.equals("Name"))) {
                String tempTextBlockValue = textBlocks.get(i + 1).getValue();
                char[] characters = tempTextBlockValue.toCharArray();
                containsDigit = false;
                for (char c : characters) {
                    if (Character.isDigit(c))
                        containsDigit = true;
                }
                if (!containsDigit)
                    name = tempTextBlockValue;
            } else if (fatherName.equals("Deciding...") && (s.equals("Father Name"))) {
                String tempTextBlockValue = textBlocks.get(i + 1).getValue();
                char[] characters = tempTextBlockValue.toCharArray();
                containsDigit = false;
                for (char c : characters) {
                    if (Character.isDigit(c))
                        containsDigit = true;
                }
                if (!containsDigit)
                    fatherName = tempTextBlockValue;
            } else if (gender.equals("Deciding...")
                    && (textBlock.getValue().contains("M"))
                    && (textBlock.getValue().indexOf("M") == 0)
                    && (textBlock.getValue().length() == 1)) {
                gender = "Male";
            } else if (gender.equals("Deciding...")
                    && (textBlock.getValue().contains("F"))
                    && (textBlock.getValue().indexOf("F") == 0)
                    && textBlock.getValue().length() == 1) {
                gender = "Female";
            } else if (((identityNumber.equals("Deciding..."))
                    || dateOfBirth.equals("Deciding..."))
                    && (textBlock.getValue().contains("-"))
                    && (textBlock.getValue().contains("."))) {

                String string = textBlock.getValue();
                //identityNumberCheck
                if ((string.split(" ")[0].length() == 15) && (string.split(" ")[0].contains("-")))
                    identityNumber = string.split(" ")[0];
                //dateOfBirthCheck
                if ((string.split(" ")[1].length() == 10) && (string.split(" ")[1].contains("."))) {
                    if (textBlock.getValue().split("\\.").length == 3) {
                        // age cannot be less than 18
                        if ((Integer.parseInt(string.split(" ")[1].split("\\.")[2])) < 2004)
                            dateOfBirth = string.split(" ")[1];
                    }
                }
            } else if (dateOfBirth.equals("Deciding...")
                    && (textBlock.getValue().length() == 10)
                    && (textBlock.getValue().contains("."))) {
                if (textBlock.getValue().split("\\.").length == 3) {
                    // age cannot be less than 18
                    if ((Integer.parseInt(textBlock.getValue().split("\\.")[2])) < 2004)
                        dateOfBirth = textBlock.getValue();
                }
            } else if ((identityNumber.equals("Deciding..."))
                    && (textBlock.getValue().contains("-"))
                    && (textBlock.getValue().length() == 15)) {
                String string = textBlock.getValue();
                if (string.substring(5).equals("-") && string.substring(13).equals("-"))
                    identityNumber = string;
            } else if ((dateOfExpiry.equals("Deciding...")) && !(dateOfIssue.equals("Deciding..."))) {
                if (textBlock.getValue().contains(".")
                        && (textBlock.getValue().length() == 10)
                        && (!textBlock.getValue().equals(dateOfIssue)))
                    try {
                        if (textBlock.getValue().split("\\.").length == 3) {
                            //because NADRA started issuing smartcards from 2012
                            if ((Integer.parseInt(textBlock.getValue().split("\\.")[2])) > 2022)
                                dateOfExpiry = textBlock.getValue();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
            } else if ((dateOfIssue.equals("Deciding..."))) {
                if (textBlock.getValue().contains(".")
                        && (textBlock.getValue().length() == 10)
                        && (!textBlock.getValue().equals(dateOfExpiry)))
                    try {
                        if (textBlock.getValue().split("\\.").length == 3) {
                            if ((Integer.parseInt(textBlock.getValue().split("\\.")[2])) < 2021
                                    && (Integer.parseInt(textBlock.getValue().split("\\.")[2])) > 2004) {
                                dateOfIssue = textBlock.getValue();

                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
            }
        }
            if (!name.equals("Deciding..."))
                countryOfStay = "Pakistan";



    }












}