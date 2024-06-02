package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 根据实体查询评论
     * @param entityType 实体类型（帖子评论、评论的评论...）
     * @param entityId 实体id
     * @param offset 分页：当前页起始行
     * @param limit 分页：展示条数
     * @return 实体的集合
     */
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 根据实体查询评论总条数
     * @param entityType
     * @param entityId
     * @return
     */
    int selectCountByEntity(int entityType, int entityId);

    /**
     * 新增评论
     * @param comment
     * @return
     */
    int insertComment(Comment comment);

    /**
     * 根据id查询评论
     * @param entityId
     * @return
     */
    Comment findCommentById(int entityId);
}
