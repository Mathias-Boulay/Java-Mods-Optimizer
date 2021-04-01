package com.spse.javamodsoptimiser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.codekidlabs.storagechooser.StorageChooser;
import com.spse.javamodsoptimiser.asynctask.FileCopier;
import com.spse.javamodsoptimiser.asynctask.FileParser;
import com.spse.javamodsoptimiser.asynctask.FileUnzipper;
import com.spse.javamodsoptimiser.asynctask.FileZipper;
import com.spse.javamodsoptimiser.asynctask.JsonMinifier;
import com.spse.javamodsoptimiser.asynctask.SoundOptimizer;
import com.spse.javamodsoptimiser.asynctask.Task;
import com.spse.javamodsoptimiser.asynctask.TextureOptimizer;

import java.io.File;
import java.util.ArrayList;
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
    public Boolean MULTIPLE_MODS_CHECKED;
    public int modIndex = 0;
    public ArrayList<String> modList;


    private MinecraftMod mod;
    public ProgressBar copyProgressBar;
    public ProgressBar unzipProgressBar;
    public ProgressBar parsingProgressBar;
    public ProgressBar textureProgressBar;
    public ProgressBar soundProgressBar;
    public ProgressBar jsonProgressBar;
    public ProgressBar zipProgressBar;

    private TextView modInfoName;
    private TextView modInfoTextureNumber;
    private TextView modInfoSoundNumber;

    private CheckBox deleteOriginalFile;
    private CheckBox removeSignatures;
    private CheckBox reducedQuality;

    private PowerManager.WakeLock wakelock;
    public ImageButton filepickerBtn;


    public static final int FILEPICKER_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_processing_layout);


        if(fileExists(TEMP_PATH)){
            File[] root = new File(TEMP_PATH).listFiles();
            if (root != null && root.length > 0) {
                //Then it means the previous work has been interrupted somehow.
                //We have to notify the user about this issue
                AlertDialog.Builder tempFilesFound = new AlertDialog.Builder(MAIN_ACTIVITY);
                tempFilesFound.setTitle(R.string.dialog_temp_files_found_title);
                tempFilesFound.setMessage(R.string.dialog_temp_files_found_message);

                tempFilesFound.setNeutralButton(R.string.dialog_temp_files_found_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //We start cleaning the leftovers
                        FileManager.removeLeftOvers();
                    }
                });

                tempFilesFound.create().show();
            }
        }

        //Create folders used by the app:
        createFolder(FOLDER_PATH);
        createFolder(TEMP_PATH);
        createFolder(OUT_PATH);

        copyProgressBar = findViewById(R.id.progressBarCopying);
        unzipProgressBar = findViewById(R.id.progressBarUnzipping);
        parsingProgressBar = findViewById(R.id.progressBarParsing);
        textureProgressBar = findViewById(R.id.progressBarTexture);
        soundProgressBar = findViewById(R.id.progressBarSound);
        jsonProgressBar = findViewById(R.id.progressBarJson);
        zipProgressBar = findViewById(R.id.progressBarZipping);

        modInfoName = findViewById(R.id.modInfoNameData);
        modInfoTextureNumber = findViewById(R.id.modInfoTextureNumberData);
        modInfoSoundNumber = findViewById(R.id.modInfoSoundNumberData);

        deleteOriginalFile = findViewById(R.id.optionDeleteOriginal);
        removeSignatures = findViewById(R.id.optionRemoveSignatures);
        reducedQuality = findViewById(R.id.optionReducedQuality);

        //Pre-activate recommended options
        removeSignatures.setChecked(true);
        reducedQuality.setChecked(true);






        filepickerBtn = findViewById(R.id.filePicker);
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
                .setDialogTitle("Choose a mod (.zip/.jar)")

                .build();

        // 2. Retrieve the selected path by the user and show in a toast !
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                //We aren't optimizing multiple mods.
                MULTIPLE_MODS_CHECKED = false;

                //Activate the CPU wakelock
                setWakelockState(true);

                init(path);


            }
        });

        chooser.setOnMultipleSelectListener(new StorageChooser.OnMultipleSelectListener(){
            @Override
            public void onDone(ArrayList<String> selectedFilePaths) {
                //We are optimizing multiple mods
                MULTIPLE_MODS_CHECKED = true;

                //Activate the CPU wakelock
                setWakelockState(true);

                modList = new ArrayList<String>(selectedFilePaths.size());

                for(String mods : selectedFilePaths){
                    modList.add(mods);
                }

                String path = modList.get(modIndex);
                modIndex++;
                init(path);


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
                //Json minify step
                new JsonMinifier().execute(new Task(jsonProgressBar, mod, MAIN_ACTIVITY));
                break;

            case 7:
                //Repacking step
                new FileZipper().execute(new Task(zipProgressBar, mod, MAIN_ACTIVITY));
                break;

            default:
                Toast.makeText(MainActivity.this, "The async task launcher tried to launch a non-existing task ! (".concat(Integer.toString(step)).concat(")"),Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void setInfoTextureNumber(int number){
        modInfoTextureNumber.setText(Integer.toString(number));
    }
    public void setInfoSoundNumber(int number){
        modInfoSoundNumber.setText(Integer.toString(number));
    }
    public boolean isQualityReduced(){
        return reducedQuality.isChecked();
    }
    public boolean haveSignaturesRemoved(){
        return removeSignatures.isChecked();
    }
    public boolean haveOriginalDeleted(){
        return deleteOriginalFile.isChecked();
    }

    public void setWakelockState(boolean awake){
        if(awake){
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"Minecraft Java Mods Optimizer::ProcessingWakelock");
            wakelock.acquire();
        }else{
            wakelock.release();
        }
    }

    private boolean isPathIllegal(String absolutePath){
        if(absolutePath.contains(OUT_PATH)){
            AlertDialog.Builder illegalPathDialog = new AlertDialog.Builder(MAIN_ACTIVITY);
            illegalPathDialog.setTitle(R.string.dialog_illegal_path_title);
            illegalPathDialog.setMessage(R.string.dialog_illegal_path_message);


            illegalPathDialog.setNeutralButton(R.string.dialog_illegal_path_button, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            illegalPathDialog.create().show();
            return true;
        }
        return false;
    }

    public void init(String absolutePath){

        if(isPathIllegal(absolutePath)){
            return;
        }

        //Make the button un-clickable for the rest of the process.
        filepickerBtn.setClickable(false);

        //Reset the progressBar progression:
        copyProgressBar.setProgress(0, true);
        unzipProgressBar.setProgress(0, true);
        parsingProgressBar.setProgress(0, true);
        textureProgressBar.setProgress(0, true);
        jsonProgressBar.setProgress(0, true);
        soundProgressBar.setProgress(0, true);
        zipProgressBar.setProgress(0, true);

        //Create the mod
        mod = new MinecraftMod(absolutePath);

        //Actualise info
        modInfoName.setText(mod.getFullName());
        modInfoTextureNumber.setText(R.string.mod_info_unknown);
        modInfoSoundNumber.setText(R.string.mod_info_unknown);

        //Check if the same mod doesn't exist in output files
        if(fileExists(OUT_PATH + mod.getFullName())) {
            removeFile(OUT_PATH + mod.getFullName());
        }


        //Launch the first task, each task will launch the next one when it finishes
        launchAsyncTask(1);

        Toast.makeText(MainActivity.this,"Launching optimization process !\n" + mod.getName(),Toast.LENGTH_LONG).show();
    }

}
