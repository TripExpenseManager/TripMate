package android.app.models;

/**
 * Created by vinee_000 on 21-06-2017.
 */

public class NotesModel {
    String title,date,body;

    public NotesModel() {
    }

    public NotesModel(String title, String date, String body) {
        this.title = title;
        this.date = date;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
