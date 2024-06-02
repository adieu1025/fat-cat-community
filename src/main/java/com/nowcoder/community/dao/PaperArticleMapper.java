package com.nowcoder.community.dao;

import com.nowcoder.community.entity.PaperArticle;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaperArticleMapper {
    int selectRowsByPaperId(int id);

    void batchInsert(List<PaperArticle> list);

    List<PaperArticle> selectByPaperId(Integer paperId);

    void batDelByPaperId(Integer id);
}
