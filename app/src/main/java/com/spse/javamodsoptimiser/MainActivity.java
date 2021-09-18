package com.spse.javamodsoptimiser;

import static com.spse.javamodsoptimiser.FileManager.createFolder;
import static com.spse.javamodsoptimiser.FileManager.fileExists;
import static com.spse.javamodsoptimiser.FileManager.getFileSize;
import static com.spse.javamodsoptimiser.FileManager.getUriData;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.setting.Setting.OUTPUT_PATH;
import static com.spse.javamodsoptimiser.setting.Setting.TEMP_PATH;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.spse.javamodsoptimiser.asynctask.FileCopier;
import com.spse.javamodsoptimiser.asynctask.FileParser;
import com.spse.javamodsoptimiser.asynctask.FileUnzipper;
import com.spse.javamodsoptimiser.asynctask.FileZipper;
import com.spse.javamodsoptimiser.asynctask.JsonMinifier;
import com.spse.javamodsoptimiser.asynctask.SoundOptimizer;
import com.spse.javamodsoptimiser.asynctask.TextureOptimizer;
import com.spse.javamodsoptimiser.setting.Setting;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public static int INTENT_OPEN_MOD_FILE = 1;

    private Dialog settingDialog;

    private PowerManager.WakeLock wakelock;
    private Button filePickerBtn;

    private TextView currentTaskTextView;
    private ProgressBar currentTaskProgressBar;
    private ProgressBar totalTaskProgressBar;
    private ProgressBar modItemProgressBar;

    public ArrayList<MinecraftMod> modStack = new ArrayList<>(1);
    private LinearLayout modList;
    private int modListIndex = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        Setting.initializeSettings(this);

        //Create folders used by the app:
        createFolder(TEMP_PATH);
        createFolder(OUTPUT_PATH);

        settingDialog =  new Dialog(this);
        settingDialog.setContentView(R.layout.setting_layout);

        currentTaskTextView = findViewById(R.id.currentTaskTextView2);
        currentTaskProgressBar = findViewById(R.id.currentTaskProgressBar);
        totalTaskProgressBar = findViewById(R.id.totalTaskProgressBar);
        modList = findViewById(R.id.listView);
        filePickerBtn = findViewById(R.id.addModButton);

        removeLeftOvers();

        filePickerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            String[] mimeTypes = {  MimeTypeMap.getSingleton().getMimeTypeFromExtension("jar"),
                                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("zip")};
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, INTENT_OPEN_MOD_FILE);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK) return;
        if(requestCode == INTENT_OPEN_MOD_FILE){
            if(data == null) return;
            try {
                for(Uri uri : getUriData(data)) addMod(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void launchAsyncTask(int step){
        switch (step){
            case 1: new FileCopier(this).execute(); break;
            case 2: new FileUnzipper(this).execute(); break;
            case 3: new FileParser(this).execute(); break;
            case 4: new TextureOptimizer(this).execute(); break;
            case 5: new SoundOptimizer(this).execute(); break;
            case 6: new JsonMinifier(this).execute(); break;
            case 7: new FileZipper(this).execute(); break;

            default:
                Toast.makeText(MainActivity.this,"The async task launcher tried to launch a non-existing task ! (".concat(Integer.toString(step)).concat(")"),Toast.LENGTH_LONG).show();
                return;
        }
        setTotalTaskProgress(Math.max(0, step-1));
    }

    public void setWakelockState(boolean awake){
        if(awake){
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"Minecraft Java Mods Optimizer::ProcessingWakelock");
            wakelock.acquire(30*60*1000L /*30 minutes*/);
        }else{
            if(wakelock == null) return;
            wakelock.release();
        }
    }

    public void showSettings(View v){
        settingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        settingDialog.show();
    }

    /**
     * Remove the left overs from the temp path
     */
    private void removeLeftOvers(){
        new Thread(() -> {
            FileManager.removeEverything(TEMP_PATH);
            FileManager.removeEverything(OUTPUT_PATH);
        }).start();
    }

    public void launchOptimization(){
        if(modStack.size() == 0){
            ((Button)findViewById(R.id.optimizeButton)).setClickable(true);
            setWakelockState(false);
            return;
        }

        ((Button)findViewById(R.id.optimizeButton)).setClickable(false);
        setWakelockState(true);

        //Check if the same mod doesn't exist in output files
        if(fileExists(OUTPUT_PATH + modStack.get(0).getFullName())) {
            removeFile(OUTPUT_PATH + modStack.get(0).getFullName());
        }

        //Reset progress bars
        currentTaskProgressBar.setProgress(0);
        totalTaskProgressBar.setProgress(0);

        modListIndex += 1; //Index of the list to actualise,
        modItemProgressBar = modList.getChildAt(modListIndex).findViewById(R.id.modProgress);

        launchAsyncTask(1);
    }

    private void addMod(Uri uri) throws FileNotFoundException {
        //Initialise mod properties
        String modName = FileManager.getFileName(this, uri);
        String modExtension = modName.substring(modName.lastIndexOf('.'));
        if(modExtension.trim().isEmpty()) modExtension = ".jar";
        modName = modName.substring(0, modName.lastIndexOf('.'));
        long modSize = getFileSize(this, uri);

        //Add the mod to the stack
        MinecraftMod mod = new MinecraftMod(modName, modExtension, modSize, getContentResolver().openInputStream(uri));
        modStack.add(mod);

        //And add it visually to the layout
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.mod_layout, modList, false );

        ((TextView) v.findViewById(R.id.modTitle)).setText(mod.getName());
        ((TextView) v.findViewById(R.id.modDetails)).setText(String.valueOf(mod.getFileSize()/1000000).concat(" MB"));
        ((ImageView) v.findViewById(R.id.modLogo)).setImageResource(mod.getExtension().endsWith(".zip") ? R.drawable.zip_icon : R.drawable.jar_icon);

        modList.addView(v);
    }

    /**
     * Set a mod as done with optimisation, allowing it to be shared.
     */
    public void setModDone(){
        View v = modList.getChildAt(modListIndex);
        final MinecraftMod modToShare = modStack.get(0);
        modItemProgressBar.setVisibility(View.GONE);
        ImageButton modItemShareButton = v.findViewById(R.id.shareModButton);
        modItemShareButton.setVisibility(View.VISIBLE);
        ((TextView) v.findViewById(R.id.modDetails)).append(" | " + new File(modToShare.getOutputPath()).length() / 1000000 + "MB");

        modItemShareButton.setOnClickListener(view -> {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider",
                    new File(modToShare.getOutputPath())));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setType("application/" + (modToShare.getExtension().endsWith(".zip") ? "zip" : "java-archive"));
            startActivity(shareIntent);
        });
    }

    public void setCurrentTaskProgress(int progress){
        currentTaskProgressBar.setProgress(progress, true);
    }

    public void setTotalTaskProgress(int progress){
        totalTaskProgressBar.setProgress(progress, true);
        modItemProgressBar.setProgress(progress, true);
    }

    public void setCurrentTaskTextView(String taskAtHand){
        currentTaskTextView.setText(taskAtHand);
    }

    public void launchOptimizationManually(View view) {
        launchOptimization();
    }

}
