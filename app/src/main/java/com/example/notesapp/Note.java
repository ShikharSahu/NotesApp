package com.example.notesapp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Note implements Serializable {
    public static final int NO_PRIORITY = 0;
    public int priority;
    String title, description;
    Date timeCreated;
    boolean hasProgress, isDone;
    int tasksDone, maxTask;
    final public static int MAX_PRIORITY = 3, MID_PRIORITY = 2, MIN_PRIORITY = 1;

    public Note(String title, String description, boolean hasProgress, int tasksDone, int maxTask, int priority) {
        this.title = title;
        this.description = description;
        this.timeCreated = Calendar.getInstance().getTime();
        this.hasProgress = hasProgress;
        this.tasksDone = tasksDone;
        this.maxTask = maxTask;
        this.isDone = false;
        this.priority = priority;
    }
    public Note(String title, String description, boolean hasProgress, int tasksDone, int maxTask) {
        this.title = title;
        this.description = description;
        this.timeCreated = Calendar.getInstance().getTime();
        this.hasProgress = hasProgress;
        this.tasksDone = tasksDone;
        this.maxTask = maxTask;
        this.isDone = false;
        this.priority = 0;
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
