package com.nowcoder.community.entity;

import java.util.HashMap;

//UserPaper的拓展，用来接受前端答题页面的数据
public class UserPaperDto extends UserPaper{
    //问题id-用户答案的map
    private HashMap<Integer, String> quesKeys;
    //开始答题的时间
    private Long startTime;
    //提交时间
    private Long submitTime;

    public HashMap<Integer, String> getQuesKeys() {
        return quesKeys;
    }

    public void setQuesKeys(HashMap<Integer, String> quesKeys) {
        this.quesKeys = quesKeys;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Long submitTime) {
        this.submitTime = submitTime;
    }
}
