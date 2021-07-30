package com.example.cnicreader.Representation.DocumentRepresentation.Instances;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cnicreader.Extraction.DocumentExtraction.Instances.BaseCnicExtractor;
import com.example.cnicreader.Extraction.DocumentExtraction.Instances.CardExtractor;
import com.example.cnicreader.R;
import com.example.cnicreader.Representation.DocumentRepresentation.Base.BaseDocumentRepresentator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class CardRepresentator extends BaseDocumentRepresentator {

    Activity mainActivity;
    TextView detectedTextView;

    String accountNumber;
    String validDate;
    String expiryDate;
    String fullName;


    public CardRepresentator(Activity activity) {
        super(activity);
        this.mainActivity = activity;
    }

    public void initializeViews(){
        detectedTextView = mainActivity.findViewById(R.id.detected_text);
        detectedTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void checking() {
        accountNumber = CardExtractor.accountNumber;
        validDate = CardExtractor.validDate;
        expiryDate = CardExtractor.expiryDate;
        fullName = CardExtractor.fullName;

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
                detectedTextView.setText("Account Number: " + accountNumber + "\nValid From: " + validDate + "\nValid Through: " + validDate +
                        "\nName: " + fullName );

            }
        });
    }

    @Override
    public void saveData() {
        checking();
        if (!accountNumber.equals("Deciding...")
                && (!validDate.equals("Deciding..."))
                && (!expiryDate.equals("Deciding..."))
                && (!fullName.equals("Deciding..."))) {

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
                    Files.write(Paths.get(csvPath), Collections.singleton("Account Number: " + accountNumber + "\nValid From: " + validDate + "\nValid Through: " + validDate +
                            "\nName: " + fullName));
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
