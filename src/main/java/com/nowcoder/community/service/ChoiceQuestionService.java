package com.nowcoder.community.service;

import com.nowcoder.community.dao.ChoiceQuestionMapper;
import com.nowcoder.community.entity.ChoiceQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class ChoiceQuestionService {

    @Autowired
    private ChoiceQuestionMapper choiceQuestionMapper;

    @Autowired
    private CategoryService categoryService;

    public ChoiceQuestion selectById(int id) {
        return choiceQuestionMapper.selectById(id);
    }

    public List<ChoiceQuestion> selectAll(int offset, int limit) {
        return choiceQuestionMapper.selectAll(offset, limit);
    }

    public int insertOne(ChoiceQuestion choiceQuestion) {
        return choiceQuestionMapper.insertOne(choiceQuestion);
    }

    //查询单选题数量
    public int selectSingleRows() {
        return choiceQuestionMapper.selectSingleRows();
    }

    //根据分类id查询题目
    public List<ChoiceQuestion> selectByCategoryId(Integer id) {
        return choiceQuestionMapper.selectByCategoryId(id);
    }

    //根据分类id查询题目数量
    public int countByCategoryId(Integer categoryId) {
        return choiceQuestionMapper.countByCategoryId(categoryId);
    }

    //根据分类id集合，去查询题目数量
    public int batchCountByCateId(Integer[] ids) {
        return choiceQuestionMapper.batchCountByCateId(ids);
    }

    //检查前端传入的数据是否合法
    public boolean checkSingle(ChoiceQuestion single) {
        if (single.getStem().equals("") && single.getImageUrl().equals("1")) {
            //题干为空且图片为空(imageUrl为1,因为前端此属性默认为1)，则说明无题干，
            // 需要提醒管理员添加题干或者题目图片（待完善）
            System.out.println("题干或者题目图片缺失！保存问题失败！");
            return false;
        }
        //判断答案
        String answer = single.getAnswer();
        if (answer.equals("")) {
            System.out.println("答案缺失！保存问题失败！");
            return false;
        } else {
            //判断是单选题还是多选题
            if (answer.length() > 1) {
                single.setIsMultiple(1);
            }
        }
        //判断分类
        if (categoryService.selectById(single.getCategoryId()) == null) {
            System.out.println("分类数据缺失！保存问题失败！");
            return false;
        }
        return true;
    }

    public int updateSingle(ChoiceQuestion single) {
        return choiceQuestionMapper.updateSingle(single);
    }

    //查询所有问题的id
    public List<Integer> findIds() {
        return choiceQuestionMapper.findIds();
    }

    //根据id集合批量查询问题
    public List<ChoiceQuestion> batchSelectByIds(List<Integer> ids) {
        return choiceQuestionMapper.batchSelectByIds(ids);
    }

    //根据id删除题目,（把id_delete字段改为1）
    public int delById(Integer id) {
        return choiceQuestionMapper.delById(id);
    }

    //根据id集合批量删除
    public int batchDel(Integer[] ids) {
        return choiceQuestionMapper.batchDel(ids);
    }

    //根据id集合查询答案
    public List<String> selectBatchKeysByIds(Collection<Integer> quesIds) {
        return choiceQuestionMapper.selectBatchKeysByIds(quesIds);
    }

    //根据分类id批量查询问题的id
    public List<Integer> findIdsByCateId(int id) {
        return choiceQuestionMapper.findIdsByCateId(id);
    }
}
