package com.goosen.commons.interceptor;

import com.goosen.commons.annotations.ResponseResult;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;


/**
 * 接口响应体控制拦截器
 * @author Goosen
 * @since 2018-05-31 pm
 */
@Component
public class ResponseResultInterceptor implements HandlerInterceptor {

	public static final String RESPONSE_RESULT = "RESPONSE-RESULT";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//System.out.println("进入ResponseResultInterceptor拦截器了");
		if (handler instanceof HandlerMethod) {
			final HandlerMethod handlerMethod = (HandlerMethod) handler;
			final Class<?> clazz = handlerMethod.getBeanType();
			final Method method = handlerMethod.getMethod();
			if (clazz.isAnnotationPresent(ResponseResult.class)) {
				request.setAttribute(RESPONSE_RESULT, clazz.getAnnotation(ResponseResult.class));
			} else if (method.isAnnotationPresent(ResponseResult.class)) {
				request.setAttribute(RESPONSE_RESULT, method.getAnnotation(ResponseResult.class));
			}
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		// nothing to do
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		// nothing to do
	}

}
