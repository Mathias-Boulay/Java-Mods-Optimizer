package com.spse.javamodsoptimiser.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;
import com.spse.javamodsoptimiser.R;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.WeakReference;

import static com.spse.javamodsoptimiser.MainActivity.TEMP_PATH;

public class FileParser extends AsyncTask<Void, Object, Void> {

    WeakReference<MainActivity> activityWeakReference;
    MinecraftMod mod;

    public FileParser(MainActivity activity){
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected void onPreExecute() {
        activityWeakReference.get().setCurrentTaskTextView(activityWeakReference.get().getResources().getString(R.string.process_status_parsing));
    }

    FileFilter numberFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                mod.folderNumber++;
                return true;
            }
            if (pathname.toString().contains(".png")) {
                mod.textureNumber++;
                return false;
            }
            if (pathname.toString().contains(".ogg")) {
                mod.soundNumber++;
                return false;
            }
            if (pathname.toString().contains(".json")) {
                mod.jsonNumber++;
                return false;
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
                return false;
            }
            if (pathname.toString().contains(".ogg")) {
                mod.soundPath[mod.soundIndex] = pathname.getAbsolutePath();
                mod.soundIndex++;
                return false;
            }
            if (pathname.toString().contains(".json")) {
                mod.jsonPath[mod.jsonIndex] = pathname.getAbsolutePath();
                mod.jsonIndex++;
                return false;
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
    public Void doInBackground(Void[] voids) {
        //Parse arguments
        mod = activityWeakReference.get().modStack.get(0);

        //First count how many textures and sounds we have
        walk(TEMP_PATH, numberFilter);

        publishProgress(50);

        //Then assign arrays to store both textures and sounds paths
        mod.texturePath = new String[mod.textureNumber];
        mod.soundPath = new String[mod.soundNumber];
        mod.jsonPath = new String[mod.jsonNumber];
        mod.otherFilePath = new String[mod.otherFileNumber];
        mod.folderPath = new String[mod.folderNumber];

        //Then store those files path
        walk(TEMP_PATH, fileFilter);

        publishProgress(100);

        return null;
    }

    @Override
    protected void onProgressUpdate(Object... argument) {
        activityWeakReference.get().setCurrentTaskProgress((int)argument[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        activityWeakReference.get().launchAsyncTask(4);
    }
}