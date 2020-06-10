package com.spse.javamodsoptimiser;

import android.os.AsyncTask;

import com.nicdahlquist.pngquant.LibPngQuant;

import java.io.File;
import java.io.IOException;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class TextureOptimizer extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object ... argument){
        //First parse the arguments
        String[] texturePaths = (String[]) argument[0];
        int textureNumber = (int) argument[1];


        //Optimize textures
        for(int i=0; i < textureNumber; i++){
            File inputFile = new File(texturePaths[i]);
            File outputFile = new File(texturePaths[i].concat("-min.png"));
            new LibPngQuant().pngQuantFile(inputFile,outputFile);

            //ONCE THE OPTIMISATION IS DONE
            if (fileExists(texturePaths[i].concat("-min.png"))) {
                removeFile(texturePaths[i]);
                try {
                    renameFile(texturePaths[i].concat("-min.png"), texturePaths[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }



}
