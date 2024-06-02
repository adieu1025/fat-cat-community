package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.constant.EntityConstant;
import com.nowcoder.community.constant.TopicConstant;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.utils.HostHolder;
import com.sun.jndi.cosnaming.CNNameParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;


    /**
     * 新增评论（回帖）
     * @param discussPostId
     * @param comment
     * @return
     */
    @PostMapping("/add/{discussPostId}")
    @LoginRequired
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发评论事件，系统发送消息通知被评论的用户
        Event event = new Event()
                .setTopic(TopicConstant.COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if(comment.getEntityType() == EntityConstant.ENTITY_TYPE_POST) {
            //如果评论的目标是一个帖子
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if(comment.getEntityType() == EntityConstant.ENTITY_TYPE_COMMENT){
            //如果评论的目标是一个评论
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        //以上添加帖子评论的同时，也对帖子的评论数进行了修改，相当于更新了帖子，也需要通知 es 去修改索引库
        if(comment.getEntityType() == EntityConstant.ENTITY_TYPE_POST) {
            //只有在对帖子评论时才触发，对评论进行评论不触发
            event = new Event()
                    .setTopic(TopicConstant.PUBLISH)
                    .setEntityType(EntityConstant.ENTITY_TYPE_POST)
                    .setEntityId(discussPostId)
                    .setUserId(comment.getUserId());
            eventProducer.fireEvent(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }

}
