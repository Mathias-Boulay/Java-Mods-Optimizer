package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;

import java.io.File;
import java.io.FileFilter;

import static com.spse.javamodsoptimiser.MainActivity.TEMP_PATH;

public class FileParser extends AsyncTask<Task, Object, MainActivity> {

    MinecraftMod mod;

    FileFilter numberFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                mod.folderNumber++;
                return true;
            }
            if (pathname.toString().contains(".png")) {
                mod.textureNumber++;
                return true;
            }
            if (pathname.toString().contains(".ogg")) {
                mod.soundNumber++;
                return true;
            }
            mod.otherFileNumber++;
            return false;
        }
    };

    FileFilter fileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                mod.folderPath[mod.folderIndex] = pathname.getAbsolutePath();
                mod.folderIndex++;
                return true;
            }
            if (pathname.toString().contains(".png")) {
                mod.texturePath[mod.textureIndex] = pathname.getAbsolutePath();
                mod.textureIndex++;
                return true;
            }
            if (pathname.toString().contains(".ogg")) {
                mod.soundPath[mod.soundIndex] = pathname.getAbsolutePath();
                mod.soundIndex++;
                return true;
            }
            mod.otherFilePath[mod.otherFileIndex] = pathname.getAbsolutePath();
            mod.otherFileIndex++;
            return false;
        }
    };

    private void walk(String path, FileFilter filter) {

        File root = new File(path);
        File[] list = root.listFiles(filter);

        if (list == null) return;

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath(), filter);
            }
        }
    }

    @Override
    public MainActivity doInBackground(Task[] task) {
        //Parse arguments
        mod = task[0].getMod();
        ProgressBar progressBar = task[0].getProgressBar();


        //First count how many textures and sounds we have
        walk(TEMP_PATH, numberFilter);

        publishProgress(progressBar, 50);

        //Then assign arrays to store both textures and sounds paths
        mod.texturePath = new String[mod.textureNumber];
        mod.soundPath = new String[mod.soundNumber];
        mod.otherFilePath = new String[mod.otherFileNumber];
        mod.folderPath = new String[mod.folderNumber];



        //Then store those files path
        walk(TEMP_PATH, fileFilter);

        publishProgress(progressBar, 100);

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
        activity.setInfoSoundNumber(mod.getSoundNumber());
        activity.setInfoTextureNumber(mod.getTextureNumber());

        activity.launchAsyncTask(4);
    }
}