package com.spse.javamodsoptimiser;

import java.io.File;

public class MinecraftMod{
    //Interact with outside the class
    private final String name;
    private final String extension;
    private final long sizeInBytes; //Size of the UNoptimized archive
    private final String fileFolder; //Folder where the file is stored

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
    public int jsonNumber = 0;
    public int otherFileNumber = 0;
    public int folderNumber = 0;


    public String[] texturePath;
    public String[] soundPath;
    public String[] jsonPath;
    public String[] otherFilePath;
    public String[] folderPath;

    public int textureIndex = 0;
    public int soundIndex = 0;
    public int jsonIndex = 0;
    public int otherFileIndex = 0;
    public int folderIndex = 0;


    //Here all access to variables
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
    public int getOtherFileNumber(){return otherFileNumber;}
    public String getOtherFilePath(int index){
        if (otherFilePath == null){return "NULL";}
        return otherFilePath[index];
    }
    public int getFolderNumber(){return folderNumber;}
    public String getFolderPath(int index){return folderPath[index];}
    public int getJsonNumber(){return jsonNumber;}
    public String getJsonPath(int index) {
        return jsonPath[index];
    }
}
