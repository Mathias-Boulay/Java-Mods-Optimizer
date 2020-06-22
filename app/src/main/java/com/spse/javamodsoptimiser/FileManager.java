package com.spse.javamodsoptimiser;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import static com.spse.javamodsoptimiser.MainActivity.TEMP_PATH;

public class FileManager {
    //Class related to everything related to files


    public static void removeFile(String inputPath, String inputFile){
        try {
            // delete the original file
            new File(inputPath + inputFile).delete();
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }
    public static void removeFile(String absolutePath){
        try {
            // delete the original file
            new File(absolutePath).delete();
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    public static void renameFile(String filePath, String newFilePath) throws IOException{
        File file = new File(filePath);
        File renamedFile = new File(newFilePath);

        if (!file.exists()){
            throw new java.io.IOException("input file not found");
        }
        if (renamedFile.exists()){
            throw new java.io.IOException("renamedFile already exists");
        }

        boolean renamed = file.renameTo(renamedFile);
        if (!renamed){
            throw new java.io.IOException("failed to rename the file !");
        }
    }

    public static boolean fileExists(String absolutePath){
        File file = new File(absolutePath);
        if (file.exists()){
            return true;
        }
        return false;
    }

    public static void createFolder(String absolutePathToFolder){
        File file = new File(absolutePathToFolder);
        if(!file.exists()){
            file.mkdir();
        }
    }

    public static void removeLeftOvers(){
        //Remove anything within the TEMP_PATH

        walkAndRemove(TEMP_PATH);
    }

    private static void walkAndRemove(String path) {

        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                walkAndRemove(f.getAbsolutePath());
            }else{
                removeFile(f.getAbsolutePath());
                return;
            }
            removeFile(f.getAbsolutePath());
        }
    }


}
