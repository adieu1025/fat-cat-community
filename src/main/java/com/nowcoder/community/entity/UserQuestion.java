package com.nowcoder.community.entity;

//保存用户在某试卷中的某题的答题信息
public class UserQuestion {

    private int id;
    private int userPaperId;
    private int questionId;
    private String userAnswer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserPaperId() {
        return userPaperId;
    }

    public void setUserPaperId(int userPaperId) {
        this.userPaperId = userPaperId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }


    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    @Override
    public String toString() {
        return "UserQuestion{" +
                "id=" + id +
                ", userPaperId=" + userPaperId +
                ", questionId=" + questionId +
                ", userAnswer='" + userAnswer + '\'' +
                '}';
    }
}
