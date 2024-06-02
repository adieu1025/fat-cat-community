package com.nowcoder.community.dao;

import com.nowcoder.community.entity.ObjectivePaper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ObjectivePaperMapper {

    int selectRows();

    List<ObjectivePaper> selectByPage(int offset, int limit);

    void insertOne(ObjectivePaper paper);

    ObjectivePaper selectById(Integer paperId);

    String selectNameById(int paperId);
}
