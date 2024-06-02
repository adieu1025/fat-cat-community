package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Subject;
import com.nowcoder.community.entity.SubjectiveQuestion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubjectiveQuestionMapper {

    int selectRows();

    List<SubjectiveQuestion> selectPage(int offset, int limit);

    void insertOne(SubjectiveQuestion subjectiveQuestion);

    SubjectiveQuestion selectById(Integer id);

    void updateOne(SubjectiveQuestion subjectiveQuestion);

    void delOne(Integer id);

    int countByCateId(int id);

    List<Subject> selectPageByCate(int offset, int limit, int cateId);
}
