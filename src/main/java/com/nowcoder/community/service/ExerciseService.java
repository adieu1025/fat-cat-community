package com.nowcoder.community.service;

import com.nowcoder.community.constant.QuesConstant;
import com.nowcoder.community.dao.ObjectivePaperMapper;
import com.nowcoder.community.entity.Category;
import com.nowcoder.community.entity.ChoiceQuestion;
import com.nowcoder.community.entity.ObjectivePaper;
import com.nowcoder.community.entity.UserPaper;
import com.nowcoder.community.utils.HostHolder;
import com.sun.mail.util.QEncoderStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExerciseService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ChoiceQuestionService questionService;

    @Autowired
    private SubjectiveQuestionService subjectiveQuestionService;

    @Autowired
    private ObjectivePaperService paperService;

    @Autowired
    private ObjectivePaperMapper paperMapper;

    @Autowired
    private PaperQuestionService paperQuestionService;

    @Autowired
    private UserPaperService userPaperService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 专项练习，自动生成试卷、生成userPaper
     * @param num
     * @param cateName
     * @return
     */
    @Transactional
    public Integer doQuestion(Integer num, String cateName) {
        //接收参数生成试卷
        ObjectivePaper paper = new ObjectivePaper();
        paper.setChoiceNum(num);
        //查询并设置试卷分类id
        Category category = categoryService.selectByName(QuesConstant.PAPER_CATE_SPECIAL_EXERCISE);
        paper.setCategoryId(category.getId());
        //判断要生成哪种类型的题目,并设置试卷名称
        String paperName = getPaperName(cateName);
        if(paperName != null) {
            paper.setName(paperName);
        } else {
            return null;
        }
        //根据传入的分类名，获取对应分类的题目id集合
        Category category1 = categoryService.selectByName(cateName);
        if (category1 == null) {
            return null;
        }
        //根据分类id批量查询该分类下所有的题目id，去进行随机组卷
        List<Integer> idsByCate = questionService.findIdsByCateId(category1.getId());
        //根据id集合及题目数量去生成题目id的集合
        List<Integer> questionIds = paperService.questionIds(num, idsByCate);
        if(questionIds == null || questionIds.size() == 0) {
            return null;
        }
        //设置好试卷信息后，插入试卷本身
        paper.setChoiceNum(num);
        paper.setIsDelete(0);
        paper.setType(0);
        //paperService.insertOne(paper);  不能调用paperService中的insertOne!!! 因为其本身也有业务逻辑!!
        paperMapper.insertOne(paper);
        //插入试卷-问题信息
        paperQuestionService.batchInsert(paper.getId(), questionIds);

        //创建userPaper
        UserPaper userPaper = new UserPaper();
        userPaper.setUserId(hostHolder.getUser().getId());
        userPaper.setPaperId(paper.getId());
        userPaper.setIsFinish(0);
        userPaper.setCreateTime(new Date());//创建时间，若未完成，在练习历史中显示
        //保存到数据库
        userPaperService.insertOne(userPaper);

        return paper.getId();
    }

    /**
     * 根据传入的题目分类名，返回试卷名称、题目分类名称
     * @param cateName
     * @return
     */
    public String getPaperName(String cateName) {
        switch (cateName) {
            case QuesConstant.LANGUAGE_COMPREHENSION:
                return QuesConstant.PAPER_NAME_LANGUAGE;
            case QuesConstant.JUDGE_REASONING:
                return QuesConstant.PAPER_NAME_JUDGE;
            case QuesConstant.QUANTITY_RELATION:
                return QuesConstant.PAPER_NAME_QUANTITY;
            case QuesConstant.COMMON_SENSE_JUDGE:
                return QuesConstant.PAPER_NAME_COMMON;

            default: return null;
        }
    }

    //查找各分类下的题目数量，包括行测、申论、面试科目下的各分类
    public Map<String, Integer> countQuesByCate() {
        Map<String, Integer> quesNumMap = new HashMap<>();
        //行测
        int languageNum = questionService.countByCategoryId(categoryService.selectIdByName(QuesConstant.LANGUAGE_COMPREHENSION));//言语
        int commonNum = questionService.countByCategoryId(categoryService.selectIdByName(QuesConstant.COMMON_SENSE_JUDGE));//常识
        int judgeNum = questionService.countByCategoryId(categoryService.selectIdByName(QuesConstant.JUDGE_REASONING));//判断
        int quantityNum = questionService.countByCategoryId(categoryService.selectIdByName(QuesConstant.QUANTITY_RELATION));//数量
        //申论
        int summarizeNum = subjectiveQuestionService.countByCateId(categoryService.selectIdByName(QuesConstant.SUMMARIZE_CONCLUDE));//概括归纳
        int excerptNum = subjectiveQuestionService.countByCateId(categoryService.selectIdByName(QuesConstant.EXCERPT));//摘抄
        int analysisNum = subjectiveQuestionService.countByCateId(categoryService.selectIdByName(QuesConstant.ANALYSIS_DERIVATION));//分析推导
        //面试
        int socialNum = subjectiveQuestionService.countByCateId(categoryService.selectIdByName(QuesConstant.SOCIAL_PHENOMENON));//社会现象
        int attitudeNum = subjectiveQuestionService.countByCateId(categoryService.selectIdByName(QuesConstant.ATTITUDE_VIEW));//态度观点
        int organizationNum = subjectiveQuestionService.countByCateId(categoryService.selectIdByName(QuesConstant.ORGANIZATION_MANAGEMENT));//组织管理
        int emergencyNum = subjectiveQuestionService.countByCateId(categoryService.selectIdByName(QuesConstant.EMERGENCY_RESPONSE));//应急应变
        int relationshipNum = subjectiveQuestionService.countByCateId(categoryService.selectIdByName(QuesConstant.PERSONAL_RELATIONSHIP));//人际关系
        int simulationNum = subjectiveQuestionService.countByCateId(categoryService.selectIdByName(QuesConstant.SCENE_SIMULATION));//现场模拟
        int leaderlessNum = subjectiveQuestionService.countByCateId(categoryService.selectIdByName(QuesConstant.LEADERLESS_GROUP));//无领导小组

        //把各分类问题数量的数据存入到map中
        quesNumMap.put("languageNum", languageNum);
        quesNumMap.put("commonNum", commonNum);
        quesNumMap.put("judgeNum",judgeNum);
        quesNumMap.put("quantityNum",quantityNum);
        quesNumMap.put("summarizeNum",summarizeNum);
        quesNumMap.put("excerptNum",excerptNum);
        quesNumMap.put("analysisNum",analysisNum);
        quesNumMap.put("socialNum",socialNum);
        quesNumMap.put("attitudeNum",attitudeNum);
        quesNumMap.put("organizationNum",organizationNum);
        quesNumMap.put("emergencyNum",emergencyNum);
        quesNumMap.put("relationshipNum",relationshipNum);
        quesNumMap.put("simulationNum",simulationNum);
        quesNumMap.put("leaderlessNum",leaderlessNum);

        return quesNumMap;
    }

}
