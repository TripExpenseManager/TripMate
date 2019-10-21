package android.app.models;

/**
 * Created by vinee_000 on 05-07-2017.
 */

public class TodoModel {
    private String name="";
    private boolean isCompleted;
    private  int note_ContentType; // 1 - Notes 2 - CheckList

    public int getNote_ContentType() {
        return note_ContentType;
    }

    public TodoModel() {
    }

    public TodoModel(String name, boolean isCompleted) {
        this.name = name;
        this.isCompleted = isCompleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
