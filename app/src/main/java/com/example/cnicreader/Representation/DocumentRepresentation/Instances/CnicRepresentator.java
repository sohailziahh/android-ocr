package com.example.cnicreader.Representation.DocumentRepresentation.Instances;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cnicreader.Extraction.DocumentExtraction.Instances.CnicExtractor;
import com.example.cnicreader.R;
import com.example.cnicreader.Representation.DocumentRepresentation.Base.BaseDocumentRepresentator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class CnicRepresentator extends BaseDocumentRepresentator {

    Activity mainActivity;
    TextView detectedTextView;

    String name;
    String fatherName;
    String gender;
    String identityNumber;
    String dateOfBirth;
    String dateOfIssue;
    String dateOfExpiry;
    String countryOfStay;

    public void checking() {
        name = CnicExtractor.name;
        fatherName = CnicExtractor.fatherName;
        gender = CnicExtractor.gender;
        identityNumber = CnicExtractor.identityNumber;
        dateOfBirth = CnicExtractor.dateOfBirth;
        dateOfIssue = CnicExtractor.dateOfIssue;
        dateOfExpiry = CnicExtractor.dateOfExpiry;
        countryOfStay = CnicExtractor.countryOfStay;
    }




    public CnicRepresentator(Activity activity){
        super(activity);
        this.mainActivity = activity;

    }

    public void initializeViews(){
        detectedTextView = mainActivity.findViewById(R.id.detected_text);
        detectedTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void setText(StringBuilder message)
    {
        detectedTextView.post(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run()
            {
                checking();
                detectedTextView.setText("Name: " + name + "\nFather Name: " + fatherName + "\nGender: " + gender +
                        "\nIdentity Number: " + identityNumber + "\nDate Of Birth: " + dateOfBirth +
                        "\nDate Of Issue: " + dateOfIssue + "\nDate Of Expiry: " + dateOfExpiry + "\nCountry Of Stay: " + countryOfStay);

                Log.d("check",dateOfIssue);
            }
        });
    }

    @Override
    public void saveData() {
        checking();
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

}
