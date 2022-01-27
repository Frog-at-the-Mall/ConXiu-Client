package com.example.nfcproject;


//exposes the str method, implemented in the classes URIRecord,
//TextRecord and a combination of the two: SmartPoster
public interface ParsedNdefRecord {

    String str();

}
