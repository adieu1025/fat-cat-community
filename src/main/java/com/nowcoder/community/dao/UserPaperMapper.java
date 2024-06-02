package com.nowcoder.community.dao;

import com.nowcoder.community.entity.UserPaper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserPaperMapper {

    void insertOne(UserPaper userPaper);

    UserPaper selectById(Integer id);

    List<UserPaper> selectByUserId(int id);

    int countByUidPid(int userId, int paperId);

    UserPaper selectByUidPid(int uId, int pId);

    int updateById(UserPaper userPaper);
}
