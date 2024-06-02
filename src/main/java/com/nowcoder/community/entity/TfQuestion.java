package com.nowcoder.community.entity;

import java.math.BigDecimal;

//判断题
public class TfQuestion {

    private int id;
    private String stem;//题干
    private String imageUrl;
    private int answer;// 1正确0错误
    private String analysis;
    private BigDecimal score;
    private int categoryId;
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

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
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

    @Override
    public String toString() {
        return "TfQuestion{" +
                "id=" + id +
                ", stem='" + stem + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", answer=" + answer +
                ", analysis='" + analysis + '\'' +
                ", score=" + score +
                ", categoryId=" + categoryId +
                ", isDelete=" + isDelete +
                '}';
    }
}
