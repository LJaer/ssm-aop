package cn.ljaer.ssm.interceptor;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class ControllerCostLogInterceptor extends HandlerInterceptorAdapter {

	private NamedThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("StopWatch-StartTime");

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//设置开始时间
		startTimeThreadLocal.set(System.currentTimeMillis());
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long endTime = System.currentTimeMillis();
		long beginTime = startTimeThreadLocal.get();
		long consumeTime = endTime - beginTime;

		StringBuilder logMsg = new StringBuilder("\nController execute report -------- " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
				+ " -------------------------------------");
		logMsg.append("\nURI         : ").append(request.getRequestURI()).append(", Method : ")
				.append(request.getMethod());
		logMsg.append("\nController  : ").append(((HandlerMethod) handler).getBeanType().getName())
				.append(", Method : ").append(((HandlerMethod) handler).getMethod().getName());

		if (request.getMethod().equalsIgnoreCase("GET")) {
			logMsg.append("\nQueryString : ").append(URLDecoder.decode(StringUtils.isBlank(request.getQueryString()) ? "" : request.getQueryString(),"UTF-8"));
		} else if (request.getMethod().equalsIgnoreCase("POST")) {
			logMsg.append("\nParameter   : ").append(JSONObject.toJSON(request.getParameterMap()));
		}

		logMsg.append("\nCost Time   : ").append(consumeTime).append(" ms");
		logMsg.append("\n--------------------------------------------------------------------------------------------");
		log.info(logMsg.toString());
		
		startTimeThreadLocal.remove();

	}
}
