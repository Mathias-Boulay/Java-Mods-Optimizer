package com.spse.javamodsoptimiser.setting;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class Setting {
    public static SharedPreferences DEFAULT_PREF;
    public static String APPLICATION_PATH;
    public static String OUTPUT_PATH;
    public static String TEMP_PATH;

    public static boolean REMOVE_SIGNATURE_FILES = false;

    public static boolean SKIP_TEXTURE_OPTIMIZATION = false;
    public static boolean SKIP_SOUND_OPTIMIZATION = false;
    public static boolean SKIP_JSON_OPTIMIZATION = false;

    public static String TEXTURE_QUALITY = "Medium";
    public static String SOUND_QUALITY = "High";
    public static boolean LENIENT_TEXTURE_QUALITY_CHECK = false;


    public static void loadSettings(){
        REMOVE_SIGNATURE_FILES = DEFAULT_PREF.getBoolean("remove_signature_files", false);

        SKIP_TEXTURE_OPTIMIZATION = DEFAULT_PREF.getBoolean("skip_texture", false);
        SKIP_SOUND_OPTIMIZATION = DEFAULT_PREF.getBoolean("skip_sound", false);
        SKIP_JSON_OPTIMIZATION = DEFAULT_PREF.getBoolean("skip_json", false);

        TEXTURE_QUALITY = DEFAULT_PREF.getString("texture_quality", "Medium");
        SOUND_QUALITY = DEFAULT_PREF.getString("sound_quality", "High");
        LENIENT_TEXTURE_QUALITY_CHECK = DEFAULT_PREF.getBoolean("texture_quality_check", false);
    }

    public static void initializeSettings(Context ctx){
        DEFAULT_PREF = PreferenceManager.getDefaultSharedPreferences(ctx);
        APPLICATION_PATH = ctx.getExternalFilesDir(null).getAbsolutePath();
        OUTPUT_PATH = APPLICATION_PATH + "/OUTPUT/";
        TEMP_PATH = APPLICATION_PATH + "/TEMP/";

        loadSettings();
    }


}
