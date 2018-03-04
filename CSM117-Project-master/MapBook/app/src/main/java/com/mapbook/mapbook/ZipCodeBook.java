package com.mapbook.mapbook;

import java.util.HashMap;

/**
 * Created by g090617 on 11/13/17.
 */

public class ZipCodeBook {
    public HashMap<String, String> zipBookMap;

    public void addBookToZipCode(String zipCode, String bookID, String bookStatus){
        this.zipBookMap = new HashMap<>();

    }

    public void addBookToZip(String zipCode, String bookID){
        zipBookMap.put(zipCode, bookID);
    }
}
