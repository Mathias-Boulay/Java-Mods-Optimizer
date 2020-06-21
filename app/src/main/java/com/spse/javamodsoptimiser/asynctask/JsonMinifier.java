package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

import com.spse.javamodsoptimiser.FileManager;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;
import com.whoischarles.util.json.Minify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class JsonMinifier extends AsyncTask<Task, Object, MainActivity> {
    @Override
    protected MainActivity doInBackground(Task[] task) {



        MinecraftMod mod = task[0].getMod();
        ProgressBar progressBar = task[0].getProgressBar();

        float progress = 0;
        float increment = mod.getJsonNumber()/100f;

        for(int i=0; i < mod.getJsonNumber(); i++ ){
            try{
                FileInputStream jis = new FileInputStream(mod.getJsonPath(i));
                FileOutputStream jos = new FileOutputStream(mod.getJsonPath(i).concat("-min.json"));

                new Minify().minify(jis,jos);

                if (fileExists(mod.getJsonPath(i).concat("-min.json"))){
                    removeFile(mod.getJsonPath(i));
                    renameFile(mod.getJsonPath(i).concat("-min.json"), mod.getJsonPath(i));
                }

            }catch (IOException | Minify.UnterminatedCommentException | Minify.UnterminatedStringLiteralException | Minify.UnterminatedRegExpLiteralException io){
                io.printStackTrace();
            }

            progress += increment;
            publishProgress(progressBar,Math.round(progress));

        }

        return task[0].getActivity();
    }


    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        ProgressBar progressBar = (ProgressBar) values[0];
        int progress = (int) values[1];

        progressBar.setProgress(progress, true);
    }

    @Override
    protected void onPostExecute(MainActivity activity) {
        super.onPostExecute(activity);
        activity.launchAsyncTask(7);
    }
}
