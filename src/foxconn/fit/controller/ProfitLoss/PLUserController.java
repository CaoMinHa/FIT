package foxconn.fit.controller.ProfitLoss;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.base.EnumUserType;
import foxconn.fit.entity.base.User;
import foxconn.fit.service.base.UserService;
import foxconn.fit.service.bi.PoRoleService;
import foxconn.fit.util.ExceptionUtil;
import foxconn.fit.util.SecurityUtils;



	/**
	 * cxj 20241116
	 **/
	@Controller
	@RequestMapping("/bi/plUser")
	public class PLUserController  extends BaseController {

	    @Autowired
	    private UserService userService;
	    @Autowired
	    private PoRoleService poRoleService;


	    @RequestMapping(value = "index")
	    public String index(PageRequest pageRequest, Model model, HttpServletRequest request) {
	        try {
	            Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
	            pageRequest.setOrderBy("serial");
	            pageRequest.setOrderDir("asc");
	            User user = userService.getByUsername(SecurityUtils.getLoginUsername());
	            model.addAttribute("attribute", user.getAttribute());
	            List entityList = userService.listBySql("select distinct entity from CUX_BUDGET_EXPENSE_HP_USD where scenario='Budget' and data_type='PL' ");
				model.addAttribute("entityList", entityList);
	        } catch (Exception e) {
	            logger.error("查询明细配置表列表信息失败", e);
	        }
	        return "/bi/plUser/index";
	    }


	    @RequestMapping(value="/list")
	    public String userList(Model model, PageRequest pageRequest,HttpServletRequest request,String name,String username) {
	        try {
	            String sql="select distinct ID , USERNAME ,REALNAME, TYPE ,budget_legal,ENABLE,CREATOR,create_time,update_time " +
	                    " from FIT_USER where 1=1 and enable='T' and menus like '%dataSource%' or menus like '%legalProfitloss%' ";
	            if(!StringUtils.isBlank(name)){
	                name="%"+name+"%";
	                sql=sql+" and USERNAME like "+"'"+name.trim()+"'";
	            }
	            sql+=" order by ID";
	            //System.out.println(sql);
	            Page<Object[]> page = poRoleService.findPageBySql(pageRequest, sql);
	            int index=1;
	            if(pageRequest.getPageNo()>1){
	                index=2;
	            }
	            model.addAttribute("index", index);
	            model.addAttribute("tableName", "FIT_USER");
	            model.addAttribute("page", page);
	        } catch (Exception e) {
	            logger.error("查询明细配置表列表失败:", e);
	        }
	        return "/bi/plUser/list";
	    }	

	    
	    @RequestMapping(value="/detail")
		public String detail(PageRequest pageRequest,Model model,String id){
			try {
				
				Assert.hasText(id, "用户ID不能为空");
				User user = userService.get(id);
				String legalList= user.getBudgetLegal();
				String targetList="";
				model.addAttribute("user", user);
				//System.out.println(legalList);
				if (StringUtils.isNotEmpty(legalList)) {
					for (String string : legalList.split(",")) {
						List<String> entity = userService.listBySql("select distinct entity_name from CUX_BUDGET_EXPENSE_HP_USD where scenario='Budget' and data_type='PL' and entity='"+string+"'");
						targetList+=entity.get(0)+",";
					}
					targetList=targetList.substring(0, targetList.length()-1);
				}

				model.addAttribute("legalList", targetList);
				//System.out.println(targetList);
				if (user.getType()==EnumUserType.BI) {
					List entityList = userService.listBySql("select distinct entity_name from CUX_BUDGET_EXPENSE_HP_USD where scenario='Budget' and data_type='PL' ");
					model.addAttribute("entityList", entityList);
				}
			} catch (Exception e) {
				logger.error("查询用户信息失败:", e);
			}
			
			return "/bi/plUser/detail";
		}
	    
	    @RequestMapping(value="/update2")
		@ResponseBody
		public String update(HttpServletRequest request,AjaxResult ajaxResult,String id,String username,String realname,String[] sbu){
			try {
				Assert.hasText(id, "用户ID不能为空(User Not Null)");
				User target = userService.get(id);
				target.setUsername(username);
				target.setRealname(realname);
				//System.out.println(realname);
				EnumUserType userType = target.getType();
				if (EnumUserType.BI==userType) {
					

					Assert.isTrue(sbu!=null, "请添加LEGAL[Please Add LEGAL");
					if(sbu!=null){
						Map<String,String> sbuMap=new HashMap<String,String>();
						for (String SBU : sbu) {
							sbuMap.put(SBU, SBU);
						}
						String targetSBU="";
						for (String SBU : sbuMap.values()) {
							//System.out.println(SBU.trim());
							List<String> entity = userService.listBySql("select distinct entity from CUX_BUDGET_EXPENSE_HP_USD where scenario='Budget' and data_type='PL' and entity_name='"+SBU.trim()+"'");
							//targetSBU+=SBU.trim()+",";
							targetSBU+=entity.get(0)+",";
						}
						targetSBU=targetSBU.substring(0, targetSBU.length()-1);
						target.setBudgetLegal(targetSBU);
					}
				}
				userService.update(target);
			}catch (Exception e) {
				logger.error("更新用户信息失败", e);
				ajaxResult.put("flag", "fail");
				ajaxResult.put("msg", "更新用户信息失败(Update User Fail) : " + ExceptionUtil.getRootCauseMessage(e));
			}
			
			return ajaxResult.getJson();
		}
	    
	    
	   
	    
	  
	}
