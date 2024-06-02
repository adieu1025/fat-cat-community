package com.nowcoder.community.dao;

import com.nowcoder.community.entity.UserQuestion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserQuestionMapper {

    void insertOne(UserQuestion userQuestion);

    int batchInsert(List<UserQuestion> list);

    List<UserQuestion> selectByUserPaperId(Integer id);
}
