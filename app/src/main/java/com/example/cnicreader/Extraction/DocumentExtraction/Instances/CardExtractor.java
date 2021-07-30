package com.example.cnicreader.Extraction.DocumentExtraction.Instances;

import android.app.Activity;

import com.example.cnicreader.Extraction.DocumentExtraction.Base.BaseDocumentExtractor;
import com.google.android.gms.vision.text.TextBlock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CardExtractor extends BaseDocumentExtractor {

    static public String accountNumber = "Deciding...";
    static public String validDate = "Deciding...";    //optional
    static public String expiryDate = "Deciding...";
    static public String fullName = "Deciding...";

    Activity mainActivity;
    HashMap<String, String> patterns = new HashMap<>();
    HashMap<String, String> values = new HashMap<>();

    public CardExtractor(Activity mainActivity) {
        this.mainActivity = mainActivity;

        patterns.put("Account Number","\\b\\d{4}[ ]?\\d{4}[ ]?\\d{4}[ ]?\\d{4}\\b");
        patterns.put("Valid From","^(0[1-9]|1[0-2])\\/?([0-9]{2})$");
        patterns.put("Valid Through","^(0[1-9]|1[0-2])\\/?([0-9]{2})$");
        patterns.put("Name","^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$");

        values.put("Account Number","Deciding...");
        values.put("Valid From","Deciding...");
        values.put("Valid Through","Deciding...");
        values.put("Name","Deciding...");
    }

    @Override
    public void imageToText(List<TextBlock> textBlocks){
        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.get(i);
            String s = textBlock.getValue();
            s = s.trim();

            Iterator pIter = patterns.entrySet().iterator();
            String string = textBlock.getValue();
            while (pIter.hasNext()){

                Map.Entry element = (Map.Entry) pIter.next();

                if (Pattern.matches(element.getValue().toString(), s)) {
                    if (values.get(element.getKey().toString()) == "Deciding...") {
                        values.replace(element.getKey().toString(), s);

                    }

                    }


            }


        }
        accountNumber = values.get("Account Number");
        validDate = values.get("Valid From");
        expiryDate = values.get("Valid Through");
        fullName = values.get("Name");




    }
}
