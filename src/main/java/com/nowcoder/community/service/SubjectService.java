package com.nowcoder.community.service;

import com.nowcoder.community.dao.SubjectMapper;
import com.nowcoder.community.entity.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectMapper subjectMapper;

    //根据id查询科目
    public Subject selectById(int id){
        return subjectMapper.selectById(id);
    }

    //分页查询所有科目
    public List<Subject> selectAll(int offset, int limit){
        return subjectMapper.selectAll(offset, limit);
    }


    //添加科目
    public int insertSubject(Subject subject){
        return subjectMapper.insertSubject(subject);
    }

    //查询科目总条数
    public int findRows() {
        return subjectMapper.findRows();
    }

    //根据名称查询科目
    public Subject selectByName(String name) {
        return subjectMapper.selectByName(name);
    }

    //更新科目
    public int updateSubject(Subject subject){
        return subjectMapper.updateSubject(subject);
    }

    //删除科目
    public void deleteById(Integer id){
        subjectMapper.deleteById(id);
    }

    //批量删除科目
    public int batchDel(Integer[] ids) {
        return subjectMapper.batchDel(ids);
    }

    //查询所有科目，不分页
    public List<Subject> getAll() {
        return subjectMapper.getAll();
    }
}
