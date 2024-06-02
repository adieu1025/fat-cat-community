package com.nowcoder.community.dao;

import com.nowcoder.community.entity.TfQuestion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TfQuestionMapper {

    int selectRows();

    List<TfQuestion> selectPage(int offset, int limit);

    void insertOne(TfQuestion tfQuestion);

    TfQuestion selectById(Integer tfId);

    void updateOne(TfQuestion tf);

    void delById(Integer id);
}
