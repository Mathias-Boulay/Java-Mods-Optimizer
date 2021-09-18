package com.spse.javamodsoptimiser.asynctask;

import static com.spse.javamodsoptimiser.setting.Setting.TEMP_PATH;

import android.os.AsyncTask;

import com.spse.javamodsoptimiser.FileManager;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUnzipper  extends AsyncTask<Void, Object, Void> {

    WeakReference<MainActivity> activityWeakReference;

    public FileUnzipper(MainActivity activity){
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        activityWeakReference.get().setCurrentTaskTextView(activityWeakReference.get().getResources().getString(R.string.process_status_unzipping));
    }

    @Override
    protected Void doInBackground(Void[] voids) {
        try {
            unzip(TEMP_PATH + activityWeakReference.get().modStack.get(0).getFullName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void unzip(String zipFile) throws IOException{
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
                byte[] data = new byte[BUFFER];

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
                    publishProgress(intProgress);
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
        activityWeakReference.get().setCurrentTaskProgress((int)argument[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        activityWeakReference.get().launchAsyncTask(3);
    }
}
