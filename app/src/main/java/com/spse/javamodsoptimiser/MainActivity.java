package com.spse.javamodsoptimiser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;

import com.codekidlabs.storagechooser.StorageChooser;
import com.spse.javamodsoptimiser.asynctask.FileCopier;
import com.spse.javamodsoptimiser.asynctask.FileParser;
import com.spse.javamodsoptimiser.asynctask.FileUnzipper;
import com.spse.javamodsoptimiser.asynctask.FileZipper;
import com.spse.javamodsoptimiser.asynctask.JsonMinifier;
import com.spse.javamodsoptimiser.asynctask.SoundOptimizer;
import com.spse.javamodsoptimiser.asynctask.TextureOptimizer;

import java.io.File;
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

    public MinecraftMod mod;

    private ProgressBar stepProgressBar;
    private TextView stepTitle;
    private TextView stepText;

    public ImageButton settingsSwitch;
    private boolean settingsShown = false;

    private CheckBox replaceOriginalFile;
    private CheckBox removeSignatures;
    private CheckBox reducedQuality;

    private final TextView[] userLogs = new TextView[5];

    private final ConstraintSet layoutSettingsHidden = new ConstraintSet();
    private final ConstraintSet layoutSettingShown = new ConstraintSet();

    private PowerManager.WakeLock wakelock;
    public ImageButton filepickerBtn;


    public static final int FILEPICKER_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        if(fileExists(TEMP_PATH)){
            File[] root = new File(TEMP_PATH).listFiles();
            if (root.length > 0) {
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
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
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


        stepProgressBar = findViewById(R.id.progressBar);
        stepTitle = findViewById(R.id.textViewStepTitle);
        stepText = findViewById(R.id.textViewStep);

        replaceOriginalFile = findViewById(R.id.checkBoxRemoveFile);
        removeSignatures = findViewById(R.id.checkBoxRemoveSignatures);
        reducedQuality = findViewById(R.id.checkBoxReducedQuality);



        //Pre-activate recommended options
        replaceOriginalFile.setChecked(false);
        removeSignatures.setChecked(false);
        reducedQuality.setChecked(true);

        userLogs[0] = findViewById(R.id.textViewLog1);
        userLogs[1] = findViewById(R.id.textViewLog2);
        userLogs[2] = findViewById(R.id.textViewLog3);
        userLogs[3] = findViewById(R.id.textViewLog4);
        userLogs[4] = findViewById(R.id.textViewLog5);

        setLogVisibility(View.INVISIBLE);




        filepickerBtn = findViewById(R.id.imageViewAddMod);
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


        layoutSettingsHidden.clone((ConstraintLayout) findViewById(R.id.MainActivity));
        layoutSettingShown.clone(this, R.layout.main_activity_settings_shown);

        settingsSwitch = findViewById(R.id.imageViewSettingsSwitch);
        settingsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition((ConstraintLayout) findViewById(R.id.MainActivity));
                ConstraintSet constrain;
                if(settingsShown){
                    constrain = layoutSettingsHidden;
                    setAddMinecraftModClickable(true);
                    setSettingsClickable(false);
                }else{
                    constrain = layoutSettingShown;
                    setAddMinecraftModClickable(false);
                    setSettingsClickable(true);
                }

                constrain.applyTo((ConstraintLayout) findViewById(R.id.MainActivity));
                settingsShown = !settingsShown;
                setLogVisibility(View.INVISIBLE);
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
                //Activate the CPU wakelock
                setWakelockState(true);

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        //Mark the progress
        setStepProgress(step);

        switch (step){
            case 1:
                //File copy step
                new FileCopier().execute(MAIN_ACTIVITY);
                break;

            case 2:
                //File unzip step
                new FileUnzipper().execute(MAIN_ACTIVITY);
                break;

            case 3:
                //File parsing step
                new FileParser().execute(MAIN_ACTIVITY);
                break;

            case 4:
                //Texture optimization step
                new TextureOptimizer().execute(MAIN_ACTIVITY);
                break;

            case 5:
                //Sound optimization step
                new SoundOptimizer().execute(MAIN_ACTIVITY);
                break;

            case 6:
                //Json minify step
                new JsonMinifier().execute(MAIN_ACTIVITY);
                break;

            case 7:
                //Repacking step
                new FileZipper().execute(MAIN_ACTIVITY);
                break;

            default:
                Toast.makeText(MainActivity.this, "The async task launcher tried to launch a non-existing task ! (".concat(Integer.toString(step)).concat(")"),Toast.LENGTH_LONG).show();
                break;
        }
    }

    public boolean isQualityReduced(){
        return reducedQuality.isChecked();
    }
    public boolean haveSignaturesRemoved(){
        return removeSignatures.isChecked();
    }
    public boolean haveOriginalDeleted(){
        return replaceOriginalFile.isChecked();
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
        settingsSwitch.setClickable(false);


        //Create the mod
        mod = new MinecraftMod(absolutePath);


        //Check if the same mod doesn't exist in output files
        if(fileExists(OUT_PATH + mod.getFullName())) {
            removeFile(OUT_PATH + mod.getFullName());
        }

        setLogVisibility(View.VISIBLE);
        filepickerBtn.setVisibility(View.INVISIBLE);

        //Launch the first task, each task will launch the next one when it finishes
        launchAsyncTask(1);

        Toast.makeText(MainActivity.this,"Launching optimization process !\n" + mod.getName(),Toast.LENGTH_LONG).show();
    }

    public void postOptimize(){
        filepickerBtn.setClickable(true);
        filepickerBtn.setVisibility(View.VISIBLE);
        setLogVisibility(View.INVISIBLE);
        settingsSwitch.setClickable(true);

        setStepProgress(0);

        Toast.makeText(this,getString(R.string.mod_is_optimized) + mod.getFullName(), Toast.LENGTH_LONG).show();

        //Deactivate the CPU wakelock
        setWakelockState(false);
    }

    @SuppressLint("DefaultLocale")
    public void setStepProgress(int step){
        //Changes the circular progressBar, with its text inside it.
        stepTitle.setText(String.format("%s %d/7", getString(R.string.step), step));
        stepProgressBar.setProgress((int) Math.max((100f/7f)*step, 0));

        switch (step){
            case 0:
                stepText.setText(getString(R.string.step_user));
                break;

            case 1:
                stepText.setText(getString(R.string.step_copying));
                break;

            case 2:
                stepText.setText(getString(R.string.step_unzipping));
                break;

            case 3:
                stepText.setText(getString(R.string.step_parsing));
                break;

            case 4:
                stepText.setText(getString(R.string.step_texture_optimization));
                break;

            case 5:
                stepText.setText(getString(R.string.step_sound_optimization));
                break;

            case 6:
                stepText.setText(getString(R.string.step_trimming));
                break;

            case 7:
                stepText.setText(getString(R.string.step_repacking));
                break;

            default:
                Log.d("SET STEP", "Wrong STEP : " + step);
                break;

        }
    }

    private void setSettingsClickable(boolean state){
        replaceOriginalFile.setClickable(state);
        removeSignatures.setClickable(state);
        reducedQuality.setClickable(state);
    }

    private void setAddMinecraftModClickable(boolean state){
        filepickerBtn.setClickable(state);
    }

    public void addUserLog(String log){
        for(int i = 1; i < userLogs.length; i++){
            userLogs[i-1].setText(userLogs[i].getText().toString());
        }
        userLogs[userLogs.length-1].setText(log);
    }

    public void addUserLog(int constLog){
        for(int i = 1; i < userLogs.length; i++){
            userLogs[i-1].setText(userLogs[i].getText().toString());
        }
        userLogs[userLogs.length-1].setText(getString(constLog));
    }

    public void addUserLog(int constLog, String modularLog){
        for(int i = 1; i < userLogs.length; i++){
            userLogs[i-1].setText(userLogs[i].getText().toString());
        }
        userLogs[userLogs.length-1].setText(String.format("%s%s", getString(constLog), modularLog));
    }

    public void editLastUserLog(int constLog, String modularLog){
        userLogs[userLogs.length-1].setText(String.format("%s%s", getString(constLog), modularLog));
    }

    public void setLogVisibility(int state){
        for (TextView userLog : userLogs) {
            userLog.setVisibility(state);
        }
    }


}
