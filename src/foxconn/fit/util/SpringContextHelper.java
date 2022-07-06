package foxconn.fit.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHelper implements ApplicationContextAware{

	private static ApplicationContext applicationContext;
	
	@SuppressWarnings("static-access")
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		if (this.applicationContext==null) {
			this.applicationContext=context;
		}
	}
	
	public static Object getBean(String beanName){
		if (applicationContext!=null) {
			return applicationContext.getBean(beanName);
		}
		
		return null;
	}
	
}
