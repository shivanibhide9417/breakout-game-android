package com.test.breakout.breakoutgame.highScores;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class FileIO {

    //Creating a new ArrayList hashmap
    public ArrayList<HashMap<String, String>> list; // stores the return value
    public static final String FIRST_COLUMN = "First";
    public static final String SECOND_COLUMN = "Second";
    public static final String THIRD_COLUMN = "Third";
    public String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    File file = new File(baseDir, "scores.txt");




    //Function to read all the entries already existing in the file,append new data and write
    public void writeFile(String name, String score, String date)
    {
      //  File file = new File(baseDir, "scores.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("CREATED NEW FILE");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter writer = null;
        String line = "";
        StringBuilder text1 = new StringBuilder();

        try {
            FileReader fReader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(fReader);

            while( (line = bReader.readLine()) != null  ){
                text1.append(line);
            }
            writer = new FileWriter(file);
          //clear the file->  //writer.write("");
            writer.write(text1.toString() + "   "+ name + "   " + score + "   " + date);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Function to read the obtained values from file into the list
    public ArrayList<HashMap<String, String>> populateList() {

        InputStream input;

        File sdcard = Environment.getExternalStorageDirectory();


        try {
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("CREATED NEW FILE");
            }


            list = new ArrayList<HashMap<String, String>>();

            HashMap<String, String> hashmap = new HashMap<String, String>();
            hashmap.put(FIRST_COLUMN, "Player name");
            hashmap.put(SECOND_COLUMN, "Score");
            hashmap.put(THIRD_COLUMN, "Date");
            list.add(hashmap);

            String FILE_NAME = "scores.txt";

            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(baseDir, FILE_NAME);

            String line = "";
            StringBuilder text1 = new StringBuilder();

            try {
                FileReader fReader = new FileReader(file);
                BufferedReader bReader = new BufferedReader(fReader);

                while ((line = bReader.readLine()) != null) {
                    text1.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
           StringTokenizer st = new StringTokenizer(text1.toString());
          //  System.out.println(text1.toString());
            while (st.hasMoreTokens()) {
                HashMap<String, String> hashmap2 = new HashMap<String, String>();
                hashmap2.put(FIRST_COLUMN, st.nextToken());
                hashmap2.put(SECOND_COLUMN, st.nextToken());
                hashmap2.put(THIRD_COLUMN, st.nextToken());
                list.add(hashmap2);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
            return list;

    }
}
