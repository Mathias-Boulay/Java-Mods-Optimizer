package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;

import java.io.IOException;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class SoundOptimizer extends AsyncTask<Task, Object, MainActivity> {

    @Override
    public MainActivity doInBackground(Task[] task){
        //Parse arguments
        MinecraftMod mod = task[0].getMod();
        ProgressBar progressBar = task[0].getProgressBar();
        
        float increment = 100f/mod.getSoundNumber();
        float progress = 0;
        int intProgress;


        for(int i=0; i < mod.getSoundNumber(); i++) {
            String command = "-y -i '" + mod.getSoundPath(i) + "' -c:a libvorbis -b:a 48k -ac 1 -ar 26000 '" + mod.getSoundPath(i) + "-min.ogg'";

            FFmpeg.execute(command);

            if (fileExists(mod.getSoundPath(i).concat("-min.ogg"))) {
                removeFile(mod.getSoundPath(i));
                try {
                    renameFile(mod.getSoundPath(i).concat("-min.ogg"), mod.getSoundPath(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            progress += increment;
            intProgress = Math.round(progress);
            publishProgress(progressBar,intProgress);
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
        activity.launchAsyncTask(6);

    }
}
