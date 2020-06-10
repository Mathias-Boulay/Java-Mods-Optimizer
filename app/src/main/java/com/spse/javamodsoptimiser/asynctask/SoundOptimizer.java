package com.spse.javamodsoptimiser;

import android.os.AsyncTask;

import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.IOException;

import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.renameFile;

public class SoundOptimizer extends AsyncTask<Object, Void, Void> {

    @Override
    public Void doInBackground(Object ... argument){
        //Parse arguments
        String[] soundPaths = (String[]) argument[0];
        int soundNumber = (int) argument[1];


        for(int i=0; i < soundNumber; i++) {
            String command = "-y -i '" + soundPaths[i] + "' -c:a libvorbis -b:a 48k -ac 1 -ar 26000 '" + soundPaths[i] + "-min.ogg'";

            int rc = FFmpeg.execute(command);

            if (fileExists(soundPaths[i].concat("-min.ogg"))) {
                removeFile(soundPaths[i]);
                try {
                    renameFile(soundPaths[i].concat("-min.ogg"), soundPaths[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


}
