package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.spse.javamodsoptimiser.MainActivity.FOLDER_PATH;

public class FileCopier extends AsyncTask<Task, Object, Void> {


    @Override
    protected Void doInBackground(Task[] task) {


        //Parse arguments
        String inputPath = (String) task[0].getArgument(0);
        String inputFile = (String) task[0].getArgument(1);
        String outputPath = FOLDER_PATH;
        float fileSize = new File(inputPath.concat(inputFile)).length()/1024f;
        float increment = 100/fileSize;
        float progress = 0;
        int intprogress;

        ProgressBar progressBar = task[0].getProgressBar();


        InputStream in;
        OutputStream out ;
        try {
            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()){dir.mkdirs();}


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

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

        return null;
    }

    @Override
    protected void onProgressUpdate(Object... argument) {
        super.onProgressUpdate(argument);
        ProgressBar progressBar = (ProgressBar) argument[0];
        int progress = (int) argument[1];

        progressBar.setProgress(progress,true);
    }


}
