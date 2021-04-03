package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;
import com.spse.javamodsoptimiser.setting.Setting;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class SoundOptimizer extends AsyncTask<Void, Object, Void> {

    WeakReference<MainActivity> activityWeakReference;

    public SoundOptimizer(MainActivity activity){
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public Void doInBackground(Void[] voids){

        if(Setting.SKIP_SOUND_OPTIMIZATION)
            return null;

        //Parse arguments
        MinecraftMod mod = activityWeakReference.get().modStack.get(0);
        
        float increment = 100f/mod.getSoundNumber();
        float progress = 0;
        int intProgress;


        for(int i=0; i < mod.getSoundNumber(); i++) {
            String command;

            if(Setting.SOUND_QUALITY.equals("Destructive")){
                command = "-y -i '" + mod.getSoundPath(i) + "' -c:a libvorbis -b:a 36k -ac 1 -ar 26000 '" + mod.getSoundPath(i) + "-min.ogg'";
            }else {
                command = "-y -i '" + mod.getSoundPath(i) + "' -c:a libvorbis -b:a 48k -ac 1 -ar 26000 '" + mod.getSoundPath(i) + "-min.ogg'";
            }

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
            publishProgress(intProgress);


        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Object... argument) {
        activityWeakReference.get().setCurrentTaskProgress((int)argument[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        activityWeakReference.get().launchAsyncTask(6);
    }
}
