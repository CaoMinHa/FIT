package foxconn.fit.controller.unauth;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import foxconn.fit.controller.BaseController;
import foxconn.fit.service.base.UserService;
import foxconn.fit.util.ExceptionUtil;

@Controller
@RequestMapping("/unauth")
public class UnauthController extends BaseController{
	private Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "alarmCallback")
	public void alarmCallback(HttpServletRequest request,HttpServletResponse response,Model model,String id){
		try {
			Assert.hasText(id, "预警发送记录ID不能为空");
			List<Object[]> list = userService.listBySql("select reporturlpc,reporturlphone,reporturlpad from BIDEV.SIXP_INTERFACE where id='"+id+"'");
			Assert.isTrue(list!=null && list.size()==1, "未查询到对应的预警发送记录");
			Object[] objects = list.get(0);
			String pcUrl=objects[0].toString();
			String phoneUrl=objects[1].toString();
			String padUrl=objects[2].toString();
			String redirectUrl=pcUrl;
			String User_Agent = request.getHeader("User-Agent");
			if (User_Agent.contains("Android") || User_Agent.contains("iPhone") || User_Agent.contains("Linux")) {
				redirectUrl=phoneUrl;
			}else if (User_Agent.contains("iPad")) {
				redirectUrl=padUrl;
			}
			
			response.sendRedirect(redirectUrl);
		} catch (Exception e) {
			logger.error("重定向失败【ID = "+id+"】", e);
			try {
				response.setHeader("Content-type", "text/html;charset=UTF-8");
				PrintWriter writer = response.getWriter();
				writer.write(ExceptionUtil.getRootCauseMessage(e));
				writer.flush();
			} catch (IOException e1) {
			}
		}
	}

}
