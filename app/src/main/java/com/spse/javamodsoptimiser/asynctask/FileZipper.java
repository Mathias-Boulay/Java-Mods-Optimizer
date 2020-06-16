package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.spse.javamodsoptimiser.FileManager;
import com.spse.javamodsoptimiser.MainActivity;
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
        MinecraftMod mod = (MinecraftMod) task[0].getMod();

        repackMod(task[0].getMod(), task[0].getProgressBar());
        return null;
    }

    private void repackMod(MinecraftMod mod, ProgressBar progressBar){
        //First step is to create I/O streams
        String zipFile = OUT_PATH.concat(mod.getFullName());
        if(new File(zipFile).exists()){
            FileManager.removeFile(zipFile);
        }

        int fileNumber = mod.getOtherFileNumber() + mod.getFolderNumber() + mod.getTextureNumber() + mod.getSoundNumber();
        float progress = 0;
        float increment = fileNumber/100f;

        try {
            FileOutputStream fos = new FileOutputStream(zipFile);

            ZipOutputStream zos = new ZipOutputStream(fos);

            //Add all files
            for(int i=0; i < mod.getTextureNumber(); i++){
                addFileToZip(zos, mod.getTexturePath(i));
                progress = incrementProgress(progressBar,progress,increment);
            }
            for(int i=0;i < mod.getSoundNumber();i++){
                addFileToZip(zos,mod.getSoundPath(i));
                progress = incrementProgress(progressBar,progress,increment);
            }
            for(int i=0;i < mod.getOtherFileNumber();i++){
                addFileToZip(zos, mod.getOtherFilePath(i));
                progress = incrementProgress(progressBar,progress,increment);
            }
            for(int i=mod.getFolderNumber()-1;i >= 0;i--){
                FileManager.removeFile(mod.getFolderPath(i) + "/");
                progress = incrementProgress(progressBar,progress,increment);
            }

            zos.close();
            fos.close();

            new File(TEMP_PATH).delete();

        }catch (IOException io){
            io.printStackTrace();
        }

    }

    private float incrementProgress(ProgressBar progressBar, float currentProgress, float increment){
        currentProgress += increment;
        int intProgress = Math.round(currentProgress);
        publishProgress(progressBar,intProgress);

        return currentProgress;
    }

    private void addFileToZip(ZipOutputStream zos, String filePath) throws IOException{
        if(!FileManager.fileExists(filePath)){
            return;
        }

        FileInputStream fis = new FileInputStream(filePath);
        byte[] BUFFER = new byte[1024];
        zos.putNextEntry(new ZipEntry(filePath.replace(TEMP_PATH,"/") ));

        int length;
        while ((length = fis.read(BUFFER)) > 0) {
            zos.write(BUFFER, 0, length);
        }
        zos.closeEntry();
        fis.close();
        FileManager.removeFile(filePath);

    }

    @Override
    protected void onProgressUpdate(Object... argument) {
        super.onProgressUpdate(argument);
        ProgressBar progressBar = (ProgressBar) argument[0];
        int progress = (int) argument[1];

        progressBar.setProgress(progress, true);

    }


}
