package foxconn.fit.controller.ExpenseAllocation;

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
import foxconn.fit.entity.ExpenseAllocation.AllocationRatio;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.service.ExpenseAllocation.AllocationRatioService;
import foxconn.fit.util.ExceptionUtil;

/**
 * @author cxj20241219
 * 分摊預比例
 */
@Controller
@RequestMapping("/bi/allocationRatio")
public class AllocationRatioController extends BaseController {

	@Autowired
	private AllocationRatioService allocationRatioService;

		//private static String budgetTable="CUX_BUDGET_ALLOCATION_RATIO";
		@RequestMapping(value = "index")
		public String index(Model model,HttpServletRequest request) {
			model=allocationRatioService.index(model);
			return "/bi/allocationRatio/index";
		}

		@RequestMapping(value="/list")
		@Log(name = "預算分摊比例-->查詢")
		public String list(Model model,PageRequest pageRequest,@Log(name="情景") String scenarios,@Log(name="年份") String years,@Log(name="數據類型") String types) {
			try {
				if(scenarios.equals("Budget")){
					pageRequest.setPageSize(20);
					String sql=allocationRatioService.viewList(years,types,scenarios);
					Page<Object[]> page = allocationRatioService.findPageBySql(pageRequest, sql, AllocationRatio.class);		
					
					model.addAttribute("page", page);
				}

				model.addAttribute("scenarios", scenarios);
				model.addAttribute("year", years.substring(2));
			} catch (Exception e) {
				logger.error("查询分攤比例失败:", e);
			}
			return "/bi/allocationRatio/list";
		}


		
		@RequestMapping(value = "download")
		@ResponseBody
		@Log(name = "預算分摊比例-->下载")
		public synchronized String download(HttpServletRequest request,PageRequest pageRequest,AjaxResult result,
				@Log(name = "年份") String years,@Log(name="數據類型") String types,@Log(name="情景")String scenarios){
			try {
				Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
				Assert.hasText(years, getLanguage(locale, "年份不能为空", "Year can not be null"));
				Assert.hasText(types, getLanguage(locale, "法人不能为空", "Entity can not be null"));
				Map<String,String> map=allocationRatioService.downloadBudget(years,types,request,pageRequest,scenarios);
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