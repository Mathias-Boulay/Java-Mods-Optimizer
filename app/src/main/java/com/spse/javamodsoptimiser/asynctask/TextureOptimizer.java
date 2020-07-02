package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.nicdahlquist.pngquant.LibPngQuant;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;

import java.io.File;
import java.io.IOException;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class TextureOptimizer extends AsyncTask<Task, Object, MainActivity> {

    @Override
    protected MainActivity doInBackground(Task[] task){
        //First parse the arguments
        MinecraftMod mod = task[0].getMod();

        ProgressBar progressBar = task[0].getProgressBar();

        float increment = 100f/mod.getTextureNumber();
        float progress = 0;
        int intProgress;


        //Optimize textures
        for(int i=0; i < mod.getTextureNumber(); i++){
            File inputFile = new File(mod.getTexturePath(i));
            File outputFile = new File(mod.getTexturePath(i).concat("-min.png"));
            if(task[0].getActivity().isQualityReduced()) {
                new LibPngQuant().pngQuantFile(inputFile, outputFile, 0, 65);
            }else{
                new LibPngQuant().pngQuantFile(inputFile, outputFile, 45, 85);
            }

            //ONCE THE OPTIMISATION IS DONE
            if (fileExists(mod.getTexturePath(i).concat("-min.png"))) {
                removeFile(mod.getTexturePath(i));
                try {
                    renameFile(mod.getTexturePath(i).concat("-min.png"), mod.getTexturePath(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            progress += increment;
            intProgress = Math.round(progress);
            publishProgress(progressBar, intProgress);
        }
        return task[0].getActivity();
    }

    @Override
    protected void onProgressUpdate(Object... argument) {
        super.onProgressUpdate(argument);
        ProgressBar progressBar = (ProgressBar) argument[0];
        int progress = (int) argument[1];

        progressBar.setProgress(progress, true);

    }

    @Override
    protected void onPostExecute(MainActivity activity) {
        super.onPostExecute(activity);
        activity.textureProgressBar.setProgress(100);

        activity.launchAsyncTask(5);
    }
}
