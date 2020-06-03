package com.spse.javamodsoptimiser;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.codekidlabs.storagechooser.StorageChooser;
import com.wrmndfzzy.pngquant.LibPngQuant;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.spse.javamodsoptimiser.FileManager.copyFile;
import static com.spse.javamodsoptimiser.FileManager.removeFile;
import static com.spse.javamodsoptimiser.FileManager.unzip;


public class MainActivity extends AppCompatActivity {
    String folderPath = Environment.getExternalStorageDirectory().toString().concat("/Mods Optimizer/");



    public static final int FILEPICKER_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Toast.makeText(MainActivity.this, "The selected path is : " + path, Toast.LENGTH_SHORT).show();
                int index = path.length()-1;
                while (!(path.charAt(index) == "/".charAt(0))){
                    index --;
                }
                //Once the index is found:
                String realPath = path.substring(0,index+1);
                String fileName = path.substring(index+1,path.length()-4);
                String fileExtension = path.substring(path.length()-4);

                Toast.makeText(MainActivity.this,realPath,Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this,fileName,Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this,fileExtension,Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this,folderPath.concat(fileName).concat(fileExtension),Toast.LENGTH_LONG).show();

                copyFile(realPath,fileName.concat(fileExtension),folderPath);

                try {
                    unzip(folderPath.concat(fileName).concat(fileExtension));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"bruh, something went wrong",Toast.LENGTH_LONG).show();
                }
                removeFile(folderPath,fileName.concat(fileExtension));

                MinecraftMod mod = new MinecraftMod(fileName,fileExtension);
                mod.parseUncompressedMod(folderPath);
                int temp = mod.getSoundNumber();

                Toast.makeText(MainActivity.this,String.valueOf(temp),Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this,String.valueOf(mod.getTextureNumber()),Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this,mod.getSoundPath(0),Toast.LENGTH_LONG).show();


                /*
                for(int i=0; i < mod.getSoundNumber(); i++){
                    String command = "-y -i '" + mod.getSoundPath(i) + "' -c:a libvorbis -b:a 48k -ac 1 -ar 26000 '" + mod.getSoundPath(i) + "-min.ogg'";
                    Toast.makeText(MainActivity.this,command,Toast.LENGTH_LONG).show();
                    int rc = FFmpeg.execute(command);
                    if (rc == RETURN_CODE_SUCCESS) {
                        Log.i(Config.TAG, "Command execution completed successfully.");
                    } else if (rc == RETURN_CODE_CANCEL) {
                        Log.i(Config.TAG, "Command execution cancelled by user.");
                    } else {
                        Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
                        Config.printLastCommandOutput(Log.INFO);
                    }
                    removeFile(mod.getSoundPath(i));
                    try {
                        renameFile(mod.getSoundPath(i).concat("-min.ogg"),mod.getSoundPath(i));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }*/ //FFMPEG STUFF

                for(int i=0; i < 1; i++){
                    File inputFile = new File(mod.getTexturePath(i));
                    File outputFile = new File(mod.getTexturePath(i).concat("-min.png"));
                    new LibPngQuant().pngQuantFile(inputFile,outputFile);
                }//PNGQUANT STUFF

                Toast.makeText(MainActivity.this,"SUCCESS !",Toast.LENGTH_LONG).show();






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



}