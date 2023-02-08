package foxconn.fit.controller.budget;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.budget.BudgetDetailRevenue;
import foxconn.fit.entity.budget.ForecastSalesRevenueV;
import foxconn.fit.service.budget.BudgetForecastDetailRevenueService;
import foxconn.fit.service.budget.BudgetRevenueCostService;
import foxconn.fit.service.budget.ForecastDetailRevenueSrcService;
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
@RequestMapping("/bi/budgetRevenueCost")
public class BudgetRevenueCostController extends BaseController {

	@Autowired
	private BudgetRevenueCostService budgetRevenueCostService;

	@RequestMapping(value = "index")
	public String index(Model model,HttpServletRequest request) {
		model=budgetRevenueCostService.index(model);
		return "/bi/budgetRevenueCost/index";
	}

	@RequestMapping(value="/list")
	@Log(name = "銷售收入-->查詢")
	public String list(Model model,HttpServletRequest request,PageRequest pageRequest,@Log(name="場景") String scenarios,@Log(name ="SBU") String entitys,
					   @Log(name="年份") String year,@Log(name="版本") String version) {
		try {
			pageRequest.setPageSize(15);
			Page<Object[]> page = budgetRevenueCostService.budgetList(pageRequest,year,version,entitys,scenarios);
			model.addAttribute("page", page);
			model.addAttribute("year", year.substring(2));
			model.addAttribute("scenarios", scenarios);
		} catch (Exception e) {
			logger.error("查询預算(預測)營收明細列表失败:", e);
		}
		return "/bi/budgetRevenueCost/list";
	}


	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "銷售收入-->下载")
	public synchronized String download(HttpServletRequest request,HttpServletResponse response,PageRequest pageRequest,AjaxResult result,
			@Log(name = "SBU") String entitys,@Log(name = "年") String year,@Log(name = "版本") String version,@Log(name="場景")String scenarios){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(scenarios, getLanguage(locale, "場景不能为空", "The scene cannot be empty"));
			Assert.hasText(year, getLanguage(locale, "年不能为空", "Year can not be null"));
			Assert.hasText(entitys, getLanguage(locale, "SBU不能为空", "SBU can not be null"));
			Map<String,String> map=budgetRevenueCostService.download(scenarios,entitys,year,version,request,pageRequest);
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
