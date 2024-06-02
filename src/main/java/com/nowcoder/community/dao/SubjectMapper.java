package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Subject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubjectMapper {

    int insertSubject(Subject subject);

    Subject selectById(int id);

    List<Subject> selectAll(int offset, int limit);

    int findRows();

    Subject selectByName(String name);

    int updateSubject(Subject subject);

    void deleteById(Integer id);

    int batchDel(Integer[] ids);

    List<Subject> getAll();
}
