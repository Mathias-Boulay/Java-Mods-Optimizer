package com.spse.javamodsoptimiser;

import java.io.File;
import java.io.FileFilter;

public class MinecraftMod{
    //Interact with outside the class
    private String name;
    private String fileExtension;

    MinecraftMod(String name, String fileExtension){
        this.name = name;
        this.fileExtension = fileExtension;
    }

    private int textureNumber = 0;
    private int soundNumber = 0;

    private String[] texturePath;
    private String[] soundPath;

    //Don't interact outside of the class
    private int textureIndex = 0;
    private int soundIndex = 0;

    FileFilter numberFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()){
                return true;
            }
            if (pathname.toString().contains(".png")){
                textureNumber ++;
                return true;
            }
            if (pathname.toString().contains(".ogg")){
                soundNumber ++;
                return true;
            }
            return false;
        }
    };

    FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()){
                return true;
            }
            if (pathname.toString().contains(".png")){
                texturePath[textureIndex] = pathname.getAbsolutePath();
                textureIndex++;
                return true;
            }
            if (pathname.toString().contains(".ogg")){
                soundPath[soundIndex] = pathname.getAbsolutePath();
                soundIndex ++;
                return true;
            }
            return false;
        }
    };

    private void walk(String path, FileFilter filter) {

        File root = new File( path );
        File[] list = root.listFiles(filter);

        if (list == null) return;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( f.getAbsolutePath(), filter);
            }
        }
    }

    public void parseUncompressedMod(String modPath){
        //First count how many textures and sounds we have
        walk(modPath, numberFilter);

        //Then assign arrays to store both textures and sounds paths
        texturePath = new String[textureNumber];
        soundPath = new String[soundNumber];

        //Then store those files path
        walk(modPath, fileFilter);
    }


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



}
