package com.nowcoder.community.entity;

import java.math.BigDecimal;
import java.util.Date;

//客观题试卷
public class ObjectivePaper {

    private int id;
    private String name;
    private int choiceNum;
    private int tfNum;
    private Date startTime;//用户开始答题时间
    private Date endTime;//用户结束答题时间
    private int duration;//试卷限制的答题时长（单位：分钟）
    private BigDecimal totalScore;//试卷总分
    private int categoryId;
    private int isDelete;
    //试卷类型，用以区分管理员/普通用户发布的试卷，防止在历年真题模块中将用户自己练习的试卷也展示出来，1为管理员创建的（公有）0私有，默认0
    private int type;

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

    public int getChoiceNum() {
        return choiceNum;
    }

    public void setChoiceNum(int choiceNum) {
        this.choiceNum = choiceNum;
    }

    public int getTfNum() {
        return tfNum;
    }

    public void setTfNum(int tfNum) {
        this.tfNum = tfNum;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ObjectivePaper{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", choiceNum=" + choiceNum +
                ", tfNum=" + tfNum +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                ", totalScore=" + totalScore +
                ", categoryId=" + categoryId +
                ", isDelete=" + isDelete +
                ", type=" + type +
                '}';
    }
}
