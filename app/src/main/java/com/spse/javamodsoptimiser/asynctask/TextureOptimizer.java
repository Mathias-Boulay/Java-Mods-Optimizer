package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;

import com.nicdahlquist.pngquant.LibPngQuant;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;
import com.spse.javamodsoptimiser.R;

import java.io.File;
import java.io.IOException;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class TextureOptimizer extends AsyncTask<MainActivity, Object, MainActivity> {

    @Override
    protected MainActivity doInBackground(MainActivity[] activity){
        //First parse the arguments
        MinecraftMod mod = activity[0].mod;



        //Optimize textures
        for(int i=0; i < mod.getTextureNumber(); i++){
            File inputFile = new File(mod.getTexturePath(i));
            File outputFile = new File(mod.getTexturePath(i).concat("-min.png"));
            if(activity[0].isQualityReduced()) {
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
                publishProgress(activity[0], R.string.log_file_texture_1, mod.getTexturePath(i).substring(mod.getTexturePath(i).lastIndexOf('/') + 1));
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
        activity.launchAsyncTask(5);
    }
}
