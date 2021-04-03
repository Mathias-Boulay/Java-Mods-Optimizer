package com.spse.javamodsoptimiser;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import static com.spse.javamodsoptimiser.MainActivity.TEMP_PATH;

public class FileManager {
    //Class related to everything related to files


    public static boolean removeFile(String inputPath, String inputFile){
        return removeFile(inputPath + inputFile);
    }
    public static boolean removeFile(String absolutePath){
        try {
            // delete the original file
            return new File(absolutePath).delete();
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        return false;
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
        return file.exists();
    }

    public static boolean createFolder(String absolutePathToFolder){
        File file = new File(absolutePathToFolder);
        if(!file.exists()){
            return file.mkdir();
        }
        return true;
    }

    public static void removeLeftOvers(){
        //Remove anything within the TEMP_PATH

        walkAndRemove(TEMP_PATH);
    }

    public static boolean compareFileSize(String fileOne, String fileTwo) throws IOException {
        //return true if file1 > file2
        File file1 = new File(fileOne);
        File file2 = new File(fileTwo);

        if(!file1.exists())
            throw new IOException("File 1 not found !");

        if(!file2.exists())
            throw new IOException("File 2 not found !");

        return file1.length() > file2.length();
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
