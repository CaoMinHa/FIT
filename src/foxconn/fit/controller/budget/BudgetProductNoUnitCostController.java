package foxconn.fit.controller.budget;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.budget.BudgetProductNoUnitCost;
import foxconn.fit.entity.budget.ForecastSalesCostV;
import foxconn.fit.service.budget.BudgetProductNoUnitCostService;
import foxconn.fit.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

/**
 * @author maggao
 */
@Controller
@RequestMapping("/bi/budgetProductNoUnitCost")
public class BudgetProductNoUnitCostController extends BaseController {

	@Autowired
	private BudgetProductNoUnitCostService budgetProductNoUnitCostService;


	@RequestMapping(value = "index")
	public String index(Model model,HttpServletRequest request) {
		model=budgetProductNoUnitCostService.index(model);
		return "/bi/budgetProductNoUnitCost/index";
	}

	/**頁面查詢*/
	@RequestMapping(value="/list")
	@Log(name = "銷售成本-->查詢")
	public String list(Model model,HttpServletRequest request,PageRequest pageRequest,@Log(name="版本") String scenarios,@Log(name ="SBU") String entitys,
					   @Log(name="年份") String year,@Log(name="版本") String version) {
		try {
			if(scenarios.equals("budget")){
				String sql=budgetProductNoUnitCostService.budgetList(year,version,entitys);
				Page<Object[]> page = budgetProductNoUnitCostService.findPageBySql(pageRequest, sql, BudgetProductNoUnitCost.class);
				model.addAttribute("page", page);
			}else{
				String sql=budgetProductNoUnitCostService.forecastList(year,version,entitys);
				Page<Object[]> page = budgetProductNoUnitCostService.findPageBySql(pageRequest, sql, ForecastSalesCostV.class);
				model.addAttribute("page", page);
			}
			model.addAttribute("scenarios", scenarios);
			model.addAttribute("year", year.substring(2));
		} catch (Exception e) {
			logger.error("查询預算(預測)營收明細列表失败:", e);
		}
		return "/bi/budgetProductNoUnitCost/list";
	}

	/**單條數據刪除*/
	@RequestMapping(value="/delete")
	@ResponseBody
	@Log(name = "銷售成本-->單條數據刪除")
	public String delete(HttpServletRequest request,AjaxResult ajaxResult,Model model,@Log(name="ID")String id,@Log(name = "場景") String scenarios){
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		ajaxResult.put("msg", getLanguage(locale, "删除成功", "Delete Success"));
		try {
			Assert.hasText(id, getLanguage(locale, "ID不能为空", "Id can not be null"));
			if(scenarios.equals("budget")){
				budgetProductNoUnitCostService.delete(id);
			}else{
				budgetProductNoUnitCostService.deleteForecast(id);
			}
		} catch (Exception e) {
			logger.error("删除失败:", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", getLanguage(locale, "删除失败", "Delete Fail")+ " : " + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}

	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "銷售成本-->上传")
	public String upload(HttpServletRequest request,HttpServletResponse response, AjaxResult result,@Log(name="場景") String scenarios) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale, "上传成功", "Upload Success"));
		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
		if(scenarios.equals("budget")) {
			return budgetProductNoUnitCostService.uploadBudget(result, locale, multipartHttpServletRequest);
		}else{
			return budgetProductNoUnitCostService.uploadForecast(result, locale, multipartHttpServletRequest);
		}
	}

	
	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "销售成本-->下载")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "SBU") String entitys,@Log(name = "年") String year,@Log(name = "版本") String version,@Log(name="場景")String scenarios){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(scenarios, getLanguage(locale, "場景不能为空", "The scene cannot be empty"));
			Assert.hasText(year, getLanguage(locale, "年不能为空", "Year can not be null"));
			Assert.hasText(entitys, getLanguage(locale, "SBU不能为空", "SBU can not be null"));
			Map<String,String> map=null;
			if("forecast".equals(scenarios)){
				map=budgetProductNoUnitCostService.downloadForecast(entitys,year,version,request,pageRequest);
			}else{
				map=budgetProductNoUnitCostService.downloadBudget(entitys,year,version,request,pageRequest);
			}
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


	/**銷售成本 下載模板*/
	@RequestMapping(value = "template")
	@ResponseBody
	@Log(name = "銷售成本-->下載上傳模板")
	public synchronized String template(HttpServletRequest request, HttpServletResponse response, AjaxResult result,@Log(name="場景") String type) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> map=null;
		if("budget".equals(type)){
			map=budgetProductNoUnitCostService.templateBudget(request);
		}else{
			map=budgetProductNoUnitCostService.templateForecast(request);
		}
		if("Y".equals(map.get("result"))){
			result.put("fileName", map.get("file"));
		}else{
			result.put("flag", "fail");
			result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + map.get("str"));
		}
		return result.getJson();
	}

	/**
	 * 產品料號單位成本預算 下載簡易模板
	 */
	@RequestMapping(value = "simplifyTemplate")
	@ResponseBody
	@Log(name = "銷售成本預測-->下載簡易版上傳模板")
	public synchronized String simplifyTemplate(HttpServletRequest request, HttpServletResponse response, AjaxResult result,@Log(name="場景") String type) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> map=null;
		if("budget".equals(type)){
			map=budgetProductNoUnitCostService.simplifyTemplateBudget(request);
		}else{
			map=budgetProductNoUnitCostService.simplifyTemplateForecast(request);
		}
		if(map.get("result")=="Y"){
			result.put("fileName", map.get("file"));
		}else{
			result.put("flag", "fail");
			result.put("msg", getLanguage(locale, "下載模板文件失敗", "Fail to download template file") + " : " + map.get("str"));
		}
		return result.getJson();
	}

	/**
	 * 存儲版本
	 */
	@RequestMapping(value = "version")
	@ResponseBody
	@Log(name = "銷售成本-->存儲版本")
	public synchronized String version(HttpServletRequest request, HttpServletResponse response, AjaxResult result,@Log(name = "場景") String scenarios) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		String version="";
		if("forecast".equals(scenarios)){
			version=budgetProductNoUnitCostService.versionForecast();
			if(version.indexOf("_")!=-1){
				result.put("flag", "fail");
				result.put("msg", getByLocale(locale,version));
				return result.getJson();
			}
		}else{
			version=budgetProductNoUnitCostService.versionBudget();
			if(version.indexOf("_")!=-1){
				result.put("flag", "fail");
				result.put("msg", getByLocale(locale,version));
				return result.getJson();
			}
		}
		result.put("version", version);
		return result.getJson();
	}
}
