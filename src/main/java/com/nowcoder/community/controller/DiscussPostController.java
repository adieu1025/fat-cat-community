package com.nowcoder.community.controller;


import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.constant.EntityConstant;
import com.nowcoder.community.constant.TopicConstant;
import com.nowcoder.community.entity.*;

import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.*;

import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import org.elasticsearch.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    //引入搜索业务层，查询帖子详情页面的相关帖子列表
    @Autowired
    private ElasticsearchService elasticsearchService;

    /**
     * 新增（发布）帖子
     * @param title
     * @param content
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录哦!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //触发发帖事件,通知 es 同步更新索引库
        Event event = new Event()
                .setTopic(TopicConstant.PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(EntityConstant.ENTITY_TYPE_POST)
                .setEntityId(post.getId())
                .setEntityUserId(post.getUserId());
        eventProducer.fireEvent(event);

        // 报错的情况,将来统一处理.
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    /**
     * 帖子详情
     * @param discussPostId
     * @param model
     * @return
     */
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        // 查询帖子作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        //存入当前登陆用户
        User loginUser = hostHolder.getUser();
        model.addAttribute("loginUser", loginUser);
        //查询帖子的赞数量
        long likeCount = likeService.findEntityLikeCount(EntityConstant.ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        //查询当前用户的点赞的状态（是否对当前帖子点赞？）
        int likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus
                (hostHolder.getUser().getId(), EntityConstant.ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                EntityConstant.ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                //查询评论的赞数量
                likeCount = likeService.findEntityLikeCount(EntityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                //查询当前用户的点赞的状态（是否对当前帖子点赞？）
                likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus
                        (hostHolder.getUser().getId(), EntityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);

                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        EntityConstant.ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        //查询回复的赞数量
                        likeCount = likeService.findEntityLikeCount(EntityConstant.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        //查询当前用户的点赞的状态（是否对当前帖子点赞？）
                        likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus
                                (hostHolder.getUser().getId(), EntityConstant.ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 回复数量
                int replyCount = commentService.findCommentCount(EntityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        //查询相关帖子（"你或许感兴趣"模块，固定查询5条进行展示）
        org.springframework.data.domain.Page<DiscussPost> searchResults = elasticsearchService.searchDiscussPost(post.getTitle(), 0, 4);
        //从searchPost中获取数据,装进list中，存到model
        List<DiscussPost> searchPosts = new ArrayList<>();
        if(searchResults != null){
            for(DiscussPost discussPost : searchResults){
                searchPosts.add(discussPost);
            }
        }
        model.addAttribute("searchPosts", searchPosts);

        return "/site/discuss-detail";
    }

    /**
     * 置顶/取消置顶帖子
     * @param id
     * @return
     */
    @PostMapping("/top")
    @ResponseBody
    public String setTop(int id){
        //获取置顶状态：1为置顶，0为普通，1^1=0, 0^1=1,新的type值直接取 当前type值异或1 即可
        DiscussPost discussPostById = discussPostService.findDiscussPostById(id);
        int type = discussPostById.getType() ^ 1;
        //调用业务层修改置顶状态
        discussPostService.updateType(id, type);

        //返回修改结果
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);

        //触发发帖事件
        Event event = new Event()
                .setTopic(TopicConstant.PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(id)
                .setEntityType(EntityConstant.ENTITY_TYPE_POST);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, null, map);
    }

    /**
     * 加精/取消加精帖子
     * @param id
     * @return
     */
    @PostMapping("/wonderful")
    @ResponseBody
    public String setWonderful (int id){
        DiscussPost discussPostById = discussPostService.findDiscussPostById(id);
        //1加精 0普通  1 ^ 1 = 0, 0 ^ 1 = 1
        int status = discussPostById.getStatus() ^ 1;
        discussPostService.updateStatus(id, status);

        //返回修改结果
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);

        //触发发帖事件(通知es更改帖子状态)
        Event event = new Event()
                .setTopic(TopicConstant.PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(id)
                .setEntityType(EntityConstant.ENTITY_TYPE_POST);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, null, map);
    }

    /**
     * 删除帖子
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public String delete (int id){
        discussPostService.updateStatus(id, 2);

        //触发删帖事件
        Event event = new Event()
                .setTopic(TopicConstant.DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(id)
                .setEntityType(EntityConstant.ENTITY_TYPE_POST);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

}
