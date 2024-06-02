package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    Category selectById(int id);

    List<Category> selectAll(int offset, int limit);

    int insertCategory(Category category);

    List<Category> findBySubjectId(Integer id);

    Category findByName(String name);

    int findRows();

    int updateCategory(Category category);

    int deleteById(Integer id);

    int countBySubjectId(Integer id);

    int batchCountBySubId(Integer[] ids);

    int batchDel(Integer[] ids);

    List<Category> findAll();

    //根据科目id批量查询分类
    List<Category> batchSelectBySubjectId(List<Integer> subjectIds);

    int selectIdByName(String name);
}
