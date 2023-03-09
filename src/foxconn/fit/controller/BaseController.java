package foxconn.fit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public abstract class BaseController {

	public Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	public SpringValidatorAdapter validator;

	protected void out(Object result, HttpServletResponse response) throws IOException {
		ServletOutputStream out = response.getOutputStream();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(out, result);
		out.flush();
	}

	protected String getByLocale(Locale locale,String value){
		if (StringUtils.isNotEmpty(value) && value.indexOf("_")>0) {
			if (locale!=null && "en_US".equals(locale.toString())) {
				return value.substring(0,value.lastIndexOf("_"));
			}else{
				return value.substring(value.lastIndexOf("_")+1, value.length());
			}
		}
		return value;
	}

	protected String getLanguage(Locale locale,String zh,String en){
		if ("en_US".equals(locale.toString())) {
			return en;
		}else{
			return zh;
		}
	}
}
