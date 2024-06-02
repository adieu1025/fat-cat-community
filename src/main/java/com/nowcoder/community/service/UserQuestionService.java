package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserQuestionMapper;
import com.nowcoder.community.entity.UserQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserQuestionService {

    @Autowired
    private UserQuestionMapper userQuestionMapper;


    public int batchInsert(List<UserQuestion> userQuestionList) {
        return userQuestionMapper.batchInsert(userQuestionList);
    }

    //根据user_paper_id 查询
    public List<UserQuestion> selectByUserPaperId(Integer userPaperId) {
        return userQuestionMapper.selectByUserPaperId(userPaperId);
    }
}
