package com.nowcoder.community.dao;

import com.nowcoder.community.entity.PaperEssay;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaperEssayMapper {

    int selectRowsByPaperId(int id);

    void batchInsert(List<PaperEssay> list);

    List<PaperEssay> selectByPaperId(Integer paperId);

    void batchDelByPaperId(Integer id);
}
