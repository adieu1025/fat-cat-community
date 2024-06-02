package com.nowcoder.community.service;


import com.nowcoder.community.dao.SubjectiveQuestionMapper;
import com.nowcoder.community.entity.Subject;
import com.nowcoder.community.entity.SubjectiveQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectiveQuestionService {

    @Autowired
    private SubjectiveQuestionMapper subjectiveQuestionMapper;

    //查找主观题题目数量
    public int selectRows() {
        return subjectiveQuestionMapper.selectRows();
    }

    //分页查询主观题题目
    public List<SubjectiveQuestion> selectPage(int offset, int limit) {
        return subjectiveQuestionMapper.selectPage(offset, limit);
    }

    //插入一条主观题
    public void insertOne(SubjectiveQuestion subjectiveQuestion) {
        subjectiveQuestionMapper.insertOne(subjectiveQuestion);
    }

    public SubjectiveQuestion selectById(Integer subjectiveId) {
        return subjectiveQuestionMapper.selectById(subjectiveId);
    }

    public void updateOne(SubjectiveQuestion subjectiveQuestion) {
        subjectiveQuestionMapper.updateOne(subjectiveQuestion);
    }

    public void delOne(Integer id) {
        subjectiveQuestionMapper.delOne(id);
    }

    //根据分类id查询题目数量
    public int countByCateId(int id) {
        return subjectiveQuestionMapper.countByCateId(id);
    }

    //根据分类id，分页查询题目
    public List<Subject> selectPageByCate(int offset, int limit, int cateId) {
        return subjectiveQuestionMapper.selectPageByCate(offset, limit, cateId);
    }
}
