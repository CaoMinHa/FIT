package foxconn.fit.controller.unauth;

import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.MenuMappingList;
import foxconn.fit.entity.ebs.Parameter;
import foxconn.fit.service.base.MenuService;
import foxconn.fit.service.ebs.ParameterService;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;
import foxconn.fit.util.VerifyCodeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Controller
public class MainController extends BaseController{

	private Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private ParameterService parameterService;
	@Autowired
	private MenuService menuService;
	
	@RequestMapping(value = {"index","","welcome"})
	public String index(HttpServletRequest request,Model model){
		Locale locale1 = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		String locale="zh";
		Locale local=Locale.US;
		if(null != locale1){
			locale=locale1.getLanguage();
		}
		HttpSession session = request.getSession(false);
		if (!"en".equals(locale)) {
			locale="zh_CN";
			local=Locale.SIMPLIFIED_CHINESE;
		}else{
			locale="en_US";
		}
		session.setAttribute("languageS",locale);
		WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, local);
		if (SecurityUtils.getLoginUser()!=null) {
			try {
				Parameter task = parameterService.get("taskId");
				String taskId="";
				if (task!=null) {
					taskId=task.getValue();
				}
				model.addAttribute("taskId", taskId);
				
				Collection<GrantedAuthority> authorities = SecurityUtils.getLoginUser().getAuthorities();
				if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
					model.addAttribute("isAdmin", "true");
				}
				if(authorities.contains(new SimpleGrantedAuthority("ROLE_BI"))){
					model.addAttribute("menuBI", "Y");
					List<MenuMappingList> list=menuService.selectMenu();
					session.setAttribute("menuList",list);
				}
			} catch (Exception e) {
				logger.error("查询数据同步任务失败", e);
			}
			return "welcome";
		}else {
			return "redirect:login?lang="+locale;
		}
	}

	/**
	 * 指向登录页面
	 */
	@RequestMapping(value = "/login")
	public String getLoginPage(ModelMap model,@RequestParam(value="error",required=false) boolean error,HttpServletRequest request,HttpServletResponse response,
							   String lang,String task,String taskId,String statusType,String roleCode) {
		try {
			HttpSession session = request.getSession(false);
			Locale local=Locale.US;
			List<MenuMappingList> list=menuService.selectMenu();
			session.setAttribute("menuList",list);
			session.setAttribute("languageS","en_US");
			if (!"en_US".equals(lang)) {
				local=Locale.SIMPLIFIED_CHINESE;
				session.setAttribute("languageS","zh_CN");
			}
			WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, local);
			if(null!=task){
				session.setAttribute("task","Y");
			}else{
				session.setAttribute("task","N");
			}
			if(null!=taskId&&null!=statusType){
				session.setAttribute("roleCode",roleCode);
				session.setAttribute("statusType",statusType);
				session.setAttribute("taskId",taskId);
				session.setAttribute("detailsTsak","Y");
			}else if(null!=taskId&&null==statusType){
				session.setAttribute("detailsTsak","ok");
				session.setAttribute("taskId",taskId);
			}else{
				session.setAttribute("detailsTsak","N");
			}
			if (error) {
				Object message = request.getAttribute("message");
				if (message!=null && message instanceof String) {
					model.put("error", message);
				}else {
					if (session!=null) {
						Object exception = session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
						if (exception==null) {
							model.put("error","logon_error");
						}else if (exception instanceof BadCredentialsException) {
							model.put("error","logon_error");
						}else if (exception instanceof LoginException) {
							model.put("error",ExceptionUtil.getRootCauseMessage((Exception)exception));
						}else if (exception instanceof CredentialExpiredException) {
							model.put("error","logon_expire_error");
						}else {
							model.put("error","logon_error");
						}
					}else {
						model.put("error", "");
					}
				}
			}else {
				model.put("error", "");
			}
		} catch (Exception e) {
			model.put("error", "");
		}

		return "login";
	}

	/**
	 * 指定无访问额权限页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/denied")
	public String getDeniedPage() {
		logger.error("访问受限!");
		
		return "error/denied";
	}
	
	/**
	 * 默认错误页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/error")
	public String error() {
		return "error/error";
	}
	
    @RequestMapping(value = "getVerifyCode")
    public void getImage(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "width", defaultValue = "80") int width,
            @RequestParam(value = "height", defaultValue = "34") int height) throws Exception {
        VerifyCodeUtils image = new VerifyCodeUtils(width, height, request, response);
        image.getImage();
    }
    
    @RequestMapping(value="checkVerifyCode")
    @ResponseBody
	public String checkVerifyCode(String verifyCode,HttpServletRequest request) throws Exception{
		String result="error";
		try {
			if (StringUtils.isNotEmpty(verifyCode)) {
				HttpSession session = request.getSession(false);
				if (session!=null) {
					Object attr = session.getAttribute("verifyCode");
					long time = (Long) session.getAttribute("verifyCodeTime");
					
					if ((System.currentTimeMillis()-time)>2*60*1000) {
						result="expired";
						return result;
					}else if (verifyCode.toUpperCase().equals(attr)) {
						result="";
					}	
				}
			}
		} catch (Exception e) {
			logger.error("验证码校验错误:", e);
		}
		
		return result;
	}
	
}
