package foxconn.fit.controller.investment;

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
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.investment.SignedINVBudget;
import foxconn.fit.service.investment.SignedINVBudgetService;
import foxconn.fit.util.ExceptionUtil;

/**
 * @author cxj20250211
 * 投資預算
 */
@Controller
@RequestMapping("/bi/signedINVBudget")
public class SignedINVBudgetController extends BaseController {

	@Autowired
	private SignedINVBudgetService signedINVBudgetService;

	private static String budgetTable="CUX_INV_BUDGET_INFO";

	@RequestMapping(value = "index")
	public String index(Model model) {
		model=signedINVBudgetService.index(model);
		return "/bi/signedINVBudget/index";
	}

	@RequestMapping(value="/list")
	@Log(name = "投資預算-->查詢")
	public String list(Model model,PageRequest pageRequest,@Log(name="情景") String scenarios,@Log(name ="SBU") String entitys,
					   @Log(name="年份") String year) {
		try {
			if(scenarios.equals("Budget")){
				pageRequest.setPageSize(20);
				String sql=signedINVBudgetService.viewList(year,entitys,budgetTable);
				Page<Object[]> page = signedINVBudgetService.findPageBySql(pageRequest, sql, SignedINVBudget.class);
				//logger.info(sql);				
				model.addAttribute("page", page);
			}
			model.addAttribute("year", year.substring(2));
			model.addAttribute("scenarios", scenarios);
		} catch (Exception e) {
			logger.error("查询投資預算明細列表失败:", e);
		}
		return "/bi/signedINVBudget/list";
	}

	

	

	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "投資預算-->下载")
	public synchronized String download(HttpServletRequest request,AjaxResult result,PageRequest pageRequest,
			@Log(name = "ID") String id,@Log(name = "年份") String years){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			logger.info(id);
			logger.info(years);
			Assert.hasText(years, getLanguage(locale, "年份不能为空", "The year cannot be empty"));
			Assert.hasText(id, getLanguage(locale, "id不能为空", "The id cannot be empty"));
			Map<String,String> map=signedINVBudgetService.downloadBudget(years,id,request,pageRequest);
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
