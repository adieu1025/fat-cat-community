package com.nowcoder.community.dao;

import org.apache.ibatis.annotations.Mapper;

//记录试卷-判断题
@Mapper
public interface PaperTfMapper {

    int countByTfId(Integer id);
}
