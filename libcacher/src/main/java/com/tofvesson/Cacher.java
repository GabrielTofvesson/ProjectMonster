package com.tofvesson;

import com.sun.istack.internal.Nullable;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class Cacher implements Closeable{

    private class CacheDataHolder{ int unixTimeCached = 0; String data = ""; public CacheDataHolder(String s, int i){ unixTimeCached=i; data=s; } }

    private PrintStream cache;
    private BufferedReader cacheIn;
    private FileReader cacheFileReader;
    private File cacheFile;
    private Map<String, CacheDataHolder> cachedValues = new HashMap<>();

    public Cacher(File file, String s) throws FileNotFoundException, UnsupportedEncodingException {
        if(!(cacheFile = file).isFile()) try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        cache = new PrintStream(file, s);
        cacheIn = new BufferedReader(cacheFileReader = new FileReader(file));
        if(!verifyCacheIntegrity(false, System.out)){
            cache.close();
            try { cacheIn.close(); } catch (IOException e) { e.printStackTrace(); }
            file.delete();
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
            cache = new PrintStream(file, s);
            cacheIn = new BufferedReader(cacheFileReader = new FileReader(file));
        }
    }

    public Cacher(File file) throws FileNotFoundException {
        if(!(cacheFile = file).isFile()) try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        cache = new PrintStream(file);
        cacheIn = new BufferedReader(cacheFileReader = new FileReader(file));
        if(!verifyCacheIntegrity(false, System.out)){
            cache.close();
            try { cacheIn.close(); } catch (IOException e) { e.printStackTrace(); }
            file.delete();
            try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
            cache = new PrintStream(file);
            cacheIn = new BufferedReader(cacheFileReader = new FileReader(file));
        }
    }

    public Cacher(String s, String s1) throws FileNotFoundException, UnsupportedEncodingException {
        if(!(cacheFile = new File(s)).isFile()) try { new File(s).createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        cache = new PrintStream(s, s1);
        cacheIn = new BufferedReader(cacheFileReader = new FileReader(s));
        if(!verifyCacheIntegrity(false, System.out)){
            cache.close();
            try { cacheIn.close(); } catch (IOException e) { e.printStackTrace(); }
            File f = new File(s);
            f.delete();
            try { f.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
            cache = new PrintStream(s, s1);
            cacheIn = new BufferedReader(cacheFileReader = new FileReader(f));
        }
    }

    public Cacher(String s) throws FileNotFoundException {
        if(!(cacheFile = new File(s)).isFile()) try { new File(s).createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        cache = new PrintStream(s);
        cacheIn = new BufferedReader(cacheFileReader = new FileReader(s));
        if(!verifyCacheIntegrity(false, System.out)){
            cache.close();
            try { cacheIn.close(); } catch (IOException e) { e.printStackTrace(); }
            File f = new File(s);
            f.delete();
            try { f.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
            cache = new PrintStream(f);
            cacheIn = new BufferedReader(cacheFileReader = new FileReader(f));
        }
    }

    private void resetReader(){
        try {
            cacheFileReader.reset();
            cacheIn = new BufferedReader(cacheFileReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean cacheContainsKey(String k){
        String s = "";
        try { resetReader(); while((s = cacheIn.readLine())!=null) if(k.equals(s.substring(0, s.indexOf(" ")))) return true; } catch (IOException e) { return false; }
        return false;
    }

    public void store(String k, String v, int cacheTime){
        if(cachedValues.containsKey(k = k.replace("[", "[lb").replace(" ", "[sp"))) cachedValues.replace(k, new CacheDataHolder(v.replace("[", "[lb").replace(" ", "[sp"), cacheTime));
        else cachedValues.put(k, new CacheDataHolder(v.replace("[", "[lb").replace(" ", "[sp"), cacheTime));
    }

    public void store(String k, String v){ store(k, v, (int) System.currentTimeMillis() / 1000); }

    @Nullable
    public CacheDataHolder load(String k) {
        k = k.replace("[", "[lb").replace(" ", "[sp");
        CacheDataHolder tmp;
        if(cachedValues.containsKey(k)) return new CacheDataHolder((tmp=cachedValues.get(k)).data.replace(" ", "[sp").replace("[", "[lb"), tmp.unixTimeCached);
        else if(cacheContainsKey(k)) return new CacheDataHolder((tmp=loadFromCache(k)).data.replace(" ", "[sp").replace("[", "[lb"), tmp.unixTimeCached);
        return null;
    }

    @Nullable
    private CacheDataHolder loadFromCache(String k){
        k = k.replace("[", "[lb").replace(" ", "[sp");
        String s = "";
        try {
            while((s = cacheIn.readLine())!=null) if(s.substring(0, s.indexOf(" ")).equals(k)) //value 123
                return new CacheDataHolder(s.substring(s.indexOf(" ")+1, s.substring(s.indexOf(" ")+1).indexOf(" ")+s.indexOf(" ")).replace(" ", "[sp").replace("[", "[lb"),
                        Integer.parseInt(s.substring(s.substring(s.indexOf(" ")+1).indexOf(" ")+1+s.indexOf(" "))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> loadKeysFromCache(){
        String s = "";
        List<String> keys = new ArrayList<>();
        try { resetReader(); while((s = cacheIn.readLine())!=null) keys.add(s.substring(s.indexOf(" "))); } catch (IOException e) { e.printStackTrace(); }

        return keys;
    }

    public void flush() throws IOException {
        List<String> fileKeys = loadKeysFromCache();
        for(String k : fileKeys)
            if(cachedValues.containsKey(k)) cachedValues.replace(k, loadFromCache(k));
            else cachedValues.put(k, loadFromCache(k));
        cacheFile.delete();
        cacheFile.createNewFile();
        cache = new PrintStream(cacheFile);
        cacheIn = new BufferedReader(cacheFileReader = new FileReader(cacheFile));
        for(String k : cachedValues.keySet())
            store(k, cachedValues.get(k).data, cachedValues.get(k).unixTimeCached);
    }

    public boolean verifyCacheIntegrity(boolean debug, PrintStream debugOut){

        String s = "";
        int count = 0;
        if(debug) debugOut.println("Checking RAM cache integrity...");
        for(String j : cachedValues.keySet())
            if(j == null){
                if(debug) System.out.println("Found a null key at index "+count);
                return false;
            }else if(debug) ++count;
        for(CacheDataHolder j : cachedValues.values())
            if(j == null || j.unixTimeCached<0 || j.data == null){
                if(debug) System.out.println("Found a null value at index "+count);
                return false;
            }else if(debug) ++count;
        System.out.println("RAM cache integrity is sound. Verifying filesystem integrity...");
        try {
            while((s = cacheIn.readLine())!=null){
                if(!s.contains(" ") || s.indexOf(" ")==0 || allOccurrencesOf(s, " ")!=2) return false;
                s = s.substring(s.indexOf(" ")+1);
                if(!s.contains(" ") || s.indexOf(" ")==0) return false;
                //noinspection ResultOfMethodCallIgnored
                Integer.parseInt(s.substring(s.indexOf(" ")+1));
            }
        } catch (Exception e) {
            if(debug) System.out.println("Encountered error when attempting to read cache file! Integrity is not sound!\n"+e.toString());
            return false;
        }
        return true;
    }

    private int allOccurrencesOf(String s, String sequence){
        int i = 0;
        //Assign string to the substring *after* first instance of sequence by incrementing counter, dividing counter variable by itself (i/i = 1)
        //and multiplying by length of sequence to exclude it from new string
        while(s.indexOf(sequence)!=0) s = s.substring(s.indexOf(sequence)+(((++i)/i)*sequence.length()));
        return i;
    }

    @Override
    public void close() throws IOException {
        cache.close();
        cacheIn.close();
    }
}
