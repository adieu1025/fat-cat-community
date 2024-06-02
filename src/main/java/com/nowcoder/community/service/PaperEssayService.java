package com.nowcoder.community.service;

import com.nowcoder.community.dao.PaperEssayMapper;
import com.nowcoder.community.entity.PaperEssay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaperEssayService {

    @Autowired
    private PaperEssayMapper paperEssayMapper;

    public int selectRowsByPaperId(int id) {
        return paperEssayMapper.selectRowsByPaperId(id);
    }

    public void batchInsert(List<PaperEssay> paperEssays) {
        paperEssayMapper.batchInsert(paperEssays);
    }

    public List<PaperEssay> selectByPaperId(Integer paperId) {
        return paperEssayMapper.selectByPaperId(paperId);
    }

    public void batchDelByPaperId(Integer id) {
        paperEssayMapper.batchDelByPaperId(id);
    }
}
