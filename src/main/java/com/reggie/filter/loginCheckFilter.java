package com.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.reggie.common.R;
import com.reggie.common.baseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//检查用户是否完成登录
@WebFilter(filterName = "loginCheckFilter" ,urlPatterns = "/*")
@Slf4j
//@Component
public class loginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取uri并判断是否符合条件
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/favicon.ico",
                "/orderDetail/**",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };
        boolean check = check(requestURI, urls);
        //不需要处理直接放行
        if (check) {
            log.info("本次请求{}无需处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //判断登录状态
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录");
            Long empId = (Long) request.getSession().getAttribute("employee");
            log.info("登录用户id为{}",empId);
            baseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //判断移动端登录状态
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录");
            Long id = (Long) request.getSession().getAttribute("user");
            log.info("登录用户id为{}",id);
            baseContext.setCurrentId(id);
            filterChain.doFilter(request,response);
            return;
        }
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }
    public boolean check(String uri,String[] urls){
        for (String url : urls) {
            boolean match = ANT_PATH_MATCHER.match(url, uri);
            if (match) return true;
        }
        return false;
    }
}
