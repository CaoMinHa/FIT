package foxconn.fit.controller.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.ebs.Parameter;
import foxconn.fit.service.ebs.ParameterService;
import foxconn.fit.util.ExceptionUtil;

@Controller
@RequestMapping("/admin/parameter")
public class ParameterController extends BaseController{

	@Autowired
	private ParameterService parameterService;
	
	@RequestMapping(value="/update")
	@ResponseBody
	public String update(HttpServletRequest request,AjaxResult ajaxResult,String value){
		try {
			Assert.hasText(value, "数据同步作业名称不能为空");
			
			Parameter task = parameterService.get("taskId");
			if (task==null) {
				task=new Parameter();
				task.setKey("taskId");
			}
			
			task.setValue(value);
			parameterService.save(task);
		}catch (Exception e) {
			logger.error("更新数据同步作业失败", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", "更新数据同步作业名称:" + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}
	
}