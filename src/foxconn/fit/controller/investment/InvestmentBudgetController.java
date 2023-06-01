package foxconn.fit.controller.investment;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.investment.InvestmentBudget;
import foxconn.fit.entity.investment.InvestmentForecast;
import foxconn.fit.service.investment.InvestmentBudgetService;
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
import java.util.Locale;
import java.util.Map;

/**
 * @author maggao
 * 投資預算
 */
@Controller
@RequestMapping("/bi/investmentBudget")
public class InvestmentBudgetController extends BaseController {

	@Autowired
	private InvestmentBudgetService investmentBudgetService;

	private static String budgetTable="FIT_INVESTMENT_BUDGET";
	private static String forecastTabel="FIT_INVESTMENT_FORECAST";

	@RequestMapping(value = "index")
	public String index(Model model) {
		model=investmentBudgetService.index(model);
		model.addAttribute("versionList", investmentBudgetService.versionVal(budgetTable,forecastTabel));
		return "/bi/investmentBudget/index";
	}

	@RequestMapping(value="/list")
	@Log(name = "投資預算-->查詢")
	public String list(Model model,PageRequest pageRequest,@Log(name="版本") String scenarios,@Log(name ="SBU") String entitys,
					   @Log(name="年份") String year,@Log(name="版本") String version) {
		try {
			if(scenarios.equals("budget")){
				String sql=investmentBudgetService.viewList(year,version,entitys,budgetTable);
				Page<Object[]> page = investmentBudgetService.findPageBySql(pageRequest, sql, InvestmentBudget.class);
				model.addAttribute("page", page);
			}else{
				String sql=investmentBudgetService.viewList(year,version,entitys,forecastTabel);
				Page<Object[]> page = investmentBudgetService.findPageBySql(pageRequest, sql, InvestmentForecast.class);
				model.addAttribute("page", page);
			}
			model.addAttribute("year", year.substring(2));
			model.addAttribute("scenarios", scenarios);
		} catch (Exception e) {
			logger.error("查询投資預算明細列表失败:", e);
		}
		return "/bi/investmentBudget/list";
	}

	@RequestMapping(value="/delete")
	@ResponseBody
	@Log(name = "投資預算-->單條數據刪除")
	public String delete(HttpServletRequest request,AjaxResult ajaxResult,@Log(name="ID") String id,@Log(name = "場景")String scenarios){
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		ajaxResult.put("msg", getLanguage(locale, "删除成功", "Delete Success"));
		try {
			Assert.hasText(id, getLanguage(locale, "ID不能为空", "Id can not be null"));
			if(scenarios.equals("budget")){
				investmentBudgetService.delete(id);
			}else{
				investmentBudgetService.deleteForecast(id);
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
	@Log(name = "投資預算-->上传")
	public String upload(HttpServletRequest request, AjaxResult result,@Log(name="場景") String scenarios) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale, "上传成功", "Upload Success"));
		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
		return investmentBudgetService.uploadBudget(result, locale, multipartHttpServletRequest,scenarios);
	}


	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "投資預算-->下载")
	public synchronized String download(HttpServletRequest request,PageRequest pageRequest,AjaxResult result,
			@Log(name = "SBU") String entitys,@Log(name = "年") String year,@Log(name = "版本") String version,@Log(name="場景")String scenarios){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(scenarios, getLanguage(locale, "場景不能为空", "The scene cannot be empty"));
			Assert.hasText(year, getLanguage(locale, "年不能为空", "Year can not be null"));
			Assert.hasText(entitys, getLanguage(locale, "SBU不能为空", "SBU can not be null"));
			Map<String,String> map=investmentBudgetService.downloadBudget(entitys,year,version,request,pageRequest,scenarios);
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

	/**
	 * 下載維度表
	 */
	@RequestMapping(value = "dimension")
	@ResponseBody
	@Log(name = "投資預算-->下載維度表")
	public synchronized String dimension(HttpServletRequest request, AjaxResult result) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		 Map<String,String> map=investmentBudgetService.dimension(request);
			if(map.get("result")=="Y"){
				result.put("fileName", map.get("str"));
			}else{
				result.put("flag", "fail");
				result.put("msg", getLanguage(locale, "下載失敗", "download failed") + " : " + map.get("str"));
			}
		return result.getJson();
	}


	/**
	 * 下載模板
	 */
	@RequestMapping(value = "template")
	@ResponseBody
	@Log(name = "投資預算-->下載模板")
	public synchronized String template(HttpServletRequest request, AjaxResult result,@Log(name = "場景") String type) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> map=investmentBudgetService.template(request,type);
		if(map.get("result").equals("Y")){
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
	@Log(name = "投資預算-->存儲版本")
	public synchronized String version(HttpServletRequest request, AjaxResult result,@Log(name = "場景") String scenarios) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		String version="";
		if("forecast".equals(scenarios)){
			version=investmentBudgetService.versionBudget(forecastTabel);
		}else{
			version=investmentBudgetService.versionBudget(budgetTable);
		}
		if(version.contains("_")){
			result.put("flag", "fail");
			result.put("msg", getByLocale(locale,version));
			return result.getJson();
		}
		result.put("version", version);
		return result.getJson();
	}
}
