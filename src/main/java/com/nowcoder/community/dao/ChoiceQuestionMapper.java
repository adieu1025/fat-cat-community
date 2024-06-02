package com.nowcoder.community.dao;

import com.nowcoder.community.entity.ChoiceQuestion;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ChoiceQuestionMapper {

    ChoiceQuestion selectById(int id);

    List<ChoiceQuestion> selectAll(int offset, int limit);

    int insertOne(ChoiceQuestion choiceQuestion);

    int selectSingleRows();

    List<ChoiceQuestion> selectByCategoryId(Integer id);

    int countByCategoryId(Integer categoryId);

    int batchCountByCateId(Integer[] ids);

    int updateSingle(ChoiceQuestion single);

    List<Integer> findIds();

    List<ChoiceQuestion> batchSelectByIds(List<Integer> ids);

    int delById(Integer id);

    int batchDel(Integer[] ids);

    List<String> selectBatchKeysByIds(Collection<Integer> ids);

    List<Integer> findIdsByCateId(int categoryId);
}
