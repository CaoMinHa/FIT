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
import foxconn.fit.entity.ExpenseAllocation.AllocationResult;
import foxconn.fit.entity.ExpenseAllocation.AllocationResultInfo;
import foxconn.fit.entity.ProfitLoss.LegalProfitloss;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.investment.ProjectBudget;
import foxconn.fit.entity.investment.ProjectForecast;
import foxconn.fit.service.ExpenseAllocation.AllocationResultService;
import foxconn.fit.service.ProfitLoss.LegalProfitlossService;
import foxconn.fit.service.investment.ProjectBudgetService;
import foxconn.fit.util.ExceptionUtil;
/**
 * @author cxj20241220
 * 分摊結果
 */
@Controller
@RequestMapping("/bi/allocationResult")
public class AllocationResultController extends BaseController {
		@Autowired
		private AllocationResultService allocationResultService;

		@RequestMapping(value = "index")
		public String index(Model model,HttpServletRequest request) {
			model=allocationResultService.index(model);
			return "/bi/allocationResult/index";
		}

		@RequestMapping(value="/list")
		@Log(name = "預算分攤結果-->查詢")
		public String list(Model model,PageRequest pageRequest,@Log(name="情景") String scenarios,@Log(name ="數據類型") String types, @Log(name="年份") String years) {
			try {
				if(scenarios.equals("Budget")){
					pageRequest.setPageSize(20);
					String sql=allocationResultService.viewList(years,scenarios,types);
					Page<Object[]> page = allocationResultService.findPageBySql(pageRequest, sql, AllocationResultInfo.class);
					
					model.addAttribute("page", page);
				}
				model.addAttribute("year", years.substring(2));
				model.addAttribute("scenarios", scenarios);
			} catch (Exception e) {
				logger.error("查询分攤版本信息失败:", e);
			}
			return "/bi/allocationResult/list";
		}

		@RequestMapping(value = "download")
		@ResponseBody
		@Log(name = "預算分攤結果-->下载")
		
		
		public synchronized String download(HttpServletRequest request,AjaxResult result,PageRequest pageRequest,
				@Log(name = "ID") String id,@Log(name = "年份") String years){
			try {
				Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
				Assert.hasText(years, getLanguage(locale, "年份不能为空", "The year cannot be empty"));
				Assert.hasText(id, getLanguage(locale, "id不能为空", "The id cannot be empty"));
				logger.info(id);
				//logger.info(years);
				Map<String,String> map=allocationResultService.downloadBudget(years,id,request,pageRequest);
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