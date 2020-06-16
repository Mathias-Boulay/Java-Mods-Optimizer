package com.spse.javamodsoptimiser.asynctask;


import android.widget.ProgressBar;

import com.spse.javamodsoptimiser.MainActivity;
import com.spse.javamodsoptimiser.MinecraftMod;

public class Task {
    //A task is a progressBar with a set of arguments defined by ... whatever code instantiates it.
    //This set of argument is passed through the AsyncTaskManager to execute 6 async tasks

    private ProgressBar progressBar;
    private MinecraftMod mod;
    private MainActivity activity;
    public Task(ProgressBar progressBar, MinecraftMod mod, MainActivity activity){
        this.progressBar = progressBar;
        this.mod = mod;
        this.activity = activity;
    }

    public ProgressBar getProgressBar(){
        return progressBar;
    }

    public MinecraftMod getMod() {
        return mod;
    }
    public MainActivity getActivity(){return activity;}

}
