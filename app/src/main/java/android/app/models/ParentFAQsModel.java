package android.app.models;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sai Krishna on 9/6/2017.
 */

public class ParentFAQsModel implements Parent<String> {
    String que;

    List<String> answerList = new ArrayList<>();

    public String getQue() {
        return que;
    }

    public void setQue(String que) {
        this.que = que;
    }

    public List<String> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<String> answerList) {
        this.answerList = answerList;
    }

    @Override
    public List<String> getChildList() {
        return answerList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
