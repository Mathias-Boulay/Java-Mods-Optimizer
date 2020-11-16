package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;

import com.spse.javamodsoptimiser.FileManager;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;
import com.spse.javamodsoptimiser.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.spse.javamodsoptimiser.MainActivity.OUT_PATH;
import static com.spse.javamodsoptimiser.MainActivity.TEMP_PATH;


public class FileZipper extends AsyncTask<MainActivity, Object, MainActivity> {



    @Override
    protected MainActivity doInBackground(MainActivity... activity) {

        repackMod(activity[0].mod, activity[0]);

        return activity[0];
    }

    private void repackMod(MinecraftMod mod, MainActivity activity){
        //First step is to create I/O streams
        String zipFile = OUT_PATH.concat(mod.getFullName());



        try {
            FileOutputStream fos = new FileOutputStream(zipFile);

            ZipOutputStream zos = new ZipOutputStream(fos);

            //Add all files
            for(int i=0; i < mod.getTextureNumber(); i++){
                addFileToZip(zos, mod.getTexturePath(i));
                publishProgress(activity, R.string.log_file_zipper_1, mod.getTexturePath(i).substring(mod.getTexturePath(i).lastIndexOf('/') + 1));
            }
            for(int i=0;i < mod.getSoundNumber();i++){
                addFileToZip(zos,mod.getSoundPath(i));
                publishProgress(activity, R.string.log_file_zipper_1, mod.getSoundPath(i).substring(mod.getSoundPath(i).lastIndexOf('/') + 1));
            }
            for (int i=0; i < mod.getJsonNumber(); i++){
                addFileToZip(zos,mod.getJsonPath(i));
                publishProgress(activity, R.string.log_file_zipper_1, mod.getJsonPath(i).substring(mod.getJsonPath(i).lastIndexOf('/') + 1));
            }

            for(int i=0;i < mod.getOtherFileNumber();i++){
                //Remove signatures if needed
                if (!activity.haveSignaturesRemoved()) {
                    addFileToZip(zos, mod.getOtherFilePath(i));
                    publishProgress(activity, R.string.log_file_zipper_1, mod.getOtherFilePath(i).substring(mod.getOtherFilePath(i).lastIndexOf('/') + 1));
                }else{
                    if (!mod.getOtherFilePath(i).contains(".RSA") && !mod.getOtherFilePath(i).contains(".MF") && !mod.getOtherFilePath(i).contains(".SF")){
                        addFileToZip(zos, mod.getOtherFilePath(i));
                        publishProgress(activity, R.string.log_file_zipper_1, mod.getOtherFilePath(i).substring(mod.getOtherFilePath(i).lastIndexOf('/') + 1));
                    }else{
                        //else we don't add the file back but we still have to delete it.
                        FileManager.removeFile(mod.getOtherFilePath(i));
                    }
                }

            }
            for(int i=mod.getFolderNumber()-1;i >= 0;i--){
                FileManager.removeFile(mod.getFolderPath(i) + "/");
            }

            zos.close();
            fos.close();

            new File(TEMP_PATH).delete();

        }catch (IOException io){
            io.printStackTrace();
        }

        if(activity.haveOriginalDeleted()){
            FileManager.removeFile(mod.getFolder() + mod.getFullName());
        }

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
        MainActivity activity = (MainActivity) argument[0];
        activity.addUserLog((int) argument[1], (String) argument[2]);

    }

    @Override
    protected void onPostExecute(MainActivity activity) {
        super.onPostExecute(activity);
        activity.postOptimize();
    }

}

