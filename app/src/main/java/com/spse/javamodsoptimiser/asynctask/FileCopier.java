package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.spse.javamodsoptimiser.MainActivity.TEMP_PATH;

public class FileCopier extends AsyncTask<Task, Object, MainActivity> {


    @Override
    protected MainActivity doInBackground(Task[] task) {


        //Parse arguments
        MinecraftMod mod = task[0].getMod();
        ProgressBar progressBar = task[0].getProgressBar();

        float increment = 100f/(mod.getFileSize()/1024f);
        float progress = 0;
        int intprogress;

        //Then copy the file
        InputStream in;
        OutputStream out ;
        try {
            //create output directory if it doesn't exist
            File dir = new File(TEMP_PATH);
            if (!dir.exists()){dir.mkdirs();}


            in = new FileInputStream(mod.getFolder() + mod.getFullName());
            out = new FileOutputStream(TEMP_PATH + mod.getFullName());

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);


                progress += increment;
                intprogress = Math.round(progress);
                publishProgress(progressBar,intprogress);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

        return task[0].getActivity();
    }

    @Override
    protected void onProgressUpdate(Object... argument) {
        super.onProgressUpdate(argument);
        ProgressBar progressBar = (ProgressBar) argument[0];
        int progress = (int) argument[1];



        progressBar.setProgress(progress,true);



    }

    @Override
    protected void onPostExecute(MainActivity activity) {
        super.onPostExecute(activity);
        activity.copyProgressBar.setProgress(100);

        activity.launchAsyncTask(2);
    }
}
