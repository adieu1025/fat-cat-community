package com.nowcoder.community.dao;

import com.nowcoder.community.entity.SubjectivePaper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubjectivePaperMapper {

    int selectRows();

    List<SubjectivePaper> selectPage(int offset, int limit);

    void insertOne(SubjectivePaper paper);

    SubjectivePaper selectById(Integer id);

    void delById(Integer id);
}
