package com.goosen.commons.handler;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.goosen.commons.annotations.ResponseResult;
import com.goosen.commons.interceptor.ResponseResultInterceptor;
import com.goosen.commons.result.DefaultErrorResult;
import com.goosen.commons.result.PlatformResult;
import com.goosen.commons.result.Result;
import com.goosen.commons.utils.RequestContextUtil;

/**
 * 接口响应体处理器
 * @author Goosen
 * @since 2018-05-31 pm
 */
@ControllerAdvice
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		//System.out.println("进来ResponseResultHandler了1");
		ResponseResult responseResultAnn = (ResponseResult) RequestContextUtil.getRequest().getAttribute(ResponseResultInterceptor.RESPONSE_RESULT);
		return responseResultAnn == null ? false : true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		ResponseResult responseResultAnn = (ResponseResult) RequestContextUtil.getRequest().getAttribute(ResponseResultInterceptor.RESPONSE_RESULT);

		//System.out.println("进来ResponseResultHandler了2");
		Class<? extends Result> resultClazz = responseResultAnn.value();

		if (resultClazz.isAssignableFrom(PlatformResult.class)) {
			if (body instanceof DefaultErrorResult) {
				DefaultErrorResult defaultErrorResult = (DefaultErrorResult) body;
				PlatformResult platformResult = new PlatformResult();
				platformResult.setCode(defaultErrorResult.getCode());
				platformResult.setMessage(defaultErrorResult.getMessage());
				platformResult.setData(defaultErrorResult.getErrors());
				return platformResult;
//				return PlatformResult.builder()
//						.code(defaultErrorResult.getCode())
//						.msg(defaultErrorResult.getMessage())
//						.data(defaultErrorResult.getErrors())
//						.build();
			}

			return PlatformResult.success(body);
		}

		return body;
	}

}
