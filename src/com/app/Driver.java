package com.app;

import com.app.services.PropertyLoader;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Driver {

    private static String CONFIG_FILE_PATH = "/Users/j0s0p9w/work/personal/tx/subtitleAdjuster/config.properties";

    private static PropertyLoader propertyLoader;

    private static final String SRC_FILE_PATH = "source.srt.file";

    private static final String DEST_FILE_PATH = "dest.srt.file";

    private static final String SUBTITLE_ADJUST_SEC = "subtitle.adjust.sec";

    private static final String IDENTIFIER = " --> ";

    private static final String TERMINATOR = "\n\r";

    public static void main(String[] args) {
        try {
            System.out.println("version 0.1");
            if (args.length > 0) {
                CONFIG_FILE_PATH = args[0];
            }
            propertyLoader = new PropertyLoader(CONFIG_FILE_PATH);
            adjustSrt();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void adjustSrt() throws IOException {
        FileReader reader = new FileReader(propertyLoader.getProperty(SRC_FILE_PATH));
        FileWriter writer = new FileWriter(propertyLoader.getProperty(DEST_FILE_PATH));
        int read;
        StringBuilder sb = new StringBuilder();
        while ((read = reader.read()) != -1) {
            char c = (char) read;
            sb.append(c);
            if (c == '\n') {
                String strToWrite = sb.toString();
                if (strToWrite.contains(IDENTIFIER)) {
                    String[] strs = strToWrite.split(IDENTIFIER);
                    String startTime = adjustTime(strs[0]);
                    String endTime = adjustTime(strs[1]);
                    writer.write(startTime + IDENTIFIER + endTime);
                } else {
                    writer.write(strToWrite);
                }
                sb = new StringBuilder();
            }
        }
    }

    private static String adjustTime(String time) {
        LocalTime localTime = LocalTime.parse(time.substring(0, time.indexOf(",")), DateTimeFormatter.ofPattern("HH:mm:ss"));
        localTime = localTime.plusSeconds(Long.parseLong(propertyLoader.getProperty(SUBTITLE_ADJUST_SEC)));
        String adjustedTime = localTime.toString();
        if(adjustedTime.split(":").length < 3){
            String [] token = adjustedTime.split(":");
            if(token.length == 0){
                adjustedTime = "00:00:00";
            } else if(token.length == 1){
                adjustedTime += ":00:00";
            } else if(token.length == 2){
                adjustedTime += ":00";
            }
        }
        return adjustedTime + time.substring(time.indexOf(","));
    }


}
