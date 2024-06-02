package com.nowcoder.community.interceptor;


import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 登陆状态检查，通过注解@LoginRequired标记，若未登陆，则跳转到登陆页面
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果拦截到的是一个方法，那么该方法是 HandlerMethod 类型
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取拦截的方法
            Method method = handlerMethod.getMethod();
            //通过反射获取方法上的注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //当前方法需要登陆 && 没登陆，则需要拦截，重定向到登陆页面
            if (loginRequired != null && hostHolder.getUser() == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
