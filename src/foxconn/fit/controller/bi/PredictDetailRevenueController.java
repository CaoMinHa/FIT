package foxconn.fit.controller.bi;

import foxconn.fit.advice.Log;
import foxconn.fit.controller.BaseController;
import foxconn.fit.entity.base.AjaxResult;
import foxconn.fit.entity.budget.PredictDetailRevenue;
import foxconn.fit.service.budget.ForecastDetailRevenueSrcService;
import foxconn.fit.service.budget.PredictDetailRevenueService;
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
 *營收下的銷貨收入預測表
 */
@Controller
@RequestMapping("/bi/predictDetailRevenue")
public class PredictDetailRevenueController extends BaseController {

	@Autowired
	private PredictDetailRevenueService forecastRevenueService;
	
	@Autowired
	private ForecastDetailRevenueSrcService forecastDetailRevenueSrcService;

	@RequestMapping(value = "index")
	public String index(Model model) {
		model=forecastRevenueService.index(model);
		return "/bi/predictDetailRevenue/index";
	}

	@RequestMapping(value="/list")
	public String list(Model model,PageRequest pageRequest,String entity,String year,String version) {
		try {
			String sql=forecastRevenueService.list(year,version,entity);
			Page<Object[]> page = forecastRevenueService.findPageBySql(pageRequest, sql, PredictDetailRevenue.class);
			model.addAttribute("page", page);
			model.addAttribute("year", year.substring(2));
		} catch (Exception e) {
			logger.error("查询營收銷貨收入預測表列表失败:", e);
		}
		return "/bi/predictDetailRevenue/list";
	}

	@RequestMapping(value="/delete")
	@ResponseBody
	public String delete(HttpServletRequest request,AjaxResult ajaxResult,String id){
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		ajaxResult.put("msg", getLanguage(locale, "删除成功", "Delete Success"));
		try {
			Assert.hasText(id, getLanguage(locale, "ID不能为空", "Id can not be null"));
			forecastRevenueService.delete(id);
		} catch (Exception e) {
			logger.error("删除失败:", e);
			ajaxResult.put("flag", "fail");
			ajaxResult.put("msg", getLanguage(locale, "删除失败", "Delete Fail")+ " : " + ExceptionUtil.getRootCauseMessage(e));
		}
		
		return ajaxResult.getJson();
	}
	
	@RequestMapping(value = "upload")
	@ResponseBody
	@Log(name = "營收銷貨收入預測表-->上传")
	public String upload(HttpServletRequest request, AjaxResult result) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		result.put("msg", getLanguage(locale, "上传成功", "Upload Success"));
		MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
		return forecastRevenueService.upload(result,locale,multipartHttpServletRequest);
	}

	
	@RequestMapping(value = "download")
	@ResponseBody
	@Log(name = "營收銷貨收入預測表-->下载")
	public synchronized String download(HttpServletRequest request,PageRequest pageRequest,AjaxResult result,
			@Log(name = "SBU") String entitys,@Log(name = "年") String year,@Log(name = "版本") String version){
		try {
			Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			Assert.hasText(year, getLanguage(locale, "年不能为空", "Year can not be null"));
			Assert.hasText(entitys, getLanguage(locale, "SBU不能为空", "SBU can not be null"));
			Map<String,String> map=forecastRevenueService.download(entitys,year,version,request,pageRequest);
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
	public synchronized String dimension(HttpServletRequest request, AjaxResult result) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		 Map<String,String> map=forecastDetailRevenueSrcService.dimension(request);
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
	public synchronized String template(HttpServletRequest request, AjaxResult result,@Log(name ="年份")String year) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Map<String,String> map=forecastRevenueService.template(request,year);
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
	public synchronized String version(HttpServletRequest request, AjaxResult result) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request,SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		String version=forecastRevenueService.version();
		if(version.indexOf("_")!=-1){
			result.put("flag", "fail");
			result.put("msg", getByLocale(locale,version));
			return result.getJson();
		}
		result.put("version", version);
		return result.getJson();
	}
}
