package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，登陆状态检查，标记在方法上，可以拦截请求，防止用户通过浏览器地址栏访问不该访问的页面、
 * 点击不该点击的按钮（比如点赞、评论、关注...）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {

}
