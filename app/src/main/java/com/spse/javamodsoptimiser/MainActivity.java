package com.spse.javamodsoptimiser;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.codekidlabs.storagechooser.StorageChooser;
import com.spse.javamodsoptimiser.asynctask.FileCopier;
import com.spse.javamodsoptimiser.asynctask.FileParser;
import com.spse.javamodsoptimiser.asynctask.FileUnzipper;
import com.spse.javamodsoptimiser.asynctask.FileZipper;
import com.spse.javamodsoptimiser.asynctask.SoundOptimizer;
import com.spse.javamodsoptimiser.asynctask.Task;
import com.spse.javamodsoptimiser.asynctask.TextureOptimizer;

import java.util.Arrays;
import java.util.List;

import static com.spse.javamodsoptimiser.FileManager.createFolder;
import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.removeFile;


public class MainActivity extends AppCompatActivity {
    public static final String FOLDER_PATH = Environment.getExternalStorageDirectory().toString().concat("/Mods Optimizer/");
    public static final String TEMP_PATH = FOLDER_PATH.concat("TMP/");
    public static final String OUT_PATH = FOLDER_PATH.concat("OUTPUT/");
    public final MainActivity MAIN_ACTIVITY = this;

    private MinecraftMod mod;
    public ProgressBar copyProgressBar;
    public ProgressBar unzipProgressBar;
    public ProgressBar parsingProgressBar;
    public ProgressBar textureProgressBar;
    public ProgressBar soundProgressBar;
    public ProgressBar zipProgressBar;


    public static final int FILEPICKER_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_processing_layout);

        //Create folders used by the app:
        createFolder(FOLDER_PATH);
        createFolder(TEMP_PATH);
        createFolder(OUT_PATH);

        copyProgressBar = findViewById(R.id.progressBarCopying);
        unzipProgressBar = findViewById(R.id.progressBarUnzipping);
        parsingProgressBar = findViewById(R.id.progressBarParsing);
        textureProgressBar = findViewById(R.id.progressBarTexture);
        soundProgressBar = findViewById(R.id.progressBarSound);
        zipProgressBar = findViewById(R.id.progressBarZipping);


        Button filepickerBtn = findViewById(R.id.filePicker);
        filepickerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
                String[] PERMISSIONS = {
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

                if(hasPermissions(MainActivity.this, PERMISSIONS)){
                    ShowFilepicker();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, FILEPICKER_PERMISSIONS);
                }
            }
        });
    }

    /**
     * Method that displays the filepicker of the StorageChooser.
     */
    public void ShowFilepicker(){
        //Prep: Create custom filter
        List<String> filters = Arrays.asList("zip","jar");

        // 1. Initialize dialog
        final StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(MainActivity.this)
                .withFragmentManager(getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .customFilter(filters)
                .disableMultiSelect()

                .build();

        // 2. Retrieve the selected path by the user and show in a toast !
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {

                //Create the mod
                mod = new MinecraftMod(path);

                //Check if the same mod doesn't exist in output files
                if(fileExists(OUT_PATH + mod.getFullName())) {
                    removeFile(OUT_PATH + mod.getFullName());
                }

                //Launch the first task, each task will launch the next one when it finishes
                launchAsyncTask(1);

                Toast.makeText(MainActivity.this,"Launching optimization process !",Toast.LENGTH_LONG).show();


            }
        });

        // 3. Display File Picker !
        chooser.show();
    }

    /**
     * Helper method that verifies whether the permissions of a given array are granted or not.
     *
     * @param context
     * @param permissions
     * @return {Boolean}
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Callback that handles the status of the permissions request.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FILEPICKER_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                            MainActivity.this,
                            "Permission granted! Please click on pick a file once again.",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Permission denied to read your External storage :(",
                            Toast.LENGTH_SHORT
                    ).show();
                }

                return;
            }
        }
    }


    public void launchAsyncTask(int step){
        switch (step){
            case 1:
                //File copy step
                new FileCopier().execute(new Task(copyProgressBar, mod, MAIN_ACTIVITY));
                break;

            case 2:
                //File unzip step
                new FileUnzipper().execute(new Task(unzipProgressBar,mod, MAIN_ACTIVITY));
                break;

            case 3:
                //File parsing step
                new FileParser().execute(new Task(parsingProgressBar, mod, MAIN_ACTIVITY));
                break;

            case 4:
                //Texture optimization step
                new TextureOptimizer().execute(new Task(textureProgressBar, mod, MAIN_ACTIVITY));
                break;

            case 5:
                //Sound optimization step
                new SoundOptimizer().execute(new Task(soundProgressBar, mod, MAIN_ACTIVITY));
                break;

            case 6:
                //Repacking step
                new FileZipper().execute(new Task(zipProgressBar, mod, MAIN_ACTIVITY));
                break;
        }

    }

}
