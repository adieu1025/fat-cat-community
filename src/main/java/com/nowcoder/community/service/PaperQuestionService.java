package com.nowcoder.community.service;

import com.nowcoder.community.dao.PaperQuestionMapper;
import com.nowcoder.community.dao.PaperTfMapper;
import com.nowcoder.community.entity.PaperQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaperQuestionService {

    //试卷-选择题
    @Autowired
    private PaperQuestionMapper paperQuestionMapper;

    //试卷-判断题
    @Autowired
    private PaperTfMapper paperTfMapper;

    //批量保存
    public int batchInsert(int paperId, List<Integer> questionIds) {
        //构造集合
        List<PaperQuestion> list = new ArrayList<>();
        for(Integer id : questionIds) {
            PaperQuestion paperQuestion = new PaperQuestion(paperId, id);
            list.add(paperQuestion);
        }
        //调用mapper批量保存
        return paperQuestionMapper.batchInsert(list);
    }

    //根据试卷id查询试卷_问题信息，在试卷详情中展示
    public List<PaperQuestion> selectByPaperId(int paperId) {
        return paperQuestionMapper.selectByPaperId(paperId);
    }

    //根据试卷id去查询问题id的集合
    public List<Integer> selectQuesIdByPaperId(int id) {
        return paperQuestionMapper.selectQuesIdByPaperId(id);
    }

    //根据问题id 查询 试卷_问题 的数量
    public int countByQuesId(int id) {
        return paperQuestionMapper.countByQuesId(id);
    }

    //根据问题id集合查询试卷_问题的数量
    public int batchCountByQuesId(Integer[] ids) {
        return paperQuestionMapper.batchCountByQuesId(ids);
    }

    //根据判断题id查询试卷-判断题表中记录的数量
    public int countByTfId(Integer id) {
        return paperTfMapper.countByTfId(id);
    }
}
