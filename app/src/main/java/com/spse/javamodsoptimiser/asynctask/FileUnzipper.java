package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.spse.javamodsoptimiser.FileManager;
import com.spse.javamodsoptimiser.MainActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.spse.javamodsoptimiser.MainActivity.TEMP_PATH;

public class FileUnzipper  extends AsyncTask<Task, Object, MainActivity> {

    @Override
    protected MainActivity doInBackground(Task[] task) {
        try {
            unzip(TEMP_PATH + task[0].getMod().getFullName(), task[0].getProgressBar());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return task[0].getActivity();
    }

    private void unzip(String zipFile, ProgressBar updateBar) throws IOException{
        int BUFFER = 2048;
        File file = new File(zipFile);

        float approximatedUncompressedFileSize = file.length();
        approximatedUncompressedFileSize *= 1.3f;
        approximatedUncompressedFileSize /= BUFFER;

        float increment = 100/approximatedUncompressedFileSize;
        float progress = 0f;
        int intProgress;



        ZipFile zip = null;
        try {
            zip = new ZipFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String newPath = TEMP_PATH;

        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements())
        {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            //destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            if (!entry.isDirectory())
            {
                BufferedInputStream is = new BufferedInputStream(zip
                        .getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);

                    //Actualise progress
                    progress += increment;
                    intProgress = Math.round(progress);
                    publishProgress(updateBar, intProgress);
                }
                dest.flush();
                dest.close();
                is.close();
            }

        }

        FileManager.removeFile(zipFile);
    }

    @Override
    protected void onProgressUpdate(Object... argument) {
        super.onProgressUpdate(argument);
        ProgressBar progressBar = (ProgressBar) argument[0];
        int progress = (int) argument[1];
        progressBar.setProgress(progress, true);
    }

    @Override
    protected void onPostExecute(MainActivity activity) {
        super.onPostExecute(activity);
        activity.launchAsyncTask(3);
    }
}
