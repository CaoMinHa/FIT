package foxconn.fit.advice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import foxconn.fit.entity.base.UserLog;
import foxconn.fit.service.base.UserLogService;
import foxconn.fit.util.SecurityUtils;

@Aspect
@Component
public class LogAdvice {
	private org.apache.commons.logging.Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private UserLogService userLogService;
	
	@Pointcut("execution (* foxconn.fit.controller..*.*(..))") 
	public  void controllerAspect() {}
	
	@Around("controllerAspect()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		Object retVal = joinPoint.proceed();
		try {
			String methodName = joinPoint.getSignature().getName();
			Method[] declaredMethods = joinPoint.getTarget().getClass().getDeclaredMethods();
			for (Method m : declaredMethods) {
				if (m.getName().equals(methodName)) {
					Log log = m.getAnnotation(Log.class);
					if (log!=null) {
						Object[] args = joinPoint.getArgs();
						StringBuffer paramString=new StringBuffer();
						Annotation[][] parameterAnnotations = m.getParameterAnnotations();
						for (int i = 0; i < parameterAnnotations.length; i++) {
							Annotation[] annotations = parameterAnnotations[i];
							for (int j = 0; j < annotations.length; j++) {
								Annotation annotation = annotations[j];
								if (annotation instanceof Log) {
									Log paramLog=(Log) annotation;
									paramString.append(paramLog.name()).append("=");
									if (args[i]!=null && args[i].getClass().isArray()) {
										Object[] objs=(Object[]) args[i];
										paramString.append(Arrays.toString(objs));
									}else {
										paramString.append(args[i]);
									}
									paramString.append(";	");
									break;
								}
							}
						}
						String status="成功";
						String message="";
						if (retVal instanceof String) {
							try {
								JSONObject json = JSONObject.parseObject((String)retVal);
								status=json.getString("flag");
								if ("fail".equals(status)) {
									status="失败";
								}else {
									status="成功";
								}
								message=json.getString("msg");
								if (message.length()>500) {
									message=message.substring(0,500);
								}
							} catch (Exception e) {
							}
						}
						String param = paramString.toString();
						if (param.length()>1000) {
							param=param.substring(0,1000);
						}
						UserLog userLog=new UserLog();
						userLog.setMethod(log.name());
						userLog.setParameter(param);
						userLog.setStatus(status);
						userLog.setMessage(message);
						userLog.setOperator(SecurityUtils.getLoginUsername());
						userLog.setOperatorTime(new Date());
						userLogService.save(userLog);
						
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("记录操作日志错误:", e);
		}
		return retVal;
	}
	
}
