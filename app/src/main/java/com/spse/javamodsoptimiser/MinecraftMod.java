package com.spse.javamodsoptimiser;

import java.io.File;
import java.io.FileFilter;

public class MinecraftMod{
    //Interact with outside the class
    private String name;
    private String extension;
    private long sizeInBytes;
    private String fileFolder;

    MinecraftMod(String filePath){
        int index = filePath.length()-1;
        while (!(filePath.charAt(index) == "/".charAt(0))){
            index --;
        }

        this.fileFolder = filePath.substring(0,index+1);
        this.name = filePath.substring(index+1,filePath.length()-4);
        this.extension = filePath.substring(filePath.length()-4);
        this.sizeInBytes = new File(filePath).length();
    }

    public int textureNumber = 0;
    public int soundNumber = 0;
    public int otherFileNumber = 0;
    public int folderNumber = 0;

    public String[] texturePath;
    public String[] soundPath;
    public String[] otherFilePath;
    public String[] folderPath;


    //Don't interact outside of the class
    public int textureIndex = 0;
    public int soundIndex = 0;
    public int otherFileIndex = 0;
    public int folderIndex = 0;


    //Here all access to private variables

    public int getTextureNumber(){return textureNumber;}
    public int getSoundNumber(){return soundNumber;}
    public String getTexturePath(int textureIndex){
        if (texturePath == null){return "NULL";}
        return texturePath[textureIndex];
    }
    public String getSoundPath(int soundIndex){
        if (soundPath == null){return "NULL";}
        return soundPath[soundIndex];
    }
    public String[] getSoundPaths(){
        return soundPath;
    }
    public String[] getTexturePaths(){
        return texturePath;
    }
    public long getFileSize(){
        return sizeInBytes;
    }
    public String getName(){
        return name;
    }
    public String getExtension(){
        return extension;
    }
    public String getFullName(){
        return name.concat(extension);
    }
    public String getFolder(){
        return fileFolder;
    }





}
