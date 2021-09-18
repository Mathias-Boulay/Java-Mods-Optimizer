package com.spse.javamodsoptimiser;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;

/**
 * Class for everything related to interacting with files
 */
public class FileManager {

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
            return file.mkdirs();
        }
        return true;
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

    /**
     * Remove everything inside a folder. Works recursively.
     * @param path The path to start from.
     */
    public static void removeEverything(String path) {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                removeEverything(f.getAbsolutePath());
            }
            f.delete();
        }
    }

    /**
     * Tries to get an Uri from the various sources
     */
    public static Uri[] getUriData(Intent intent){
        Uri[] mUriData = new Uri[]{intent.getData()};
        if(mUriData[0] != null) return mUriData;
        try {
            mUriData = new Uri[intent.getClipData().getItemCount()];
            for(int i=0; i < mUriData.length; ++i){
                mUriData[i] = intent.getClipData().getItemAt(i).getUri();
            }
        }catch (Exception ignored){}
        return mUriData;
    }

    /**
     * Extract the file name from an Uri
     * @param ctx Context
     * @param uri The Uri to extract from
     * @return The file name
     */
    public static String getFileName(Context ctx, Uri uri){
        Cursor returnCursor =
                ctx.getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String fileName = returnCursor.getString(nameIndex);
        returnCursor.close();
        return fileName;
    }

    /**
     * Extract the file size from an Uri
     * @param ctx Context
     * @param uri The Uri to extract from
     * @return The file name
     */
    public static long getFileSize(Context ctx, Uri uri){
        Cursor returnCursor =
                ctx.getContentResolver().query(uri, null, null, null, null);

        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        long fileSize = returnCursor.getLong(sizeIndex);
        returnCursor.close();
        return fileSize;
    }

}
