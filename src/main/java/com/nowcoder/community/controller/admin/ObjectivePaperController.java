package com.nowcoder.community.controller.admin;

import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.CategoryService;
import com.nowcoder.community.service.ChoiceQuestionService;
import com.nowcoder.community.service.ObjectivePaperService;
import com.nowcoder.community.service.PaperQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class ObjectivePaperController {

    @Autowired
    private ObjectivePaperService paperService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ChoiceQuestionService questionService;

    @Autowired
    private PaperQuestionService paperQuestionService;

    /**
     * 分页查询试卷
     *
     * @return
     */
    @GetMapping("/objectPaperList")
    public String paperList(Model model, Page page) {
        page.setPath("/admin/objectPaperList");
        page.setRows(paperService.selectRows());

        //后台管理查询试卷列表
        List<ObjectivePaper> paperList = paperService.selectByPage(page.getOffset(), page.getLimit());
        List<Map<String, Object>> paperDto = new ArrayList<>();

        if(paperList != null) {
            for(ObjectivePaper paper : paperList) {
                Map<String, Object> map = new HashMap<>();
                //存入试卷本身
                map.put("paper", paper);
                //存入分类名
                Category category = categoryService.selectById(paper.getCategoryId());
                if(category != null) {
                    map.put("categoryName", category.getName());
                }
                paperDto.add(map);
            }
        }
        model.addAttribute("paperDto", paperDto);
        return "/site/admin/objective-paper";
    }

    /**
     * 添加一条试卷信息
     * @param paper
     * @return
     */
    @PostMapping("/addObjectPaper")
    public String addPaper(ObjectivePaper paper) {
        //保存试卷信息，先设置试卷为公有
        paper.setType(1);
        paperService.insertOne(paper);
        return "redirect:/admin/objectPaperList";
    }

    /**
     * 查询试卷详情
     * @param paperId
     * @param model
     * @return
     */
    @GetMapping("/objectPaperDetail/{paperId}")
    public String paperDetail(@PathVariable("paperId") Integer paperId, Model model) {
        //查询试卷本身信息
        ObjectivePaper paper = paperService.selectById(paperId);
        //查询试卷_问题信息
        List<PaperQuestion> paperQuestions = paperQuestionService.selectByPaperId(paperId);
        if(paperQuestions != null) {
            //查询题目集合
            List<Integer> ids = new ArrayList<>();
            for(PaperQuestion paperQuestion : paperQuestions) {
                //获取题目id集合，然后进行批量查询题目，避免分条查询，提高查询效率
                ids.add(paperQuestion.getQuestionId());
            }
            //批量查询
            List<ChoiceQuestion> questionList = questionService.batchSelectByIds(ids);
            //存入页面要显示的内容(题目id,题干/图片,选项,答案，分值),把题目集合存入即可
            model.addAttribute("questionList", questionList);
        }

        return "/site/admin/objectPaper-detail";
    }

}
