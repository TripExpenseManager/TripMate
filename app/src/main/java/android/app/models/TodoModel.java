package android.app.models;

/**
 * Created by vinee_000 on 05-07-2017.
 */

public class TodoModel {
    private String name;
    private boolean isStarred;
    private boolean isCompleted;

    public TodoModel() {
    }

    public TodoModel(String name, boolean isStarred, boolean isCompleted) {
        this.name = name;
        this.isStarred = isStarred;
        this.isCompleted = isCompleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
