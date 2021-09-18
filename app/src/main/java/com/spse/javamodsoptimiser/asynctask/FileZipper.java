package com.spse.javamodsoptimiser.asynctask;

import static com.spse.javamodsoptimiser.setting.Setting.OUTPUT_PATH;
import static com.spse.javamodsoptimiser.setting.Setting.REMOVE_SIGNATURE_FILES;
import static com.spse.javamodsoptimiser.setting.Setting.TEMP_PATH;

import android.os.AsyncTask;

import com.spse.javamodsoptimiser.FileManager;
import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;
import com.spse.javamodsoptimiser.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class FileZipper extends AsyncTask<Void, Object, Void> {

    WeakReference<MainActivity> activityWeakReference;

    public FileZipper(MainActivity activity){
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        activityWeakReference.get().setCurrentTaskTextView(activityWeakReference.get().getResources().getString(R.string.process_status_zipping));
    }

    @Override
    protected Void doInBackground(Void... voids) {
        repackMod(activityWeakReference.get().modStack.get(0));
        return null;
    }

    private void repackMod(MinecraftMod mod){
        //First step is to create I/O streams
        String zipFile = OUTPUT_PATH.concat(mod.getFullName());

        int fileNumber = mod.getOtherFileNumber() + mod.getJsonNumber() + mod.getTextureNumber() + mod.getSoundNumber();
        float progress = 0;
        float increment = 100f/fileNumber;

        try {
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            //Add all files
            for(int i=0; i < mod.getTextureNumber(); i++){
                addFileToZip(zos, mod.getTexturePath(i));
                progress = incrementProgress(progress,increment);
            }
            for(int i=0;i < mod.getSoundNumber();i++){
                addFileToZip(zos,mod.getSoundPath(i));
                progress = incrementProgress(progress,increment);
            }
            for (int i=0; i < mod.getJsonNumber(); i++){
                addFileToZip(zos,mod.getJsonPath(i));
                progress = incrementProgress(progress,increment);
            }

            for(int i=0;i < mod.getOtherFileNumber();i++){
                //Remove signatures if needed

                if (!REMOVE_SIGNATURE_FILES) {
                    addFileToZip(zos, mod.getOtherFilePath(i));
                }else{
                    if (!mod.getOtherFilePath(i).contains(".RSA") && !mod.getOtherFilePath(i).contains(".MF") && !mod.getOtherFilePath(i).contains(".SF")){
                        addFileToZip(zos, mod.getOtherFilePath(i));
                    }else{
                        //else we don't add the file back but we still have to delete it.
                        FileManager.removeFile(mod.getOtherFilePath(i));
                    }
                }



                progress = incrementProgress(progress,increment);
            }
            for(int i=mod.getFolderNumber()-1;i >= 0;i--){
                FileManager.removeFile(mod.getFolderPath(i) + "/");
                progress = incrementProgress(progress,increment);
            }

            zos.close();
            fos.close();

            new File(TEMP_PATH).delete();

        }catch (IOException io){
            io.printStackTrace();
        }



    }

    private float incrementProgress(float currentProgress, float increment){
        currentProgress += increment;
        int intProgress = Math.round(currentProgress);
        publishProgress(intProgress);

        return currentProgress;
    }

    private void addFileToZip(ZipOutputStream zos, String filePath) throws IOException{
        if(!FileManager.fileExists(filePath)){
            return;
        }

        FileInputStream fis = new FileInputStream(filePath);
        byte[] BUFFER = new byte[1024];
        zos.putNextEntry(new ZipEntry(filePath.replace(TEMP_PATH,"") ));

        int length;
        while ((length = fis.read(BUFFER)) >= 0) {
            zos.write(BUFFER, 0, length);
        }
        zos.closeEntry();
        fis.close();
        FileManager.removeFile(filePath);

    }

    @Override
    protected void onProgressUpdate(Object... argument) {
        activityWeakReference.get().setCurrentTaskProgress((int)argument[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //Go for the next
        MainActivity activity = activityWeakReference.get();

        activity.setTotalTaskProgress(7);
        activity.setModDone();
        activityWeakReference.get().setCurrentTaskTextView(activityWeakReference.get().getResources().getString(R.string.process_status_none));

        activity.modStack.remove(0);
        activity.launchOptimization();

    }
}
