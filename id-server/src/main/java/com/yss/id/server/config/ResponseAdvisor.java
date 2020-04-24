package com.yss.id.server.config;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yss.id.server.context.ContextFactory;
import com.yss.id.server.context.ContextSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一返回结构
 * 
 * @author LSP
 *
 */
@ControllerAdvice
public class ResponseAdvisor implements ResponseBodyAdvice<Object> {

	private Logger logger = LoggerFactory.getLogger(ResponseAdvisor.class);

	private final String BASE_PACKAGE = "com.yss";

	private final ObjectMapper mapper = new ObjectMapper();


	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		//只处理已com.yss开头的包
		return returnType.getContainingClass().getName().startsWith(BASE_PACKAGE);
	}

	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {

		ObjectNode rootNode = mapper.createObjectNode();

		ContextSupport context = ContextFactory.getContext();
		long endTime = System.currentTimeMillis();

		try {
			rootNode.put("uri", request.getURI().getPath());
			rootNode.put("status", ((ServletServerHttpResponse) response).getServletResponse().getStatus());
			rootNode.put("startTime", context.getParameter("startTime"));
			rootNode.put("endTime", endTime);
			rootNode.put("time",  endTime - Long.parseLong(context.getParameter("startTime")));
//			rootNode.put("response", JSON.toJSONString(body));

			logger.info(rootNode.toString());
		}catch (Exception e){
			e.printStackTrace();
		}

		return body;
	}

}