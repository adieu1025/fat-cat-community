package com.nowcoder.community.controller.admin;

import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.CategoryService;
import com.nowcoder.community.service.PaperArticleService;
import com.nowcoder.community.service.PaperEssayService;
import com.nowcoder.community.service.SubjectivePaperService;
import com.nowcoder.community.utils.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class SubjectivePaperController {

    @Autowired
    private SubjectivePaperService subjectivePaperService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PaperArticleService paperArticleService;

    @Autowired
    private PaperEssayService paperEssayService;

    /**
     * 主观试卷管理列表
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/subPaperList")
    public String subPaperList(Model model, Page page) {
        page.setPath("/admin/subPaperList");
        page.setRows(subjectivePaperService.selectRows());

        List<SubjectivePaper> subPaperList = subjectivePaperService.selectPage(page.getOffset(), page.getLimit());
        List<Map<String, Object>> subPaperDto = new ArrayList<>();

        if(subPaperList != null) {
            for(SubjectivePaper paper : subPaperList) {
                Map<String, Object> map = new HashMap<>();
                //存入试卷本身
                map.put("paper", paper);
                //查询当前拥有的题目和材料数量
                int articleNum = paperArticleService.selectRowsByPaperId(paper.getId());
                int essayNum = paperEssayService.selectRowsByPaperId(paper.getId());
                map.put("articleNum", articleNum);
                map.put("essayNum", essayNum);

                //存入分类名
                Category category = categoryService.selectById(paper.getCategoryId());
                if(category != null) {
                    map.put("categoryName", category.getName());
                }
                subPaperDto.add(map);
            }
        }
        model.addAttribute("subPaperDto", subPaperDto);

        return "/site/admin/subjective-paper";
    }

    /**
     * 添加一条主观题试卷信息
     * @param paper
     * @return
     */
    @PostMapping("/addSubPaper")
    public String addSubPaper(SubjectivePaper paper) {
        subjectivePaperService.insertOne(paper);
        return "redirect:/admin/subPaperList";
    }

    /**
     * 添加试卷的材料信息
     * @param articles
     * @return
     */
    @PostMapping("/addPaperArticle")
    public String addPaperArticle(@RequestParam Map<String, String> articles) {
        subjectivePaperService.addPaperArticle(articles);
        return "redirect:/admin/subPaperList";
    }

    /**
     * 添加试卷的问题信息
     * @param essays
     * @return
     */
    @PostMapping("/addPaperEssay")
    public String addPaperEssay(@RequestParam Map<String, String> essays) {
        subjectivePaperService.addPaperEssay(essays);
        return "redirect:/admin/subPaperList";
    }

    /**
     * 查询试卷详情
     * @param paperId
     * @param model
     * @return
     */
    @GetMapping("/subPaperDetail/{paperId}")
    public String subPaperDetail(@PathVariable("paperId") Integer paperId, Model model) {
        //查询试卷
        SubjectivePaper paper = subjectivePaperService.selectById(paperId);
        //查询试卷材料
        List<PaperArticle> paperArticles = paperArticleService.selectByPaperId(paperId);
        //查询试卷题目
        List<PaperEssay> paperEssays = paperEssayService.selectByPaperId(paperId);
        model.addAttribute("subPaperName", paper.getName());
        model.addAttribute("paperArticles", paperArticles);
        model.addAttribute("paperEssays", paperEssays);
        return "/site/admin/subPaper-detail";
    }

    /**
     * 根据paperId删除试卷
     * @param id
     * @return
     */
    @PostMapping("/delSubPaper")
    public String delSubPaper(Integer id) {
        subjectivePaperService.delPaper(id);
        return CommunityUtil.getJSONString(0);
    }
}
