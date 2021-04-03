package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.nicdahlquist.pngquant.LibPngQuant;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;
import com.spse.javamodsoptimiser.setting.Setting;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class TextureOptimizer extends AsyncTask<Void, Object, Void> {

    WeakReference<MainActivity> activityWeakReference;

    public TextureOptimizer(MainActivity activity){
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected Void doInBackground(Void[] voids){

        if(Setting.SKIP_TEXTURE_OPTIMIZATION)
            return null;

        //First parse the arguments
        MinecraftMod mod = activityWeakReference.get().modStack.get(0);

        float increment = 100f/mod.getTextureNumber();
        float progress = 0;
        int intProgress;


        //Optimize textures
        for(int i=0; i < mod.getTextureNumber(); i++){
            File inputFile = new File(mod.getTexturePath(i));
            File outputFile = new File(mod.getTexturePath(i).concat("-min.png"));


            if(Setting.TEXTURE_QUALITY.equals("Destructive")) { //TODO add more granularity and control over this shit
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
}
