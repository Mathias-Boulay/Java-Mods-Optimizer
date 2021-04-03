package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;
import com.whoischarles.util.json.Minify;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class JsonMinifier extends AsyncTask<Void, Object, Void> {

    WeakReference<MainActivity> activityWeakReference;

    public JsonMinifier(MainActivity activity){
        activityWeakReference = new WeakReference<>(activity);
    }


    @Override
    protected Void doInBackground(Void[] voids) {



        MinecraftMod mod = activityWeakReference.get().modStack.get(0);

        float progress = 0;
        float increment = 100f/mod.getJsonNumber();

        for(int i=0; i < mod.getJsonNumber(); i++ ){
            try{
                FileInputStream jis = new FileInputStream(mod.getJsonPath(i));
                FileOutputStream jos = new FileOutputStream(mod.getJsonPath(i).concat("-min.json"));

                new Minify().minify(jis,jos);

                jos.close();
                jis.close();

                if (fileExists(mod.getJsonPath(i).concat("-min.json"))){
                    removeFile(mod.getJsonPath(i));
                    renameFile(mod.getJsonPath(i).concat("-min.json"), mod.getJsonPath(i));
                }


            }catch (IOException | Minify.UnterminatedCommentException | Minify.UnterminatedStringLiteralException | Minify.UnterminatedRegExpLiteralException io){
                io.printStackTrace();
            }

            progress += increment;
            publishProgress(Math.round(progress));

        }

        return null;
    }


    @Override
    protected void onProgressUpdate(Object... argument) {
        activityWeakReference.get().setCurrentTaskProgress((int)argument[0]);
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        activityWeakReference.get().launchAsyncTask(7);
    }
}
