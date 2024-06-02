package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserPaperMapper;
import com.nowcoder.community.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class UserPaperService {

    @Autowired
    private UserPaperMapper userPaperMapper;

    @Autowired
    private ChoiceQuestionService questionService;

    @Autowired
    private UserQuestionService userQuestionService;

    @Autowired
    private ObjectivePaperService paperService;

    public void insertOne(UserPaper userPaper) {
        userPaperMapper.insertOne(userPaper);
    }

    //根据userId和paperId 去查询userPaper的数量
    public int countByUidPid(int userId, int paperId) {
        return userPaperMapper.countByUidPid(userId, paperId);
    }

    //保存用户-试卷、用户-问题 两张表的信息，需要用到事务管理注解，因此在service中实现
    @Transactional
    public int saveUserPaperAndUserQues(UserPaperDto userPaperDto) {
        HashMap<Integer, String> quesKeys = userPaperDto.getQuesKeys();
        //核对答对题目数量及分数
        int rightNum = 0;//答对的题数
        float userScore = 0;//得分
        BigDecimal totalScore = new BigDecimal(0);//总分
        List<Integer> quesIds = new ArrayList<>(quesKeys.keySet());
        List<ChoiceQuestion> questionList = questionService.batchSelectByIds(quesIds);
        for (int i = 0; i < quesIds.size(); i++) {
            ChoiceQuestion single = questionList.get(i);
            String answer = single.getAnswer();
            BigDecimal score = single.getScore();
            totalScore = totalScore.add(score);
            //获取用户答案
            String userAnswer = quesKeys.get(single.getId());
            if (userAnswer.equals(answer)) {
                rightNum += 1;
                userScore += 1;
            }
        }
        //将所得信息保存到 userPaper对象中，并插入数据库，
        //插入前先根据试卷的type=1(公有)/0(私有)进行判断
        //如果type=0，则在专项练习的业务中已经预先创建一个userPaper并插入了数据库了，不能重复插入同一个userPaper，对其进行更新即可
        ObjectivePaper paper = paperService.selectById(userPaperDto.getPaperId());

        if (paper.getType() == 1) {
            // type=1说明该试卷是公有的，它可以被用户重复去完成，因此可以进行创建新的userPaper去插入信息
            UserPaper userPaper = new UserPaper(userPaperDto.getUserId(), userPaperDto.getPaperId());
            userPaper.setUserScore(new BigDecimal(userScore));
            userPaper.setTotalScore(totalScore);
            userPaper.setRightNum(rightNum);
            userPaper.setTotalNum(userPaperDto.getTotalNum());
            userPaper.setUserTime(userPaperDto.getUserTime());
            userPaper.setCreateTime(new Date(userPaperDto.getStartTime()));
            userPaper.setFinishTime(new Date(userPaperDto.getSubmitTime()));
            userPaper.setIsFinish(1);
            //保存到数据库user_paper表
            insertOne(userPaper);

            int userPaperId = userPaper.getId();
            List<UserQuestion> userQuestionList = new ArrayList<>();
            for (Integer quesId : quesIds) {
                UserQuestion userQuestion = new UserQuestion();
                userQuestion.setUserPaperId(userPaperId);
                userQuestion.setQuestionId(quesId);
                userQuestion.setUserAnswer(quesKeys.get(quesId));
                userQuestionList.add(userQuestion);
            }

            //保存数据到user_question表
            userQuestionService.batchInsert(userQuestionList);

            return userPaperId;
        }

        //试卷type=0，说明是私有的，即用户自行创建的专项习题试卷，数据库中必然已经有userPaper的信息了，对其进行更新即可
        UserPaper userPaper = selectByUidPid(userPaperDto.getUserId(), userPaperDto.getPaperId());
        //设置属性后进行数据库更新
        userPaper.setUserScore(new BigDecimal(userScore));
        userPaper.setTotalScore(totalScore);
        userPaper.setRightNum(rightNum);
        userPaper.setTotalNum(userPaperDto.getTotalNum());
        userPaper.setUserTime(userPaperDto.getUserTime());
        userPaper.setFinishTime(new Date(userPaperDto.getSubmitTime()));
        userPaper.setIsFinish(1);
        updateById(userPaper);

        //插入 userQuestion数据
        List<UserQuestion> userQuestionList = new ArrayList<>();
        for (Integer quesId : quesIds) {
            UserQuestion userQuestion = new UserQuestion();
            userQuestion.setUserPaperId(userPaper.getId());
            userQuestion.setQuestionId(quesId);
            userQuestion.setUserAnswer(quesKeys.get(quesId));
            userQuestionList.add(userQuestion);
        }
        //保存数据到user_question表
        userQuestionService.batchInsert(userQuestionList);

        return userPaper.getId();
    }

    public UserPaper selectById(Integer userPaperId) {
        return userPaperMapper.selectById(userPaperId);
    }

    //根据userId 查询userPaper
    public List<UserPaper> selectByUserId(int id) {
        return userPaperMapper.selectByUserId(id);
    }

    //根据userId、paperId查询userPaper
    public UserPaper selectByUidPid(int uId, int pId) {
        return userPaperMapper.selectByUidPid(uId, pId);
    }

    //根据id更新userPaper
    public int updateById(UserPaper userPaper) {
        return userPaperMapper.updateById(userPaper);
    }
}
