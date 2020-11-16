package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;

import com.spse.javamodsoptimiser.FileManager;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.spse.javamodsoptimiser.MainActivity.TEMP_PATH;

public class FileUnzipper  extends AsyncTask<MainActivity, Object, MainActivity> {

    @Override
    protected MainActivity doInBackground(MainActivity[] activity) {
        publishProgress(activity[0], R.string.log_file_unzipper_1, activity[0].mod.getName());
        try {
            unzip(TEMP_PATH + activity[0].mod.getFullName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        publishProgress(activity[0], R.string.log_file_unzipper_2, activity[0].mod.getName());
        return activity[0];
    }

    private void unzip(String zipFile) throws IOException{
        int BUFFER = 2048;
        File file = new File(zipFile);





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
                byte[] data = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);

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
        MainActivity activity = (MainActivity) argument[0];
        activity.addUserLog((int) argument[1], (String) argument[2]);
    }

    @Override
    protected void onPostExecute(MainActivity activity) {
        super.onPostExecute(activity);
        activity.launchAsyncTask(3);
    }
}
