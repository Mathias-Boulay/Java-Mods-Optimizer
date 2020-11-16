package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;

import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;
import com.spse.javamodsoptimiser.R;
import com.whoischarles.util.json.Minify;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class JsonMinifier extends AsyncTask<MainActivity, Object, MainActivity> {
    @Override
    protected MainActivity doInBackground(MainActivity[] activity) {



        MinecraftMod mod = activity[0].mod;

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
                    publishProgress(activity[0], R.string.log_file_minifier_1,  mod.getJsonPath(i).substring(mod.getJsonPath(i).lastIndexOf('/') + 1));
                }


            }catch (IOException | Minify.UnterminatedCommentException | Minify.UnterminatedStringLiteralException | Minify.UnterminatedRegExpLiteralException io){
                io.printStackTrace();
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
        activity.launchAsyncTask(7);
    }
}
