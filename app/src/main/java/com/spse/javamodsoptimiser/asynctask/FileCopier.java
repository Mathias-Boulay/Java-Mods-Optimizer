package com.spse.javamodsoptimiser.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import static com.spse.javamodsoptimiser.MainActivity.TEMP_PATH;

public class FileCopier extends AsyncTask<Void, Object, Void> {

    WeakReference<MainActivity> activityWeakReference;

    public FileCopier(MainActivity activity){
        activityWeakReference = new WeakReference<>(activity);
    }

    protected Void doInBackground(Void... aVoid) {

        //Parse arguments
        MinecraftMod mod = activityWeakReference.get().modStack.get(0);

        float increment = 100f/(mod.getFileSize()/1024f);
        float progress = 0;
        int intProgress;

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

                //Progress update
                progress += increment;
                intProgress = Math.round(progress);
                publishProgress(intProgress);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (Exception fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Object[] progress) {
        activityWeakReference.get().setCurrentTaskProgress((int)progress[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        activityWeakReference.get().launchAsyncTask(2);
    }
}
