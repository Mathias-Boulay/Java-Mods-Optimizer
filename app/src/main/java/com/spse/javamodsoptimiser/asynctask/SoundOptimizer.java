package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;
import com.spse.javamodsoptimiser.R;
import com.spse.javamodsoptimiser.setting.Setting;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.spse.javamodsoptimiser.FileManager.compareFileSize;
import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class SoundOptimizer extends AsyncTask<Void, Object, Void> {

    WeakReference<MainActivity> activityWeakReference;
    String frequency;
    String bitrate;

    public SoundOptimizer(MainActivity activity){
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        activityWeakReference.get().setCurrentTaskTextView(activityWeakReference.get().getResources().getString(R.string.process_status_sound));
    }

    @Override
    public Void doInBackground(Void[] voids){

        if(Setting.SKIP_SOUND_OPTIMIZATION)
            return null;

        setQuality();

        //Parse arguments
        MinecraftMod mod = activityWeakReference.get().modStack.get(0);

        
        float increment = 100f/mod.getSoundNumber();
        float progress = 0;
        int intProgress;


        for(int i=0; i < mod.getSoundNumber(); i++) {
            String command;
            String oggPath = mod.getSoundPath(i);
            String minOggPath = oggPath.concat("-min.ogg");

            command = "-y -i '" + oggPath + "' -c:a libvorbis "+ bitrate +" -ac 1 "+ frequency +" '" + minOggPath + "'";
            FFmpeg.execute(command);

            try {
                if (compareFileSize(oggPath, minOggPath)) {
                    removeFile(mod.getSoundPath(i));
                    renameFile(mod.getSoundPath(i).concat("-min.ogg"), mod.getSoundPath(i));
                }
            } catch (IOException e) {
                e.printStackTrace();
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

    private void setQuality(){
        switch (Setting.SOUND_QUALITY){
            case "Destructive":
                bitrate = "-b:a 36k";
                frequency = "-ar 26000";
                break;

            case "Low":
                bitrate = "-b:a 48k";
                frequency = "-ar 26000";
                break;

            case "Medium":
                bitrate = "-b:a 56k";
                frequency = "-ar 38000";
                break;

            case "High":
                bitrate = "-b:a 64k";
                frequency = "-ar 38000";
                break;

            case "Very High":
                bitrate = "-b:a 80k";
                frequency = ""; //Native frequency which is likely 44100 KHz
                break;

            default:
                Log.e("SET QUALITY", "Unable to set quality !");
        }
    }

}
