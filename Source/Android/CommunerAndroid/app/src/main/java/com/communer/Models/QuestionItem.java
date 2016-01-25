package com.communer.Models;

public class QuestionItem {

    private String questionID;
    private String title;
    private String description;
    private boolean isPublic;

    public QuestionItem(String questionID, String title, String description, boolean isPublic) {
        this.questionID = questionID;
        this.title = title;
        this.description = description;
        this.isPublic = isPublic;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
