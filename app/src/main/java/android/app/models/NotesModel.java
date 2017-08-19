package android.app.models;

/**
 * Created by vinee_000 on 21-06-2017.
 */

public class NotesModel {
    private String note_Title,note_Date,note_Body,note_Id,note_TripId,note_ContentStatus;
    private  int note_ContentType; // 1 - Notes 2 - CheckList

    public int getNote_ContentType() {
        return note_ContentType;
    }

    public String getNote_Body() {
        return note_Body;
    }

    public String getNote_Date() {
        return note_Date;
    }

    public String getNote_Id() {
        return note_Id;
    }

    public String getNote_Title() {
        return note_Title;
    }

    public String getNote_TripId() {
        return note_TripId;
    }

    public void setNote_Body(String note_Body) {
        this.note_Body = note_Body;
    }

    public void setNote_ContentType(int note_ContentType) {
        this.note_ContentType = note_ContentType;
    }

    public void setNote_Date(String note_Date) {
        this.note_Date = note_Date;
    }

    public void setNote_Id(String note_Id) {
        this.note_Id = note_Id;
    }

    public void setNote_Title(String note_Title) {
        this.note_Title = note_Title;
    }

    public void setNote_TripId(String note_TripId) {
        this.note_TripId = note_TripId;
    }

    public String getNote_ContentStatus() {
        return note_ContentStatus;
    }

    public void setNote_ContentStatus(String note_ContentStatus) {
        this.note_ContentStatus = note_ContentStatus;
    }
}
