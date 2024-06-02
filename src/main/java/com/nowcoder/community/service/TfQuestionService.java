package com.nowcoder.community.service;

import com.nowcoder.community.dao.TfQuestionMapper;
import com.nowcoder.community.entity.TfQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TfQuestionService {

    @Autowired
    private TfQuestionMapper tfQuestionMapper;

    @Autowired
    private CategoryService categoryService;

    public int selectRows() {
        return tfQuestionMapper.selectRows();
    }

    public List<TfQuestion> selectPage(int offset, int limit) {
        return tfQuestionMapper.selectPage(offset, limit);
    }

    public boolean check(TfQuestion tfQuestion) {
        //判断参数是否合规
        if(tfQuestion.getStem().equals("") && tfQuestion.getImageUrl().equals("1")) {
            System.out.println("题干或者题目图片缺失！保存问题失败！");
            return false;
        }
        //判断答案
        int answer = tfQuestion.getAnswer();
        if (answer != 0 && answer != 1) {
            System.out.println("答案缺失！保存问题失败！");
            return false;
        }
        //判断分类
        if (categoryService.selectById(tfQuestion.getCategoryId()) == null) {
            System.out.println("分类数据缺失！保存问题失败！");
            return false;
        }

        return true;
    }

    public void insertOne(TfQuestion tfQuestion) {
        if(check(tfQuestion)) {
            tfQuestionMapper.insertOne(tfQuestion);
        }

    }

    public TfQuestion selectById(Integer tfId) {
        return tfQuestionMapper.selectById(tfId);
    }

    public void updateOne(TfQuestion tf) {
        if(check(tf)) {
            tfQuestionMapper.updateOne(tf);
        }
    }

    public void delById(Integer id) {
        tfQuestionMapper.delById(id);
    }
}
