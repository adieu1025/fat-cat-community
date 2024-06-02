package com.nowcoder.community.entity;

import java.math.BigDecimal;
import java.util.Date;

//保存用户_试卷信息,展示x用户完成x试卷的结果，包括时间、答题情况等
public class UserPaper {
    private int id;
    private int userId;
    private int paperId;
    private BigDecimal totalScore;
    private BigDecimal userScore;
    private int totalNum;
    private int rightNum;
    private Date createTime;
    private Date finishTime;
    private int userTime;
    private int isFinish;//是否已完成，1是0否，默认0

    public UserPaper() {
    }

    public UserPaper(int userId, int paperId) {
        this.userId = userId;
        this.paperId = paperId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPaperId() {
        return paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public BigDecimal getUserScore() {
        return userScore;
    }

    public void setUserScore(BigDecimal userScore) {
        this.userScore = userScore;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getRightNum() {
        return rightNum;
    }

    public void setRightNum(int rightNum) {
        this.rightNum = rightNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public int getUserTime() {
        return userTime;
    }

    public void setUserTime(int userTime) {
        this.userTime = userTime;
    }

    public int getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(int isFinish) {
        this.isFinish = isFinish;
    }

    @Override
    public String toString() {
        return "UserPaper{" +
                "id=" + id +
                ", userId=" + userId +
                ", paperId=" + paperId +
                ", totalScore=" + totalScore +
                ", userScore=" + userScore +
                ", totalNum=" + totalNum +
                ", rightNum=" + rightNum +
                ", createTime=" + createTime +
                ", finishTime=" + finishTime +
                ", userTime=" + userTime +
                ", isFinish=" + isFinish +
                '}';
    }
}
