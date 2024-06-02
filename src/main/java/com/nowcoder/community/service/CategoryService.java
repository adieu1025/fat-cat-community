package com.nowcoder.community.service;

import com.nowcoder.community.dao.CategoryMapper;
import com.nowcoder.community.entity.Category;
import com.nowcoder.community.entity.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SubjectService subjectService;

    public Category selectById(int id) {
        return categoryMapper.selectById(id);
    }

    public List<Category> selectAll(int offset, int limit) {
        return categoryMapper.selectAll(offset, limit);
    }

    public int insertCategory(Category category) {
        return categoryMapper.insertCategory(category);
    }

    //根据科目id查询分类集合
    public List<Category> findBySubjectId(Integer id) {
        return categoryMapper.findBySubjectId(id);
    }

    public int findRows() {
        return categoryMapper.findRows();
    }

    public Category selectByName(String name) {
        return categoryMapper.findByName(name);
    }

    public int updateCategory(Category category) {
        return categoryMapper.updateCategory(category);
    }

    public int deleteById(Integer id) {
        return categoryMapper.deleteById(id);
    }

    //根据科目id查询分类的数量
    public int countBySubjectId(Integer id) {
        return categoryMapper.countBySubjectId(id);
    }

    //根据传入的科目id集合，批量查询分类的数量
    public int batchCountBySubId(Integer[] ids) {
        return categoryMapper.batchCountBySubId(ids);
    }

    //批量删除分类
    public int batchDel(Integer[] ids) {
        return categoryMapper.batchDel(ids);
    }

    //查询全部分类，不分页
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }

    //查找申论、面试科目下的分类列表
    public List<Category> findSubjectiveCate() {
        //查找科目id
        Subject subject = subjectService.selectByName("申论");
        Subject subject1 = subjectService.selectByName("面试");

        if(subject != null && subject1 != null) {
            //根据科目id批量查询分类
            List<Integer> subjectIds = new ArrayList<>();
            subjectIds.add(subject.getId());
            subjectIds.add(subject1.getId());
            return categoryMapper.batchSelectBySubjectId(subjectIds);
        }
        return null;
    }


    public int selectIdByName(String name) {
        return categoryMapper.selectIdByName(name);
    }
}
