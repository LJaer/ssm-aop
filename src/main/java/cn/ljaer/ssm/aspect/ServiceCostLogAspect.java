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
