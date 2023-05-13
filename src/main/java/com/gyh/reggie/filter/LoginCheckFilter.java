package com.gyh.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.gyh.reggie.common.BaseContext;
import com.gyh.reggie.common.R;
import com.gyh.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 检查用户是已经完成登录
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //在log中 {}相当于占位符，会将后面的数据放入其中
//        log.info("拦截到请求 : {}", request.getRequestURI());
        //过滤器具体的处理逻辑如下:
        // 1、获取本次请求的URI
        // 如果访问/backend/index.html但在urls字符串中却有通配符该如何处理呢
        String requestURI = request.getRequestURI();
        log.info("拦截到请求 : {}", requestURI);

        // 定义放行的请求路径
        String[] urls = new String[]{
          "/employee/login",
          "employee/logout",
          "/backend/**",
          "/front/**",
          "/user/sendMsg",//移动端发送短信
          "/user/login",//移动端登录页面

        };


        //2、判断本次请求是否需要处理
        boolean check = check(requestURI, urls);

        //3、如果不需要处理，则直接放行
        log.info("本次请求{} 不需要处理",requestURI);
        if(check){
            filterChain.doFilter(request,response);
            return;
        }
        //4-1、判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已经登陆,用户id为{}", request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        //4-2、判断移动端用户登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user") != null){
            log.info("用户已经登陆,用户id为{}", request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        // 5、如果未登录则返回未登录结果,通过输出流方式 向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;



    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String requestURI,String[] urls){
        for (String url : urls) {
            //第一个参数是遍历出来的每个链接
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }


}
