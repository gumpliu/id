package com.yss.id.server.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yss.id.server.context.ContextFactory;
import com.yss.id.server.context.ContextSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 打印日志
 *
 */
public class IdServerInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(IdServerInterceptor.class);

    private static final String SEGMENT = "segment";

    private static final String SNOWFLAKE = "snowflake";

    private IdServerProperties idServerProperties;

    public IdServerInterceptor(IdServerProperties idServerProperties){
        this.idServerProperties = idServerProperties;
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String lowerCaseUri = request.getRequestURI().toLowerCase();

            if(lowerCaseUri.contains(SEGMENT)) {
                if (!idServerProperties.getSegement().isEnable()) {
                    logger.error("segment 模式不可用");
                    return false;
                }
            }

            if(lowerCaseUri.contains(SNOWFLAKE)){
                if(!idServerProperties.getSnowflake().isEnable()){
                    logger.error("snowflake 模式不可用");
                    return false;
                }
            }

            Method method = ((HandlerMethod)handler).getMethod();

            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            //创建一个 json 对象，用来存放 http 日志信息

            //请求方式 Get Post
            String requestMethod =  requestWrapper.getMethod();
            JsonNode newNode;
            if (requestMethod.equals("GET")) {
                newNode = mapper.valueToTree(requestWrapper.getParameterMap());
            } else {
                newNode =  mapper.readTree(requestWrapper.getContentAsByteArray());
            }

            ObjectNode rootNode = mapper.createObjectNode();

            ContextSupport context = ContextFactory.getContext();
            context.setParameter("startTime", String.valueOf(System.currentTimeMillis()));

            rootNode.put("uri", requestWrapper.getRequestURI());
            rootNode.put("requestMethod", requestMethod);
            rootNode.put("className", ((HandlerMethod)handler).getBeanType().getName());
            rootNode.put("method", method.getName());
            rootNode.set("request", newNode);

            logger.info(rootNode.toString());
        }catch (Exception e){
            logger.error(e.getMessage());
        }

        return true;
    }
}