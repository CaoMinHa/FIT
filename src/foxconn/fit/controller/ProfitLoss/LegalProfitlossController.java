package foxconn.fit.controller.ProfitLoss;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.ProfitLoss.DataSource;
import foxconn.fit.entity.ProfitLoss.LegalProfitloss;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.ProfitLoss.LegalProfitlossService;
import foxconn.fit.util.ExceptionUtil;
/**
 * @author cxj
 * 法人損益表
 */
@Controller
@RequestMapping("/bi/legalProfitloss")
public class LegalProfitlossController extends BaseController {

	@Autowired
	private LegalProfitlossService legalProfitlossService;

	@RequestMapping(value = "index")
	public String index(Model model,HttpServletRequest request) {
		model=legalProfitlossService.index(model);
		model.addAttribute("entityList", legalProfitlossService.entityVal());
		return "/bi/legalProfitloss/index";
	}

	@RequestMapping(value="/list")
	@Log(name = "法人損益表-->查詢")
	public String list(Model model,PageRequest pageRequest,@Log(name="情景") String scenarios,@Log(name ="法人") String entitys, @Log(name="年份") String years) {
		try {
			if(scenarios.equals("Budget")){
				pageRequest.setPageSize(20);
				String sql=legalProfitlossService.viewList(years,scenarios,entitys);
				Page<Object[]> page = legalProfitlossService.findPageBySql(pageRequest, sql, LegalProfitloss.class);
				//Page<Object[]> page = legalProfitlossService.findPageBySql(pageRequest, sql);
				model.addAttribute("page", page);
			}
			model.addAttribute("year", years.substring(2));
			model.addAttribute("scenarios", scenarios);
		} catch (Exception e) {
			logger.error("查询法人損益表明細列表失败:", e);
		}
		return "/bi/legalProfitloss/list";
	}

	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "法人損益表-->下载")
	/*
	public synchronized String download(HttpServletRequest request,PageRequest pageRequest,AjaxResult result,
			@Log(name = "法人") String entitys,@Log(name = "年份") String years,@Log(name = "版本") String versions,@Log(name="場景")String scenarios){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(scenarios, getLanguage(locale, "場景不能为空", "The scene cannot be empty"));
			Assert.hasText(years, getLanguage(locale, "年不能为空", "Year can not be null"));
			Assert.hasText(entitys, getLanguage(locale, "法人不能为空", "Legal can not be null"));
			logger.info(years);
			logger.info(entitys);
			logger.info(versions);
			logger.info(scenarios);
			Map<String,String> map=legalProfitlossService.downloadBudget(entitys,years,versions,request,pageRequest,scenarios);
			if(map.get("result").equals("Y")){
				result.put("fileName", map.get("file"));
			}else{
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + map.get("str"));
			}
		} catch (Exception e) {
			logger.error("下载Excel失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}*/
	
	public synchronized String download(HttpServletRequest request,AjaxResult result,
			@Log(name = "ID") String id,@Log(name = "年份") String years){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(years, getLanguage(locale, "年份不能为空", "The year cannot be empty"));
			Assert.hasText(id, getLanguage(locale, "id不能为空", "The id cannot be empty"));
			//logger.info(id);
			//logger.info(years);
			Map<String,String> map=legalProfitlossService.downloadBudget(years,id,request);
			if(map.get("result").equals("Y")){
				result.put("fileName", map.get("file"));
			}else{
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + map.get("str"));
			}
		} catch (Exception e) {
			logger.error("下载Excel失败", e);
			result.put("flag", "fail");
			result.put("msg", ExceptionUtil.getRootCauseMessage(e));
		}
		return result.getJson();
	}


	
}