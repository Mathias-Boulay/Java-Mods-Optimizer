package com.spse.javamodsoptimiser.asynctask;


import android.widget.ProgressBar;

public class Task {
    //A task is a progressBar with a set of arguments defined by ... whatever code instantiates it.
    //This set of argument is passed through the AsyncTaskManager to execute 6 async tasks

    private ProgressBar progressBar;
    private Object[] argument;
    public Task(ProgressBar progressBar, Object[] argument){
        this.progressBar = progressBar;
        this.argument = argument;
    }

    public ProgressBar getProgressBar(){
        return progressBar;
    }

    public Object getArgument(int index) {
        return argument[index];
    }

    public Object getArguments() {
        return argument;
    }
}
