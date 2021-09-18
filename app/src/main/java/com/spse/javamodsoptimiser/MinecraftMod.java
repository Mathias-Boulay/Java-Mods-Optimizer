package com.spse.javamodsoptimiser;

import static com.spse.javamodsoptimiser.setting.Setting.OUTPUT_PATH;

import android.net.Uri;

import java.io.File;
import java.io.InputStream;

public class MinecraftMod{
    //Interact with outside the class
    private String name;
    private String extension;
    private long sizeInBytes; //Size of the UNoptimized archive
    private InputStream inputStream; //ContentResolver data when a file is picked.

    MinecraftMod(String name, String extension, long sizeInBytes, InputStream inputStream){
        this.inputStream = inputStream;
        this.sizeInBytes = sizeInBytes;
        this.extension = extension;
        this.name = name;
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
    public InputStream getInputStream(){
        return inputStream;
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

    /**
     * @return Where the optimized version of the mod is supposed to be
     */
    public String getOutputPath(){
        return OUTPUT_PATH + name + extension;
    }
}
