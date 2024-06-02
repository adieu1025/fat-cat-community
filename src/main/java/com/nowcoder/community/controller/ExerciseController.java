package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.constant.QuesConstant;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.*;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/exercises")
public class ExerciseController {

    @Autowired
    private ObjectivePaperService paperService;

    @Autowired
    private SubjectivePaperService subjectivePaperService;

    @Autowired
    private PaperQuestionService paperQuestionService;

    @Autowired
    private ChoiceQuestionService questionService;

    @Autowired
    private SubjectiveQuestionService subjectiveQuestionService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserPaperService userPaperService;

    @Autowired
    private UserQuestionService userQuestionService;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 跳转到题库首页，若未登陆，则先跳转到登陆页面
     *
     * @return
     */
    @LoginRequired
    @GetMapping
    public String exercisesIndex(Model model) {
        //分类查询题目信息，主要是题目总数
        Map<String, Integer> questionNumMap = exerciseService.countQuesByCate();
        //存入map，前端从map中取各分类的题目数量即可
        model.addAttribute("questionNumMap", questionNumMap);
        return "/site/exercises";
    }

    /**
     * 查询(客观题)试卷信息并列表展示
     *
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/papers")
    public String getPaperSimulation(Model model, Page page) {
        page.setPath("/exercises/papers");
        page.setRows(paperService.selectRows());

        //查询试卷信息，并列表展示给用户
        List<ObjectivePaper> paperList = paperService.selectByPage(page.getOffset(), page.getLimit());
        List<Map<String, Object>> paperDto = new ArrayList<>();

        if (paperList != null) {
            for (ObjectivePaper paper : paperList) {
                Map<String, Object> map = new HashMap<>();
                map.put("paperId", paper.getId());
                map.put("paperName", paper.getName());
                Category category = categoryService.selectById(paper.getCategoryId());
                if (category != null) {
                    map.put("categoryName", category.getName());
                }
                paperDto.add(map);
            }
        }
        model.addAttribute("paperDto", paperDto);
        return "/site/papers";
    }

    /**
     * 查询(主观题)试卷信息并列表展示
     *
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/subPapers")
    public String getSubPaperSimulation(Model model, Page page) {
        page.setPath("/exercises/subPapers");
        page.setRows(subjectivePaperService.selectRows());
        List<SubjectivePaper> subjectivePapers = subjectivePaperService.selectPage(page.getOffset(), page.getLimit());
        List<Map<String, Object>> paperDto = new ArrayList<>();
        if(subjectivePapers != null) {
            for(SubjectivePaper paper : subjectivePapers) {
                Map<String, Object> map = new HashMap<>();
                //科目、分类名
                Category category = categoryService.selectById(paper.getCategoryId());
                if (category != null) {
                    map.put("categoryName", category.getName());
                    Subject subject = subjectService.selectById(category.getSubjectId());
                    if(subject != null) {
                        map.put("subjectName", subject.getName());
                    }
                }
                map.put("paperId", paper.getId());
                map.put("paperName", paper.getName());
                paperDto.add(map);
            }
        }
        model.addAttribute("paperDto", paperDto);
        return "/site/subPapers";
    }

    /**
     * 试卷练习页面（客观题）
     *
     * @param paperId
     * @param model
     * @return
     */
    @GetMapping("/paper/{paperId}")
    public String exercisePaper(@PathVariable("paperId") Integer paperId, Model model) {
        //先查询试卷内容
        ObjectivePaper paper = paperService.selectById(paperId);
        if (paper != null) {
            model.addAttribute("paper", paper);
            //查询试卷关联的题目
            List<Integer> singleIds = paperQuestionService.selectQuesIdByPaperId(paper.getId());
            List<ChoiceQuestion> singleList = questionService.batchSelectByIds(singleIds);
            if (singleList != null) {
                model.addAttribute("singleList", singleList);
            }
        } else {
            System.out.println("试卷信息为空！");
        }
        //查询当前登陆的用户
        User user = hostHolder.getUser();
        if (user != null) {
            model.addAttribute("userId", user.getId());
        } else {
            //跳转到登陆页面
            return "/site/login";
        }
        return "/site/exercise-paper";
    }

    /**
     * 接受前端传的试卷答案，进行分数计算及数据库保存
     *
     * @param
     * @return
     */
    @ResponseBody
    @PostMapping("/paper/userScore")
    public String userScore(@RequestBody UserPaperDto userPaperDto) {
        //参数判断
        if (userPaperDto == null) {
            return CommunityUtil.getJSONString(403, "参数异常！请重新提交！");
        }
        HashMap<Integer, String> quesKeys = userPaperDto.getQuesKeys();
        if (quesKeys == null) {
            return CommunityUtil.getJSONString(403, "参数异常！请重新提交！");
        } else {
            Integer userPaperId = userPaperService.saveUserPaperAndUserQues(userPaperDto);
            //把新存储的 userPaperId 返回给前端，给页面跳转到答题详情提供参数
            return CommunityUtil.getJSONString(0, userPaperId);
        }

    }

