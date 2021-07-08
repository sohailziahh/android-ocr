package com.example.cnicreader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.TextBlock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class CnicExtractor extends BaseDocumentExtractor {

    public String csvName = "CNIC-DATA-" + new Timestamp(System.currentTimeMillis()) + ".csv";
    private String csvDir = "CNIC_DATA/";
    private String csvPath;
    private TextView detectedTextView;
    Calendar year = Calendar.getInstance();
    int curYear = year.get(Calendar.YEAR);

    String gender = "Deciding...";
    String name = "Deciding...";
    String fatherName = "Deciding...";
    String dateOfBirth = "Deciding...";
    String dateOfIssue = "Deciding...";
    String dateOfExpiry = "Deciding...";
    String identityNumber = "Deciding..";
    String countryOfStay = "Deciding...";
    boolean containsDigit = true;

    public CnicExtractor(Activity activity) {
        super(activity);
    }



    //    public Cnic(Activity activity){
//        this.mainActivity = activity;
//        csvPath = activity.getExternalFilesDir(csvDir).getAbsolutePath() + "/" + csvName;
//        detectedTextView = activity.findViewById(R.id.detected_text);
//        detectedTextView.setMovementMethod(new ScrollingMovementMethod());
//
//
//    }
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
                        if (textBlock.getValue().split("\\.").length == 3)
                        {
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
                            if ((Integer.parseInt(textBlock.getValue().split("\\.")[2])) < curYear
                                    && (Integer.parseInt(textBlock.getValue().split("\\.")[2])) > 2004)
                                dateOfIssue = textBlock.getValue();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
            }
        }
        if (!name.equals("Deciding..."))
            countryOfStay = "Pakistan";

    }
  boolean dataSaved;



    @Override
    public void saveData() {

        if (!identityNumber.equals("Deciding...")
                && (!dateOfBirth.equals("Deciding..."))
                && (!dateOfIssue.equals("Deciding..."))
                && (!dateOfExpiry.equals("Deciding..."))
                && (!gender.equals("Deciding..."))
                && (!fatherName.equals("Deciding..."))
                && (!name.equals("Deciding..."))
                && (!countryOfStay.equals("Deciding..."))) {

            try {
                File directory = mainActivity.getExternalFilesDir(csvDir);

                if (!directory.exists()) {
                    if (!directory.mkdirs()) {
                        return;
                    }
                }
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.

                try {
                    Files.write(Paths.get(csvPath), Collections.singleton("Name: " + name + "\nFather Name: " + fatherName + "\nGender: " + gender +
                            "\nIdentity Number: " + identityNumber + "\nDate Of Birth: " + dateOfBirth +
                            "\nDate Of Issue: " + dateOfIssue + "\nDate Of Expiry: " + dateOfExpiry + "\nCountry Of Stay: " + countryOfStay));
                    dataSaved = true;
                    Log.d("file-sohail", csvPath);
                    mainActivity.runOnUiThread(() -> Toast.makeText(mainActivity.getBaseContext(), "Date stored in a CSV.", Toast.LENGTH_LONG).show());

                } catch (IOException e) {
                    Log.e("error", e.getMessage());
                }
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }

        }
    }

    @Override
    public void setText(StringBuilder message) {

        detectedTextView.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run()
            {

                detectedTextView.setText("Name: " + name + "\nFather Name: " + fatherName + "\nGender: " + gender +
                        "\nIdentity Number: " + identityNumber + "\nDate Of Birth: " + dateOfBirth +
                        "\nDate Of Issue: " + dateOfIssue + "\nDate Of Expiry: " + dateOfExpiry + "\nCountry Of Stay: " + countryOfStay);
            }
        });
    }






}
