package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;
import com.spse.javamodsoptimiser.R;

import java.io.IOException;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class SoundOptimizer extends AsyncTask<MainActivity, Object, MainActivity> {

    @Override
    public MainActivity doInBackground(MainActivity[] activity){
        
        MinecraftMod mod = activity[0].mod;

        for(int i=0; i < mod.getSoundNumber(); i++) {
            String command;
            if(activity[0].isQualityReduced()){
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
                publishProgress(activity[0], R.string.log_file_sound_1 , mod.getSoundPath(i).substring(mod.getSoundPath(i).lastIndexOf('/') + 1));
            }
        }

        return activity[0];
    }

    @Override
    protected void onProgressUpdate(Object... argument) {
        super.onProgressUpdate(argument);
        MainActivity activity = (MainActivity) argument[0];
        activity.addUserLog((int) argument[1], (String) argument[2]);
    }

    @Override
    protected void onPostExecute(MainActivity activity) {
        super.onPostExecute(activity);
        activity.launchAsyncTask(6);
    }
}