    /**
     * 跳转到答题详情页面，显示用户分数/做对的题数，每道问题的答题情况及答案和解析展示
     * @param model
     * @return
     */
    @GetMapping("/paper/userPaper/{userPaperId}")
    public String userPaper(@PathVariable("userPaperId") Integer userPaperId, Model model) {
        //根据userPaperId 查询答题详情
        UserPaper userPaper = userPaperService.selectById(userPaperId);
        String paperName = paperService.selectNameById(userPaper.getPaperId());
        //存入问题总数、用户正确数、总分、用户得分、交卷时间、答题时间、每道题目详情、用户答案
        model.addAttribute("totalNum", userPaper.getTotalNum());
        model.addAttribute("rightNum", userPaper.getRightNum());
        model.addAttribute("totalScore", userPaper.getTotalScore());
        model.addAttribute("userScore", userPaper.getUserScore());
        model.addAttribute("submitTime", userPaper.getFinishTime());
        model.addAttribute("userTime", userPaper.getUserTime());
        model.addAttribute("paperName", paperName);
        //查找题目集合
        List<Integer> quesIds = paperQuestionService.selectQuesIdByPaperId(userPaper.getPaperId());
        List<ChoiceQuestion> questionList = questionService.batchSelectByIds(quesIds);
        List<UserQuestion> userQuestionList = userQuestionService.selectByUserPaperId(userPaperId);
        //创建map：quesId:userAnswer 方便进行用户答案的查找
        Map<Integer, String> qIduAnswer = new HashMap<>();
        for(UserQuestion userQuestion : userQuestionList) {
            qIduAnswer.put(userQuestion.getQuestionId(), userQuestion.getUserAnswer());
        }

        List<Map<String, Object>> userQuesDto = new ArrayList<>();
        for(ChoiceQuestion question : questionList) {
            Map<String, Object> map = new HashMap<>();
            map.put("question", question);
            map.put("userAnswer", qIduAnswer.get(question.getId()));
            userQuesDto.add(map);
        }
        model.addAttribute("userQuesDto", userQuesDto);

        //跳转到答题详情页面
        return "/site/userPaper-result";
    }

    /**
     * 练习历史页面列表
     * @return
     */
    @GetMapping("/history")
    public String userPaperHistory(Model model) {
        User user = hostHolder.getUser();
        //查询userPaper集合
        List<UserPaper> userPaperList = userPaperService.selectByUserId(user.getId());
        List<Map<String, Object>> userPaperDto = new ArrayList<>();
        //保存对应数据到页面(id、试卷名称、创建时间、完成时间、是否完成、总题数、做对题数)
        if(userPaperList != null || userPaperList.size() != 0) {
            for(UserPaper userPaper : userPaperList) {
                Map<String, Object> map = new HashMap<>();
                //试卷名称
                String paperName = paperService.selectNameById(userPaper.getPaperId());
                map.put("paperName", paperName);
                map.put("paperId", userPaper.getPaperId());
                map.put("userPaperId", userPaper.getId());
                map.put("createTime", userPaper.getCreateTime());
                map.put("finishTime", userPaper.getFinishTime());
                map.put("isFinish", userPaper.getIsFinish());
                map.put("totalNum", userPaper.getTotalNum());
                map.put("rightNum", userPaper.getRightNum());
                userPaperDto.add(map);
            }
        }
        model.addAttribute("userPaperDto", userPaperDto);
        return "/site/userPaper-history";
    }

    /**
     * 接收前端专项练习参数：题目数量、题目分类名称，生成试卷并跳转到答题页面
     * @return
     */
    @ResponseBody
    @PostMapping("/doQuestion")
    public String exerciseQuestions(Integer num, String cateName) {

        if(num == null || num < 1 || cateName == null || cateName.equals("")) {
            return CommunityUtil.getJSONString(403, "参数错误，请刷新重试！");
        }
        Integer paperId = exerciseService.doQuestion(num, cateName);
        if(paperId == null) {
            return CommunityUtil.getJSONString(403, "生成专项练习试卷失败！请刷新重试！");
        }
        return CommunityUtil.getJSONString(0, paperId);
    }

    /**
     * 接收前端练习主观题的请求，获取相应分类的题目数据，返回到主观题答题页面
     * @param cateName
     * @return
     */
    @GetMapping("/essayList/{cateName}")
    public String doEssay(@PathVariable("cateName") String cateName, Model model, Page page) {
        page.setPath("/exercises/essayList/"+ cateName);
        int cateId = categoryService.selectIdByName(cateName);
        page.setRows(subjectiveQuestionService.countByCateId(cateId));
        page.setLimit(1);//每页展示一条题目
        //查询题目
        List<Subject> essays = subjectiveQuestionService.selectPageByCate(page.getOffset(), page.getLimit(), cateId);
        model.addAttribute("essays", essays);
        model.addAttribute("current", page.getCurrent());
        model.addAttribute("total", page.getTotal());

        return "/site/exercise-essay";
    }

    /**
     * 主观题试卷练习页面
     * @param paperId
     * @return
     */
    @GetMapping("/subPaper/{paperId}")
    public String exerciseSubPaper(@PathVariable("paperId") Integer paperId, Model model) {
        Map<String, Object> paperDetail = subjectivePaperService.getPaperDetail(paperId);
        model.addAttribute("paperDetail", paperDetail);
        return "/site/exercise-subPaper";
    }

}
