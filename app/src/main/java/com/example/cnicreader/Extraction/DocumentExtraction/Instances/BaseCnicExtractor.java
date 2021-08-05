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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseCnicExtractor extends BaseDocumentExtractor {



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
    boolean containsDigit;


    Activity mainActivity;
    HashMap<String, String> patterns = new HashMap<>();
    HashMap<String, String> values = new HashMap<>();

    public BaseCnicExtractor(Activity activity) {
        this.mainActivity = activity;

        patterns.put("Gender","^M?$|^F?$");
        patterns.put("Identity Number","^[0-9]{5}-[0-9]{7}-[0-9]$");
        patterns.put("Date of Birth","^\\s*(3[01]|[12][0-9]|0?[1-9])\\.(1[012]|0?[1-9])\\.((?:19|20)\\d{2})\\s*$");
        patterns.put("Date of Issue","^\\s*(3[01]|[12][0-9]|0?[1-9])\\.(1[012]|0?[1-9])\\.((?:19|20)\\d{2})\\s*$");
        patterns.put("Date of Expiry","^\\s*(3[01]|[12][0-9]|0?[1-9])\\.(1[012]|0?[1-9])\\.((?:19|20)\\d{2})\\s*$");

        values.put("Name","Deciding...");
        values.put("Father Name","Deciding...");
        values.put("Gender","Deciding...");
        values.put("Country of Stay","Deciding...");
        values.put("Identity Number","Deciding...");
        values.put("Date of Birth","Deciding...");
        values.put("Date of Issue","Deciding...");
        values.put("Date of Expiry","Deciding...");


//
//
    }


    @Override
    public void imageToText(List<TextBlock> textBlocks) {
        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.get(i);
            String s = textBlock.getValue();
            s = s.trim();
            Pattern namePattern = Pattern.compile("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$");
            if (name.equals("Deciding...") && (s.equals("Name"))) {
                String tempTextBlockValue = textBlocks.get(i + 1).getValue();
                Matcher m = namePattern.matcher(tempTextBlockValue);
                if (m.matches()){
                    name = tempTextBlockValue;
                }

            } else if (fatherName.equals("Deciding...") && ((s.equals("Father Name")))) {
                String tempTextBlockValue = textBlocks.get(i + 1).getValue();
                Matcher m = namePattern.matcher(tempTextBlockValue);
                if (m.matches()) {
                    fatherName = tempTextBlockValue;
                }

            } else if (gender.equals("Deciding...") && (Pattern.matches("M", s))) {
                gender = "Male";
            } else if (gender.equals("Deciding...") && (Pattern.matches("F", s))) {
                gender = "Female";
            } else if (((identityNumber.equals("Deciding..."))
                    || dateOfBirth.equals("Deciding..."))
                    && (s.contains("-"))
                    && (s.contains("."))) {

                s = textBlock.getValue();
                //identityNumberCheck
                if ((s.split(" ")[0].length() == 15) && (s.split(" ")[0].contains("-")))
                    identityNumber = s.split(" ")[0];
                //dateOfBirthCheck
                if ((s.split(" ")[1].length() == 10) && (s.split(" ")[1].contains("."))) {
                    if (s.split("\\.").length == 3) {
                        // age cannot be less than 18
                        if ((Integer.parseInt(s.split(" ")[1].split("\\.")[2])) < 2004)
                            dateOfBirth = s.split(" ")[1];
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




    public void alternateToText(List<TextBlock> textBlocks) {

            for (int i = 0; i < textBlocks.size(); i++) {
                TextBlock textBlock = textBlocks.get(i);
                String s = textBlock.getValue();
                s = s.trim();


                //name and father name
                if (s.equals("Name") || s.equals("Father Name") ) {
                    if ((name.equals("Deciding...") || fatherName.equals("Deciding..."))){
                    String tempString = textBlocks.get(i + 1).getValue();
                    if (Pattern.matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", tempString)) {
                        values.replace(s, tempString);
                    }
                    }
                } else {
                    Iterator pIter = patterns.entrySet().iterator();
                    String string = textBlock.getValue();

                    while (pIter.hasNext()) {
                        Map.Entry element = (Map.Entry) pIter.next();

                        //date of birth
                        if ((string.split(" ").length == 2) && (Pattern.matches(element.getValue().toString(), string.split(" ")[1]))) {
                            if (Integer.parseInt(s.split("\\.")[2]) < 2004 ) {
                                if (values.get("Date of Birth") == "Deciding..."){
                                    values.replace("Date of Birth", string.split(" ")[1]);
                                    pIter.remove();
                            }}
                        }
                        // gender, date of issue, date of expiry
                        else if (Pattern.matches(element.getValue().toString(), s)) {
                            if (values.get(element.getKey().toString()) == "Deciding...") {
                                if (s.split("\\.").length == 3) {
                                    if (Integer.parseInt(s.split("\\.")[2]) <= 2021 && Integer.parseInt(s.split("\\.")[2]) > 2004) {
                                        values.replace("Date of Issue", s);
                                        pIter.remove();
                                    } else if ((Integer.parseInt(s.split("\\.")[2])) > 2022) {
                                        values.replace("Date of Expiry", s);
                                        pIter.remove();
                                    }
                                } else {
                                    values.replace(element.getKey().toString(), s);
                                    pIter.remove();
                                }
                            }

                        }
                        //identity number
                        else if (((string.split(" ").length == 2) ) && Pattern.matches(element.getValue().toString(), string.split(" ")[0])) {
                            if (values.get(element.getKey().toString()) == "Deciding..."){
                                values.replace(element.getKey().toString(), string.split(" ")[0]);
                                pIter.remove();

                            }
                        }




                    }

                }

            }
            //country of stay
            if (values.get("Name") != "Deciding...")
                values.replace("Country of Stay","Pakistan");

            name = values.get("Name");
            fatherName = values.get("Father Name");
            gender = values.get("Gender");
            countryOfStay = values.get("Country of Stay");
            identityNumber = values.get("Identity Number");
            dateOfBirth = values.get("Date of Birth");
            dateOfIssue = values.get("Date of Issue");
            dateOfExpiry = values.get("Date of Expiry");



    }
}
