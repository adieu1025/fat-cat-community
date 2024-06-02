package com.nowcoder.community.entity;

import java.math.BigDecimal;

//主观题试卷
public class SubjectivePaper {

    private int id;
    private String name;
    private int essayNum; //主观题数量
    private int articleNum; //材料数量
    private int categoryId;
    private int duration;// 试卷考试时长
    private BigDecimal totalScore;
    private int isDelete;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEssayNum() {
        return essayNum;
    }

    public void setEssayNum(int essayNum) {
        this.essayNum = essayNum;
    }

    public int getArticleNum() {
        return articleNum;
    }

    public void setArticleNum(int articleNum) {
        this.articleNum = articleNum;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    @Override
    public String toString() {
        return "SubjectivePaper{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", essayNum=" + essayNum +
                ", articleNum=" + articleNum +
                ", categoryId=" + categoryId +
                ", isDelete=" + isDelete +
                ", duration=" + duration +
                ", totalScore=" + totalScore +
                '}';
    }
}
