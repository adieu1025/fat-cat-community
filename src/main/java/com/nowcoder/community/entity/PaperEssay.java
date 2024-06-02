package com.nowcoder.community.entity;

//试卷-主观题
public class PaperEssay {

    private int id;
    private int paperId;
    private String essay; //题干
    private String answer; //参考答案
    private int essayOrder; //题目序号


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

    public String getEssay() {
        return essay;
    }

    public void setEssay(String essay) {
        this.essay = essay;
    }

    public int getEssayOrder() {
        return essayOrder;
    }

    public void setEssayOrder(int essayOrder) {
        this.essayOrder = essayOrder;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "PaperEssay{" +
                "id=" + id +
                ", paperId=" + paperId +
                ", essay='" + essay + '\'' +
                ", essayOrder=" + essayOrder +
                ", answer='" + answer + '\'' +
                '}';
    }

}
