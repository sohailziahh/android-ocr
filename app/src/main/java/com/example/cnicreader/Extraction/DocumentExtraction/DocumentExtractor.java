package com.example.cnicreader.Extraction.DocumentExtraction;

import com.example.cnicreader.Extraction.DocumentExtraction.Base.BaseDocumentExtractor;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;


abstract public class DocumentExtractor implements DocumentExtractorInterface {



    abstract public void extract(BaseDocumentExtractor docType, List<TextBlock> textBlocks);

    abstract public void imageToText(List<TextBlock> textBlocks);

    abstract public void setText(StringBuilder detectedText);

    abstract public void saveData();


}
