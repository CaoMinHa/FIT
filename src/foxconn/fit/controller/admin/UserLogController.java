package foxconn.fit.controller.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.controller.BaseController;
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.base.UserLog;
import foxconn.fit.service.base.UserLogService;

@Controller
@RequestMapping("/admin/userLog")
public class UserLogController extends BaseController{

	@Autowired
	private UserLogService userLogService;
	
	@RequestMapping(value = "/index")
	public String index(Model model,PageRequest pageRequest) {
		try {
			List<String> statusList = userLogService.listBySql("select distinct status from FIT_USER_LOG order by status ");
			List<String> methodList = userLogService.listBySql("select distinct method from FIT_USER_LOG order by method");
			List<String> userList = userLogService.listBySql("select distinct username from FIT_USER where username not like '%admin%' order by username");
			model.addAttribute("statusList", statusList);
			model.addAttribute("methodList", methodList);
			model.addAttribute("userList", userList);
		} catch (Exception e) {
			logger.error("查询用户日志信息失败:", e);
		}
		return "admin/userLog/index";
	}
	
	@RequestMapping(value="/list")
	public String list(Model model, PageRequest pageRequest,String operator,String method,String status,String startTime,String endTime) {
		try {
			List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
			
			if (StringUtils.isNotEmpty(operator)) {
				filters.add(new PropertyFilter("LIKES_operator",operator));
			}
			if (StringUtils.isNotEmpty(method)) {
				filters.add(new PropertyFilter("LIKES_method",method));
			}
			if (StringUtils.isNotEmpty(status)) {
				filters.add(new PropertyFilter("EQS_status",status));
			}
			if (StringUtils.isNotEmpty(startTime)) {
				filters.add(new PropertyFilter("GED_operatorTime",startTime));
			}
			if (StringUtils.isNotEmpty(endTime)) {
				filters.add(new PropertyFilter("LED_operatorTime",endTime));
			}
			
			Page<UserLog> page = userLogService.findPageByHQL(pageRequest, filters);
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询用户日志列表失败:", e);
		}
		return "/admin/userLog/list";
	}
	
}