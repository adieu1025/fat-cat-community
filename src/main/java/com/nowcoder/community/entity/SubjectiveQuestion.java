package com.nowcoder.community.entity;

import java.math.BigDecimal;

//主观题--面试--结构化、无领导/申论小题
public class SubjectiveQuestion {

    private int id;
    private String stem;//题干
    private String imageUrl;
    private String article;//材料
    private String answer; //参考答案
    private BigDecimal score;
    private int categoryId;
    private int subjectId;//科目id
    private int isDelete;//0正常1删除（拉黑），默认0

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
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

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return "SubjectiveQuestion{" +
                "id=" + id +
                ", stem='" + stem + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", article='" + article + '\'' +
                ", answer='" + answer + '\'' +
                ", score=" + score +
                ", categoryId=" + categoryId +
                ", subjectId=" + subjectId +
                ", isDelete=" + isDelete +
                '}';
    }
}
