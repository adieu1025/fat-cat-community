package com.nowcoder.community.controller.admin;

import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台用户管理
 */
@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 跳转到管理页面首页
     * @return
     */
    @GetMapping("/index")
    public String adminIndex(Model model){
        //获取当前登陆用户，给后台管理页面提供用户名回显
        User user = hostHolder.getUser();
        if(user != null) {
            model.addAttribute("loginAdmin", user);
        }
        return "/site/admin/admin-index";
    }

    /**
     * 后台管理用户列表
     * @param model
     * @param page
     * @return
     */
    @GetMapping("/userList")
    public String getUserList(Model model, Page page){
        //分页信息
        page.setPath("/admin/userList");
        page.setRows(userService.findUserCount());

        List<User> userList = userService.selectAllUser(page.getOffset(), page.getLimit());

        //获取当前登陆用户，给后台管理页面提供用户名回显
        User user = hostHolder.getUser();
        if(user != null) {
            model.addAttribute("loginAdmin", user);
        }

        model.addAttribute("userList", userList);

        return "/site/admin/user-manager";
    }

    /**
     * 激活/禁用 用户账号
     * @param id
     * @return
     */
    @PostMapping("/setUserStatus")
    @ResponseBody
    public String updateStatus(Integer id){
        //获取用户状态，1为激活，0为禁用，1^1=0，1^0=1
        User user = userService.findUserById(id);
        int status = user.getStatus()^1;
        //调用业务层修改状态
        userService.updateStatus(id, status);

        //返回修改结果
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);

        return CommunityUtil.getJSONString(0, null, map);
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @PostMapping("/delUser")
    @ResponseBody
    public String delUser(Integer id){
        //查询该用户发布的帖子
        int discussPostRows = discussPostService.findDiscussPostRows(id);
        if(discussPostRows < 1){
            //未发布过帖子，可以删除
            userService.deleteById(id);
            return CommunityUtil.getJSONString(0);
        } else {
            return CommunityUtil.getJSONString(403, "存在帖子绑定该用户信息，删除失败。");
        }
    }

    /**
     * 设置用户权限
     * @param id
     * @return
     */
    @GetMapping("/setPermissions/{id}")
    public String setPermissions(@PathVariable("id") Integer id) {
        //0普通用户，1超级管理员，1^1=0，1^0=1
        User user = userService.findUserById(id);
        int type = user.getType()^1;
        userService.updateType(id, type);
        return "redirect:/admin/userList";
    }

}
