package com.spse.javamodsoptimiser;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
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
import com.spse.javamodsoptimiser.asynctask.TextureOptimizer;
import com.spse.javamodsoptimiser.setting.Setting;

import org.jetbrains.annotations.NotNull;

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
    public static final int FILEPICKER_PERMISSIONS = 1;

    private final MainActivity MAIN_ACTIVITY = this;

    private Dialog settingDialog;

    private PowerManager.WakeLock wakelock;
    private ImageButton filePickerBtn;

    private ProgressBar currentTaskProgressBar;
    private ProgressBar totalTaskProgressBar;
    private ProgressBar modItemProgressBar;

    public ArrayList<MinecraftMod> modStack = new ArrayList<>(1);
    private LinearLayout modList;
    private int modListIndex = -2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        Setting.initializeSettings(this);

        //Create folders used by the app:
        createFolder(FOLDER_PATH);
        createFolder(TEMP_PATH);
        createFolder(OUT_PATH);

        settingDialog =  new Dialog(this);
        settingDialog.setContentView(R.layout.setting_layout);

        currentTaskProgressBar = findViewById(R.id.currentTaskProgressBar);
        totalTaskProgressBar = findViewById(R.id.totalTaskProgressBar);
        modList = findViewById(R.id.listView);
        filePickerBtn = findViewById(R.id.addModButton);

        checkForLeftOvers();

        filePickerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String[] PERMISSIONS = {
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                };

                if(hasPermissions(MainActivity.this, PERMISSIONS)){
                    ShowFilePicker();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, FILEPICKER_PERMISSIONS);
                }
            }
        });

    }

    /**
     * Method that displays the filePicker of the StorageChooser.
     */
    public void ShowFilePicker(){
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
                .setDialogTitle("Choose some mods (.zip/.jar)")



                .build();

        // 2. Retrieve the selected path by the user and show in a toast !
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                addMod(path);
            }
        });

        chooser.setOnMultipleSelectListener(new StorageChooser.OnMultipleSelectListener(){
            @Override
            public void onDone(ArrayList<String> selectedFilePaths) {
                //We are optimizing multiple mods
                addMod(selectedFilePaths);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == FILEPICKER_PERMISSIONS) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission granted! Please click on pick a file once again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission denied to read your External storage :(", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public void launchAsyncTask(int step){
        switch (step){
            case 1:
                //File copy step
                new FileCopier(MAIN_ACTIVITY).execute();
                break;

            case 2:
                //File unzip step
                new FileUnzipper(MAIN_ACTIVITY).execute();
                break;

            case 3:
                //File parsing step
                new FileParser(MAIN_ACTIVITY).execute();
                break;

            case 4:
                //Texture optimization step
                new TextureOptimizer(MAIN_ACTIVITY).execute();
                break;

            case 5:
                //Sound optimization step
                new SoundOptimizer(MAIN_ACTIVITY).execute();
                break;

            case 6:
                //Json minify step
                new JsonMinifier(MAIN_ACTIVITY).execute();
                break;

            case 7:
                //Repacking step
                new FileZipper(MAIN_ACTIVITY).execute();
                break;

            default:
                Toast.makeText(MainActivity.this, "The async task launcher tried to launch a non-existing task ! (".concat(Integer.toString(step)).concat(")"),Toast.LENGTH_LONG).show();
                return;
        }
        setTotalTaskProgress(step);
    }



    public void setWakelockState(boolean awake){
        if(awake){
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"Minecraft Java Mods Optimizer::ProcessingWakelock");
            wakelock.acquire(30*60*1000L /*30 minutes*/);
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
                public void onClick(DialogInterface dialog, int which) {}
            });

            illegalPathDialog.create().show();
            return true;
        }
        return false;
    }

    public void showSettings(View v){
        settingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        settingDialog.show();
    }

    private void checkForLeftOvers(){
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
    }

    public void launchOptimization(){
        if(modStack.size() == 0){
            ((Button)findViewById(R.id.optimizeButton)).setClickable(true);
            return;
        }

        ((Button)findViewById(R.id.optimizeButton)).setClickable(false);
        setWakelockState(true);

        //Check if the same mod doesn't exist in output files
        if(fileExists(OUT_PATH + modStack.get(0).getFullName())) {
            removeFile(OUT_PATH + modStack.get(0).getFullName());
        }

        //Reset progress bars
        currentTaskProgressBar.setProgress(0);
        totalTaskProgressBar.setProgress(0);

        modListIndex += 2; //Index of the list to actualise, has to jump over the space
        modItemProgressBar = modList.getChildAt(modListIndex).findViewById(R.id.modProgress);

        launchAsyncTask(1);
    }

    private void addMod(String modPath){
        //Failsafes
        if(isPathIllegal(modPath)){
            throw new IllegalArgumentException(" The file is in an illegal path !");
        }

        MinecraftMod mod = new MinecraftMod(modPath);
        modStack.add(mod);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.mod_layout, null);

        ((TextView) v.findViewById(R.id.modTitle)).setText(mod.getName());
        ((TextView) v.findViewById(R.id.modDetails)).setText(String.valueOf(mod.getFileSize()/1024).concat(" KB"));
        ((ImageView) v.findViewById(R.id.modLogo)).setImageResource(mod.getExtension().equals(".zip") ? R.drawable.zip_icon : R.drawable.jar_icon);

        modList.addView(v);

        v = new Space(this);
        v.setMinimumHeight(34 /*Yep, this is a magic number*/);
        modList.addView(v);
    }

    private void addMod(ArrayList<String> modPath){
        for(String str : modPath){
            addMod(str);
        }
    }

    public void setCurrentTaskProgress(int progress){
        currentTaskProgressBar.setProgress(progress, true);
    }

    private void setTotalTaskProgress(int progress){
        totalTaskProgressBar.setProgress(progress, true);
        modItemProgressBar.setProgress(progress, true);
    }


    public void launchOptimizationManually(View view) {
        launchOptimization();
    }
}
