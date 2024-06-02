package com.nowcoder.community.service;

import com.nowcoder.community.constant.ActivationConstant;
import com.nowcoder.community.constant.AuthorityConstant;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    /**
     * 注册
     * @param user
     * @return
     */
    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空~");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空~");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空~");
            return map;
        }

        //验证账号（用户名是否重复）
        User user1 = userMapper.selectByName(user.getUsername());
        if(user1 != null){
            map.put("usernameMsg", "用户名已存在~");
            return map;
        }

        //验证邮箱是否重复（因邮箱有限，故开发环境下取消邮箱不允许重复的限制）
//        user1 = userMapper.selectByEmail(user.getEmail());
//        if(user1 != null){
//            map.put("emailMsg", "该邮箱已经被注册~");
//            return map;
//        }

        //注册用户
        user.setSalt(CommunityUtil.createUUID().substring(0, 5));
        //设置密码加盐处理
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0); //0 为未激活
        user.setActivationCode(CommunityUtil.createUUID()); //设置激活码
        //设置默认头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件(邮件内容模板为 /mail/activation.html)
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //邮件内容中的激活地址为：http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号~~", content);

        return map; //最后没有出现任何问题，注册成功，则map为空
    }

    /**
     * 处理邮件激活
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);

        if(user.getStatus() == 1){
            return ActivationConstant.ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            return ActivationConstant.ACTIVATION_SUCCESS;
        }else {
            return ActivationConstant.ACTIVATION_FAILURE;
        }
    }

    /**
     * 处理登陆业务
     * @param username
     * @param password
     * @param expiredSeconds 希望登陆凭证保留的秒数
     * @return
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空哦~");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空哦~");
            return map;
        }

        //去数据库验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "该账号不存在，好好检查输入哦~");
            return map;
        }
        //验证状态
        if(user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活，留意您的邮箱信息哦~");
            return map;
        }
        //验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码不正确，好好检查输入哦~");
            return map;
        }

        //生成登陆凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.createUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    /**
     * 退出登陆
     * @param ticket
     */
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket, 1);
    }

    /**
     * 根据登陆凭证查询登陆信息
     * @param ticket
     * @return
     */
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    /**
     * 更新用户头像
     * @param id
     * @param headerUrl
     */
    public void updateHeader(int id, String headerUrl) {
        userMapper.updateHeader(id, headerUrl);
    }

    /**
     * 根据username 查询用户
     * @param username
     * @return
     */
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    /**
     * 查询用户的权限
     * @param userId
     * @return
     */
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AuthorityConstant.ADMIN;
                    case 2:
                        return AuthorityConstant.MODERATOR;
                    default:
                        return AuthorityConstant.USER;
                }
            }
        });
        return list;
    }

    /**
     * 查询用户总数
     * @return
     */
    public int findUserCount(){
        return userMapper.findUserCount();
    }

    /**
     * 查询所有用户
     * @param offset
     * @param limit
     * @return
     */
    public List<User> selectAllUser(int offset, int limit){
        return userMapper.selectAllUser(offset, limit);
    }

    /**
     * 激活/禁用用户账号 1激活0禁用
     * @param id
     * @param status
     */
    public int updateStatus(int id, int status) {
        return userMapper.updateStatus(id, status);
    }

    public void deleteById(int id) {
        userMapper.deleteById(id);
    }

    //设置权限（角色）
    public void updateType(Integer id, int type) {
        userMapper.updateType(id, type);
    }
}
