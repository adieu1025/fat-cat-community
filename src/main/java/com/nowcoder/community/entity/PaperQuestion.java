package com.nowcoder.community.entity;

//关联试卷与问题
public class PaperQuestion {

    private int id;
    private int paperId;
    private int questionId;

    public PaperQuestion() {
    }

    public PaperQuestion(int paperId, int questionId) {
        this.paperId = paperId;
        this.questionId = questionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPaperId() {
        return paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    @Override
    public String toString() {
        return "PaperQuestion{" +
                "id=" + id +
                ", paperId=" + paperId +
                ", questionId=" + questionId +
                '}';
    }
}
