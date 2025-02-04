package com.marzane.notepad;

public enum ActionValues {

    NOACTION(0),
    OPEN_FILE_PROVIDER(1),
    SAVE_FILE_AS(2) ,
    NEW_FILE(3),
    CLOSE_APP(4),
    CLOSE_ACTIVITY(5),
    CLEAR_LIST(6),
    CLOSE_EDITOR(7),
    REMOVE_FROM_LIST(8),
    DELETE_FILE(9),
    RENAME_FILE(10);

    private int id;

    private ActionValues(int id){
        this.id = id;
    }

    public int getID(){
        return id;
    }

}
