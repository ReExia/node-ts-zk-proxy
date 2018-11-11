package com.salty.provider.core.inteceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class HttpInteceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpInteceptor.class);

    private static final String START_TIME = "requestStartTime";

    /**
     * 拦截请求url和请求参数
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //请求时间和请求参数
        String url = request.getRequestURI();
        Map<String, String[]> parameterMap = request.getParameterMap();
        long start = System.currentTimeMillis();
        logger.info("requets start | url : [{}], params : [{}]", url);
        //放入请求初始时间
        request.setAttribute(START_TIME, start);
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //todo 此处移除用户信息 用户信息放在 threadLocal中
    }

    /**
     * 请求结束后，计算url花费时间
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String url  = request.getRequestURI();
        long start = (long) request.getAttribute(START_TIME);
        long end = System.currentTimeMillis();
        long costTime = end - start;
        //todo 此处移除用户信息
    }
}
