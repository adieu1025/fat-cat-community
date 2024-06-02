package com.nowcoder.community.service;

import com.nowcoder.community.dao.PaperArticleMapper;
import com.nowcoder.community.entity.PaperArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaperArticleService {

    @Autowired
    private PaperArticleMapper paperArticleMapper;

    //查询某试卷下的材料数量
    public int selectRowsByPaperId(int id) {
        return paperArticleMapper.selectRowsByPaperId(id);
    }

    //批量插入数据
    public void batchInsert(List<PaperArticle> paperArticles) {
        paperArticleMapper.batchInsert(paperArticles);
    }

    public List<PaperArticle> selectByPaperId(Integer paperId) {
        return paperArticleMapper.selectByPaperId(paperId);
    }

    public void batchDelByPaperId(Integer id) {
        paperArticleMapper.batDelByPaperId(id);
    }
}
