package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.nicdahlquist.pngquant.LibPngQuant;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;
import com.spse.javamodsoptimiser.R;
import com.spse.javamodsoptimiser.setting.Setting;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.spse.javamodsoptimiser.FileManager.compareFileSize;
import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class TextureOptimizer extends AsyncTask<Void, Object, Void> {

    WeakReference<MainActivity> activityWeakReference;
    int minQuality;
    int maxQuality;

    public TextureOptimizer(MainActivity activity){
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        activityWeakReference.get().setCurrentTaskTextView(activityWeakReference.get().getResources().getString(R.string.process_status_texture));
    }

    @Override
    protected Void doInBackground(Void[] voids){

        if(Setting.SKIP_TEXTURE_OPTIMIZATION)
            return null;

        //First parse the arguments
        MinecraftMod mod = activityWeakReference.get().modStack.get(0);

        setQuality();

        float increment = 100f/mod.getTextureNumber();
        float progress = 0;
        int intProgress;


        //Optimize textures
        for(int i=0; i < mod.getTextureNumber(); i++){
            File inputFile = new File(mod.getTexturePath(i));
            File outputFile = new File(mod.getTexturePath(i).concat("-min.png"));

            new LibPngQuant().pngQuantFile(inputFile, outputFile, minQuality, maxQuality);

            //ONCE THE OPTIMISATION IS DONE
            try {
                if (compareFileSize(mod.getTexturePath(i), mod.getTexturePath(i).concat("-min.png"))) {
                    removeFile(mod.getTexturePath(i));
                    renameFile(mod.getTexturePath(i).concat("-min.png"), mod.getTexturePath(i));
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
       activityWeakReference.get().launchAsyncTask(5);
    }

    private void setQuality(){
        switch (Setting.TEXTURE_QUALITY){
            case "Very High":
                maxQuality = 100;
                minQuality = 85;
                break;

            case "High":
                maxQuality = 90;
                minQuality = 75;
                break;

            case "Medium":
                maxQuality = 80;
                minQuality = 60;
                break;

            case "Low":
                maxQuality = 65;
                minQuality = 45;
                break;

            case "Destructive":
                maxQuality = 45;
                minQuality = 0;
                break;

            default:
                Log.e("SET_QUALITY","Failed to get quality setting !");
        }

        if(Setting.LENIENT_TEXTURE_QUALITY_CHECK){
            minQuality = 0;
        }
    }
}
