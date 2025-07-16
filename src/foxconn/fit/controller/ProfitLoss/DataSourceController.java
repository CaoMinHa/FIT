package foxconn.fit.controller.ProfitLoss;
import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.dao.base.PropertyFilter;
import foxconn.fit.entity.ProfitLoss.DataSource;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.investment.ProjectBudget;
import foxconn.fit.service.ProfitLoss.DataSourceService;
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
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * @author cxj
 * 法人損益表數據源
 */
@Controller
@RequestMapping("/bi/dataSource")
public class DataSourceController extends BaseController {

	@Autowired
	private DataSourceService dataSourceService;


	private static String budgetTable="CUX_BUDGET_EXPENSE_HP_USD";
	@RequestMapping(value = "index")
	public String index(Model model,HttpServletRequest request) {
		model=dataSourceService.index(model);
		model.addAttribute("versionList", dataSourceService.versionVal());
		model.addAttribute("entityList", dataSourceService.entityVal());
		return "/bi/dataSource/index";
	}

	@RequestMapping(value="/list")
	@Log(name = "數據源-->查詢")
	public String list(Model model,PageRequest pageRequest,@Log(name="情景") String scenarios,@Log(name ="法人") String entitys,
					   @Log(name="年份") String years,@Log(name="版本") String versions) {
		try {
			if(scenarios.equals("Budget")){
				pageRequest.setPageSize(20);
				String sql=dataSourceService.viewList(years,versions,entitys,scenarios);
				Page<Object[]> page = dataSourceService.findPageBySql(pageRequest, sql, DataSource.class);		
				
				model.addAttribute("page", page);
			}

			model.addAttribute("scenarios", scenarios);
			model.addAttribute("year", years.substring(2));
		} catch (Exception e) {
			logger.error("查询數據源明細列表失败:", e);
		}
		return "/bi/dataSource/list";
	}


	
	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "數據源-->下载")
	public synchronized String download(HttpServletRequest request,PageRequest pageRequest,AjaxResult result,
			@Log(name = "法人") String entitys,@Log(name = "年份") String years,@Log(name = "版本") String versions,@Log(name="情景")String scenarios){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(versions, getLanguage(locale, "版本不能为空", "The version cannot be empty"));
			Assert.hasText(years, getLanguage(locale, "年份不能为空", "Year can not be null"));
			Assert.hasText(entitys, getLanguage(locale, "法人不能为空", "Entity can not be null"));
			Map<String,String> map=dataSourceService.downloadBudget(entitys,years,versions,request,pageRequest,scenarios);
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