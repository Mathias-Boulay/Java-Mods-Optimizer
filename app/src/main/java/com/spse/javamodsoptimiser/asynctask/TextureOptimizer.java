package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.nicdahlquist.pngquant.LibPngQuant;

import java.io.File;
import java.io.IOException;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class TextureOptimizer extends AsyncTask<Task, Object, Void> {

    @Override
    protected Void doInBackground(Task[] task){
        //First parse the arguments
        String[] texturePaths = (String[]) task[0].getArgument(0);
        int textureNumber = texturePaths.length;

        ProgressBar progressBar = task[0].getProgressBar();

        float increment = 100f/textureNumber;
        float progress = 0;
        int intProgress;


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
            progress += increment;
            intProgress = Math.round(progress);
            publishProgress(progressBar, intProgress);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Object... argument) {
        super.onProgressUpdate(argument);
        ProgressBar progressBar = (ProgressBar) argument[0];
        int progress = (int) argument[1];

        progressBar.setProgress(progress, true);

    }
}
