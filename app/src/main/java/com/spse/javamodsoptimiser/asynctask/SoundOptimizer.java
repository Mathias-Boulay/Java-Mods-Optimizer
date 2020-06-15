package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.IOException;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class SoundOptimizer extends AsyncTask<Task, Object, Void> {

    @Override
    public Void doInBackground(Task[] task){
        //Parse arguments
        String[] soundPaths = (String[]) task[0].getArgument(0);
        int soundNumber = soundPaths.length;

        ProgressBar progressBar = task[0].getProgressBar();
        float increment = 100f/soundNumber;
        float progress = 0;
        int intProgress;


        for(int i=0; i < soundNumber; i++) {
            String command = "-y -i '" + soundPaths[i] + "' -c:a libvorbis -b:a 48k -ac 1 -ar 26000 '" + soundPaths[i] + "-min.ogg'";

            int rc = FFmpeg.execute(command);

            if (fileExists(soundPaths[i].concat("-min.ogg"))) {
                removeFile(soundPaths[i]);
                try {
                    renameFile(soundPaths[i].concat("-min.ogg"), soundPaths[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            progress += increment;
            intProgress = Math.round(progress);
            publishProgress(progressBar,intProgress);
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
