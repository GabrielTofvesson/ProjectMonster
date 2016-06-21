package com.tofvesson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Cacher {

    private PrintStream cache;
    Map<String, String> cachedValues = new HashMap<>();

    public Cacher(File file, String s) throws FileNotFoundException, UnsupportedEncodingException {
        cache = new PrintStream(file, s);
    }

    public Cacher(File file) throws FileNotFoundException {
        cache = new PrintStream(file);
    }

    public Cacher(String s, String s1) throws FileNotFoundException, UnsupportedEncodingException {
        cache = new PrintStream(s, s1);
    }

    public Cacher(String s) throws FileNotFoundException {
        cache = new PrintStream(s);
    }

    public void print(String k, String v){

    }

    public void println(String k, String v){

    }
}
