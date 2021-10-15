package com.example.notesapp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Note implements Serializable {
    String title, description;
    Date timeCreated;
    boolean hasProgress, isDone;
    int tasksDone, maxTask;

    public Note(String title, String description, boolean hasProgress, int tasksDone, int maxTask) {
        this.title = title;
        this.description = description;
        this.timeCreated = Calendar.getInstance().getTime();
        this.hasProgress = hasProgress;
        this.tasksDone = tasksDone;
        this.maxTask = maxTask;
        this.isDone = false;
    }


    public boolean incrementTask(){
        if (hasProgress && !isTaskCompleted()){
            tasksDone++;
            return true;
        }
        return false;
    }

    public boolean decrementTask(){
        if( !isTaskCompleted() && hasProgress && tasksDone>0 ){
            tasksDone--;
            return true;
        }
        return false;
    }
    public boolean isTaskCompleted(){
        return isDone || (hasProgress && tasksDone >= maxTask);
    }
}
