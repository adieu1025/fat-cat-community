package com.nowcoder.community.service;

import com.nowcoder.community.dao.SubjectivePaperMapper;
import com.nowcoder.community.entity.PaperArticle;
import com.nowcoder.community.entity.PaperEssay;
import com.nowcoder.community.entity.SubjectivePaper;
import jdk.nashorn.internal.ir.CallNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SubjectivePaperService {

    @Autowired
    private SubjectivePaperMapper subjectivePaperMapper;

    @Autowired
    private PaperArticleService paperArticleService;

    @Autowired
    private PaperEssayService paperEssayService;

    public int selectRows() {
        return subjectivePaperMapper.selectRows();
    }

    public List<SubjectivePaper> selectPage(int offset, int limit) {
        return subjectivePaperMapper.selectPage(offset, limit);
    }

    //检查试卷信息是否符合规范，符合规范的才保存到数据库
    private boolean check(SubjectivePaper paper) {
        if (paper == null) {
            System.out.println("主观题试卷信息为空，保存失败！");
            return false;
        }
        if (paper.getName() == null || paper.getName().equals("")) {
            System.out.println("主观题试卷名称为空，保存失败！");
            return false;
        }

        return true;
    }

    public void insertOne(SubjectivePaper paper) {
        if (check(paper)) {
            //保存试卷信息
            subjectivePaperMapper.insertOne(paper);
        }
    }

    //添加试卷的材料信息
    public void addPaperArticle(Map<String, String> articles) {
        //检查参数，若存在value为空，即存在某材料的内容为空，不保存数据
        if (articles == null || articles.isEmpty() || articles.containsValue("")) {
            System.out.println("提交的材料数据中，存在为空的材料，添加试卷材料失败！");
        } else {
            // 获取试卷id
            int id = Integer.parseInt(articles.get("id"));
            //获取各篇材料
            List<String> values = new ArrayList<>(articles.values());
            // 创建试卷-材料对象集合保存数据
            List<PaperArticle> paperArticles = new ArrayList<>();
            for (int i = 1; i < values.size(); i++) {
                PaperArticle paperArticle = new PaperArticle();
                paperArticle.setPaperId(id);
                paperArticle.setArticle(values.get(i));
                paperArticle.setArticleOrder(i);
                paperArticles.add(paperArticle);
            }
            //批量插入到 paper_article 表
            paperArticleService.batchInsert(paperArticles);
        }
    }

    //添加试卷的题目信息
    public void addPaperEssay(Map<String, String> essays) {
        //检查参数，若存在value为空，即存在某材料的内容为空，不保存数据
        if (essays == null || essays.isEmpty() || essays.containsValue("")) {
            System.out.println("提交的题目数据中，存在为空的题目，添加试卷题目失败！");
        } else {
            // 获取试卷id
            int id = Integer.parseInt(essays.get("id"));
            //获取各题目及参考答案
            List<String> values = new ArrayList<>(essays.values());
            List<PaperEssay> paperEssays = new ArrayList<>();
            for (int i = 1; i < values.size(); i++) {
                //当i为偶数时直接跳到下一次循环
                if (i % 2 != 0) {
                    PaperEssay paperEssay = new PaperEssay();
                    paperEssay.setPaperId(id);
                    //设置题目
                    paperEssay.setEssay(values.get(i));
                    paperEssay.setEssayOrder((i + 1) / 2);
                    //设置参考答案
                    paperEssay.setAnswer(values.get(i + 1));
                    paperEssays.add(paperEssay);
                }
            }
            // 批量插入到paper_essay表
            paperEssayService.batchInsert(paperEssays);
        }
    }


    public SubjectivePaper selectById(Integer paperId) {
        return subjectivePaperMapper.selectById(paperId);
    }

    //根据id删除试卷、同时删除试卷材料、试卷题目
    @Transactional
    public void delPaper(Integer id) {
        //删除试卷材料
        paperArticleService.batchDelByPaperId(id);
        //删除试卷题目
        paperEssayService.batchDelByPaperId(id);
        //删除试卷本身
        subjectivePaperMapper.delById(id);
    }

    //根据试卷id查询试卷、试卷材料、题目
    public Map<String, Object> getPaperDetail(Integer paperId) {
        SubjectivePaper paper = subjectivePaperMapper.selectById(paperId);
        List<PaperArticle> paperArticles = paperArticleService.selectByPaperId(paperId);
        List<PaperEssay> paperEssays = paperEssayService.selectByPaperId(paperId);
        Map<String, Object> result = new HashMap<>();
        result.put("paper", paper);
        result.put("paperArticles", paperArticles);
        result.put("paperEssays", paperEssays);
        return result;
    }
}

