# spring4_springmvc_mybatis
环境：
win7
tomcat8
jdk8
spring4
##maven搭建的spring4+springmvc+mybatis框架

并进行简单测试
创建test数据库并导入sql文件夹下的sql语句建表

在浏览器输入http://localhost:8080/ssm/selectByPrimaryKey?id=1

可得到：User{id=1, username='zk', password='123'}

## Spring AOP 测试 ，Spring MVC　interceptor 测试

###　１、在 Service 层使用 AOP 打印 Service 日志

applicationContext-service.xml

	<bean class="cn.ljaer.ssm.aspect.ServiceCostLogAspect" />
	
ServiceCostLogAspect.java

```java
package cn.ljaer.ssm.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Aspect
public class ServiceCostLogAspect {

	/**
	 * Pointcut
	 * 定义Pointcut，Pointcut的名称为aspectjMethod()，此方法没有返回值和参数
	 * 该方法就是一个标识，不进行调用
	 */
	@Pointcut("execution(public * cn.ljaer.ssm.service.*.*(..))")
	private void aspectjMethod(){}

    @Before("aspectjMethod()")
    public void before() {
        log.info("已经记录下操作日志@Before 方法执行前");
    }
    
    @After("aspectjMethod()")
    public void after() {
    	log.info("已经记录下操作日志@After 方法执行后");
    }
    
    @AfterReturning("aspectjMethod()")
    public void afterReturning() {
    	log.info("已经记录下操作日志@AfterReturning 返回参数之后");
    }
    
    @AfterThrowing("aspectjMethod()")
    public void afterThrowing() {
    	log.info("已经记录下操作日志@AfterThrowing 方法执行后抛出异常时");
    }
	
	@Around(value = "aspectjMethod()")
	public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {

		String className = pjp.getTarget().getClass().getName();
		String methodName = pjp.getSignature().getName();
		Object[] args = pjp.getArgs();

		StringBuilder logMsg = new StringBuilder("\nService execute report -------- " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " ----------------------------------------");
		logMsg.append("\nService   : ").append(className);
		logMsg.append("\nMethod    : ").append(methodName);
		logMsg.append("\nParameter : ").append(Joiner.on(",").join(Lists.transform(Arrays.asList(args), new Function<Object, String>() {
			@Override
			public String apply(Object input) {
				return JSONObject.toJSONString(input);
			}
		})));

		long startTime = System.currentTimeMillis();
		Object retVal = null;
		try {
			retVal = pjp.proceed();

			logMsg.append("\nResult    : ").append(JSONObject.toJSON(retVal));
			logMsg.append("\nCost Time : ").append(System.currentTimeMillis() - startTime).append(" ms");
			logMsg.append("\n--------------------------------------------------------------------------------------------");
			log.info(logMsg.toString());
			return retVal;
		} catch (Throwable e) {
			log.error(className + "."+ methodName + " Occur Exception : ", e);
			throw e;
		}
	}
}
```

###　2、在 Controller 层使用 Interceptor 打印 Controller 日志
	
springmvc.xml

```
<!-- 配置Controller拦截器 -->
<mvc:interceptors>
	<!-- 使用bean定义一个Interceptor，直接定义在mvc:interceptors根下面的Interceptor将拦截所有的请求 -->
	<bean class="cn.ljaer.ssm.interceptor.ControllerCostLogInterceptor" />
	<mvc:interceptor>
		<mvc:mapping path="/**" />
		<mvc:exclude-mapping path="/easyui/**" />
		<mvc:exclude-mapping path="/js/**" />
		<mvc:exclude-mapping path="/html/**" />
		<mvc:exclude-mapping path="/diagram-viewer/**" />
		<mvc:exclude-mapping path="/editor-app/**" />
		<mvc:exclude-mapping path="/static/**" />
		<mvc:exclude-mapping path="/css/**" />
		<mvc:exclude-mapping path="/images/**" />
		<mvc:exclude-mapping path="/scripts/**" />
		<mvc:exclude-mapping path="/themes/**" />
		<!-- 定义在mvc:interceptor下面的表示是对特定的请求才进行拦截的 -->
		<bean class="cn.ljaer.ssm.interceptor.ControllerCostLogInterceptor" />
	</mvc:interceptor>
</mvc:interceptors>
```

ControllerCostLogInterceptor.java

```java
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

```

在浏览器输入http://localhost:8080/ssm/redis/getRedis

可得到：User{id=1, username='zk', password='123'}

控制台打印：

```
 INFO [http-nio-8080-exec-13] - 已经记录下操作日志@Before 方法执行前
DEBUG [http-nio-8080-exec-13] - Creating a new SqlSession
DEBUG [http-nio-8080-exec-13] - Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@58583a5b]
DEBUG [http-nio-8080-exec-13] - Fetching JDBC Connection from DataSource
DEBUG [http-nio-8080-exec-13] - Registering transaction synchronization for JDBC Connection
DEBUG [http-nio-8080-exec-13] - JDBC Connection [jdbc:mysql://192.168.99.100:3306/test, UserName=root@192.168.99.1, MySQL Connector Java] will be managed by Spring
DEBUG [http-nio-8080-exec-13] - ==>  Preparing: select id, username, password from user where id = ? 
DEBUG [http-nio-8080-exec-13] - ==> Parameters: 1(Integer)
DEBUG [http-nio-8080-exec-13] - <==      Total: 1
DEBUG [http-nio-8080-exec-13] - Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@58583a5b]
 INFO [http-nio-8080-exec-13] -
Service execute report -------- 2017-09-01 17:53:23 ----------------------------------------
Service   : cn.ljaer.ssm.service.impl.UserServiceImpl
Method    : selectByPrimaryKey
Parameter : 1
Result    : {"id":1,"password":"123","username":"zk"}
Cost Time : 549 ms
--------------------------------------------------------------------------------------------
 INFO [http-nio-8080-exec-13] - 已经记录下操作日志@After 方法执行后
 INFO [http-nio-8080-exec-13] - 已经记录下操作日志@AfterReturning 返回参数之后
DEBUG [http-nio-8080-exec-13] - Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@58583a5b]
DEBUG [http-nio-8080-exec-13] - Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@58583a5b]
DEBUG [http-nio-8080-exec-13] - Returning JDBC Connection to DataSource
DEBUG [http-nio-8080-exec-13] - Null ModelAndView returned to DispatcherServlet with name 'springmvc_rest': assuming HandlerAdapter completed request handling
 INFO [http-nio-8080-exec-13] - 
Controller execute report -------- 2017-09-01 17:53:23 -------------------------------------
URI         : /ssm/selectByPrimaryKey, Method : GET
Controller  : cn.ljaer.ssm.controller.UserController, Method : selectByPrimaryKey
QueryString : id=1
Cost Time   : 700 ms
--------------------------------------------------------------------------------------------
```
