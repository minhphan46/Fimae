package com.example.fimae.utils;

public class StringUtils {
    //Remove Vietnamese accent
    public static String removeAccent(String s) {
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        return temp.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
