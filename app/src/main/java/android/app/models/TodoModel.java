package android.app.models;

/**
 * Created by vinee_000 on 05-07-2017.
 */

public class TodoModel {
    private String name;
    private boolean isStarred;

    public TodoModel() {
    }

    public TodoModel(String name, boolean isStarred) {
        this.name = name;
        this.isStarred = isStarred;
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
}
