package com.nowcoder.community.dao;

import com.nowcoder.community.entity.PaperQuestion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaperQuestionMapper {

    int batchInsert(List<PaperQuestion> list);

    List<PaperQuestion> selectByPaperId(int paperId);

    int countByQuesId(int id);

    int batchCountByQuesId(Integer[] ids);

    List<Integer> selectQuesIdByPaperId(int id);
}
