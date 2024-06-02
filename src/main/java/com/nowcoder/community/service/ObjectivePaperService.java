package com.nowcoder.community.service;

import com.nowcoder.community.dao.ObjectivePaperMapper;
import com.nowcoder.community.entity.ObjectivePaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ObjectivePaperService {

    @Autowired
    private ObjectivePaperMapper paperMapper;

    @Autowired
    private ChoiceQuestionService questionService;

    @Autowired PaperQuestionService paperQuestionService;

    //查询试卷总数
    public int selectRows() {
        return paperMapper.selectRows();
    }

    public List<ObjectivePaper> selectByPage(int offset, int limit) {
        return paperMapper.selectByPage(offset, limit);
    }

    //对传入的试卷信息进行检查
    public boolean check(ObjectivePaper paper) {
        if(paper == null) {
            System.out.println("传入试卷数据为空！");
            return false;
        }
        if(paper.getName() == null){
            System.out.println("试卷名称不能为空！");
            return false;
        }
        return true;
    }

    //保存试卷信息
    @Transactional
    public void insertOne(ObjectivePaper paper) {
        if(check(paper)) {
            //根据传入的试卷信息，随机组卷（加入题目），生成一张试卷
            int choiceNum = paper.getChoiceNum();
            // int tfNum = paper.getTfNum();
            List<Integer> ids = questionService.findIds();//所有题目的id集合
            if(choiceNum > 0) {
                List<Integer> questionIds = questionIds(choiceNum, ids);
                if(questionIds == null || questionIds.size() == 0) {
                    throw new RuntimeException("参数：选择题数量 超出范围，生成试卷失败！");
                } else {
                    // 插入试卷本身的信息
                    paperMapper.insertOne(paper);
                    //调用 paperQuestionService 批量保存试卷-题目信息
                    paperQuestionService.batchInsert(paper.getId(), questionIds);
                }
            }
        }
    }

    /**
     * 随机组题
     * @param questionNum 题目数量
     * @param ids 题目id集合
     * @return 题目id集合
     */
    public List<Integer> questionIds(int questionNum, List<Integer> ids) {
        int size = ids.size();
        //需求题目数量 > 题库中题目的数量
        if(questionNum > size){
            return null;
        } else if(questionNum == size) {
            return ids;
        }

        //创建set临时保存ids，set能保证id不重复
        Set<Integer> resultSet = new HashSet<>();
        Random random = new Random();
        while(resultSet.size() < questionNum) {
            int index = random.nextInt(size);
            resultSet.add(ids.get(index));
        }

        List<Integer> result = new ArrayList<>(resultSet);
        //将得到的问题id集合进行从小到大排序，因为用通过id集合查询mysql得到的问题集合的问题是根据问题id从小到大进行排序的
        //为了防止后续业务错乱，先把问题id进行排序
        result.sort(Comparator.naturalOrder());

        return result;
    }

    public ObjectivePaper selectById(Integer paperId) {
        return paperMapper.selectById(paperId);
    }

    public String selectNameById(int paperId) {
        return paperMapper.selectNameById(paperId);
    }
}
