package foxconn.fit.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import foxconn.fit.entity.base.*;
import foxconn.fit.service.base.MenuService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.controller.BaseController;
import foxconn.fit.service.base.UserDetailImpl;
import foxconn.fit.service.base.UserService;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;

@Controller
@RequestMapping("/admin/user")
public class UserController extends BaseController{

	@Autowired
	private UserService userService;
	@Autowired
	private MenuService menuService;
	@RequestMapping(value = "/index")
	public String index(Model model,PageRequest pageRequest) {
		try {
			List<MenuMapping> list=menuService.selectMenuAll();
			model.addAttribute("menuList",list);
			String sql="select id,type,username from FIT_USER t where t.type in ('HFM','BI') order by username";
			List userList = userService.listBySql(sql);
			model.addAttribute("userList", userList);
			List sbuList = userService.listBySql("select distinct parent from fit_dimension where type='"+EnumDimensionType.Entity+"' order by parent");
			model.addAttribute("sbuList", sbuList);
		} catch (Exception e) {
			logger.error("查询用户列表失败:", e);
		}
		return "admin/user/index";
	}
	
	@RequestMapping(value = "/getUserInfo")
	@ResponseBody
	public String getUserInfo(AjaxResult ajaxResult,String userId) {
		try {
			User user = userService.get(userId);
			Assert.notNull(user, "用户不存在");
			String corporationCode = user.getCorporationCode();
			String[] corpCodes=null;
			if (StringUtils.isNotEmpty(corporationCode)) {
				corpCodes = user.getCorporationCode().split(",");
			}
			ajaxResult.put("corpCodes", corpCodes);
		} catch (Exception e) {
			logger.error("查询用户信息失败:", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", "查询用户信息失败:" + ExceptionUtil.getRootCauseMessage(e));
		}
		return ajaxResult.getJson();
	}
	
	@RequestMapping(value="/list")
	public String list(Model model, PageRequest pageRequest,String username,String corporationCode,String type) {
		try {
			String sql="select * from FIT_USER t where t.username <> 'admin'";
			if (StringUtils.isNotEmpty(username)) {
				sql+=" and t.username like '%"+username+"%'";
			}
			if (StringUtils.isNotEmpty(corporationCode)) {
				sql+=" and t.corporation_code like '%"+corporationCode+"%'";
			}
			if (StringUtils.isNotEmpty(type)) {
				sql+=" and t.type='"+type+"'";
			}
			
			sql+=" order by t.update_time desc,type asc";
			
			Page<Object[]> page = userService.findPageBySql(pageRequest, sql,User.class);
			
			model.addAttribute("page", page);
		} catch (Exception e) {
			logger.error("查询用户列表失败:", e);
		}
		return "/admin/user/list";
	}
	
	@RequestMapping(value="/detail")
	public String detail(PageRequest pageRequest,Model model,String id){
		try {
			List<MenuMapping> list=menuService.selectMenuAll();
			model.addAttribute("menuList",list);

			Assert.hasText(id, "用户ID不能为空");
			User user = userService.get(id);
			if (StringUtils.isNotEmpty(user.getCorporationCode())) {
				String[] codes = user.getCorporationCode().split(",");
				model.addAttribute("codeList", codes);
			}
			if (StringUtils.isNotEmpty(user.getEntity())) {
				String[] entitys = user.getEntity().split(",");
				model.addAttribute("entityList", entitys);
			}
			if (StringUtils.isNotEmpty(user.getEbs())) {
				String[] ebs = user.getEbs().split(",");
				model.addAttribute("ebsList", ebs);
			}
			model.addAttribute("user", user);
			
			if (user.getType()==EnumUserType.BI) {
				String menu = user.getMenus();
				if (StringUtils.isNotEmpty(menu)) {
					String[] menus = user.getMenus().split(",");
					model.addAttribute("menus", menus);
				}
			}else if (user.getType()==EnumUserType.Budget) {
				String menu = user.getMenus();
				if (StringUtils.isNotEmpty(menu)) {
					String[] menus = user.getMenus().split(",");
					model.addAttribute("menus", menus);
				}
				List sbuList = userService.listBySql("select distinct parent from fit_dimension where type='"+EnumDimensionType.Entity+"' order by parent");
				model.addAttribute("sbuList", sbuList);
			}
		} catch (Exception e) {
			logger.error("查询用户信息失败:", e);
		}
		
		return "/admin/user/detail";
	}
	
	@RequestMapping(value="/delete")
	@ResponseBody
	public String delete(HttpServletRequest request,AjaxResult ajaxResult,Model model,String id){
		try {
			Assert.hasText(id, "用户ID不能为空(User Not Found)");
			User user = userService.get(id);
			Assert.notNull(user, "用户不存在(User Not Found)");
			userService.delete(id);
		} catch (Exception e) {
			logger.error("删除用户失败:", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", "删除用户失败(Delete User Fail):" + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}
	
	@RequestMapping(value="/resetPwd")
	@ResponseBody
	public String resetPwd(HttpServletRequest request,AjaxResult ajaxResult,Model model,String id){
		try {
			Assert.hasText(id, "用户ID不能为空(User Not Found)");
			User user = userService.get(id);
			Assert.notNull(user, "用户不存在(User Not Found)");
			Md5PasswordEncoder encoder=new Md5PasswordEncoder();
			user.setPassword(encoder.encodePassword("11111111", null));
			userService.save(user);
			ajaxResult.put("msg", "重置密碼成功(Reset Password Success)");
		} catch (Exception e) {
			logger.error("重置密码失败:", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", "重置密碼失败(Reset Password Fail):" + ExceptionUtil.getRootCauseMessage(e));
		}
		return ajaxResult.getJson();
	}
	
	@RequestMapping(value="/add")
	@ResponseBody
	public String add(HttpServletRequest request,AjaxResult ajaxResult,String username,String type,String attribute,String[] menus,String[] corporationCode,boolean[] readonly,String[] entity,boolean[] entityReadonly,String[] ebs,boolean[] ebsReadonly,String[] sbu){
		try {
			Assert.hasText(username, "用户名不能为空(User Not Null)");
			User existUser = userService.getByUsername(username);
			Assert.isNull(existUser, "用户名已存在(Username Is Exist)");
			
			EnumUserType userType = EnumUserType.valueOf(type);
			
			User user=new User();
			user.setUsername(username);
			user.setType(userType);
			
			if (EnumUserType.HFM==userType) {
				user.setAttribute(attribute);
				
				String corporation="";
				if (corporationCode !=null && readonly!=null) {
					Assert.isTrue(corporationCode.length==readonly.length, "法人参数错误(Entity Parameter Error)");
					for (int i = 0; i < corporationCode.length; i++) {
						corporation+=(readonly[i]?"T_":"F_") + corporationCode[i].trim()+",";
					}
					corporation=corporation.substring(0, corporation.length()-1);
				}
				user.setCorporationCode(corporation);
				
				String v_entity="";
				if (entity !=null && entityReadonly!=null) {
					Assert.isTrue(entity.length==entityReadonly.length, "HFM公司编码参数错误(Entity Parameter Error)");
					for (int i = 0; i < entity.length; i++) {
						v_entity+=(entityReadonly[i]?"T_":"F_") + entity[i].trim()+",";
					}
					v_entity=v_entity.substring(0, v_entity.length()-1);
				}
				user.setEntity(v_entity);
				
				String v_ebs="";
				if (ebs !=null && ebsReadonly!=null) {
					Assert.isTrue(ebs.length==ebsReadonly.length, "EBS公司编码参数错误(Entity Parameter Error)");
					for (int i = 0; i < ebs.length; i++) {
						v_ebs+=(ebsReadonly[i]?"T_":"F_") + ebs[i].trim()+",";
					}
					v_ebs=v_ebs.substring(0, v_ebs.length()-1);
				}
				user.setEbs(v_ebs);
			}else if (EnumUserType.BI==userType) {
				Assert.isTrue(menus!=null, "请选择菜单[Please Select Menu]");
				
				String menu="";
				for (int i = 0; i < menus.length; i++) {
					menu+=menus[i].trim()+",";
				}
				menu=menu.substring(0, menu.length()-1);
				user.setMenus(menu);
				
				String corporation="";
				if (corporationCode !=null && readonly!=null) {
					Assert.isTrue(corporationCode.length==readonly.length, "法人参数错误(Entity Parameter Error)");
					for (int i = 0; i < corporationCode.length; i++) {
						corporation+=(readonly[i]?"T_":"F_") + corporationCode[i].trim()+",";
					}
					corporation=corporation.substring(0, corporation.length()-1);
				}
				user.setCorporationCode(corporation);
			}else if(EnumUserType.Budget==userType){
				Assert.isTrue(menus!=null, "请选择菜单[Please Select Menu]");
				String menu="";
				for (int i = 0; i < menus.length; i++) {
					menu+=menus[i].trim()+",";
				}
				menu=menu.substring(0, menu.length()-1);
				user.setMenus(menu);
				
				Assert.isTrue(sbu!=null, "请添加SBU[Please Add SBU]");
				Map<String,String> sbuMap=new HashMap<String,String>();
				for (String SBU : sbu) {
					sbuMap.put(SBU, SBU);
				}
				String targetSBU="";
				for (String SBU : sbuMap.values()) {
					targetSBU+=SBU.trim()+",";
				}
				targetSBU=targetSBU.substring(0, targetSBU.length()-1);
				
				user.setCorporationCode(targetSBU);
			}
			
			Md5PasswordEncoder encoder=new Md5PasswordEncoder();
			user.setPassword(encoder.encodePassword("11111111", null));
			userService.save(user);
		}catch (Exception e) {
			logger.error("新增用户失败", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", "新增用户失败(Add User Fail) : " + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}
	
	@RequestMapping(value="/update")
	@ResponseBody
	public String update(HttpServletRequest request,AjaxResult ajaxResult,String id,String username,String attribute,String[] menus,String[] corporationCode,boolean[] readonly,String[] entity,boolean[] entityReadonly,String[] ebs,boolean[] ebsReadonly,String[] sbu){
		try {
			Assert.hasText(id, "用户ID不能为空(User Not Null)");
			User target = userService.get(id);
			target.setUsername(username);
			
			EnumUserType userType = target.getType();
			if (EnumUserType.HFM==userType) {
				target.setAttribute(attribute);
				
				String corporation="";
				if (corporationCode !=null && readonly!=null) {
					Assert.isTrue(corporationCode.length==readonly.length, "法人参数错误(Entity Parameter Error)");
					for (int i = 0; i < corporationCode.length; i++) {
						corporation+=(readonly[i]?"T_":"F_") + corporationCode[i].trim()+",";
					}
					corporation=corporation.substring(0, corporation.length()-1);
				}
				target.setCorporationCode(corporation);
				
				String v_entity="";
				if (entity !=null && entityReadonly!=null) {
					Assert.isTrue(entity.length==entityReadonly.length, "HFM公司编码参数错误(Entity Parameter Error)");
					for (int i = 0; i < entity.length; i++) {
						v_entity+=(entityReadonly[i]?"T_":"F_") + entity[i].trim()+",";
					}
					v_entity=v_entity.substring(0, v_entity.length()-1);
				}
				target.setEntity(v_entity);
				
				String v_ebs="";
				if (ebs !=null && ebsReadonly!=null) {
					Assert.isTrue(ebs.length==ebsReadonly.length, "EBS公司编码参数错误(Entity Parameter Error)");
					for (int i = 0; i < ebs.length; i++) {
						v_ebs+=(ebsReadonly[i]?"T_":"F_") + ebs[i].trim()+",";
					}
					v_ebs=v_ebs.substring(0, v_ebs.length()-1);
				}
				target.setEbs(v_ebs);
			}else if (EnumUserType.BI==userType) {
				Assert.isTrue(menus!=null, "请选择菜单[Please Select Menu]");
				
				String menu="";
				for (int i = 0; i < menus.length; i++) {
					menu+=menus[i].trim()+",";
				}
				menu=menu.substring(0, menu.length()-1);
				target.setMenus(menu);
				
				String corporation="";
				if (corporationCode !=null && readonly!=null) {
					Assert.isTrue(corporationCode.length==readonly.length, "法人参数错误(Entity Parameter Error)");
					for (int i = 0; i < corporationCode.length; i++) {
						corporation+=(readonly[i]?"T_":"F_") + corporationCode[i].trim()+",";
					}
					corporation=corporation.substring(0, corporation.length()-1);
				}
				target.setCorporationCode(corporation);
			}else if(EnumUserType.Budget==userType){
				Assert.isTrue(menus!=null, "请选择菜单[Please Select Menu]");
				
				String menu="";
				for (int i = 0; i < menus.length; i++) {
					menu+=menus[i].trim()+",";
				}
				menu=menu.substring(0, menu.length()-1);
				target.setMenus(menu);
				
				Assert.isTrue(sbu!=null, "请添加SBU[Please Add SBU]");
				Map<String,String> sbuMap=new HashMap<String,String>();
				for (String SBU : sbu) {
					sbuMap.put(SBU, SBU);
				}
				String targetSBU="";
				for (String SBU : sbuMap.values()) {
					targetSBU+=SBU.trim()+",";
				}
				targetSBU=targetSBU.substring(0, targetSBU.length()-1);
				target.setCorporationCode(targetSBU);
			}
			
			userService.update(target);
		}catch (Exception e) {
			logger.error("更新用户信息失败", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", "更新用户信息失败(Update User Fail) : " + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}
	
	@RequestMapping(value="/enable")
	@ResponseBody
	public String enable(HttpServletRequest request,AjaxResult ajaxResult,String id,boolean enable){
		try {
			Assert.hasText(id,"用户ID不能为空(User Not Null)");
			
			User user = userService.get(id);
			
			Assert.notNull(user, "操作的用户不存在(User Not Null)");
			
			user.setEnable(enable);
			userService.save(user);
		}catch (Exception e) {
			logger.error("变更用户状态失败", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", "变更用户状态失败(Operation Fail):" + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}
	
	@RequestMapping(value = "/pwdModify", method = RequestMethod.POST)
	@ResponseBody
	public String pwdModify(AjaxResult ajaxResult,HttpServletResponse response,String tokenId,String password,String newPassword) {
		try {
			Assert.hasText(password, "原始密码不能为空");
			Assert.hasText(newPassword, "新密码不能为空");
			Assert.isTrue(!password.equals(newPassword), "新密码不能和原始密码相同");
			UserDetailImpl loginUser = SecurityUtils.getLoginUser();
			User user = userService.getEnableByUsername(loginUser.getUsername());
			
			Assert.isTrue(user.getPassword().equals(password), "原始密码不正确");
			
			user.setPassword(newPassword);
			
			userService.save(user);
			
			return ajaxResult.getJson();
		} catch (Exception e) {
			logger.error("修改密码失败:", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", "Modify Password Fail:"+ExceptionUtil.getRootCauseMessage(e));
			
			return ajaxResult.getJson();
		}
	}
	
}