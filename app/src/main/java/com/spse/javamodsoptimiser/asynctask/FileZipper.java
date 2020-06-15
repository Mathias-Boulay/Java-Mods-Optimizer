package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.spse.javamodsoptimiser.MinecraftMod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.spse.javamodsoptimiser.MainActivity.FOLDER_PATH;
import static com.spse.javamodsoptimiser.MainActivity.OUT_PATH;
import static com.spse.javamodsoptimiser.MainActivity.TEMP_PATH;


public class FileZipper extends AsyncTask<Task, Object, Void> {

    @Override
    protected Void doInBackground(Task... task) {
        MinecraftMod mod = (MinecraftMod) task[0].getArgument(0);
        String zipFile = OUT_PATH.concat(mod.getFullName());

        String srcDir = TEMP_PATH;

        try {
            FileOutputStream fos = new FileOutputStream(zipFile);

            ZipOutputStream zos = new ZipOutputStream(fos);

            File srcFile = new File(srcDir);

            addToArchive(zos, srcFile, "");

            // close the ZipOutputStream
            zos.close();

        }
        catch (IOException ioe) {
            System.out.println("Error creating zip file: " + ioe);
        }


        return null;
    }

    private void addToArchive(ZipOutputStream zos, File srcFile, String subfolder) {
        //MinecraftMod mod = (MinecraftMod) task.getArgument(0);

        File[] files = srcFile.listFiles();

        System.out.println("Adding directory: " + srcFile.getName());

        for (int i = 0; i < files.length; i++) {

            // if the file is directory, use recursion
            if (files[i].isDirectory()) {
                if (!files[i].getName().contains("OUTPUT")) {
                    addToArchive(zos, files[i], files[i].getAbsolutePath());
                    files[i].delete();
                }
                continue;
            }

            try {
                byte[] buffer = new byte[1024];

                FileInputStream fis = new FileInputStream(files[i]);

                zos.putNextEntry(new ZipEntry(subfolder.replace(TEMP_PATH,"") + "/" + files[i].getName()));


                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                    //progress += 100f/((mod.getFileSize()*0.70)/1024);
                    //publishProgress((ProgressBar) task.getProgressBar(), mod.getFileSize()*0.70, Math.round(progress));
                }
                zos.closeEntry();
                // close the InputStream
                fis.close();
                files[i].delete();

            } catch (IOException ioe) {
                System.out.println("IOException :" + ioe);
            }



        }

    }

    @Override
    protected void onProgressUpdate(Object... argument) {
        super.onProgressUpdate(argument);
        ProgressBar progressBar = (ProgressBar) argument[0];
        int progress = (int) argument[1];

        progressBar.setProgress(progress, true);

    }
}
